package finance.tradista.core.trade.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;

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

public class DefaultTradeValidator implements TradeValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5806272321571799302L;

	@Override
	public void validateTrade(Trade<? extends Product> trade)
			throws TradistaBusinessException {
		StringBuilder errMsg = validateTradeBasics(trade);
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	protected StringBuilder validateTradeBasics(Trade<? extends Product> trade)
			throws TradistaBusinessException {
		// Existence controls
		if (trade == null) {
			throw new TradistaBusinessException("The trade cannot be null.");
		}
		StringBuilder errMsg = new StringBuilder();
		if (trade.getTradeDate() == null) {
			errMsg.append(String.format("The trade date is mandatory.%n"));
		}
		if (trade.getAmount() == null) {
			errMsg.append(String.format("The amount is mandatory.%n"));
		}
		if (trade.getCurrency() == null) {
			errMsg.append(String.format("The currency is mandatory.%n"));
		}

		if (trade.getTradeDate() != null && trade.getSettlementDate() != null) {
			if (trade.getSettlementDate().isBefore(trade.getTradeDate())) {
				errMsg.append(String
						.format("The settlement date (%s) cannot be before trade date (%s).%n",
								trade.getSettlementDate(), trade.getTradeDate()));
			}
		}

		if (trade.getCounterparty() == null) {
			errMsg.append(String.format("The counterparty is mandatory.%n"));
		}
		if (trade.getBook() == null) {
			errMsg.append(String.format("The book is mandatory.%n"));
		}
		return errMsg;
	}

}
