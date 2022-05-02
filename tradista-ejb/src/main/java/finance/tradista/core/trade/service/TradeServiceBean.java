package finance.tradista.core.trade.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.persistence.TradeSQL;
import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.core.trade.service.TradeService;

/*
 * Copyright 2019 Olivier Asuncion
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
public class TradeServiceBean implements TradeService {

	@Override
	public List<Trade<? extends Product>> getTradesByCreationDate(LocalDate creationDate) {
		return TradeSQL.getTradesByCreationDate(creationDate);
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public List<Trade<? extends Product>> getTradesByDates(LocalDate startCreationDate, LocalDate endCreationDate,
			LocalDate startTradeDate, LocalDate endTradeDate) {
		return TradeSQL.getTradesByDates(startCreationDate, endCreationDate, startTradeDate, endTradeDate);
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public Trade<? extends Product> getTradeById(long id) {
		return TradeSQL.getTradeById(id, false);
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public Trade<? extends Product> getTradeById(long id, boolean includeUnderlying) {
		return TradeSQL.getTradeById(id, includeUnderlying);
	}

	@Override
	public Set<Trade<? extends Product>> getTrades(PositionDefinition posDef) {
		return TradeSQL.getTrades(posDef);
	}

}