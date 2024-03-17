package finance.tradista.core.trade.messaging;

import finance.tradista.core.common.messaging.Event;
import finance.tradista.core.trade.model.Trade;

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

public abstract class TradeEvent<X extends Trade<?>> implements Event {

    /**
     * 
     */
    private static final long serialVersionUID = -5889419872185649873L;

    private String appliedAction;

    private X trade;

    private X oldTrade;

    public X getTrade() {
	return trade;
    }

    public void setTrade(X trade) {
	this.trade = trade;
    }

    public X getOldTrade() {
	return oldTrade;
    }

    public void setOldTrade(X oldTrade) {
	this.oldTrade = oldTrade;
    }

    /**
     * Return the last action applied to the object.
     * 
     * @return the last action applied to the object.
     */
    public String getAppliedAction() {
	return appliedAction;
    }

    public void setAppliedAction(String action) {
	appliedAction = action;
    }

}