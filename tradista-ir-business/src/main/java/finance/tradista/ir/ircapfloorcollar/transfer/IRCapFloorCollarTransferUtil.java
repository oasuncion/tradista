package finance.tradista.ir.ircapfloorcollar.transfer;

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

public final class IRCapFloorCollarTransferUtil {

	/**
	 * @param trade the ir cap/floor/collar for which we want to generate the paid
	 *              cash transfers
	 * @throws TradistaBusinessException
	 */
	public static List<CashTransfer> generateCashTransfers(IRCapFloorCollarTrade trade)
			throws TradistaBusinessException {
		Tenor frequency = trade.getIrForwardTrade().getFrequency();
		List<CashTransfer> cashTransfers = null;
		LocalDate cashFlowDate = trade.getSettlementDate();
		if (!trade.getIrForwardTrade().getFrequency().equals(Tenor.NO_TENOR)) {
			while (!cashFlowDate.isAfter(trade.getIrForwardTrade().getMaturityDate())) {
				LocalDate fixingDate = cashFlowDate;
				LocalDate settlementDate = cashFlowDate;
				LocalDate endOfPeriod = cashFlowDate;
				cashFlowDate = DateUtil.addTenor(cashFlowDate, frequency);
				/*
				 * if the end of period is after the trade 's maturity date, new end of period
				 * id trade's maturity date
				 */
				endOfPeriod = cashFlowDate.minusDays(1);
				if (endOfPeriod.isAfter(trade.getIrForwardTrade().getMaturityDate())) {
					endOfPeriod = trade.getIrForwardTrade().getMaturityDate();
				}
				if (trade.getIrForwardTrade().getInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
					settlementDate = endOfPeriod;
				}
				if (trade.getIrForwardTrade().getInterestFixing().equals(InterestPayment.END_OF_PERIOD)) {
					fixingDate = endOfPeriod;
				}

				if (cashTransfers == null) {
					cashTransfers = new ArrayList<CashTransfer>();
				}
				cashTransfers.add(createCashTransfer(trade, fixingDate, settlementDate));

			}
		} else {
			cashTransfers = new ArrayList<CashTransfer>();
			LocalDate settlementDate = trade.getSettlementDate();
			if (trade.getIrForwardTrade().getInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
				settlementDate = trade.getIrForwardTrade().getMaturityDate();
			}
			cashTransfers.add(createCashTransfer(trade, trade.getSettlementDate(), settlementDate));
		}
		return cashTransfers;
	}

	private static CashTransfer createCashTransfer(IRCapFloorCollarTrade trade, LocalDate fixingDate,
			LocalDate settlementDate) {
		CashTransfer cashTransfer = new CashTransfer(trade.getBook(), TransferPurpose.CASH_SETTLEMENT, settlementDate,
				trade, trade.getCurrency());
		cashTransfer.setFixingDateTime(fixingDate.atStartOfDay());
		cashTransfer.setCreationDateTime(LocalDateTime.now());
		cashTransfer.setStatus(Transfer.Status.UNKNOWN);
		return cashTransfer;
	}

}