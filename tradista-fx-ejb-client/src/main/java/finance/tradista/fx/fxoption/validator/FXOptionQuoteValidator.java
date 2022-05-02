package finance.tradista.fx.fxoption.validator;

import java.math.BigDecimal;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.service.CurrencyBusinessDelegate;
import finance.tradista.core.marketdata.validator.DefaultQuoteValidator;
import finance.tradista.core.trade.model.OptionTrade;
import finance.tradista.fx.fxoption.model.FXOptionTrade;
import finance.tradista.fx.fxoption.service.FXVolatilitySurfaceBusinessDelegate;

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

public class FXOptionQuoteValidator extends DefaultQuoteValidator {

	@Override
	public void validateQuoteName(String quoteName) throws TradistaBusinessException {
		validateQuoteBasics(quoteName);
		StringBuilder errMsg = new StringBuilder();
		String[] data = quoteName.split("\\.");

		if (data.length < 5) {
			throw new TradistaBusinessException(String.format(
					"The quote name (%s) must be as follows: FXOption.Currency.optionExpiry.strike.CALL(or PUT)%n",
					quoteName));
		}

		if (!data[0].equals(FXOptionTrade.FX_OPTION)) {
			errMsg.append(
					String.format("The quote name (%s) must start with %s.%n", quoteName, FXOptionTrade.FX_OPTION));
		}
		Set<Currency> currencies = new CurrencyBusinessDelegate().getAllCurrencies();
		if (currencies != null && !currencies.isEmpty()) {
			Currency curr = new Currency();
			curr.setIsoCode(data[1]);
			if (!currencies.contains(curr)) {
				errMsg.append(String.format("The currency (%s) must exist in the system: %s%n.", data[1], currencies));
			}
		} else {
			errMsg.append(String.format("The currency (%s) must exist in the system. %n.", data[1]));
		}
		Set<String> expiries = new FXVolatilitySurfaceBusinessDelegate().getAllOptionExpiriesAsString();
		if (!expiries.contains(data[2])) {
			errMsg.append(String.format("The option expiry (%s) must be a valid one: %s%n.", data[2], expiries));
		}
		try {
			new BigDecimal(data[3]);
		} catch (NumberFormatException nfe) {
			errMsg.append(String.format("The strike (%s) must be a valid one.%n", data[3]));
		}
		if (!data[4].equals(OptionTrade.Type.CALL.toString()) && !data[4].equals(OptionTrade.Type.PUT.toString())) {
			errMsg.append(String.format("The option type (%s) must be %s or %s.%n", data[4], OptionTrade.Type.CALL,
					OptionTrade.Type.PUT));
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}
