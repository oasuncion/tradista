package finance.tradista.security.equityoption.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.OptionTrade;
import finance.tradista.core.trade.model.VanillaOptionTrade;
import finance.tradista.security.equity.model.EquityTrade;

/*
 * Copyright 2015 Olivier Asuncion
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

public class EquityOptionTrade extends VanillaOptionTrade<EquityTrade> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5389991593803505087L;

	private BigDecimal quantity;

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public EquityOption getEquityOption() {
		return (EquityOption) super.getProduct();
	}

	public void setEquityOption(EquityOption product) {
		super.setProduct(product);
	}

	@Override
	public final void setProduct(Product product) {
		throw new UnsupportedOperationException("Please use setEquityOption instead.");
	}

	@Override
	public String getProductType() {
		return EquityOption.EQUITY_OPTION;
	}

	@Override
	public void setType(OptionTrade.Type type) {
		if (getEquityOption() == null) {
			super.setType(type);
		}
	}

	@Override
	public void setStrike(BigDecimal strike) {
		if (getEquityOption() == null) {
			super.setStrike(strike);
		}
	}

	@Override
	public void setMaturityDate(LocalDate maturityDate) {
		if (getEquityOption() == null) {
			super.setMaturityDate(maturityDate);
		}
	}

	@Override
	public OptionTrade.Type getType() {
		if (getEquityOption() != null) {
			return getEquityOption().getType();
		}
		return super.getType();
	}

	@Override
	public LocalDate getMaturityDate() {
		if (getEquityOption() != null) {
			return getEquityOption().getMaturityDate();
		}
		return super.getMaturityDate();
	}

	@Override
	public BigDecimal getStrike() {
		if (getEquityOption() != null) {
			return getEquityOption().getStrike();
		}
		return super.getStrike();
	}

	@Override
	public boolean isCall() {
		if (getEquityOption() != null) {
			return getEquityOption().getType().equals(OptionTrade.Type.CALL);
		}
		return super.isCall();
	}

}