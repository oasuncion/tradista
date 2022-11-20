package finance.tradista.core.inventory.service;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.MessageDrivenContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.productinventory.service.ProductInventoryService;
import finance.tradista.core.transfer.messaging.ProductTransferEvent;
import finance.tradista.core.transfer.model.Transfer;

/*
 * Copyright 2016 Olivier Asuncion
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

@MessageDriven(name = "ProductInventoryMessageDriveBean")
public class ProductInventoryMessageDrivenBean implements MessageListener {

	@Resource
	private MessageDrivenContext context;

	@EJB
	private ProductInventoryService productInventoryService;

	@Override
	public void onMessage(Message msg) {

		ObjectMessage objectMessage = (ObjectMessage) msg;
		try {
			ProductTransferEvent event = (ProductTransferEvent) objectMessage.getObject();

			// Important to have a class level lock here, otherwise deadlock can occur.
			synchronized (this.getClass()) {
				// If there was already a transfer, first we erase the trace of this
				// old transfer in the inventory
				// Note: old transfer is not null only when it was KNOWN
				if (event.getOldTransfer() != null) {
					if (event.getOldTransfer().getDirection().equals(Transfer.Direction.RECEIVE)) {
						event.getOldTransfer().setQuantity(event.getOldTransfer().getQuantity().negate());
					}
					productInventoryService.updateProductInventory(event.getOldTransfer());
				}

				// Note: transfer is not null only when it is KNOWN
				if (event.getTransfer() != null) {
					if (event.getTransfer().getDirection().equals(Transfer.Direction.PAY)) {
						event.getTransfer().setQuantity(event.getTransfer().getQuantity().negate());
					}
					productInventoryService.updateProductInventory(event.getTransfer());
				}
			}

		} catch (JMSException | TradistaBusinessException e) {
			e.printStackTrace();
			context.setRollbackOnly();
		}

	}

}