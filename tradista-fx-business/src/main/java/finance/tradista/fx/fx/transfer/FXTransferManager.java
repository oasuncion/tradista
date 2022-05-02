package finance.tradista.fx.fx.transfer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferManager;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.fx.fx.messaging.FXTradeEvent;
import finance.tradista.fx.fx.model.FXTrade;

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

public class FXTransferManager implements TransferManager<FXTradeEvent> {

	protected TransferBusinessDelegate transferBusinessDelegate;

	public FXTransferManager() {
		transferBusinessDelegate = new TransferBusinessDelegate();
	}

	@Override
	public void createTransfers(FXTradeEvent event) throws TradistaBusinessException {

		FXTrade trade = event.getTrade();
		List<Transfer> transfersToBeSaved = new ArrayList<Transfer>();

		if (event.getOldTrade() != null) {
			FXTrade oldTrade = event.getOldTrade();
			if (!oldTrade.getCurrencyOne().equals(trade.getCurrencyOne())
					|| oldTrade.getAmountOne().compareTo(trade.getAmountOne()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.PRIMARY_CURRENCY, false);
				// if the transfer is null, it is not normal, but the process should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					transfers.get(0).setStatus(Transfer.Status.CANCELED);
					transfersToBeSaved.add(transfers.get(0));
				}
				transfersToBeSaved.add(createNewPrimaryCurrencyTransfer(trade));

			}
			if (!oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getAmount().compareTo(trade.getAmount()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.QUOTE_CURRENCY, false);
				// if the transfer is null, it is not normal, but the process should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					transfers.get(0).setStatus(Transfer.Status.CANCELED);
					transfersToBeSaved.add(transfers.get(0));
				}
				transfersToBeSaved.add(createNewQuoteCurrencyTransfer(trade));

			}
		} else {
			transfersToBeSaved.add(createNewPrimaryCurrencyTransfer(trade));
			transfersToBeSaved.add(createNewQuoteCurrencyTransfer(trade));
		}
		if (!transfersToBeSaved.isEmpty()) {
			transferBusinessDelegate.saveTransfers(transfersToBeSaved);
		}
	}

	private CashTransfer createNewPrimaryCurrencyTransfer(FXTrade trade) throws TradistaBusinessException {
		CashTransfer primaryTransfer = new CashTransfer();
		primaryTransfer.setCreationDateTime(LocalDateTime.now());
		primaryTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		primaryTransfer.setSettlementDate(trade.getSettlementDate());
		primaryTransfer.setPurpose(TransferPurpose.PRIMARY_CURRENCY);
		primaryTransfer.setStatus(Transfer.Status.KNOWN);
		primaryTransfer.setTrade(trade);
		primaryTransfer.setBook(trade.getBook());
		primaryTransfer.setAmount(trade.getAmountOne());
		primaryTransfer.setCurrency(trade.getCurrencyOne());

		if (trade.isBuy()) {
			primaryTransfer.setDirection(Transfer.Direction.RECEIVE);
		} else {
			primaryTransfer.setDirection(Transfer.Direction.PAY);
		}

		return primaryTransfer;
	}

	private CashTransfer createNewQuoteCurrencyTransfer(FXTrade trade) throws TradistaBusinessException {
		CashTransfer quoteTransfer = new CashTransfer();
		quoteTransfer.setCreationDateTime(LocalDateTime.now());
		quoteTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		quoteTransfer.setSettlementDate(trade.getSettlementDate());
		quoteTransfer.setPurpose(TransferPurpose.QUOTE_CURRENCY);
		quoteTransfer.setStatus(Transfer.Status.KNOWN);
		quoteTransfer.setTrade(trade);
		quoteTransfer.setBook(trade.getBook());
		quoteTransfer.setAmount(trade.getAmount());
		quoteTransfer.setCurrency(trade.getCurrency());

		if (trade.isBuy()) {
			quoteTransfer.setDirection(Transfer.Direction.PAY);
		} else {
			quoteTransfer.setDirection(Transfer.Direction.RECEIVE);
		}

		return quoteTransfer;
	}

	@Override
	public void fixCashTransfer(CashTransfer transfer, long quoteSetId) {
	}
}