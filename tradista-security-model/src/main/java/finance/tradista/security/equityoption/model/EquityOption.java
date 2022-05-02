package finance.tradista.security.equityoption.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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

	private String code;

	private Equity underlying;

	private BigDecimal strike;

	private LocalDate maturityDate;

	private OptionTrade.Type type;

	private EquityOptionContractSpecification equityOptionContractSpecification;

	public EquityOptionContractSpecification getEquityOptionContractSpecification() {
		return equityOptionContractSpecification;
	}

	public void setEquityOptionContractSpecification(
			EquityOptionContractSpecification equityOptionContractSpecification) {
		this.equityOptionContractSpecification = equityOptionContractSpecification;
	}

	public OptionTrade.Type getType() {
		return type;
	}

	public void setType(OptionTrade.Type type) {
		this.type = type;
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

	public void setCode(String code) {
		this.code = code;
	}

	public Equity getUnderlying() {
		return underlying;
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

	public void setStrike(BigDecimal strike) {
		this.strike = strike;
	}

	public LocalDate getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(LocalDate maturityDate) {
		this.maturityDate = maturityDate;
	}

	@Override
	public Exchange getExchange() {
		if (equityOptionContractSpecification != null) {
			return equityOptionContractSpecification.getExchange();
		}
		return null;
	}

	@Override
	public void setExchange(Exchange exchange) {
		// Impossible to set the exchange, it is defined in the equity option
		// specification.
	}

	@Override
	public String toString() {
		return code + " - " + getType() + " - " + getStrike() + " - " + getMaturityDate() + " - "
				+ getEquityOptionContractSpecification();
	}

	@Override
	public boolean equals(Object o) {
		EquityOption eqo = null;
		if (o == null) {
			return false;
		}
		if (!(o instanceof EquityOption)) {
			return false;
		}
		eqo = (EquityOption) o;
		if (eqo == this) {
			return true;
		}

		boolean sameCode;
		if (eqo.getCode() == null) {
			sameCode = (code == null);
		} else {
			sameCode = eqo.getCode().equals(code);
		}
		boolean sameType;
		if (eqo.getType() == null) {
			sameType = (type == null);
		} else {
			sameType = eqo.getType().equals(type);
		}
		boolean sameStrike;
		if (eqo.getStrike() == null) {
			sameStrike = (strike == null);
		} else {
			if (strike == null) {
				sameStrike = false;
			} else {
				sameStrike = eqo.getStrike().compareTo(strike) == 0;
			}
		}
		boolean sameSpecification;
		if (eqo.getEquityOptionContractSpecification() == null) {
			sameSpecification = (equityOptionContractSpecification == null);
		} else {
			sameSpecification = eqo.getEquityOptionContractSpecification().equals(equityOptionContractSpecification);
		}
		boolean sameMaturityDate;
		if (eqo.getMaturityDate() == null) {
			sameMaturityDate = (maturityDate == null);
		} else {
			sameMaturityDate = eqo.getMaturityDate().equals(maturityDate);
		}

		return sameCode && sameType && sameStrike && sameSpecification && sameMaturityDate;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}