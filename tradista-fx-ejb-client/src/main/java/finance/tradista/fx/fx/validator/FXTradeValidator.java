package finance.tradista.fx.fx.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.validator.DefaultTradeValidator;
import finance.tradista.fx.fx.model.FXTrade;
import finance.tradista.fx.fx.service.FXTradeBusinessDelegate;

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

public class FXTradeValidator extends DefaultTradeValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8758755781962576317L;

	public FXTradeValidator() {
	}

	@Override
	public void validateTrade(Trade<? extends Product> trade) throws TradistaBusinessException {
		FXTradeBusinessDelegate fxTradeBusinessDelegate = new FXTradeBusinessDelegate();
		FXTrade fxTrade = (FXTrade) trade;
		StringBuilder errMsg = validateTradeBasics(trade);

		if (fxTrade.getAmountOne() == null) {
			errMsg.append(String.format("The amount one is mandatory.%n"));
		} else {
			if (fxTrade.getAmountOne().doubleValue() <= 0) {
				errMsg.append(
						String.format("The amount one (%s) must be positive.%n", fxTrade.getAmountOne().doubleValue()));
			}
		}

		if (fxTrade.getCurrencyOne() == null) {
			errMsg.append(String.format("The currency one is mandatory.%n"));
		} else {
			if (fxTrade.getCurrency() != null) {
				if (fxTrade.getCurrency().equals(fxTrade.getCurrencyOne())) {
					errMsg.append(String.format("The currency one (%s) must be different of currency two (%s).%n",
							fxTrade.getCurrencyOne(), fxTrade.getCurrency()));
				}
			}
		}

		// Other business controls
		if (trade.getAmount() != null && trade.getAmount().doubleValue() <= 0) {
			errMsg.append(String.format("The amount two (%s) must be positive.%n", trade.getAmount().doubleValue()));
		}

		if (trade.getSettlementDate() == null) {
			errMsg.append(String.format("The settlement date is mandatory.%n"));
		} else {
			// the settlement date date must be an open day for FX calendar and
			// for both currencies
			if (!fxTradeBusinessDelegate.isBusinessDay(fxTrade, trade.getSettlementDate())) {
				errMsg.append(String.format(
						"The settlement date must be a business day in the '%s' calendar and both FX currencies (%s/%s)'s calendars.%n",
						trade.getExchange().getCalendar().getName(), fxTrade.getCurrencyOne(), fxTrade.getCurrency()));
			}
		}

		// the trade date must be an open day for FX calendar
		if (trade.getTradeDate() != null) {
			if (!fxTradeBusinessDelegate.getFXExchange().getCalendar().isBusinessDay(trade.getTradeDate())) {
				errMsg.append(String.format("The trade date must be a business day in the '%s' calendar.%n",
						fxTradeBusinessDelegate.getFXExchange().getCalendar().getName()));
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}
