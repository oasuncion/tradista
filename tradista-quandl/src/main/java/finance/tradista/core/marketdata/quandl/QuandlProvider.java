package finance.tradista.core.marketdata.quandl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.StringUtils;
import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.util.QuandlRuntimeException;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.marketdata.model.FeedConfig;
import finance.tradista.core.marketdata.model.Provider;
import finance.tradista.core.marketdata.model.QuoteValue;

/*
 * Copyright 2017 Olivier Asuncion
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

public class QuandlProvider implements Provider {

	private static final Properties properties = new Properties();

	protected QuandlSession session;

	{
		InputStream in = getClass().getResourceAsStream("/quandl.properties");
		try {
			properties.load(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		session = QuandlSession.create();
	}

	@Override
	public List<QuoteValue> getQuoteValues(FeedConfig feedConfig, Map<String, String> properties) {
		List<QuoteValue> quoteValues = new ArrayList<QuoteValue>();
		try {
			for (String data : feedConfig.getMapping().keySet()) {
				Map<String, String> fieldMapping = feedConfig.getFieldsMapping().get(data);
				TabularResult result = session.getDataSet(DataSetRequest.Builder.of(data).withStartDate(LocalDate.now())
						.withEndDate(LocalDate.now()).build());
				for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
					if (!StringUtils.isEmpty(entry.getValue())) {
						for (final Row row : result) {
							QuoteValue quoteValue = new QuoteValue(java.time.LocalDate.now(),
									feedConfig.getMapping().get(data));
							quoteValue.setEnteredDate(java.time.LocalDate.now());
							quoteValue.setSourceName(feedConfig.getFeedType().toString());
							Double quandlValue = row.getDouble(entry.getValue());
							if (quandlValue != null) {
								BigDecimal value = BigDecimal.valueOf(quandlValue);
								switch (entry.getKey()) {
								case QuoteValue.ASK: {
									quoteValue.setAsk(value);
									break;
								}
								case QuoteValue.BID: {
									quoteValue.setBid(value);
									break;
								}
								case QuoteValue.CLOSE: {
									quoteValue.setClose(value);
									break;
								}
								case QuoteValue.HIGH: {
									quoteValue.setHigh(value);
									break;
								}
								case QuoteValue.LAST: {
									quoteValue.setLast(value);
									break;
								}
								case QuoteValue.LOW: {
									quoteValue.setLow(value);
									break;
								}
								case QuoteValue.OPEN: {
									quoteValue.setOpen(value);
									break;
								}
								}
								quoteValues.add(quoteValue);
							}
						}

					}
				}
			}
		} catch (QuandlRuntimeException qre) {
			throw new TradistaTechnicalException(qre.getMessage());
		}
		return quoteValues;
	}

}