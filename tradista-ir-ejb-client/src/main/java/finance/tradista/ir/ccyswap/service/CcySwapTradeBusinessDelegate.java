package finance.tradista.ir.ccyswap.service;

import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.ir.ccyswap.model.CcySwapTrade;
import finance.tradista.ir.ccyswap.validator.CcySwapTradeValidator;

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

public class CcySwapTradeBusinessDelegate {

	private CcySwapTradeService ccySwapTradeService;

	private CcySwapTradeValidator validator;

	public CcySwapTradeBusinessDelegate() {
		ccySwapTradeService = TradistaServiceLocator.getInstance().getCcySwapTradeService();
		validator = new CcySwapTradeValidator();
	}

	public long saveCcySwapTrade(CcySwapTrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> ccySwapTradeService.saveCcySwapTrade(trade));
	}

	public boolean isBusinessDay(CcySwapTrade ccySwapTrade, LocalDate date) throws TradistaBusinessException {
		Currency tradeCurrency;
		Calendar currencyCalendar;
		Currency tradeCurrencyTwo;
		Calendar currencyTwoCalendar;
		if (ccySwapTrade == null) {
			throw new TradistaBusinessException("The Ccy Swap trade cannot be null");
		}
		tradeCurrency = ccySwapTrade.getCurrency();
		if (tradeCurrency == null) {
			throw new TradistaBusinessException("The Ccy Swap trade currency cannot be null");
		}
		currencyCalendar = tradeCurrency.getCalendar();
		if (currencyCalendar == null) {
			// TODO add a warning log.
		}
		tradeCurrencyTwo = ccySwapTrade.getCurrencyTwo();
		if (tradeCurrencyTwo == null) {
			throw new TradistaBusinessException("The Ccy Swap trade currency two cannot be null");
		}
		currencyTwoCalendar = tradeCurrencyTwo.getCalendar();
		if (currencyTwoCalendar == null) {
			// TODO Add warning log
		}
		if (currencyCalendar != null) {
			if (currencyTwoCalendar != null) {
				return (currencyCalendar.isBusinessDay(date) && currencyTwoCalendar.isBusinessDay(date));
			} else {
				currencyCalendar.isBusinessDay(date);
			}
		} else {
			if (currencyTwoCalendar != null) {
				return (currencyTwoCalendar.isBusinessDay(date));
			} else {
				return true;
			}
		}
		return true;
	}

	public CcySwapTrade getCcySwapTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.runEx(() -> ccySwapTradeService.getCcySwapTradeById(id));
	}

}