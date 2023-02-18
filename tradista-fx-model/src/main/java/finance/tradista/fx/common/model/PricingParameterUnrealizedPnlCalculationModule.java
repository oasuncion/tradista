package finance.tradista.fx.common.model;

import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
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

public class PricingParameterUnrealizedPnlCalculationModule extends PricingParameterModule {

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

	public static class BookProductTypePair extends TradistaObject {

		private static final long serialVersionUID = -3597503317831313942L;

		@Id
		private Book book;

		@Id
		private String ProductType;

		public BookProductTypePair(Book book, String productType) {
			super();
			this.book = book;
			this.ProductType = productType;
		}

		public Book getBook() {
			return TradistaModelUtil.clone(book);
		}

		public String getProductType() {
			return ProductType;
		}

		@Override
		public BookProductTypePair clone() {
			BookProductTypePair bookProductTypePair = (BookProductTypePair) super.clone();
			bookProductTypePair.book = TradistaModelUtil.clone(book);
			return bookProductTypePair;
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
		if (unrealizedPnlCalculations == null) {
			return null;
		}
		return new HashMap<>(unrealizedPnlCalculations);
	}

	public void setUnrealizedPnlCalculations(
			Map<BookProductTypePair, UnrealizedPnlCalculation> unrealizedPnlCalculations) {
		this.unrealizedPnlCalculations = unrealizedPnlCalculations;
	}

	@Override
	public PricingParameterUnrealizedPnlCalculationModule clone() {
		PricingParameterUnrealizedPnlCalculationModule pricingParameterUnrealizedPnlCalculationModule = (PricingParameterUnrealizedPnlCalculationModule) super.clone();
		if (unrealizedPnlCalculations != null) {
			pricingParameterUnrealizedPnlCalculationModule.unrealizedPnlCalculations = new HashMap<>(
					unrealizedPnlCalculations);
		}
		return pricingParameterUnrealizedPnlCalculationModule;
	}

}