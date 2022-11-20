package finance.tradista.fx.fxndf.service;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.fx.fxndf.messaging.FXNDFTradeEvent;
import finance.tradista.fx.fxndf.model.FXNDFTrade;
import finance.tradista.fx.fxndf.persistence.FXNDFTradeSQL;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;

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
public class FXNDFTradeServiceBean implements FXNDFTradeService {

	private ConnectionFactory factory;

	private JMSContext context;

	private Destination destination;

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
	}

	@Interceptors({ FXNDFTradeProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
	@Override
	public long saveFXNDFTrade(FXNDFTrade trade) throws TradistaBusinessException {

		FXNDFTradeEvent event = new FXNDFTradeEvent();
		if (trade.getId() != 0) {
			FXNDFTrade oldTrade = FXNDFTradeSQL.getTradeById(trade.getId());
			event.setOldTrade(oldTrade);
		}

		event.setTrade(trade);
		long result = FXNDFTradeSQL.saveFXNDFTrade(trade);

		context.createProducer().send(destination, event);

		return result;

	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public FXNDFTrade getFXNDFTradeById(long id) {
		return FXNDFTradeSQL.getTradeById(id);
	}

	@PreDestroy
	private void clean() {
		context.close();
	}

}