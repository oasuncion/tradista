package finance.tradista.fx.fx.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.fx.fx.model.FXTrade;
import finance.tradista.fx.fx.model.FXTrade.Type;
import finance.tradista.fx.fx.validator.FXTradeValidator;

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

public class FXTradeBusinessDelegate extends AbstractFXTradeBusinessDelegate<FXTrade> {

	private FXTradeService fxTradeService;

	private FXTradeValidator validator;

	private ConfigurationBusinessDelegate configurationBusinessDelegate;

	public FXTradeBusinessDelegate() {
		fxTradeService = TradistaServiceLocator.getInstance().getFXTradeService();
		configurationBusinessDelegate = new ConfigurationBusinessDelegate();
		validator = new FXTradeValidator();
	}

	public long saveFXTrade(FXTrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> fxTradeService.saveFXTrade(trade));
	}

	@Override
	public boolean isBusinessDay(FXTrade fxTrade, LocalDate date) throws TradistaBusinessException {
		Currency currencyOne = fxTrade.getCurrencyOne();
		Calendar currencyOneCalendar;
		if (currencyOne == null) {
			throw new TradistaBusinessException("The FX trade currency one cannot be null");
		}
		currencyOneCalendar = currencyOne.getCalendar();
		if (currencyOneCalendar == null) {
			// TODO Add a warning log
		}
		boolean isBusinessDay = super.isBusinessDay(fxTrade, date);
		if (currencyOneCalendar != null) {
			return isBusinessDay && currencyOneCalendar.isBusinessDay(date);
		} else {
			return isBusinessDay;
		}
	}

	public Type determinateType(FXTrade trade) throws TradistaBusinessException {
		if (trade.getType() == null) {
			// FX Spot are generally settled in T+2.
			// there are some exceptions, see
			// https://en.wikipedia.org/wiki/Foreign_exchange_spot#Settlement_date
			// The settlement day must be open for both currencies.
			Type type = null;
			LocalDate maxSettleDate = DateUtil.addBusinessDay(trade.getTradeDate(), getFXExchange().getCalendar(), 2);
			boolean dateFound = false;
			while (!dateFound) {
				if (isBusinessDay(trade, maxSettleDate)) {
					dateFound = true;
				} else {
					maxSettleDate = DateUtil.nextBusinessDay(trade.getTradeDate(), getFXExchange().getCalendar());
				}
			}
			if (trade.getSettlementDate().isAfter(maxSettleDate)) {
				type = Type.FX_FORWARD;
			} else {
				type = Type.FX_SPOT;
			}
			trade.setType(type);
		}
		return trade.getType();
	}

	public BigDecimal getExchangeRate(FXTrade trade) throws TradistaBusinessException {
		if (trade == null) {
			throw new TradistaBusinessException("The trade is mandatory.");
		}
		return SecurityUtil.run(() -> trade.getAmount().divide(trade.getAmountOne(),
				configurationBusinessDelegate.getScale(), configurationBusinessDelegate.getRoundingMode()));
	}

	public FXTrade getFXTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> fxTradeService.getFXTradeById(id));
	}

}