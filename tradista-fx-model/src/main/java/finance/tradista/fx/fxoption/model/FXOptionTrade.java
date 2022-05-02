package finance.tradista.fx.fxoption.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.trade.model.VanillaOptionTrade;
import finance.tradista.fx.fx.model.FXTrade;

/*
 * Copyright 2014 Olivier Asuncion
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

public class FXOptionTrade extends VanillaOptionTrade<FXTrade> {

	public static final String FX_OPTION = "FXOption";
	/**
	 * 
	 */
	private static final long serialVersionUID = 8952352869490542697L;

	/**
	 * For FX Options, the strike is the following rate : Quote Amount / Primary
	 * Amount (the primary amount being the one to be bought).
	 */
	@Override
	public BigDecimal getStrike() {
		FXTrade underlying = getUnderlying();
		if (underlying != null && underlying.getAmount() != null && underlying.getAmountOne() != null) {
			return underlying.getAmount().divide(underlying.getAmountOne(), RoundingMode.HALF_EVEN);
		} else {
			return null;
		}
	}

	public Exchange getExchange() {
		// Exchange is the one of FX
		return new FXTrade().getExchange();
	}

	@Override
	public String getProductType() {
		return FX_OPTION;
	}

}