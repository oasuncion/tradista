package finance.tradista.fx.fx.service;

import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.exchange.service.ExchangeBusinessDelegate;
import finance.tradista.core.product.model.Product;
import finance.tradista.fx.common.model.AbstractFXTrade;

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

public class AbstractFXTradeBusinessDelegate<X extends AbstractFXTrade<Product>> {
	

	protected static Exchange fxExchange;

	public boolean isBusinessDay(X fxTrade, LocalDate date) throws TradistaBusinessException {
		Currency tradeCurrency;
		Exchange tradeExchange;
		Calendar exchangeCalendar;
		Calendar currencyCalendar;
		if (fxTrade == null) {
			throw new TradistaBusinessException("The FX trade cannot be null");
		}
		if (date == null) {
			throw new TradistaBusinessException("The date cannot be null");
		}
		tradeCurrency = fxTrade.getCurrency();
		if (tradeCurrency == null) {
			throw new TradistaBusinessException("The FX trade currency cannot be null");
		}
		currencyCalendar = tradeCurrency.getCalendar();
		if (currencyCalendar == null) {
			// TODO Add a warning log
		}
		tradeExchange = getFXExchange();
		if (tradeExchange == null) {
			throw new TradistaBusinessException("The FX trade exchange cannot be null");
		}
		exchangeCalendar = tradeExchange.getCalendar();
		if (tradeExchange.getCalendar() == null) {
			throw new TradistaBusinessException("The FX exchange calendar cannot be null");
		}
		if (currencyCalendar != null) {
			return (currencyCalendar.isBusinessDay(date) && exchangeCalendar.isBusinessDay(date));
		} else {
			return exchangeCalendar.isBusinessDay(date);
		}
	}
	
	public Exchange getFXExchange() {
		if (fxExchange == null) {
			fxExchange = new ExchangeBusinessDelegate().getExchangeByCode("FX");
		}
		return fxExchange;
	}

}