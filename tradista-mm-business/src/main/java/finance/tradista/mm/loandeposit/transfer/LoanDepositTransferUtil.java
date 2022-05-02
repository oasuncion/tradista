package finance.tradista.mm.loandeposit.transfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.interestpayment.model.InterestPayment;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.mm.loandeposit.model.LoanDepositTrade;
import finance.tradista.mm.loandeposit.model.LoanTrade;
import finance.tradista.mm.loandeposit.service.LoanDepositTradeBusinessDelegate;

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

public final class LoanDepositTransferUtil {

	private static LoanDepositTradeBusinessDelegate loanDepositBusinessDelegate = new LoanDepositTradeBusinessDelegate();

	/**
	 * @param trade the mm trade for which we want to generate the cash transfers
	 */
	public static List<CashTransfer> generateInterestPayments(LoanDepositTrade mmTrade) {
		List<CashTransfer> payments = null;

		if (!mmTrade.getPaymentFrequency().equals(Tenor.NO_TENOR)) {
			// Settlement date is the start date of the payments
			LocalDate cfDate = mmTrade.getSettlementDate();

			while (!cfDate.isAfter(mmTrade.getEndDate())) {
				LocalDate settlementDate = cfDate;
				LocalDate fixingDate = cfDate;
				LocalDate endOfPeriod = cfDate;

				try {
					cfDate = DateUtil.addTenor(cfDate, mmTrade.getPaymentFrequency());
				} catch (TradistaBusinessException abe) {
					// Should not appear here.
				}
				/*
				 * if the end of period is after the trade 's maturity date, new end of period
				 * id trade's maturity date
				 */
				endOfPeriod = cfDate.minusDays(1);
				if (endOfPeriod.isAfter(mmTrade.getEndDate())) {
					endOfPeriod = mmTrade.getEndDate();
				}
				if (mmTrade.getInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
					settlementDate = endOfPeriod;
				}
				if (mmTrade.getInterestFixing() != null) {
					if (mmTrade.getInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
						fixingDate = endOfPeriod;
					}
				}
				if (payments == null) {
					payments = new ArrayList<CashTransfer>();
				}
				payments.add(createInterestPayment(mmTrade, settlementDate, fixingDate, endOfPeriod));
			}
		} else {
			payments = new ArrayList<CashTransfer>();
			LocalDate settlementDate = mmTrade.getSettlementDate();
			LocalDate fixingDate = mmTrade.getSettlementDate();
			if (mmTrade.getInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
				settlementDate = mmTrade.getEndDate();
			}
			if (mmTrade.getInterestFixing() != null) {
				if (mmTrade.getInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
					fixingDate = mmTrade.getEndDate();
				}
			}
			payments.add(createInterestPayment(mmTrade, settlementDate, fixingDate, mmTrade.getEndDate()));
		}
		return payments;
	}

	private static CashTransfer createInterestPayment(LoanDepositTrade mmTrade, LocalDate settlementDate,
			LocalDate fixingDate, LocalDate endOfPeriod) {
		BigDecimal paymentAmount = null;
		CashTransfer ct = new CashTransfer();
		ct.setSettlementDate(settlementDate);
		ct.setCurrency(mmTrade.getCurrency());
		ct.setCreationDateTime(LocalDateTime.now());
		ct.setPurpose(TransferPurpose.INTEREST_PAYMENT);
		ct.setTrade(mmTrade);
		ct.setBook(mmTrade.getBook());
		if (mmTrade.isFixed()) {
			try {
				paymentAmount = loanDepositBusinessDelegate.getPaymentAmount(mmTrade, fixingDate, endOfPeriod, 0, 0);
			} catch (TradistaBusinessException pe) {
				// Should not happen here.
			}
			ct.setAmount(paymentAmount);
			ct.setFixingDateTime(mmTrade.getCreationDate().atStartOfDay());
			if (mmTrade.getProductType().equals(LoanTrade.LOAN)) {
				if (mmTrade.isBuy()) {
					ct.setDirection(Transfer.Direction.PAY);
				} else {
					ct.setDirection(Transfer.Direction.RECEIVE);
				}
			} else {
				if (mmTrade.isSell()) {
					ct.setDirection(Transfer.Direction.PAY);
				} else {
					ct.setDirection(Transfer.Direction.RECEIVE);
				}
			}
			ct.setStatus(Transfer.Status.KNOWN);
		} else {
			ct.setFixingDateTime(fixingDate.atStartOfDay());
			ct.setStatus(Transfer.Status.UNKNOWN);
		}

		return ct;
	}

}