package finance.tradista.core.marketdata.service;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.FeedConfig;
import finance.tradista.core.marketdata.model.Quote;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.persistence.FeedConfigSQL;
import finance.tradista.core.marketdata.service.FeedConfigFilteringInterceptor;
import finance.tradista.core.marketdata.service.FeedService;
import finance.tradista.core.marketdata.service.QuoteService;

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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class FeedServiceBean implements LocalFeedService, FeedService {

	@EJB
	private QuoteService quoteService;

	@Interceptors(FeedConfigFilteringInterceptor.class)
	@Override
	public Set<FeedConfig> getFeedConfigsByName(String name) {
		return FeedConfigSQL.getFeedConfigsByName(name);
	}

	@Interceptors(FeedConfigFilteringInterceptor.class)
	@Override
	public FeedConfig getFeedConfigById(long id) {
		return FeedConfigSQL.getFeedConfigById(id);
	}

	@Override
	public Set<String> getAllFeedConfigNames() {
		return FeedConfigSQL.getAllFeedConfigNames();
	}

	@Interceptors(FeedConfigFilteringInterceptor.class)
	@Override
	public Set<FeedConfig> getAllFeedConfigs() {
		return FeedConfigSQL.getAllFeedConfigs();
	}

	@Interceptors(FeedConfigFilteringInterceptor.class)
	@Override
	public long saveFeedConfig(FeedConfig feedConfig) throws TradistaBusinessException {
		if (feedConfig.getId() == 0) {
			checkFeedConfigExistence(feedConfig);
		} else {
			FeedConfig oldFeedConfig = FeedConfigSQL.getFeedConfigById(feedConfig.getId());
			if (!oldFeedConfig.getName().equals(feedConfig.getName())) {
				checkFeedConfigExistence(feedConfig);
			}
		}
		return FeedConfigSQL.saveFeedConfig(feedConfig);
	}

	private void checkFeedConfigExistence(FeedConfig feedConfig) throws TradistaBusinessException {
		if (FeedConfigSQL.getFeedConfigByNameAndPo(feedConfig.getName(),
				feedConfig.getProcessingOrg() == null ? 0 : feedConfig.getProcessingOrg().getId()) != null) {
			String errMsg;
			if (feedConfig.getProcessingOrg() == null) {
				errMsg = "A global feed config named %s already exists in the system.";
			} else {
				errMsg = "A feed config named %s already exists in the system for the PO %s.";
			}
			throw new TradistaBusinessException(
					String.format(errMsg, feedConfig.getName(), feedConfig.getProcessingOrg()));
		}
	}

	@Interceptors(FeedConfigFilteringInterceptor.class)
	@Override
	public boolean deleteFeedConfig(long id) throws TradistaBusinessException {
		return FeedConfigSQL.deleteFeedConfig(id);
	}

	@Override
	public Set<String> getFeedConfigsUsingQuote(String quoteName, QuoteType quoteType) {
		Set<FeedConfig> feedConfigs = FeedConfigSQL.getAllFeedConfigs();
		Set<String> results = null;
		if (feedConfigs != null) {
			for (FeedConfig conf : feedConfigs) {
				Quote quote = quoteService.getQuoteByNameAndType(quoteName, quoteType);
				if (conf.getMapping().values().contains(quote)) {
					if (results == null) {
						results = new HashSet<String>(feedConfigs.size());
					}
					results.add(conf.getName());
				}
			}
		}
		return results;
	}

}