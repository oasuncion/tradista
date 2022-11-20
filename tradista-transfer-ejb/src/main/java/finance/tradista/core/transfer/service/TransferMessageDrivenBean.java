package finance.tradista.core.transfer.service;

import jakarta.annotation.Resource;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.MessageDrivenContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.trade.messaging.TradeEvent;

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

@MessageDriven(name = "TransferMessageDriveBean")
public class TransferMessageDrivenBean implements MessageListener {

	@Resource
	private MessageDrivenContext context;

	@Override
	public void onMessage(Message msg) {

		ObjectMessage objectMessage = (ObjectMessage) msg;
		try {
			TradeEvent<?> tradeEvent = (TradeEvent<?>) objectMessage.getObject();
			TransferBusinessDelegate transferBusinessDelegate = new TransferBusinessDelegate();
			transferBusinessDelegate.createTransfers(tradeEvent);
		} catch (JMSException | TradistaBusinessException e) {
			e.printStackTrace();
			context.setRollbackOnly();
		}

	}

}