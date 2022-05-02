package finance.tradista.ai.agent.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.currency.model.Currency;

/*
 * Copyright 2017 Olivier Asuncion
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

public class Mandate extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3997175280523902567L;

	public static enum RiskLevel {
		VERY_LOW, LOW, AVERAGE, HIGH, VERY_HIGH;
		public String toString() {
			switch (this) {
			case VERY_LOW:
				return "Very Low";
			case LOW:
				return "Low";
			case AVERAGE:
				return "Average";
			case HIGH:
				return "High";
			case VERY_HIGH:
				return "Very High";
			}
			return super.toString();
		}
	};

	private RiskLevel acceptedRiskLevel;

	private LocalDateTime creationDateTime;

	private LocalDate startDate;

	private LocalDate endDate;

	private String name;

	private Map<String, Allocation> productTypeAllocations;

	private Map<String, Allocation> currencyAllocations;

	private BigDecimal initialCashAmount;

	private Currency initialCashCurrency;

	private Book book;

	public class Allocation implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8335133920081352601L;

		private short minAllocation;

		private short maxAllocation;

		public short getMinAllocation() {
			return minAllocation;
		}

		public void setMinAllocation(short minAllocation) {
			this.minAllocation = minAllocation;
		}

		public short getMaxAllocation() {
			return maxAllocation;
		}

		public void setMaxAllocation(short maxAllocation) {
			this.maxAllocation = maxAllocation;
		}
	}

	public RiskLevel getAcceptedRiskLevel() {
		return acceptedRiskLevel;
	}

	public void setAcceptedRiskLevel(RiskLevel acceptedRiskLevel) {
		this.acceptedRiskLevel = acceptedRiskLevel;
	}

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(LocalDateTime creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Allocation> getProductTypeAllocations() {
		return productTypeAllocations;
	}

	public void setProductTypeAllocations(Map<String, Allocation> productTypeAllocations) {
		this.productTypeAllocations = productTypeAllocations;
	}

	public Map<String, Allocation> getCurrencyAllocations() {
		return currencyAllocations;
	}

	public void setCurrencyAllocations(Map<String, Allocation> currencyAllocations) {
		this.currencyAllocations = currencyAllocations;
	}

	public BigDecimal getInitialCashAmount() {
		return initialCashAmount;
	}

	public void setInitialCashAmount(BigDecimal initialCashAmount) {
		this.initialCashAmount = initialCashAmount;
	}

	public Currency getInitialCashCurrency() {
		return initialCashCurrency;
	}

	public void setInitialCashCurrency(Currency initialCashCurrency) {
		this.initialCashCurrency = initialCashCurrency;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Mandate other = (Mandate) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}