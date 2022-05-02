package finance.tradista.fx.fx.model;

import java.math.BigDecimal;

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

public class FXTrade extends AbstractFXTrade<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8313585047177367366L;

	public static final String FX = "FX";

	public enum Type {
		FX_SPOT, FX_FORWARD;
		public String toString() {
			switch (this) {
			case FX_SPOT:
				return "FX Spot";
			case FX_FORWARD:
				return "FX Forward";
			}
			return super.toString();
		}
	};

	private Currency currencyOne;

	private BigDecimal amountOne;

	private Type type;

	public void setType(Type type) {
		this.type = type;
	}

	public Currency getCurrencyOne() {
		return currencyOne;
	}

	public void setCurrencyOne(Currency currencyOne) {
		this.currencyOne = currencyOne;
	}

	public BigDecimal getAmountOne() {
		return amountOne;
	}

	public void setAmountOne(BigDecimal amountOne) {
		this.amountOne = amountOne;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String getProductType() {
		if (getTradeDate() == null) {
			return "Option underlying";
		}
		if (getType() == null) {
			// Ensure to determnine type using FXTradeBusinessDelegate.determinateType
			// before calling getProductType()
			return "Unspecified FX trade";
		}
		switch (getType()) {
		case FX_SPOT:
			return "FXSpot";

		case FX_FORWARD:
			return "FXForward";
		}

		return "Unspecified FX trade";
	}

}