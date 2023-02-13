package finance.tradista.security.equityoption.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.OptionTrade;
import finance.tradista.core.trade.model.OptionTrade.SettlementType;
import finance.tradista.core.trade.model.VanillaOptionTrade;
import finance.tradista.security.equity.model.Equity;

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
/**
 * Class representing Listed Equity Options. Example of listed equity option:
 * http://www.cboe.com/products/equityoptionspecs.aspx
 * 
 * Note: Identifier is code + type + strike because: In CBOE, a symbol
 * identifies an equity option. In Euronext, a symbol can be either a call or a
 * put, and with different strikes.
 * 
 * @author OA
 *
 */
public class EquityOption extends Product {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2072515902166014837L;

	public static final String EQUITY_OPTION = "EquityOption";

	@Id
	private String code;

	private Equity underlying;

	@Id
	private BigDecimal strike;

	@Id
	private LocalDate maturityDate;

	@Id
	private OptionTrade.Type type;

	@Id
	private EquityOptionContractSpecification equityOptionContractSpecification;

	public EquityOption(String code, OptionTrade.Type type, BigDecimal strike, LocalDate maturityDate,
			EquityOptionContractSpecification equityOptionContractSpecification) {
		super(equityOptionContractSpecification != null ? equityOptionContractSpecification.getExchange() : null);
		this.code = code;
		this.type = type;
		this.strike = strike;
		this.maturityDate = maturityDate;
		this.equityOptionContractSpecification = equityOptionContractSpecification;
	}

	public EquityOptionContractSpecification getEquityOptionContractSpecification() {
		return TradistaModelUtil.clone(equityOptionContractSpecification);
	}

	public OptionTrade.Type getType() {
		return type;
	}

	public SettlementType getSettlementType() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getSettlementType();
		}
		return null;
	}

	public int getSettlementDateOffset() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getSettlementDateOffset();
		}
		return 0;
	}

	public String getCode() {
		return code;
	}

	public Equity getUnderlying() {
		return TradistaModelUtil.clone(underlying);
	}

	public void setUnderlying(Equity underlying) {
		this.underlying = underlying;
	}

	public BigDecimal getQuantity() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getQuantity();
		}
		return null;
	}

	public VanillaOptionTrade.Style getStyle() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getStyle();
		}
		return null;
	}

	public Currency getPremiumCurrency() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getPremiumCurrency();
		}
		return null;
	}

	public BigDecimal getMultiplier() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getMultiplier();
		}
		return null;
	}

	@Override
	public String getProductType() {
		return EQUITY_OPTION;
	}

	public BigDecimal getStrike() {
		return strike;
	}

	public LocalDate getMaturityDate() {
		return maturityDate;
	}

	@Override
	public Exchange getExchange() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getExchange();
		}
		return null;
	}

	@Override
	public EquityOption clone() {
		EquityOption equityOption = (EquityOption) super.clone();
		equityOption.equityOptionContractSpecification = TradistaModelUtil.clone(equityOptionContractSpecification);
		equityOption.underlying = TradistaModelUtil.clone(underlying);
		return equityOption;
	}

}