package finance.tradista.ir.irswapoption.validator;

import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.validator.DefaultQuoteValidator;
import finance.tradista.ir.irswapoption.model.IRSwapOptionTrade;
import finance.tradista.ir.irswapoption.service.SwaptionVolatilitySurfaceBusinessDelegate;

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

public class IRSwapOptionQuoteValidator extends DefaultQuoteValidator {

	@Override
	public void validateQuoteName(String quoteName)
			throws TradistaBusinessException {
		validateQuoteBasics(quoteName);
		StringBuilder errMsg = new StringBuilder();
		String[] data = quoteName.split("\\.");

		if (data.length < 5) {
			throw new TradistaBusinessException(
					String.format(
							"The quote name (%s) must be as follows: IRSwapOption.IRCurveName.frequency.swapMaturity.optionExpiry.%n",
							quoteName));
		}

		if (!data[0].equals(IRSwapOptionTrade.IR_SWAP_OPTION)) {
			errMsg.append(String.format(
					"The quote name (%s) must start with %s.%n",
					quoteName, IRSwapOptionTrade.IR_SWAP_OPTION));
		}
		Set<String> maturities = new SwaptionVolatilitySurfaceBusinessDelegate()
				.getAllSwapMaturitiesAsString();
		if (!maturities.contains(data[3])) {
			errMsg.append(String.format(
					"The swap maturity (%s) must be a valid one: %s%n.", data[3],
					maturities));
		}
		Set<String> expiries = new SwaptionVolatilitySurfaceBusinessDelegate()
				.getAllOptionExpiriesAsString();
		if (!expiries.contains(data[4])) {
			errMsg.append(String.format(
					"The option expiry (%s) must be a valid one: %s%n.", data[4],
					expiries));
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}
