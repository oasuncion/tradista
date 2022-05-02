package finance.tradista.security.equity.service;

import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.model.EquityTrade;
import finance.tradista.security.equity.service.EquityTradeService;
import finance.tradista.security.equity.validator.EquityTradeValidator;

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

public class EquityTradeBusinessDelegate {

	private EquityTradeService equityTradeService;

	private EquityTradeValidator validator;

	public EquityTradeBusinessDelegate() {
		equityTradeService = TradistaServiceLocator.getInstance().getEquityTradeService();
		validator = new EquityTradeValidator();
	}

	public long saveEquityTrade(EquityTrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> equityTradeService.saveEquityTrade(trade));
	}

	public boolean isBusinessDay(EquityTrade equityTrade, LocalDate date) throws TradistaBusinessException {
		Equity equity;
		Exchange equityExchange;
		Calendar exchangeCalendar;
		if (equityTrade == null) {
			throw new TradistaBusinessException("The Equity trade cannot be null");
		}
		equity = (Equity) equityTrade.getProduct();
		if (equity == null) {
			throw new TradistaBusinessException("The Equity product cannot be null");
		}
		equityExchange = equity.getExchange();
		if (equityExchange == null) {
			throw new TradistaBusinessException("The Equity exchange cannot be null");
		}

		exchangeCalendar = equityExchange.getCalendar();
		if (exchangeCalendar == null) {
			// TODO add a warning log.
		}

		return exchangeCalendar.isBusinessDay(date);
	}

	public List<EquityTrade> getEquityTradesBeforeTradeDateByEquityAndBookIds(LocalDate date, long equityId,
			long bookId) throws TradistaBusinessException {
		if (date == null) {
			throw new TradistaBusinessException("The date is mandatory.");
		}
		return SecurityUtil
				.run(() -> equityTradeService.getEquityTradesBeforeTradeDateByEquityAndBookIds(date, equityId, bookId));
	}

	public EquityTrade getEquityTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> equityTradeService.getEquityTradeById(id));
	}

}