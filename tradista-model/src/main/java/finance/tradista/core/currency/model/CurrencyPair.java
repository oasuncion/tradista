package finance.tradista.core.currency.model;

import java.io.Serializable;

/*
 * Copyright 2019 Olivier Asuncion
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

public class CurrencyPair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1429614201466990666L;

	private Currency primaryCurrency;

	private Currency quoteCurrency;

	public CurrencyPair(Currency primaryCurrency, Currency quoteCurrency) {
		this.primaryCurrency = primaryCurrency;
		this.quoteCurrency = quoteCurrency;
	}

	public Currency getPrimaryCurrency() {
		return primaryCurrency;
	}

	public void setPrimaryCurrency(Currency primaryCurrency) {
		this.primaryCurrency = primaryCurrency;
	}

	public Currency getQuoteCurrency() {
		return quoteCurrency;
	}

	public void setQuoteCurrency(Currency quoteCurrency) {
		this.quoteCurrency = quoteCurrency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((primaryCurrency == null) ? 0 : primaryCurrency.hashCode());
		result = prime * result + ((quoteCurrency == null) ? 0 : quoteCurrency.hashCode());
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
		CurrencyPair other = (CurrencyPair) obj;
		if (primaryCurrency == null) {
			if (other.primaryCurrency != null)
				return false;
		} else if (!primaryCurrency.equals(other.primaryCurrency))
			return false;
		if (quoteCurrency == null) {
			if (other.quoteCurrency != null)
				return false;
		} else if (!quoteCurrency.equals(other.quoteCurrency))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CurrencyPair [primaryCurrency=" + primaryCurrency + ", quoteCurrency=" + quoteCurrency + "]";
	}

}