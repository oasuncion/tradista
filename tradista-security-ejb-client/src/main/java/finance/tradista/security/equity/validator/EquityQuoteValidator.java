package finance.tradista.security.equity.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.validator.DefaultQuoteValidator;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.service.EquityBusinessDelegate;

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

public class EquityQuoteValidator extends DefaultQuoteValidator {

	@Override
	public void validateQuoteName(String quoteName)
			throws TradistaBusinessException {
		validateQuoteBasics(quoteName);
		StringBuilder errMsg = new StringBuilder();
		String[] data = quoteName.split("\\.");

		if (data.length < 3) {
			throw new TradistaBusinessException(
					String.format(
							"The quote name (%s) must be as follows: %s.Isin.ExchangeCode",
							quoteName, Equity.EQUITY));
		}

		if (!data[0].equals(Equity.EQUITY)) {
			errMsg.append(String.format(
					"The quote name (%s) must start with %s.%n", quoteName,
					Equity.EQUITY));
		}
		Equity equity = new EquityBusinessDelegate()
				.getEquityByIsinAndExchangeCode(data[1], data[2]);
		if (equity == null) {
			errMsg.append(String
					.format("The equity %s in the exchange %s must exist in the system.%n",
							data[1], data[2]));
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}
