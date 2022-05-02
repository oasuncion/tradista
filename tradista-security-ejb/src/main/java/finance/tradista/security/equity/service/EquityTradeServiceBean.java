package finance.tradista.security.equity.service;

import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.security.equity.messaging.EquityTradeEvent;
import finance.tradista.security.equity.model.EquityTrade;
import finance.tradista.security.equity.persistence.EquityTradeSQL;

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
public class EquityTradeServiceBean implements EquityTradeService {

	private ConnectionFactory factory;

	private JMSContext context;

	private Destination destination;

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
	}

	@Interceptors({ EquityProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
	@Override
	public long saveEquityTrade(EquityTrade trade) throws TradistaBusinessException {
		EquityTradeEvent event = new EquityTradeEvent();
		if (trade.getId() != 0) {
			EquityTrade oldTrade = EquityTradeSQL.getTradeById(trade.getId(), false);
			// oldTrade can be null when the trade is an option underlying that
			// has just been exercised.
			if (oldTrade != null) {
				event.setOldTrade(oldTrade);
			}
		}

		event.setTrade(trade);
		long result = EquityTradeSQL.saveEquityTrade(trade);

		context.createProducer().send(destination, event);

		return result;

	}

	@Override
	public List<EquityTrade> getEquityTradesBeforeTradeDateByEquityAndBookIds(LocalDate date, long equityId,
			long bookId) {
		return EquityTradeSQL.getEquityTradesBeforeTradeDateByEquityAndBookIds(date, equityId, bookId);
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public EquityTrade getEquityTradeById(long id) {
		return EquityTradeSQL.getTradeById(id, false);
	}

	@PreDestroy
	private void clean() {
		context.close();
	}

}