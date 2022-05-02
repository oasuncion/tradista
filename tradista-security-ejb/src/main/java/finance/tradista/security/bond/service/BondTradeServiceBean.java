package finance.tradista.security.bond.service;

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

import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.security.bond.messaging.BondTradeEvent;
import finance.tradista.security.bond.model.BondTrade;
import finance.tradista.security.bond.persistence.BondTradeSQL;

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
public class BondTradeServiceBean implements BondTradeService {

	private ConnectionFactory factory;

	private JMSContext context;

	private Destination destination;

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
	}

	@Interceptors({ BondProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
	@Override
	public long saveBondTrade(BondTrade trade) {
		BondTradeEvent event = new BondTradeEvent();
		if (trade.getId() != 0) {
			BondTrade oldTrade = BondTradeSQL.getTradeById(trade.getId());
			event.setOldTrade(oldTrade);
		}

		event.setTrade(trade);
		long result = BondTradeSQL.saveBondTrade(trade);

		context.createProducer().send(destination, event);

		return result;
	}

	@PreDestroy
	private void clean() {
		context.close();
	}

	@Override
	public List<BondTrade> getBondTradesBeforeTradeDateByBondAndBookIds(LocalDate date, long bondId, long bookId) {
		return BondTradeSQL.getBondTradesBeforeTradeDateByBondAndBookIds(date, bondId, bookId);
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public BondTrade getBondTradeById(long id) {
		return BondTradeSQL.getTradeById(id);
	}

}