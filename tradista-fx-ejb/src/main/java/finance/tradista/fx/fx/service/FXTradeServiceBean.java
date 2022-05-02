package finance.tradista.fx.fx.service;

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
import finance.tradista.fx.fx.messaging.FXTradeEvent;
import finance.tradista.fx.fx.model.FXTrade;
import finance.tradista.fx.fx.persistence.FXTradeSQL;
import finance.tradista.fx.fx.service.FXTradeBusinessDelegate;
import finance.tradista.fx.fx.service.FXTradeService;

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
public class FXTradeServiceBean implements FXTradeService {

	private ConnectionFactory factory;

	private JMSContext context;

	private Destination destination;

	private FXTradeBusinessDelegate fxTradeBusinessDelegate;

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
		fxTradeBusinessDelegate = new FXTradeBusinessDelegate();
	}

	@Interceptors({ FXProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
	@Override
	public long saveFXTrade(FXTrade trade) throws TradistaBusinessException {

		FXTradeEvent event = new FXTradeEvent();
		if (trade.getId() != 0) {
			FXTrade oldTrade = FXTradeSQL.getTradeById(trade.getId(), false);
			event.setOldTrade(oldTrade);
		}

		event.setTrade(trade);
		long result = FXTradeSQL.saveFXTrade(trade);
		try {
			fxTradeBusinessDelegate.determinateType(trade);
		} catch (TradistaBusinessException tbe) {
			// Should not happen here.
		}

		context.createProducer().send(destination, event);

		return result;
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public FXTrade getFXTradeById(long id) {
		return FXTradeSQL.getTradeById(id, false);
	}

	@PreDestroy
	private void clean() {
		context.close();
	}

}