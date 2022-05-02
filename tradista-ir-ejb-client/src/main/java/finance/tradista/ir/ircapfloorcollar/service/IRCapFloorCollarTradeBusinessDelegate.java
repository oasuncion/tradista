package finance.tradista.ir.ircapfloorcollar.service;

import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.product.model.Product;
import finance.tradista.ir.ircapfloorcollar.model.IRCapFloorCollarTrade;
import finance.tradista.ir.ircapfloorcollar.service.IRCapFloorCollarTradeService;
import finance.tradista.ir.ircapfloorcollar.validator.IRCapFloorCollarTradeValidator;
import finance.tradista.ir.irforward.model.IRForwardTrade;

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

public class IRCapFloorCollarTradeBusinessDelegate {

	private IRCapFloorCollarTradeService irCapFloorCollarTradeService;

	private IRCapFloorCollarTradeValidator validator;

	public IRCapFloorCollarTradeBusinessDelegate() {
		irCapFloorCollarTradeService = TradistaServiceLocator.getInstance().getIRCapFloorCollarTradeService();
		validator = new IRCapFloorCollarTradeValidator();
	}

	public long saveIRCapFloorCollarTrade(IRCapFloorCollarTrade trade) throws TradistaBusinessException {

		validator.validateTrade(trade);

		return SecurityUtil.runEx(() -> irCapFloorCollarTradeService.saveIRCapFloorCollarTrade(trade));

	}

	public boolean isBusinessDay(IRCapFloorCollarTrade trade, LocalDate date) throws TradistaBusinessException {
		Currency underlyingCurrency;
		IRForwardTrade<Product> underlying;
		Calendar currencyCalendar;
		if (trade == null) {
			throw new TradistaBusinessException("The IR Cap/Floor/Collar trade cannot be null");
		}
		underlying = trade.getIrForwardTrade();
		if (underlying == null) {
			throw new TradistaBusinessException("The IR Cap/Floor/Collar trade underlying cannot be null");
		}
		underlyingCurrency = underlying.getCurrency();
		if (underlyingCurrency == null) {
			throw new TradistaBusinessException("The IR Cap/Floor/Collar trade underlying currency cannot be null");
		}
		currencyCalendar = underlyingCurrency.getCalendar();
		if (currencyCalendar == null) {
			// TODO Add warning log
		}
		if (currencyCalendar != null) {
			return currencyCalendar.isBusinessDay(date);
		} else {
			return true;
		}
	}

	public IRCapFloorCollarTrade getIRCapFloorCollarTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> irCapFloorCollarTradeService.getIRCapFloorCollarTradeById(id));
	}

}