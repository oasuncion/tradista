package finance.tradista.security.gcrepo.messaging;

import finance.tradista.core.trade.messaging.TradeEvent;
import finance.tradista.security.gcrepo.model.GCRepoTrade;

/*
 * Copyright 2023 Olivier Asuncion
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

public class GCRepoTradeEvent implements TradeEvent<GCRepoTrade> {




	/**
	 * 
	 */
	private static final long serialVersionUID = -7483119942010467086L;

	private GCRepoTrade trade;

	private GCRepoTrade oldTrade;

	@Override
	public GCRepoTrade getTrade() {
		return trade;
	}

	@Override
	public void setTrade(GCRepoTrade trade) {
		this.trade = trade;
	}

	@Override
	public GCRepoTrade getOldTrade() {
		return oldTrade;
	}

	@Override
	public void setOldTrade(GCRepoTrade oldTrade) {
		this.oldTrade = oldTrade;
	}

}