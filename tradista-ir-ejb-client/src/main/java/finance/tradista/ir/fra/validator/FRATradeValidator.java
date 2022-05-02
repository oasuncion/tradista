package finance.tradista.ir.fra.validator;

import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.validator.DefaultTradeValidator;
import finance.tradista.ir.fra.model.FRATrade;
import finance.tradista.ir.fra.service.FRATradeBusinessDelegate;

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

public class FRATradeValidator extends DefaultTradeValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8162646174831211084L;

	@Override
	public void validateTrade(Trade<? extends Product> trade) throws TradistaBusinessException {
		FRATrade fraTrade = (FRATrade) trade;
		StringBuilder errMsg = validateTradeBasics(trade);

		if (fraTrade.getFixedRate() == null) {
			errMsg.append(String.format("The fixed rate is mandatory.%n"));
		}

		if (fraTrade.getReferenceRateIndex() == null) {
			errMsg.append(String.format("The reference rate index is mandatory.%n"));
		}

		if (fraTrade.getReferenceRateIndexTenor() == null) {
			errMsg.append(String.format("The reference rate index tenor is mandatory.%n"));
		}

		if (fraTrade.getDayCountConvention() == null) {
			errMsg.append(String.format("The day count convention is mandatory.%n"));
		}

		if (fraTrade.getStartDate() == null) {
			errMsg.append(String.format("The start date is mandatory.%n"));
		}

		if (fraTrade.getEndDate() == null) {
			errMsg.append(String.format("The end date is mandatory.%n"));
		} else {
			if (fraTrade.getStartDate() != null && fraTrade.getReferenceRateIndexTenor() != null
					&& !fraTrade.getReferenceRateIndexTenor().equals(Tenor.NO_TENOR)) {
				LocalDate expectedMaturityDate = DateUtil.addTenor(fraTrade.getStartDate(),
						fraTrade.getReferenceRateIndexTenor());
				if (!expectedMaturityDate.isEqual(fraTrade.getMaturityDate())) {
					errMsg.append(String.format(
							"Inconsistency detected. With this start date %tD and this reference rate index tenor %s, the end date should be %tD. %n",
							fraTrade.getStartDate(), fraTrade.getReferenceRateIndexTenor(), expectedMaturityDate));
				}
			}
		}

		// Other business controls
		if (trade.getAmount() != null && trade.getAmount().doubleValue() <= 0) {
			errMsg.append(
					String.format("The notional amount (%s) must be positive.%n", trade.getAmount().doubleValue()));
		}

		if (fraTrade.getPaymentDate() == null) {
			errMsg.append(String.format("The payment date is mandatory.%n"));
		} else if (fraTrade.getStartDate() != null) {
			LocalDate expectedStartDate = new FRATradeBusinessDelegate().getStartDate(fraTrade);
			if (!expectedStartDate.isEqual(fraTrade.getStartDate())) {
				errMsg.append(String.format(
						"Inconsistency detected. With this payment date %tD, the start date should be %tD. %n",
						fraTrade.getPaymentDate(), expectedStartDate));
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}