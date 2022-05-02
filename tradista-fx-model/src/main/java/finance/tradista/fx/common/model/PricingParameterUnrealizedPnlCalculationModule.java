package finance.tradista.fx.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.pricing.pricer.PricingParameterModule;

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

public class PricingParameterUnrealizedPnlCalculationModule implements PricingParameterModule {

	private static final long serialVersionUID = -1081293712767780488L;

	public static enum UnrealizedPnlCalculation {
		MARK_TO_MARKET, MARK_TO_MODEL;
		public String toString() {
			switch (this) {
			case MARK_TO_MARKET:
				return "MarkToMarket";
			case MARK_TO_MODEL:
				return "MarkToModel";
			}
			return super.toString();
		}
	}

	private Map<BookProductTypePair, UnrealizedPnlCalculation> unrealizedPnlCalculations;

	public PricingParameterUnrealizedPnlCalculationModule() {
		unrealizedPnlCalculations = new HashMap<BookProductTypePair, UnrealizedPnlCalculation>();
	}

	public static class BookProductTypePair implements Serializable {

		private static final long serialVersionUID = -3597503317831313942L;

		private Book book;
		private String ProductType;

		public BookProductTypePair(Book book, String productType) {
			super();
			this.book = book;
			this.ProductType = productType;
		}

		public Book getBook() {
			return book;
		}

		public void setBook(Book book) {
			this.book = book;
		}

		public String getProductType() {
			return ProductType;
		}

		public void setProductType(String productType) {
			ProductType = productType;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ProductType == null) ? 0 : ProductType.hashCode());
			result = prime * result + ((book == null) ? 0 : book.hashCode());
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
			BookProductTypePair other = (BookProductTypePair) obj;
			if (ProductType == null) {
				if (other.ProductType != null)
					return false;
			} else if (!ProductType.equals(other.ProductType))
				return false;
			if (book == null) {
				if (other.book != null)
					return false;
			} else if (!book.equals(other.book))
				return false;
			return true;
		}

	}

	@Override
	public String getProductFamily() {
		return "fx";
	}

	@Override
	public String getProductType() {
		return null;
	}

	public Map<BookProductTypePair, UnrealizedPnlCalculation> getUnrealizedPnlCalculations() {
		return unrealizedPnlCalculations;
	}

	public void setUnrealizedPnlCalculations(
			Map<BookProductTypePair, UnrealizedPnlCalculation> unrealizedPnlCalculations) {
		this.unrealizedPnlCalculations = unrealizedPnlCalculations;
	}

}