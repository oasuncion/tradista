package finance.tradista.core.transfer.model;

import java.math.BigDecimal;

import finance.tradista.core.currency.model.Currency;

/*
 * Copyright 2018 Olivier Asuncion
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

public class CashTransfer extends Transfer {



	/**
	 * 
	 */
	private static final long serialVersionUID = -7123636684384971261L;
	
	private Currency currency;

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public BigDecimal getAmount() {
		return quantityOrAmount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.quantityOrAmount = amount;
	}

	@Override
	public Type getType() {
		return Transfer.Type.CASH;
	}

}