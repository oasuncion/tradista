package finance.tradista.ir.ircapfloorcollar.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.interestpayment.model.InterestPayment;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.validator.DefaultTradeValidator;
import finance.tradista.ir.ircapfloorcollar.model.IRCapFloorCollarTrade;

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

public class IRCapFloorCollarTradeValidator extends DefaultTradeValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -13584296905203575L;

	@Override
	public void validateTrade(Trade<? extends Product> trade) throws TradistaBusinessException {
		IRCapFloorCollarTrade irCapFloorCollarTrade = (IRCapFloorCollarTrade) trade;
		StringBuilder errMsg = validateTradeBasics(trade);
		if (irCapFloorCollarTrade.getIrForwardTrade() == null) {
			errMsg.append(String.format("The underlying IR Forward trade is mandatory.%n"));
		} else {
			if (irCapFloorCollarTrade.getIrForwardTrade().getMaturityDate() == null) {
				errMsg.append(String.format("The underlying IR Forward trade maturity date is mandatory.%n"));
			} else {
				if (irCapFloorCollarTrade.getIrForwardTrade().getSettlementDate() != null) {
					if (!irCapFloorCollarTrade.getIrForwardTrade().getMaturityDate()
							.isAfter(irCapFloorCollarTrade.getIrForwardTrade().getSettlementDate())) {
						errMsg.append(String.format(
								"The underlying IR Forward trade maturity date must be after the settlement date.%n"));
					}
				}
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getAmount() == null) {
				errMsg.append(String.format("The underlying IR Forward trade notional amount is mandatory.%n"));
			} else {
				if (irCapFloorCollarTrade.getIrForwardTrade().getAmount().doubleValue() <= 0) {
					errMsg.append(
							String.format("The underlying IR Forward trade notional amount (%s) must be positive.%n",
									irCapFloorCollarTrade.getIrForwardTrade().getAmount().doubleValue()));
				}
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getFrequency() == null) {
				errMsg.append(String.format("The IR Forward trade (underlying) frequency is mandatory.%n"));
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getReferenceRateIndex() == null) {
				errMsg.append(String.format("The IR Forward trade (underlying) reference rate index is mandatory.%n"));
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getReferenceRateIndexTenor() == null) {
				errMsg.append(
						String.format("The IR Forward trade (underlying) reference rate index tenor is mandatory.%n"));
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getDayCountConvention() == null) {
				errMsg.append(String.format("The IR Forward trade (underlying) day count convention is mandatory.%n"));
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getInterestPayment() == null) {
				errMsg.append(String.format("The IR Forward trade (underlying) interest payment is mandatory.%n"));
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getInterestFixing() == null) {
				errMsg.append(String.format("The IR Forward trade (underlying) interest fixing is mandatory.%n"));
			}
			if (irCapFloorCollarTrade.getIrForwardTrade().getInterestPayment() != null && irCapFloorCollarTrade.getIrForwardTrade().getInterestFixing() != null) {
				if (irCapFloorCollarTrade.getIrForwardTrade().getInterestPayment().equals(InterestPayment.BEGINNING_OF_PERIOD) && irCapFloorCollarTrade.getIrForwardTrade().getInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
					errMsg.append(String.format("It is not possible to have interest payment before interest fixing.%n"));	
				}
			}
		}

		// Other business controls
		if (trade.getAmount() != null && trade.getAmount().doubleValue() <= 0) {
			errMsg.append(String.format("The premium (%s) must be positive.%n", trade.getAmount().doubleValue()));
		}

		if (trade.getSettlementDate() == null) {
			errMsg.append(String.format("The settlement date is mandatory.%n"));
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}