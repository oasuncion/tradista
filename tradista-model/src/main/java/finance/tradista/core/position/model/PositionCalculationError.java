package finance.tradista.core.position.model;

import java.time.LocalDate;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.error.model.Error;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;

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

public class PositionCalculationError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6672980501663576889L;

	private PositionDefinition positionDefinition;

	private LocalDate valueDate;

	private Trade<? extends Product> trade;

	private Product product;

	public static final String POSITION_CALCULATION = "PositionCalculation";

	public PositionCalculationError() {
		setType(POSITION_CALCULATION);
	}

	public PositionDefinition getPositionDefinition() {
		return TradistaModelUtil.clone(positionDefinition);
	}

	public void setPositionDefinition(PositionDefinition positionDefinition) {
		this.positionDefinition = positionDefinition;
	}

	public LocalDate getValueDate() {
		return valueDate;
	}

	public void setValueDate(LocalDate valueDate) {
		this.valueDate = valueDate;
	}

	public Trade<? extends Product> getTrade() {
		return TradistaModelUtil.clone(trade);
	}

	public void setTrade(Trade<? extends Product> trade) {
		this.trade = trade;
	}

	public Product getProduct() {
		return TradistaModelUtil.clone(product);
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Book getBook() {
		if (positionDefinition.getBook() != null) {
			return positionDefinition.getBook();
		}
		if (trade != null) {
			return trade.getBook();
		}
		return null;
	}

	@Override
	public String getSubjectKey() {
		return getType() + "-" + getPositionDefinition() + "-" + getValueDate();
	}

	@Override
	public PositionCalculationError clone() {
		PositionCalculationError positionCalculationError = (PositionCalculationError) super.clone();
		positionCalculationError.positionDefinition = TradistaModelUtil.clone(positionDefinition);
		positionCalculationError.trade = TradistaModelUtil.clone(trade);
		positionCalculationError.product = TradistaModelUtil.clone(product);
		return positionCalculationError;
	}
}