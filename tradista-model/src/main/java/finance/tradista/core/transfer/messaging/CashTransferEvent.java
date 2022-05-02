package finance.tradista.core.transfer.messaging;

import finance.tradista.core.transfer.model.CashTransfer;

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

public class CashTransferEvent implements TransferEvent<CashTransfer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8806725404338297390L;

	private CashTransfer transfer;

	private CashTransfer oldTransfer;

	@Override
	public CashTransfer getTransfer() {
		return transfer;
	}

	@Override
	public void setTransfer(CashTransfer transfer) {
		this.transfer = transfer;
	}

	@Override
	public CashTransfer getOldTransfer() {
		return oldTransfer;
	}

	@Override
	public void setOldTransfer(CashTransfer oldTransfer) {
		this.oldTransfer = oldTransfer;
	}

}