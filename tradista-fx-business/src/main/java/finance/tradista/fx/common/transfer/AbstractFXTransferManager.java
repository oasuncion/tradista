package finance.tradista.fx.common.transfer;

import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.trade.messaging.TradeEvent;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferManager;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;

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

public class AbstractFXTransferManager<X extends TradeEvent<?>> implements TransferManager<TradeEvent<?>> {

	protected TransferBusinessDelegate transferBusinessDelegate;

	public AbstractFXTransferManager() {
		transferBusinessDelegate = new TransferBusinessDelegate();
	}

	@Override
	public void createTransfers(TradeEvent<?> message) throws TradistaBusinessException {

		// If the trade is not new, we cancel the former transfers.
		if (message.getTrade().getId() != 0) {
			List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeId(message.getTrade().getId());
			// if the transfers's list is null or empty, it is not normal, but the process
			// should
			// continue.
			if (transfers == null || transfers.isEmpty()) {
				// TODO logs + Errors viewable in the error report ?
			} else {
				for (Transfer transfer : transfers) {
					transfer.setStatus(Transfer.Status.CANCELED);
					transferBusinessDelegate.saveTransfer(transfer);
				}
			}
		}
	}

	@Override
	public void fixCashTransfer(CashTransfer transfer, long quoteSetId) {
	}

}