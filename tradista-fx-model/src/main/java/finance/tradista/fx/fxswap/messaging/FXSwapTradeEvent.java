package finance.tradista.fx.fxswap.messaging;

import finance.tradista.core.trade.messaging.TradeEvent;
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

public class FXSwapTradeEvent implements TradeEvent<FXSwapTrade> {

	/**
	* 
	*/
	private static final long serialVersionUID = 2616017667907799567L;

	private FXSwapTrade trade;

	private FXSwapTrade oldTrade;

	@Override
	public FXSwapTrade getTrade() {
		return trade;
	}

	@Override
	public void setTrade(FXSwapTrade trade) {
		this.trade = trade;
	}

	@Override
	public FXSwapTrade getOldTrade() {
		return oldTrade;
	}

	@Override
	public void setOldTrade(FXSwapTrade oldTrade) {
		this.oldTrade = oldTrade;
	}

}