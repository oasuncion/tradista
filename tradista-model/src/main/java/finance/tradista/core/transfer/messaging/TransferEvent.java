package finance.tradista.core.transfer.messaging;

import finance.tradista.core.common.messaging.Event;
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

public abstract class TransferEvent<X extends Transfer> implements Event {

    /**
    * 
    */
    private static final long serialVersionUID = -1640618458564189936L;

    private X transfer;

    private X oldTransfer;

    public X getTransfer() {
	return transfer;
    }

    public void setTransfer(X transfer) {
	this.transfer = transfer;
    }

    public X getOldTransfer() {
	return oldTransfer;
    }

    public void setOldTransfer(X transfer) {
	oldTransfer = transfer;
    }

}