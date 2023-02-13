package finance.tradista.core.currency.model;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;

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

public class CurrencyPair extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1429614201466990666L;

	@Id
	private Currency primaryCurrency;

	@Id
	private Currency quoteCurrency;

	public CurrencyPair(Currency primaryCurrency, Currency quoteCurrency) {
		this.primaryCurrency = primaryCurrency;
		this.quoteCurrency = quoteCurrency;
	}

	public Currency getPrimaryCurrency() {
		return TradistaModelUtil.clone(primaryCurrency);
	}

	public Currency getQuoteCurrency() {
		return TradistaModelUtil.clone(quoteCurrency);
	}

	@Override
	public CurrencyPair clone() {
		CurrencyPair currencyPair = (CurrencyPair) super.clone();
		currencyPair.primaryCurrency = TradistaModelUtil.clone(primaryCurrency);
		currencyPair.quoteCurrency = TradistaModelUtil.clone(quoteCurrency);
		return currencyPair;
	}

}