package finance.tradista.core.marketdata.model;

import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.legalentity.model.LegalEntity;

/*
 * Copyright 2016 Olivier Asuncion
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

public class FXCurve extends GenerableCurve {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6365357379109869222L;

	public static final String FX_CURVE = "FXCurve";

	private Currency primaryCurrency;

	private Currency quoteCurrency;

	private InterestRateCurve primaryCurrencyIRCurve;

	private InterestRateCurve quoteCurrencyIRCurve;

	public FXCurve() {
		super();
	}

	public FXCurve(String name, LegalEntity po) {
		super(name, po);
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

	public InterestRateCurve getPrimaryCurrencyIRCurve() {
		return primaryCurrencyIRCurve;
	}

	public void setPrimaryCurrencyIRCurve(InterestRateCurve primaryCurrencyIRCurve) {
		this.primaryCurrencyIRCurve = primaryCurrencyIRCurve;
	}

	public InterestRateCurve getQuoteCurrencyIRCurve() {
		return quoteCurrencyIRCurve;
	}

	public void setQuoteCurrencyIRCurve(InterestRateCurve quoteCurrencyIRCurve) {
		this.quoteCurrencyIRCurve = quoteCurrencyIRCurve;
	}

	public String toString() {
		return this.getName();
	}

}