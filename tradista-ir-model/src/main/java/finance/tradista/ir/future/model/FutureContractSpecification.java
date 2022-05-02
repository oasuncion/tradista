package finance.tradista.ir.future.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.daterule.model.DateRule;
import finance.tradista.core.daycountconvention.model.DayCountConvention;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.tenor.model.Tenor;

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

public class FutureContractSpecification extends TradistaObject implements Comparable<FutureContractSpecification> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5750691520644097759L;

	private Index referenceRateIndex;
	private Tenor referenceRateIndexTenor;
	private BigDecimal notional;
	private Currency currency;
	private DayCountConvention dayCountConvention;
	private String name;
	private Exchange exchange;
	private DateRule maturityDatesDateRule;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Index getReferenceRateIndex() {
		return referenceRateIndex;
	}

	public void setReferenceRateIndex(Index referenceRateIndex) {
		this.referenceRateIndex = referenceRateIndex;
	}

	public BigDecimal getNotional() {
		return notional;
	}

	public void setNotional(BigDecimal notional) {
		this.notional = notional;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public DayCountConvention getDayCountConvention() {
		return dayCountConvention;
	}

	public void setDayCountConvention(DayCountConvention dayCountConvention) {
		this.dayCountConvention = dayCountConvention;
	}

	public Tenor getReferenceRateIndexTenor() {
		return referenceRateIndexTenor;
	}

	public void setReferenceRateIndexTenor(Tenor referenceRateIndexTenor) {
		this.referenceRateIndexTenor = referenceRateIndexTenor;
	}

	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public BigDecimal getPriceVariationByBasisPoint() {
		if (referenceRateIndexTenor.equals(Tenor.THREE_MONTHS)) {
			return notional.divide(BigDecimal.valueOf(4), RoundingMode.HALF_EVEN).divide(BigDecimal.valueOf(10000),
					RoundingMode.HALF_EVEN);
		}
		return null;
	}

	public DateRule getMaturityDatesDateRule() {
		return maturityDatesDateRule;
	}

	public void setMaturityDatesDateRule(DateRule maturityDatesDateRule) {
		this.maturityDatesDateRule = maturityDatesDateRule;
	}

	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		FutureContractSpecification spec = null;
		if (o == null) {
			return false;
		}
		if (!(o instanceof FutureContractSpecification)) {
			return false;
		}
		spec = (FutureContractSpecification) o;
		if (spec == this) {
			return true;
		}
		return spec.getName().equals(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(FutureContractSpecification fcs) {
		return name.compareTo(fcs.getName());
	}

}