package finance.tradista.security.equity.model;

import java.time.LocalDate;

import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.security.common.model.Security;

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

public class Equity extends Security {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4659470425942478262L;

	public static final String EQUITY = "Equity";

	public Equity() {
		super();
	}

	private long tradingSize;

	private long totalIssued;

	private boolean payDividend;

	private Currency dividendCurrency;

	private Tenor dividendFrequency;

	private LocalDate activeFrom;

	private LocalDate activeTo;

	public long getTradingSize() {
		return tradingSize;
	}

	public void setTradingSize(long tradingSize) {
		this.tradingSize = tradingSize;
	}

	public long getTotalIssued() {
		return totalIssued;
	}

	public void setTotalIssued(long totalIssued) {
		this.totalIssued = totalIssued;
	}

	public boolean isPayDividend() {
		return payDividend;
	}

	public void setPayDividend(boolean payDividend) {
		this.payDividend = payDividend;
	}

	public Currency getDividendCurrency() {
		return dividendCurrency;
	}

	public void setDividendCurrency(Currency dividendCurrency) {
		this.dividendCurrency = dividendCurrency;
	}

	public Tenor getDividendFrequency() {
		return dividendFrequency;
	}

	public void setDividendFrequency(Tenor dividendFrequency) {
		this.dividendFrequency = dividendFrequency;
	}

	public LocalDate getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(LocalDate activeFrom) {
		this.activeFrom = activeFrom;
	}

	public LocalDate getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(LocalDate activeTo) {
		this.activeTo = activeTo;
	}

	@Override
	public String getProductType() {
		return EQUITY;
	}

	public String toString() {
		return getIsin() + " - " + getExchange();
	}

	@Override
	public boolean equals(Object o) {
		Equity eq = null;
		if (o == null) {
			return false;
		}
		if (!(o instanceof Equity)) {
			return false;
		}
		eq = (Equity) o;
		if (eq == this) {
			return true;
		}
		return eq.getIsin().equals(getIsin())
				&& eq.getExchange().equals(getExchange());
	}

	@Override
	public int hashCode() {
		return (getIsin() + "-" + getExchange()).hashCode();
	}

}