package finance.tradista.mm.loan.transfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.interestpayment.model.InterestPayment;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.FixingError;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.mm.loandeposit.model.DepositTrade;
import finance.tradista.mm.loandeposit.model.LoanDepositTrade;
import finance.tradista.mm.loandeposit.transfer.LoanDepositTransferManager;

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

public class LoanTransferManager extends LoanDepositTransferManager {

	@Override
	public void fixCashTransfer(CashTransfer transfer, long quoteSetId) throws TradistaBusinessException {
		DepositTrade trade = (DepositTrade) transfer.getTrade();
		BigDecimal amount;
		LocalDate endOfPeriod;
		// Determinate the end of period
		LoanDepositTrade mmTrade = (LoanDepositTrade) transfer.getTrade();
		if ((mmTrade).getInterestPayment().equals(InterestPayment.END_OF_PERIOD)) {
			endOfPeriod = transfer.getSettlementDate();
		} else {
			endOfPeriod = DateUtil.addTenor(transfer.getSettlementDate(), mmTrade.getPaymentFrequency()).minusDays(1);
		}
		try {
			amount = loanDepositBusinessDelegate.getPaymentAmount(trade, transfer.getFixingDateTime().toLocalDate(),
					endOfPeriod, quoteSetId, 0);
		} catch (TradistaBusinessException abe) {
			FixingError fixingError = new FixingError();
			fixingError.setCashTransfer(transfer);
			fixingError.setErrorDate(LocalDateTime.now());
			String quoteName = Index.INDEX + "." + trade.getFloatingRateIndex() + "."
					+ trade.getFloatingRateIndexTenor();
			String errorMsg = String.format(
					"Transfer %n cannot be fixed. Impossible to get the %s index closing value as of %tD in QuoteSet %s.",
					transfer.getId(), quoteName, transfer.getFixingDateTime(), quoteSetId);
			fixingError.setMessage(errorMsg);
			fixingError.setStatus(finance.tradista.core.error.model.Error.Status.UNSOLVED);
			List<FixingError> errors = new ArrayList<FixingError>(1);
			errors.add(fixingError);
			fixingErrorBusinessDelegate.saveFixingErrors(errors);
			throw new TradistaBusinessException(errorMsg);
		}
		if (amount.signum() == 0) {
			// No transfer
			transferBusinessDelegate.deleteTransfer(transfer.getId());
			return;
			// TODO add a warn somewhere ?
		}
		Transfer.Direction direction;
		if (trade.isBuy()) {
			if (amount.signum() > 0) {
				direction = Transfer.Direction.PAY;
			} else {
				direction = Transfer.Direction.RECEIVE;
				amount = amount.negate();
			}
		} else {
			if (amount.signum() > 0) {
				direction = Transfer.Direction.RECEIVE;
			} else {
				direction = Transfer.Direction.PAY;
				amount = amount.negate();
			}
		}
		transfer.setDirection(direction);
		transfer.setAmount(amount);
		transferBusinessDelegate.saveTransfer(transfer);
	}

}