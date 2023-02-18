package finance.tradista.ir.ccyswap.model;

import java.math.BigDecimal;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.ir.irswap.model.IRSwapTrade;

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
/**
 * Class to represent Cross-Currency swaps. Note : notionalAmountTwo : the
 * notional received at the starting of the trade, and paid at the maturity of
 * the trade currencyTwo : the currency of the fixed leg (to pay)
 * 
 * @author OA
 *
 * @param <P>
 */
public class CcySwapTrade extends IRSwapTrade {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6188291608649255466L;

	public static final String CCY_SWAP = "CcySwap";

	private Currency currencyTwo;

	private BigDecimal notionalAmountTwo;

	public Currency getCurrencyTwo() {
		return TradistaModelUtil.clone(currencyTwo);
	}

	public void setCurrencyTwo(Currency currencyTwo) {
		this.currencyTwo = currencyTwo;
	}

	public BigDecimal getNotionalAmountTwo() {
		return notionalAmountTwo;
	}

	public void setNotionalAmountTwo(BigDecimal notionalAmountTwo) {
		this.notionalAmountTwo = notionalAmountTwo;
	}

	public String getProductType() {
		return CCY_SWAP;
	}

	@Override
	public CcySwapTrade clone() {
		CcySwapTrade ccySwapTrade = (CcySwapTrade) super.clone();
		ccySwapTrade.currencyTwo = TradistaModelUtil.clone(currencyTwo);
		return ccySwapTrade;
	}

}