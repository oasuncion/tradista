package finance.tradista.fx.fxswap.transfer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferManager;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.fx.fxswap.messaging.FXSwapTradeEvent;
import finance.tradista.fx.fxswap.model.FXSwapTrade;

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

public class FXSwapTransferManager implements TransferManager<FXSwapTradeEvent> {

	protected TransferBusinessDelegate transferBusinessDelegate;

	public FXSwapTransferManager() {
		transferBusinessDelegate = new TransferBusinessDelegate();
	}

	@Override
	public void createTransfers(FXSwapTradeEvent event) throws TradistaBusinessException {

		FXSwapTrade trade = event.getTrade();
		List<Transfer> transfersToBeSaved = new ArrayList<Transfer>();

		if (event.getOldTrade() != null) {
			FXSwapTrade oldTrade = event.getOldTrade();
			if (!oldTrade.getCurrencyOne().equals(trade.getCurrencyOne())
					|| oldTrade.getAmountOneSpot().compareTo(trade.getAmountOneSpot()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.PRIMARY_CURRENCY_SPOT, false);
				// if the transfer is null, it is not normal, but the process should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					transfers.get(0).setStatus(Transfer.Status.CANCELED);
					transfersToBeSaved.add(transfers.get(0));
				}
				transfersToBeSaved.add(createNewPrimaryCurrencySpotTransfer(trade));

			}
			if (!oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getAmount().compareTo(trade.getAmount()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.QUOTE_CURRENCY_SPOT, false);
				// if the transfer is null, it is not normal, but the process should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					transfers.get(0).setStatus(Transfer.Status.CANCELED);
					transfersToBeSaved.add(transfers.get(0));
				}
				transfersToBeSaved.add(createNewQuoteCurrencySpotTransfer(trade));

			}
			if (!oldTrade.getCurrencyOne().equals(trade.getCurrencyOne())
					|| oldTrade.getAmountOneForward().compareTo(trade.getAmountOneForward()) != 0
					|| !oldTrade.getSettlementDateForward().isEqual(trade.getSettlementDateForward())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.PRIMARY_CURRENCY_FORWARD, false);
				// if the transfer is null, it is not normal, but the process should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					transfers.get(0).setStatus(Transfer.Status.CANCELED);
					transfersToBeSaved.add(transfers.get(0));
				}
				transfersToBeSaved.add(createNewPrimaryCurrencyForwardTransfer(trade));

			}
			if (!oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getAmountTwoForward().compareTo(trade.getAmountTwoForward()) != 0
					|| !oldTrade.getSettlementDateForward().isEqual(trade.getSettlementDateForward())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.QUOTE_CURRENCY_FORWARD, false);
				// if the transfer is null, it is not normal, but the process should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					transfers.get(0).setStatus(Transfer.Status.CANCELED);
					transfersToBeSaved.add(transfers.get(0));
				}
				transfersToBeSaved.add(createNewQuoteCurrencyForwardTransfer(trade));

			}

		} else {
			transfersToBeSaved.add(createNewPrimaryCurrencySpotTransfer(trade));
			transfersToBeSaved.add(createNewQuoteCurrencySpotTransfer(trade));
			transfersToBeSaved.add(createNewPrimaryCurrencyForwardTransfer(trade));
			transfersToBeSaved.add(createNewQuoteCurrencyForwardTransfer(trade));
		}
		if (!transfersToBeSaved.isEmpty()) {
			transferBusinessDelegate.saveTransfers(transfersToBeSaved);
		}
	}

	private CashTransfer createNewPrimaryCurrencySpotTransfer(FXSwapTrade trade) throws TradistaBusinessException {
		CashTransfer primaryTransfer = new CashTransfer();
		primaryTransfer.setCreationDateTime(LocalDateTime.now());
		primaryTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		primaryTransfer.setSettlementDate(trade.getSettlementDate());
		primaryTransfer.setPurpose(TransferPurpose.PRIMARY_CURRENCY_SPOT);
		primaryTransfer.setStatus(Transfer.Status.KNOWN);
		primaryTransfer.setTrade(trade);
		primaryTransfer.setBook(trade.getBook());
		primaryTransfer.setAmount(trade.getAmountOneSpot());
		primaryTransfer.setCurrency(trade.getCurrencyOne());

		if (trade.isBuy()) {
			primaryTransfer.setDirection(Transfer.Direction.RECEIVE);
		} else {
			primaryTransfer.setDirection(Transfer.Direction.PAY);
		}

		return primaryTransfer;
	}

	private CashTransfer createNewQuoteCurrencySpotTransfer(FXSwapTrade trade) throws TradistaBusinessException {
		CashTransfer quoteTransfer = new CashTransfer();
		quoteTransfer.setCreationDateTime(LocalDateTime.now());
		quoteTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		quoteTransfer.setSettlementDate(trade.getSettlementDate());
		quoteTransfer.setPurpose(TransferPurpose.QUOTE_CURRENCY_SPOT);
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

	private CashTransfer createNewPrimaryCurrencyForwardTransfer(FXSwapTrade trade) throws TradistaBusinessException {
		CashTransfer primaryTransfer = new CashTransfer();
		primaryTransfer.setCreationDateTime(LocalDateTime.now());
		primaryTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		primaryTransfer.setSettlementDate(trade.getSettlementDateForward());
		primaryTransfer.setPurpose(TransferPurpose.PRIMARY_CURRENCY_FORWARD);
		primaryTransfer.setStatus(Transfer.Status.KNOWN);
		primaryTransfer.setTrade(trade);
		primaryTransfer.setBook(trade.getBook());
		primaryTransfer.setAmount(trade.getAmountOneForward());
		primaryTransfer.setCurrency(trade.getCurrencyOne());

		if (trade.isBuy()) {
			primaryTransfer.setDirection(Transfer.Direction.PAY);
		} else {
			primaryTransfer.setDirection(Transfer.Direction.RECEIVE);
		}

		return primaryTransfer;
	}

	private CashTransfer createNewQuoteCurrencyForwardTransfer(FXSwapTrade trade) throws TradistaBusinessException {
		CashTransfer quoteTransfer = new CashTransfer();
		quoteTransfer.setCreationDateTime(LocalDateTime.now());
		quoteTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		quoteTransfer.setSettlementDate(trade.getSettlementDateForward());
		quoteTransfer.setPurpose(TransferPurpose.QUOTE_CURRENCY_FORWARD);
		quoteTransfer.setStatus(Transfer.Status.KNOWN);
		quoteTransfer.setTrade(trade);
		quoteTransfer.setBook(trade.getBook());
		quoteTransfer.setAmount(trade.getAmountTwoForward());
		quoteTransfer.setCurrency(trade.getCurrency());

		if (trade.isBuy()) {
			quoteTransfer.setDirection(Transfer.Direction.RECEIVE);
		} else {
			quoteTransfer.setDirection(Transfer.Direction.PAY);
		}

		return quoteTransfer;
	}

	@Override
	public void fixCashTransfer(CashTransfer transfer, long quoteSetId) throws TradistaBusinessException {
	}

}