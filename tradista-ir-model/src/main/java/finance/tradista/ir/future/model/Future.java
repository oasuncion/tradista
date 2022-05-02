package finance.tradista.ir.future.model;

import java.time.LocalDate;

import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.daycountconvention.model.DayCountConvention;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.tenor.model.Tenor;

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

public class Future extends Product {

	/**
	 * 
	 */
	private static final long serialVersionUID = 328506381431797478L;

	private String symbol;

	private FutureContractSpecification contractSpecification;

	private LocalDate maturityDate;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public FutureContractSpecification getContractSpecification() {
		return contractSpecification;
	}

	public void setContractSpecification(FutureContractSpecification contractSpecification) {
		this.contractSpecification = contractSpecification;
	}

	public LocalDate getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(LocalDate maturityDate) {
		this.maturityDate = maturityDate;
	}

	public static final String FUTURE = "Future";

	@Override
	public String getProductType() {
		return FUTURE;
	}

	@Override
	public String toString() {
		return symbol + " - " + contractSpecification;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contractSpecification == null) ? 0 : contractSpecification.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		Future other = (Future) obj;
		if (contractSpecification == null) {
			if (other.contractSpecification != null)
				return false;
		} else if (!contractSpecification.equals(other.contractSpecification))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	public DayCountConvention getDayCountConvention() {
		if (contractSpecification != null) {
			return contractSpecification.getDayCountConvention();
		}
		return null;
	}

	public Index getReferenceRateIndex() {
		if (contractSpecification != null) {
			return contractSpecification.getReferenceRateIndex();
		}
		return null;
	}

	public Tenor getReferenceRateIndexTenor() {
		if (contractSpecification != null) {
			return contractSpecification.getReferenceRateIndexTenor();
		}
		return null;
	}

	public Currency getCurrency() {
		if (contractSpecification != null) {
			return contractSpecification.getCurrency();
		}
		return null;
	}

	public Exchange getExchange() {
		if (contractSpecification != null) {
			return contractSpecification.getExchange();
		}
		return null;
	}

	public void setExchange(Exchange exchange) {
		// Forbidden to set the exchange. The exchange is defined by the
		// contract specification.
	}

}