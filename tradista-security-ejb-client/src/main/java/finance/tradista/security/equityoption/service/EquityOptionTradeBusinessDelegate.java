package finance.tradista.security.equityoption.service;

import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.security.equity.model.EquityTrade;
import finance.tradista.security.equityoption.model.EquityOption;
import finance.tradista.security.equityoption.model.EquityOptionTrade;
import finance.tradista.security.equityoption.service.EquityOptionTradeService;
import finance.tradista.security.equityoption.validator.EquityOptionTradeValidator;

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

public class EquityOptionTradeBusinessDelegate {

	private EquityOptionTradeService equityTradeOptionService;

	private EquityOptionTradeValidator validator;

	public EquityOptionTradeBusinessDelegate() {
		equityTradeOptionService = TradistaServiceLocator.getInstance().getEquityOptionTradeService();
		validator = new EquityOptionTradeValidator();
	}

	public long saveEquityOptionTrade(EquityOptionTrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> equityTradeOptionService.saveEquityOptionTrade(trade));
	}

	public boolean isBusinessDay(EquityOptionTrade trade, LocalDate date) throws TradistaBusinessException {
		EquityOption equityOption;
		EquityTrade underlying;
		if (trade == null) {
			throw new TradistaBusinessException("The Equity Option trade cannot be null");
		}
		underlying = trade.getUnderlying();
		equityOption = trade.getEquityOption();

		if (equityOption == null) {
			if (underlying == null) {
				return true;
			} else {
				Exchange underlyingExchange = underlying.getExchange();
				if (underlyingExchange == null) {
					throw new TradistaBusinessException("The Equity Option trade underlying exchange cannot be null");
				} else {
					Calendar underlyingCalendar = underlyingExchange.getCalendar();
					if (underlyingCalendar != null) {
						return underlyingCalendar.isBusinessDay(date);
					} else {
						return true;
					}
				}
			}
		} else {
			Exchange eqoExchange = equityOption.getExchange();
			if (eqoExchange == null) {
				throw new TradistaBusinessException("The Equity Option exchange cannot be null");
			} else {
				Calendar eqoCalendar = eqoExchange.getCalendar();
				if (eqoCalendar != null) {
					return eqoCalendar.isBusinessDay(date);
				} else {
					return true;
				}
			}
		}

	}

	public List<EquityOptionTrade> getEquityOptionTradesBeforeTradeDateByEquityOptionAndBookIds(LocalDate tradeDate,
			long equityOptionId, long bookId) throws TradistaBusinessException {
		if (tradeDate == null) {
			throw new TradistaBusinessException("The trade date is mandatory.");
		}

		return SecurityUtil.run(() -> equityTradeOptionService
				.getEquityOptionTradesBeforeTradeDateByEquityOptionAndBookIds(tradeDate, equityOptionId, bookId));
	}

	public EquityOptionTrade getEquityOptionTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> equityTradeOptionService.getEquityOptionTradeById(id));
	}

}