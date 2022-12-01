package finance.tradista.ir.future.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.ir.future.messaging.FutureTradeEvent;
import finance.tradista.ir.future.model.FutureTrade;
import finance.tradista.ir.future.persistence.FutureSQL;
import finance.tradista.ir.future.persistence.FutureTradeSQL;

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
public class FutureTradeServiceBean implements FutureTradeService {

	private ConnectionFactory factory;

	private JMSContext context;

	private Destination destination;

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
	}

	@Interceptors({ FutureProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
	@Override
	public long saveFutureTrade(FutureTrade trade) throws TradistaBusinessException {

		FutureTradeEvent event = new FutureTradeEvent();

		if (trade.getProduct().getId() == 0) {
			FutureSQL.saveFuture(trade.getProduct());
		}

		if (trade.getId() != 0) {
			FutureTrade oldTrade = FutureTradeSQL.getTradeById(trade.getId());
			event.setOldTrade(oldTrade);
		}

		event.setTrade(trade);
		long result = FutureTradeSQL.saveFutureTrade(trade);

		context.createProducer().send(destination, event);

		return result;
	}

	@PreDestroy
	private void clean() {
		context.close();
	}

	@Override
	public List<FutureTrade> getFutureTradesBeforeTradeDateByFutureAndBookIds(LocalDate date, long futureId,
			long bookId) {
		return FutureTradeSQL.getFutureTradesBeforeTradeDateByFutureAndBookIds(date, futureId, bookId);
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public FutureTrade getFutureTradeById(long id) {
		return FutureTradeSQL.getTradeById(id);
	}

}