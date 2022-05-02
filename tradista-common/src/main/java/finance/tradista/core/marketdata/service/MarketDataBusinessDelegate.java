package finance.tradista.core.marketdata.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.TradistaProperties;
import finance.tradista.core.marketdata.model.FeedConfig;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.requestpreparator.RequestPreparator;
import finance.tradista.core.marketdata.requestpreparator.RequestPreparatorFactory;
import finance.tradista.core.marketdata.service.MarketDataService;
import finance.tradista.core.common.util.SecurityUtil;

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

public class MarketDataBusinessDelegate {

	private MarketDataService service;

	public MarketDataBusinessDelegate() {
		service = TradistaServiceLocator.getInstance().getMarketDataService();
	}

	public List<QuoteValue> getMarketData(Set<QuoteSet> quoteSets) throws TradistaBusinessException {
		if (quoteSets == null) {
			throw new TradistaBusinessException("The set of QuoteSets cannot be null");
		}

		if (quoteSets.isEmpty()) {
			throw new TradistaBusinessException("The set of QuoteSets cannot be empty");
		}
		Map<String, String> props = null;
		RequestPreparator requestPreparator = RequestPreparatorFactory
				.createRequestPreparator(TradistaProperties.getMarketDataProvider());
		if (requestPreparator != null) {
			props = requestPreparator.prepareRequest();
		}
		
		final Map<String, String> fProps = props;
		
		return SecurityUtil.runEx(() -> service.getMarketData(quoteSets, fProps));
	}

	public void getMarketData(Set<QuoteSet> quoteSets, FeedConfig feedConfig) throws TradistaBusinessException {

		StringBuilder errMsg = new StringBuilder();
		if (feedConfig == null) {
			errMsg.append("The feed config cannot be null.");
		}

		if (quoteSets == null) {
			errMsg.append("The set of QuoteSets cannot be null");
		} else {
			if (quoteSets.isEmpty()) {
				errMsg.append("The set of QuoteSets cannot be empty");
			}
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

		Map<String, String> props = null;
		RequestPreparator requestPreparator = RequestPreparatorFactory
				.createRequestPreparator(feedConfig.getFeedType().toString());
		if (requestPreparator != null) {
			props = requestPreparator.prepareRequest();
		}
		final Map<String, String> fProps = props;

		SecurityUtil.runEx(() -> service.getMarketData(quoteSets, fProps, feedConfig.getId()));
	}

}