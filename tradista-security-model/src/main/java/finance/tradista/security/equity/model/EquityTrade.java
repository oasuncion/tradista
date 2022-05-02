package finance.tradista.security.equity.model;

import java.math.BigDecimal;

import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.trade.model.Trade;

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
 * Class representing a trade on Equity.
 * Amount represents the unit price of a equity for this deal.
 *  *
 * @param <B> the traded equity.
 */
public class EquityTrade extends Trade<Equity> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 834419757097799136L;
	
	private BigDecimal quantity;
	
	public EquityTrade(Equity product) {
		super(product);
		// TODO Auto-generated constructor stub
	}

	public EquityTrade() {
		super();
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Currency getCurrency() {
		if (getProduct() != null) {
			return ((Equity)getProduct()).getCurrency();
		}
		return null;
	}

}
