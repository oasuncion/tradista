package finance.tradista.fx.fxoption.service;

import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.fx.fx.service.FXTradeBusinessDelegate;
import finance.tradista.fx.fxoption.model.FXOptionTrade;
import finance.tradista.fx.fxoption.service.FXOptionTradeService;
import finance.tradista.fx.fxoption.validator.FXOptionTradeValidator;

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

public class FXOptionTradeBusinessDelegate {

	private FXOptionTradeService fxOptionTradeService;

	private FXOptionTradeValidator validator;

	private FXTradeBusinessDelegate fxTradeBusinessDelegate;

	public FXOptionTradeBusinessDelegate() {
		fxOptionTradeService = TradistaServiceLocator.getInstance().getFXOptionTradeService();
		validator = new FXOptionTradeValidator();
		fxTradeBusinessDelegate = new FXTradeBusinessDelegate();
	}

	public long saveFXOptionTrade(FXOptionTrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> fxOptionTradeService.saveFXOptionTrade(trade));
	}

	public boolean isBusinessDay(FXOptionTrade fxOptionTrade, LocalDate date) throws TradistaBusinessException {
		return fxTradeBusinessDelegate.isBusinessDay(fxOptionTrade.getUnderlying(), date);
	}

	public Exchange getFXExchange() {
		return fxTradeBusinessDelegate.getFXExchange();
	}

	public FXOptionTrade getFXOptionTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.runEx(() -> fxOptionTradeService.getFXOptionTradeById(id));
	}

}