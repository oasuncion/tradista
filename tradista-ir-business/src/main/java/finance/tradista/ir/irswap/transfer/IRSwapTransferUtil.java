package finance.tradista.ir.irswap.transfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.interestpayment.model.InterestPayment;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.ir.ccyswap.model.CcySwapTrade;
import finance.tradista.ir.irswap.model.IRSwapTrade;

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

public final class IRSwapTransferUtil {

	/**
	 * @param trade the irswap for which we want to generate the paid cash transfers
	 */
	public static List<CashTransfer> generatePaymentCashTransfers(IRSwapTrade trade) {
		List<CashTransfer> cashTransfers = null;
		LocalDate cashFlowDate = trade.getSettlementDate();

		// When the trade is a Ccy, fixeg legs cashflows are based on notional 2
		// and currency 2.
		Currency currency;
		BigDecimal notional;
		if (trade instanceof CcySwapTrade) {
			notional = ((CcySwapTrade) trade).getNotionalAmountTwo();
			currency = ((CcySwapTrade) trade).getCurrencyTwo();
		} else {
			notional = trade.getAmount();
			currency = trade.getCurrency();
		}

		if (!trade.getPaymentFrequency().equals(Tenor.NO_TENOR)) {
			while (!cashFlowDate.isAfter(trade.getMaturityDate())) {
				LocalDate fixingDate = cashFlowDate;
				LocalDate settlementDate = cashFlowDate;
				LocalDate beginningOfPeriod = cashFlowDate;
				LocalDate endOfPeriod = cashFlowDate;
				if (cashTransfers == null) {
					cashTransfers = new ArrayList<CashTransfer>();
				}
				try {
					cashFlowDate = DateUtil.addTenor(cashFlowDate, trade.getPaymentFrequency());
				} catch (TradistaBusinessException abe) {
					// Should not appear here.
				}
				/*
				 * if the end of period is after the trade 's maturity date, new end of period
				 * id trade's maturity date
				 */
				endOfPeriod = cashFlowDate.minusDays(1);
				if (endOfPeriod.isAfter(trade.getMaturityDate())) {
					endOfPeriod = trade.getMaturityDate();
				}
				if (trade.getPaymentInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
					settlementDate = endOfPeriod;
				}
				if (trade.getPaymentInterestFixing() != null) {
					if (trade.getPaymentInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
						fixingDate = endOfPeriod;
					}
				}

				cashTransfers.add(createPaymentCashTransfer(trade, fixingDate, settlementDate, beginningOfPeriod,
						endOfPeriod, currency, notional));
			}
		} else {
			cashTransfers = new ArrayList<CashTransfer>();
			LocalDate settlementDate = trade.getSettlementDate();
			LocalDate fixingDate = trade.getSettlementDate();
			LocalDate beginningOfPeriod = trade.getSettlementDate();
			LocalDate endOfPeriod = trade.getMaturityDate();
			if (trade.getPaymentInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
				settlementDate = trade.getMaturityDate();
			}
			if (trade.getPaymentInterestFixing() != null) {
				if (trade.getPaymentInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
					fixingDate = trade.getMaturityDate();
				}
			}
			cashTransfers.add(createPaymentCashTransfer(trade, fixingDate, settlementDate, beginningOfPeriod,
					endOfPeriod, currency, notional));
		}

		// When it is a CcySwapTrade, notional for this leg is received at
		// settlement date by the trade buyer (who pays the fixed leg) and he
		// pays it back
		// at maturity date
		if (trade instanceof CcySwapTrade) {
			CashTransfer cashTransfer = new CashTransfer();
			cashTransfer.setCurrency(currency);
			cashTransfer.setSettlementDate(trade.getSettlementDate());
			if (trade.isBuy()) {
				cashTransfer.setDirection(Transfer.Direction.PAY);
			} else {
				cashTransfer.setDirection(Transfer.Direction.RECEIVE);
			}
			if (cashTransfers == null) {
				cashTransfers = new ArrayList<CashTransfer>();
			}
			cashTransfer.setTrade(trade);
			cashTransfer.setBook(trade.getBook());
			cashTransfer.setPurpose(TransferPurpose.FIXED_LEG_NOTIONAL_PAYMENT);
			cashTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
			cashTransfer.setAmount(notional);
			cashTransfer.setStatus(Transfer.Status.KNOWN);
			cashTransfer.setCreationDateTime(LocalDateTime.now());
			cashTransfers.add(cashTransfer);

			cashTransfer = new CashTransfer();
			cashTransfer.setCurrency(currency);
			cashTransfer.setSettlementDate(trade.getMaturityDate());
			if (trade.isSell()) {
				cashTransfer.setDirection(Transfer.Direction.PAY);
			} else {
				cashTransfer.setDirection(Transfer.Direction.RECEIVE);
			}
			cashTransfer.setTrade(trade);
			cashTransfer.setBook(trade.getBook());
			cashTransfer.setPurpose(TransferPurpose.FIXED_LEG_NOTIONAL_REPAYMENT);
			cashTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
			cashTransfer.setAmount(notional);
			cashTransfer.setStatus(Transfer.Status.KNOWN);
			cashTransfer.setCreationDateTime(LocalDateTime.now());
			cashTransfers.add(cashTransfer);
		}

		return cashTransfers;
	}

	/**
	 * @param trade the irswap for which we want to calculate the received cashflows
	 */
	public static List<CashTransfer> generateReceptionCashTransfers(IRSwapTrade trade)
			throws TradistaBusinessException {
		List<CashTransfer> cashTransfers = null;
		if (!trade.getReceptionFrequency().equals(Tenor.NO_TENOR)) {
			LocalDate cashFlowDate = trade.getSettlementDate();
			while (!cashFlowDate.isAfter(trade.getMaturityDate())) {
				LocalDate fixingDate = cashFlowDate;
				LocalDate settlementDate = cashFlowDate;
				LocalDate endOfPeriod = cashFlowDate;
				if (cashTransfers == null) {
					cashTransfers = new ArrayList<CashTransfer>();
				}
				cashFlowDate = DateUtil.addTenor(cashFlowDate, trade.getReceptionFrequency());
				/*
				 * if the end of period is after the trade 's maturity date, new end of period
				 * id trade's maturity date
				 */
				endOfPeriod = cashFlowDate.minusDays(1);
				if (endOfPeriod.isAfter(trade.getMaturityDate())) {
					endOfPeriod = trade.getMaturityDate();
				}
				if (trade.getReceptionInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
					settlementDate = endOfPeriod;
				}
				if (trade.getReceptionInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
					fixingDate = endOfPeriod;
				}

				cashTransfers.add(createReceptionCashTransfer(trade, fixingDate, settlementDate));
			}
		} else {
			cashTransfers = new ArrayList<CashTransfer>();
			LocalDate settlementDate = trade.getSettlementDate();
			LocalDate fixingDate = trade.getSettlementDate();
			if (trade.getReceptionInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
				settlementDate = trade.getMaturityDate();
			}
			if (trade.getReceptionInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
				fixingDate = trade.getMaturityDate();
			}
			cashTransfers.add(createReceptionCashTransfer(trade, fixingDate, settlementDate));
		}

		// When it is a CcySwapTrade, notional for this leg is received at
		// settlement date by the trade seller (who pays the floating leg) and
		// he pays it back
		// at maturity date
		if (trade instanceof CcySwapTrade) {
			CashTransfer cashTransfer = new CashTransfer();
			cashTransfer.setCurrency(trade.getCurrency());
			cashTransfer.setSettlementDate(trade.getSettlementDate());
			if (trade.isBuy()) {
				cashTransfer.setDirection(Transfer.Direction.PAY);
			} else {
				cashTransfer.setDirection(Transfer.Direction.RECEIVE);
			}
			if (cashTransfers == null) {
				cashTransfers = new ArrayList<CashTransfer>();
			}
			cashTransfer.setTrade(trade);
			cashTransfer.setBook(trade.getBook());
			cashTransfer.setPurpose(TransferPurpose.FLOATING_LEG_NOTIONAL_PAYMENT);
			cashTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
			cashTransfer.setAmount(trade.getAmount());
			cashTransfer.setStatus(Transfer.Status.KNOWN);
			cashTransfer.setCreationDateTime(LocalDateTime.now());
			cashTransfers.add(cashTransfer);
			cashTransfer = new CashTransfer();
			cashTransfer.setCurrency(trade.getCurrency());
			cashTransfer.setSettlementDate(trade.getMaturityDate());
			if (trade.isSell()) {
				cashTransfer.setDirection(Transfer.Direction.PAY);
			} else {
				cashTransfer.setDirection(Transfer.Direction.RECEIVE);
			}
			cashTransfer.setTrade(trade);
			cashTransfer.setBook(trade.getBook());
			cashTransfer.setPurpose(TransferPurpose.FLOATING_LEG_NOTIONAL_REPAYMENT);
			cashTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
			cashTransfer.setAmount(trade.getAmount());
			cashTransfer.setStatus(Transfer.Status.KNOWN);
			cashTransfer.setCreationDateTime(LocalDateTime.now());
			cashTransfers.add(cashTransfer);
		}

		return cashTransfers;
	}

	private static CashTransfer createReceptionCashTransfer(IRSwapTrade trade, LocalDate fixingDate,
			LocalDate settlementDate) throws TradistaBusinessException {
		CashTransfer cashTransfer = new CashTransfer();
		cashTransfer.setCurrency(trade.getCurrency());
		cashTransfer.setFixingDateTime(fixingDate.atStartOfDay());
		cashTransfer.setSettlementDate(settlementDate);
		if (trade.isBuy()) {
			cashTransfer.setDirection(Transfer.Direction.RECEIVE);
		} else {
			cashTransfer.setDirection(Transfer.Direction.PAY);
		}
		cashTransfer.setTrade(trade);
		cashTransfer.setBook(trade.getBook());
		cashTransfer.setStatus(Transfer.Status.UNKNOWN);
		cashTransfer.setPurpose(TransferPurpose.FLOATING_LEG_INTEREST_PAYMENT);
		cashTransfer.setCreationDateTime(LocalDateTime.now());
		return cashTransfer;
	}

	private static CashTransfer createPaymentCashTransfer(IRSwapTrade trade, LocalDate fixingDate,
			LocalDate settlementDate, LocalDate beginningOfPeriod, LocalDate endOfPeriod, Currency currency,
			BigDecimal notional) {
		CashTransfer cashTransfer = new CashTransfer();
		cashTransfer.setCurrency(currency);
		BigDecimal ir = null;
		if (trade.isInterestsToPayFixed()) {
			ir = trade.getPaymentFixedInterestRate();
		}
		if (!trade.isInterestsToPayFixed()) {
			cashTransfer.setFixingDateTime(fixingDate.atStartOfDay());
		} else {
			cashTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		}
		cashTransfer.setSettlementDate(settlementDate);

		// the fractioned notional is the notional of the reception leg
		// * accrual
		// factor calculated using the period between the fixing
		// date and the payment date.
		if (trade.isInterestsToPayFixed()) {
			ConfigurationBusinessDelegate cbs = new ConfigurationBusinessDelegate();
			BigDecimal fractionedNotional = notional.multiply(
					PricerUtil.daysToYear(trade.getPaymentDayCountConvention(), beginningOfPeriod, endOfPeriod));
			BigDecimal payment = fractionedNotional
					.multiply(ir.divide(BigDecimal.valueOf(100), cbs.getScale(), cbs.getRoundingMode()));
			if (trade.isBuy()) {
				if (payment.signum() > 0) {
					cashTransfer.setAmount(payment);
					cashTransfer.setDirection(Transfer.Direction.PAY);
				} else {
					cashTransfer.setDirection(Transfer.Direction.RECEIVE);
					cashTransfer.setAmount(payment.negate());
				}
			} else {
				if (payment.signum() > 0) {
					cashTransfer.setAmount(payment);
					cashTransfer.setDirection(Transfer.Direction.RECEIVE);
				} else {
					cashTransfer.setDirection(Transfer.Direction.PAY);
					cashTransfer.setAmount(payment.negate());
				}
			}
			cashTransfer.setStatus(Transfer.Status.KNOWN);
		} else {
			cashTransfer.setStatus(Transfer.Status.UNKNOWN);
		}
		cashTransfer.setTrade(trade);
		cashTransfer.setBook(trade.getBook());
		cashTransfer.setPurpose(TransferPurpose.FIXED_LEG_INTEREST_PAYMENT);
		cashTransfer.setCreationDateTime(LocalDateTime.now());

		return cashTransfer;
	}

}