package finance.tradista.core.marketdata.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.marketdata.model.FeedConfig;
import finance.tradista.core.marketdata.model.Provider;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.FeedService;
import finance.tradista.core.marketdata.service.LocalConfigurationService;
import finance.tradista.core.marketdata.service.MarketDataService;
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

@Stateless
public class MarketDataServiceBean implements MarketDataService {

	private FeedConfig feedConfig;

	private Provider provider;

	@EJB
	private LocalConfigurationService configurationBean;

	@EJB
	private FeedService feedService;

	@PostConstruct
	public void initialize() {
		String feedConfigName = configurationBean.getFeedConfigName();
		if (StringUtils.isEmpty(feedConfigName)) {
			throw new TradistaTechnicalException("feedConfig property must be set. Please set it in "
					+ MarketDataConfigurationServiceBean.CONFIG_FILE_NAME + ".");
		}
		try {
			provider = configurationBean.getProvider();
		} catch (TradistaBusinessException abe) {
			throw new TradistaTechnicalException(String.format("Provider cannot be created: %s", abe.getMessage()));
		}
		if (provider == null) {
			throw new TradistaTechnicalException(
					"Market Data provider could not be instanciated. Please check the provider implementation in  "
							+ MarketDataConfigurationServiceBean.CONFIG_FILE_NAME + ".");
		}
		provider.init();

		Set<FeedConfig> feedConfigs = feedService.getFeedConfigsByName(feedConfigName);
		if (feedConfigs == null) {
			throw new TradistaTechnicalException("'" + feedConfigName + "' FeedConfig cannot be found.");
		} else {
			feedConfig = feedConfigs.toArray(new FeedConfig[] {})[0];
		}
	}

	@Override
	public List<QuoteValue> getMarketData(Set<QuoteSet> quoteSets, Map<String, String> properties)
			throws TradistaBusinessException {
		// 1. Contact the provider with the server mapping configuration
		List<QuoteValue> values = provider.getQuoteValues(feedConfig, properties);
		List<QuoteValue> resultValues = null;

		if (values != null && !values.isEmpty()) {
			resultValues = new ArrayList<QuoteValue>();
			QuoteBusinessDelegate quoteBusinessDelegate = new QuoteBusinessDelegate();

			// 2. Save all the quotesets with the retrieved values
			for (QuoteSet quoteSet : quoteSets) {
				quoteBusinessDelegate.saveQuoteValues(quoteSet.getId(), values);
			}

			// 3. Update the retrieved quotevalues to link them to the quote
			// sets

			for (QuoteValue qv : values) {
				for (QuoteSet qs : quoteSets) {
					qv.setQuoteSet(qs);
					resultValues.add(qv);
				}
			}
		}

		// 4. Return the retrieved values
		return resultValues;
	}

	@Override
	public void getMarketData(Set<QuoteSet> quoteSets, Map<String, String> properties, long feedConfigId)
			throws TradistaBusinessException {

		FeedConfig feedConfig = feedService.getFeedConfigById(feedConfigId);
		if (feedConfig == null) {
			throw new TradistaBusinessException(String.format("FeedConfig id %s cannot be found.", feedConfigId));
		}

		Provider provider = (Provider) configurationBean.getProviderByFeedType(feedConfig.getFeedType().toString());

		// 1. Contact the provider with the server mapping configuration
		List<QuoteValue> values = provider.getQuoteValues(feedConfig, properties);

		if (values != null && !values.isEmpty()) {
			QuoteBusinessDelegate quoteBusinessDelegate = new QuoteBusinessDelegate();

			// 2. Save all the quotesets with the retrieved values
			for (QuoteSet quoteSet : quoteSets) {
				quoteBusinessDelegate.saveQuoteValues(quoteSet.getId(), values);
			}
		}

	}

}