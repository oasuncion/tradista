package finance.tradista.security.bond.service;

import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.model.BondTrade;
import finance.tradista.security.bond.validator.BondTradeValidator;

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

public class BondTradeBusinessDelegate {

	private BondTradeService bondTradeService;

	private BondTradeValidator validator;

	public BondTradeBusinessDelegate() {
		bondTradeService = TradistaServiceLocator.getInstance().getBondTradeService();
		validator = new BondTradeValidator();
	}

	public long saveBondTrade(BondTrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.run(() -> bondTradeService.saveBondTrade(trade));
	}

	public boolean isBusinessDay(BondTrade bondTrade, LocalDate date) throws TradistaBusinessException {
		Bond bond;
		Exchange bondExchange;
		Calendar exchangeCalendar;
		if (bondTrade == null) {
			throw new TradistaBusinessException("The Bond trade cannot be null");
		}
		bond = (Bond) bondTrade.getProduct();
		if (bond == null) {
			throw new TradistaBusinessException("The Bond product cannot be null");
		}
		bondExchange = bond.getExchange();
		if (bondExchange == null) {
			throw new TradistaBusinessException("The Bond exchange cannot be null");
		}

		exchangeCalendar = bondExchange.getCalendar();
		if (exchangeCalendar == null) {
			// TODO add a warning log.
		}

		return exchangeCalendar.isBusinessDay(date);
	}

	public List<BondTrade> getBondTradesBeforeTradeDateByBondAndBookIds(LocalDate date, long bondId, long bookId)
			throws TradistaBusinessException {
		if (date == null) {
			throw new TradistaBusinessException("The date is mandatory.");
		}

		return SecurityUtil
				.run(() -> bondTradeService.getBondTradesBeforeTradeDateByBondAndBookIds(date, bondId, bookId));
	}

	public BondTrade getBondTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> bondTradeService.getBondTradeById(id));
	}

}