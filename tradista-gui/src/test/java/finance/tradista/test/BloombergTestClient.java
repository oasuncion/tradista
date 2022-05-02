package finance.tradista.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.service.MarketDataBusinessDelegate;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;

/*
 * Copyright 2015 Olivier Asuncion
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

public class BloombergTestClient {

	private static Properties userProperties = new Properties();

	{
		InputStream in = getClass().getResourceAsStream("/user.properties");
		try {
			userProperties.load(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Set<QuoteSet> quoteSets = new HashSet<QuoteSet>();
		try {
			QuoteSet qs = new QuoteBusinessDelegate().getQuoteSetByName("Test");
			quoteSets.add(qs);
			new MarketDataBusinessDelegate().getMarketData(quoteSets);
		} catch (TradistaBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
