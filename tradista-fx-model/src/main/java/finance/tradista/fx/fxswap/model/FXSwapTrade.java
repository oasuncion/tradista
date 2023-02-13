package finance.tradista.fx.fxswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.product.model.Product;
import finance.tradista.fx.common.model.AbstractFXTrade;

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
 * Class representing FX Swaps.
 * 
 * @author Tradista
 * 
 *         Please note that : - getAmount() gets the quote amount of the spot
 *         leg - getCurrency() gets the quote currency
 *
 * @param <P>
 */
public class FXSwapTrade extends AbstractFXTrade<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6321516419712885556L;

	private Currency currencyOne;

	private LocalDate settlementDateForward;

	private BigDecimal amountOneForward;

	private BigDecimal amountOneSpot;

	private BigDecimal amountTwoForward;

	public static final String FX_SWAP = "FXSwap";

	public Currency getCurrencyOne() {
		return TradistaModelUtil.clone(currencyOne);
	}

	public void setCurrencyOne(Currency currencyOne) {
		this.currencyOne = currencyOne;
	}

	public LocalDate getSettlementDateForward() {
		return settlementDateForward;
	}

	public void setSettlementDateForward(LocalDate settlementDateForward) {
		this.settlementDateForward = settlementDateForward;
	}

	public BigDecimal getAmountOneForward() {
		return amountOneForward;
	}

	public void setAmountOneForward(BigDecimal amountOneForward) {
		this.amountOneForward = amountOneForward;
	}

	public BigDecimal getAmountOneSpot() {
		return amountOneSpot;
	}

	public void setAmountOneSpot(BigDecimal amountOneSpot) {
		this.amountOneSpot = amountOneSpot;
	}

	public BigDecimal getAmountTwoForward() {
		return amountTwoForward;
	}

	public void setAmountTwoForward(BigDecimal amountTwoForward) {
		this.amountTwoForward = amountTwoForward;
	}

	public String getProductType() {
		return FX_SWAP;
	}

	@Override
	public FXSwapTrade clone() {
		FXSwapTrade fxSwapTrade = (FXSwapTrade) super.clone();
		fxSwapTrade.currencyOne = TradistaModelUtil.clone(currencyOne);
		return fxSwapTrade;
	}

}