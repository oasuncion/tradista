package finance.tradista.security.equityoption.model;

import java.math.BigDecimal;

import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.daterule.model.DateRule;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.trade.model.OptionTrade.SettlementType;
import finance.tradista.core.trade.model.VanillaOptionTrade;

/*
 * Copyright 2017 Olivier Asuncion
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

public class EquityOptionContractSpecification extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8579292038343826059L;

	private DateRule maturityDatesDateRule;

	private String name;

	private SettlementType settlementType;

	private short settlementDateOffset;

	private VanillaOptionTrade.Style style;

	private BigDecimal quantity;

	private Exchange exchange;

	private BigDecimal multiplier;

	private Currency premiumCurrency;

	public DateRule getMaturityDatesDateRule() {
		return maturityDatesDateRule;
	}

	public void setMaturityDatesDateRule(DateRule maturityDatesDateRule) {
		this.maturityDatesDateRule = maturityDatesDateRule;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SettlementType getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(SettlementType settlementType) {
		this.settlementType = settlementType;
	}

	public short getSettlementDateOffset() {
		return settlementDateOffset;
	}

	public void setSettlementDateOffset(short settlementDateOffset) {
		this.settlementDateOffset = settlementDateOffset;
	}

	public VanillaOptionTrade.Style getStyle() {
		return style;
	}

	public void setStyle(VanillaOptionTrade.Style style) {
		this.style = style;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public BigDecimal getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(BigDecimal multiplier) {
		this.multiplier = multiplier;
	}

	public Currency getPremiumCurrency() {
		return premiumCurrency;
	}

	public void setPremiumCurrency(Currency premiumCurrency) {
		this.premiumCurrency = premiumCurrency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EquityOptionContractSpecification other = (EquityOptionContractSpecification) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}