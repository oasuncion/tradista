package finance.tradista.security.equity.transfer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.model.EquityTrade;

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

public final class EquityTransferUtil {

	/**
	 * Returns the list of received dividends for a given equity trade.
	 * @param equity trade the concerned equity trade 
	 * @return the list of received dividends for a given equity
	 * @throws TradistaBusinessException
	 */
	public static List<CashTransfer> generateDividends(EquityTrade trade) throws TradistaBusinessException {
		if (trade == null) {
			throw new TradistaBusinessException("The trade is mandatory.");
		}
		
		Equity equity = trade.getProduct();

		Tenor frequency = equity.getDividendFrequency();
		List<CashTransfer> dividends = new ArrayList<CashTransfer>();

		LocalDate cashFlowDate = trade.getSettlementDate();
		if (trade.getSettlementDate().isBefore(equity.getActiveFrom())) {
			cashFlowDate = equity.getActiveFrom();
		}

		while (!cashFlowDate.isAfter(equity.getActiveTo())) {
			if (cashFlowDate.isAfter(trade.getSettlementDate())) {
				CashTransfer cashTransfer = new CashTransfer();
				cashTransfer.setSettlementDate(cashFlowDate);
				cashTransfer.setCurrency(equity.getDividendCurrency());
				cashTransfer.setProduct(equity);
				cashTransfer.setCreationDateTime(LocalDateTime.now());
				cashTransfer.setFixingDateTime(cashFlowDate.atStartOfDay());
				cashTransfer.setPurpose(TransferPurpose.DIVIDEND);
				cashTransfer.setStatus(Transfer.Status.UNKNOWN);
				cashTransfer.setBook(trade.getBook());

				dividends.add(cashTransfer);
			}

			cashFlowDate = DateUtil.addTenor(cashFlowDate, frequency);
		}
		return dividends;
	}
}