package finance.tradista.ir.fra.service;

import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.ir.fra.model.FRATrade;
import finance.tradista.ir.fra.validator.FRATradeValidator;

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

public class FRATradeBusinessDelegate {

	private FRATradeService fraTradeService;

	private FRATradeValidator validator;

	public FRATradeBusinessDelegate() {
		fraTradeService = TradistaServiceLocator.getInstance().getFRATradeService();
		validator = new FRATradeValidator();
	}

	public long saveFRATrade(FRATrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> fraTradeService.saveFRATrade(trade));
	}

	public boolean isBusinessDay(FRATrade fraTrade, LocalDate date) throws TradistaBusinessException {
		Currency tradeCurrency;
		Calendar currencyCalendar;
		if (fraTrade == null) {
			throw new TradistaBusinessException("The FRA trade cannot be null");
		}
		tradeCurrency = fraTrade.getCurrency();
		if (tradeCurrency == null) {
			throw new TradistaBusinessException("The FRA trade currency cannot be null");
		}
		currencyCalendar = tradeCurrency.getCalendar();
		if (currencyCalendar == null) {
			// TODO Add warning log
		}
		if (currencyCalendar != null) {
			return currencyCalendar.isBusinessDay(date);
		} else {
			return true;
		}
	}

	public LocalDate getStartDate(FRATrade fraTrade) throws TradistaBusinessException {
		Currency tradeCurrency;
		Calendar currencyCalendar;
		if (fraTrade == null) {
			throw new TradistaBusinessException("The FRA trade cannot be null");
		}
		tradeCurrency = fraTrade.getCurrency();
		if (tradeCurrency == null) {
			throw new TradistaBusinessException("The FRA trade currency cannot be null");
		}
		currencyCalendar = tradeCurrency.getCalendar();
		if (currencyCalendar == null) {
			// TODO Add warning log
		}

		return DateUtil.addBusinessDay(fraTrade.getPaymentDate(), fraTrade.getCurrency().getCalendar(), -2);

	}

	public FRATrade getFRATradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> fraTradeService.getFRATradeById(id));
	}

}