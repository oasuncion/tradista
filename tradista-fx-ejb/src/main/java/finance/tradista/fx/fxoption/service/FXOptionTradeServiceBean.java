package finance.tradista.fx.fxoption.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.trade.model.OptionTrade;
import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.fx.fx.service.FXTradeService;
import finance.tradista.fx.fxoption.messaging.FXOptionTradeEvent;
import finance.tradista.fx.fxoption.model.FXOptionTrade;
import finance.tradista.fx.fxoption.persistence.FXOptionTradeSQL;

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
public class FXOptionTradeServiceBean implements FXOptionTradeService {

	private ConnectionFactory factory;

	private JMSContext context;

	private Destination destination;

	@EJB
	private FXTradeService fxTradeService;

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
	}

	@Interceptors({ FXOptionTradeProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
	@Override
	public long saveFXOptionTrade(FXOptionTrade trade) throws TradistaBusinessException {

		FXOptionTradeEvent event = new FXOptionTradeEvent();
		FXOptionTrade oldTrade = null;

		// If the option is exercised and the settlement is physical, make sure to
		// update the underlying
		// inventory calling the FX Service
		if (trade.getExerciseDate() != null && trade.getSettlementType().equals(OptionTrade.SettlementType.PHYSICAL)) {
			if (trade.getId() != 0) {
				oldTrade = FXOptionTradeSQL.getTradeById(trade.getId());
			}
			try {
				fxTradeService.saveFXTrade(trade.getUnderlying());
			} catch (TradistaBusinessException tbe) {
				// Should not happen here.
			}
		}

		if (trade.getId() != 0) {
			if (oldTrade == null) {
				oldTrade = FXOptionTradeSQL.getTradeById(trade.getId());
			}
			event.setOldTrade(oldTrade);
		}

		event.setTrade(trade);
		long result = FXOptionTradeSQL.saveFXOptionTrade(trade);

		context.createProducer().send(destination, event);

		return result;
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public FXOptionTrade getFXOptionTradeById(long id) {
		return FXOptionTradeSQL.getTradeById(id);
	}

	@PreDestroy
	private void clean() {
		context.close();
	}

}