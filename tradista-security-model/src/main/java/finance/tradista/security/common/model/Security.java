package finance.tradista.security.common.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.product.model.Product;

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

public abstract class Security extends Product {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2413499120965894034L;

	@Id
	private String isin;
	private LegalEntity issuer;
	private LocalDate issueDate;
	private BigDecimal issuePrice;
	private Currency currency;

	public Security(Exchange exchange, String isin) {
		super(exchange);
		this.isin = isin;
	}

	public String getIsin() {
		return isin;
	}

	public LegalEntity getIssuer() {
		return TradistaModelUtil.clone(issuer);
	}

	public void setIssuer(LegalEntity issuer) {
		this.issuer = issuer;
	}

	public long getIssuerId() {
		if (issuer != null) {
			return issuer.getId();
		}
		return 0;
	}

	public Currency getCurrency() {
		return TradistaModelUtil.clone(currency);
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public long getCurrencyId() {
		if (currency != null) {
			return currency.getId();
		} else {
			return 0;
		}
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public BigDecimal getIssuePrice() {
		return issuePrice;
	}

	public void setIssuePrice(BigDecimal issuePrice) {
		this.issuePrice = issuePrice;
	}

	public String toString() {
		return getIsin() + " - " + getExchange();
	}

	@Override
	public Security clone() {
		Security security = (Security) super.clone();
		security.issuer = TradistaModelUtil.clone(issuer);
		security.currency = TradistaModelUtil.clone(currency);
		return security;
	}
}