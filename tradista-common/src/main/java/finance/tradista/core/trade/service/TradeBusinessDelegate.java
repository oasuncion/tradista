package finance.tradista.core.trade.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.service.TradeService;

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

public class TradeBusinessDelegate {

	private TradeService tradeService;

	public TradeBusinessDelegate() {
		tradeService = TradistaServiceLocator.getInstance().getTradeService();
	}

	public List<Trade<? extends Product>> getTradesByCreationDate(LocalDate creationDate) {
		return SecurityUtil.run(() -> tradeService.getTradesByCreationDate(creationDate));
	}

	public List<Trade<? extends Product>> getTradesByDates(LocalDate startCreationDate, LocalDate endCreationDate,
			LocalDate startTradeDate, LocalDate endTradeDate) throws TradistaBusinessException {
		StringBuilder errorMsg = new StringBuilder();
		if (startCreationDate != null && endCreationDate != null) {
			if (endCreationDate.isBefore(startCreationDate)) {
				errorMsg.append(String.format("'To' creation date cannot be before 'From' creation date.%n"));
			}
		}
		if (startTradeDate != null && endTradeDate != null) {
			if (endTradeDate.isBefore(startTradeDate)) {
				errorMsg.append("'To' trade date cannot be before 'From' trade date.");
			}
		}
		if (errorMsg.length() > 0) {
			throw new TradistaBusinessException(errorMsg.toString());
		}
		return SecurityUtil.run(
				() -> tradeService.getTradesByDates(startCreationDate, endCreationDate, startTradeDate, endTradeDate));
	}

	public Trade<? extends Product> getTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> tradeService.getTradeById(id));
	}

	public Set<Trade<? extends Product>> getTrades(PositionDefinition posDef) {
		return SecurityUtil.run(() -> tradeService.getTrades(posDef));
	}

	public Trade<?> getTradeById(long tradeId, boolean includeUnderlying) throws TradistaBusinessException {
		if (tradeId <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> tradeService.getTradeById(tradeId, includeUnderlying));
	}

}