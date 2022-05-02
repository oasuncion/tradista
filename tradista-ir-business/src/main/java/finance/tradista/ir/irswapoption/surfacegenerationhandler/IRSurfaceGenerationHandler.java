package finance.tradista.ir.irswapoption.surfacegenerationhandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.marketdata.model.Quote;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.model.SurfacePoint;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.marketdata.surfacegenerationhandler.SurfaceGenerationHandler;
import finance.tradista.ir.irswapoption.model.IRSwapOptionTrade;

/*
 * Copyright 2018 Olivier Asuncion
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

public class IRSurfaceGenerationHandler implements SurfaceGenerationHandler {

	@Override
	public List<SurfacePoint<Number, Number, Number>> buildSurfacePoints(List<Quote> quotes, LocalDate quoteDate,
			String instance, QuoteSet quoteSet) {
		List<SurfacePoint<Number, Number, Number>> surfacePoints = new ArrayList<SurfacePoint<Number, Number, Number>>();
		for (Quote quote : quotes) {
			String key = quote.getName().substring(IRSwapOptionTrade.IR_SWAP_OPTION.length() + 1);
			String[] prop = key.split("\\.");
			String frequency = prop[1];
			String swapMaturity = prop[2];
			String optionExpiry = prop[3];
			LocalDate maturityDate = null;
			LocalDate optionExpiryDate = null;

			// Volatilities are only quoted for the swap having a frequency of 6
			// months
			if (frequency.equals("6M")) {
				switch (swapMaturity) {
				case "1M": {
					maturityDate = quoteDate.plus(1, ChronoUnit.MONTHS);
					break;
				}
				case "3M": {
					maturityDate = quoteDate.plus(3, ChronoUnit.MONTHS);
					break;
				}
				case "6M": {
					maturityDate = quoteDate.plus(6, ChronoUnit.MONTHS);
					break;
				}
				case "1Y": {
					maturityDate = quoteDate.plus(1, ChronoUnit.YEARS);
					break;
				}
				}
				switch (optionExpiry) {
				case "3M": {
					optionExpiryDate = quoteDate.plus(3, ChronoUnit.MONTHS);
					break;
				}
				case "6M": {
					optionExpiryDate = quoteDate.plus(6, ChronoUnit.MONTHS);
					break;
				}
				case "1Y": {
					optionExpiryDate = quoteDate.plus(1, ChronoUnit.YEARS);
					break;
				}
				}
				long maturityDecimal = DateUtil.difference(quoteDate, maturityDate);
				long optionExpiryDecimal = DateUtil.difference(quoteDate, optionExpiryDate);
				BigDecimal volatility = null;
				QuoteValue quoteValue = new QuoteBusinessDelegate().getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(
						quoteSet.getId(), quote.getName(), quote.getType(), quoteDate);

				if (quoteValue != null) {
					volatility = getPrice(quoteValue, instance);
				}

				surfacePoints.add(
						new SurfacePoint<Number, Number, Number>(optionExpiryDecimal, maturityDecimal, volatility));

			}
		}

		return surfacePoints;
	}

	private BigDecimal getPrice(QuoteValue quoteValue, String instance) {
		switch (instance) {
		case "ASK":
			return quoteValue.getAsk();
		case "BID":
			return quoteValue.getBid();
		case "MID":
			return (quoteValue.getAsk().add(quoteValue.getBid())).divide(BigDecimal.valueOf(2));
		case "CLOSE":
			return quoteValue.getClose();
		case "OPEN":
			return quoteValue.getOpen();
		}
		return null;
	}

	@Override
	public List<SurfacePoint<Integer, BigDecimal, BigDecimal>> buildSurfacePoints2(List<Quote> quotes,
			LocalDate quoteDate, String instance, QuoteSet quoteSet) {
		// TODO Auto-generated method stub
		throw new RuntimeException("method Not used");
	}

}
