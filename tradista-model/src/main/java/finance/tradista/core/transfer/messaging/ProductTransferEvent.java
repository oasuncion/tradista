package finance.tradista.core.transfer.messaging;

import finance.tradista.core.transfer.model.ProductTransfer;

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

public class ProductTransferEvent implements TransferEvent<ProductTransfer> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4979187544631866379L;

	private ProductTransfer transfer;

	private ProductTransfer oldTransfer;

	@Override
	public ProductTransfer getTransfer() {
		return transfer;
	}

	@Override
	public void setTransfer(ProductTransfer transfer) {
		this.transfer = transfer;
	}

	@Override
	public ProductTransfer getOldTransfer() {
		return oldTransfer;
	}

	@Override
	public void setOldTransfer(ProductTransfer oldTransfer) {
		this.oldTransfer = oldTransfer;
	}

}