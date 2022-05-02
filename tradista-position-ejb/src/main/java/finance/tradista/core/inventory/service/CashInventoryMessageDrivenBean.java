package finance.tradista.core.inventory.service;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import finance.tradista.core.cashinventory.service.CashInventoryService;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.transfer.messaging.CashTransferEvent;
import finance.tradista.core.transfer.model.Transfer;

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

@MessageDriven(name = "CashInventoryMessageDriveBean")
public class CashInventoryMessageDrivenBean implements MessageListener {

	@Resource
	private MessageDrivenContext context;

	@EJB
	private CashInventoryService cashInventoryService;

	@Override
	public void onMessage(Message msg) {

		ObjectMessage objectMessage = (ObjectMessage) msg;
		try {
			CashTransferEvent event = (CashTransferEvent) objectMessage.getObject();

			// Important to have a class level lock here, otherwise deadlock can occur.
			synchronized (this.getClass()) {
				// If there was already a transfer, first we erase the trace of this
				// old transfer in
				// the inventory
				// Note: old transfer is not null only when it was KNOWN
				if (event.getOldTransfer() != null) {
					if (event.getOldTransfer().getDirection().equals(Transfer.Direction.RECEIVE)) {
						event.getOldTransfer().setAmount(event.getOldTransfer().getAmount().negate());
					}
					cashInventoryService.updateCashInventory(event.getOldTransfer());
				}

				// Note: transfer is not null only when it is KNOWN
				if (event.getTransfer() != null) {
					if (event.getTransfer().getDirection().equals(Transfer.Direction.PAY)) {
						event.getTransfer().setAmount(event.getTransfer().getAmount().negate());
					}
					cashInventoryService.updateCashInventory(event.getTransfer());
				}
			}

		} catch (JMSException | TradistaBusinessException e) {
			e.printStackTrace();
			context.setRollbackOnly();
		}

	}

}