package finance.tradista.fx.fx.validator;

import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.service.CurrencyBusinessDelegate;
import finance.tradista.core.marketdata.validator.DefaultQuoteValidator;
import finance.tradista.fx.fx.model.FXTrade;

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

public class FXQuoteValidator extends DefaultQuoteValidator {

	@Override
	public void validateQuoteName(String quoteName)
			throws TradistaBusinessException {
		validateQuoteBasics(quoteName);
		StringBuilder errMsg = new StringBuilder();
		String[] data = quoteName.split("\\.");

		if (data.length < 3) {
			throw new TradistaBusinessException(
					String.format(
							"The quote name (%s) must be as follows: %s.CurrencyOne.CurrencyTwo%n",
							quoteName, FXTrade.FX));
		}

		if (!data[0].equals(FXTrade.FX)) {
			errMsg.append(String.format(
					"The quote name (%s) must start with %s.%n",
					quoteName, FXTrade.FX));
		}
		Set<Currency> currencies = new CurrencyBusinessDelegate().getAllCurrencies();
		if (currencies != null && !currencies.isEmpty()) {
			Currency curr = new Currency();
			curr.setIsoCode(data[1]);
			if (!currencies.contains(curr)) {
				errMsg.append(String.format(
						"The currency one (%s) must exist in the system: %s%n.", data[1],
						currencies));
			}
			curr.setIsoCode(data[2]);
			if (!currencies.contains(curr)) {
				errMsg.append(String.format(
						"The currency two (%s) must exist in the system: %s%n.", data[2],
						currencies));
			}
		} else {
			errMsg.append(String.format(
					"The currencies (%s and %s) must exist in the system. %n.", data[1], data[2]));
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}
