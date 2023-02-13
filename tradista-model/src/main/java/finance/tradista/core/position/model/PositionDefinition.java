package finance.tradista.core.position.model;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.product.model.Product;

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

public class PositionDefinition extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4204899247050005377L;

	@Id
	private String name;

	private Book book;

	private String productType;

	private Product product;

	private LegalEntity counterparty;

	private boolean isRealTime;

	private Currency currency;

	private PricingParameter pricingParameter;

	@Id
	private LegalEntity processingOrg;

	public PositionDefinition(String name, LegalEntity processingOrg) {
		super();
		this.name = name;
		this.processingOrg = processingOrg;
	}

	public String getName() {
		return name;
	}

	public Book getBook() {
		return TradistaModelUtil.clone(book);
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Product getProduct() {
		return TradistaModelUtil.clone(product);
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public LegalEntity getCounterparty() {
		return TradistaModelUtil.clone(counterparty);
	}

	public void setCounterparty(LegalEntity counterparty) {
		this.counterparty = counterparty;
	}

	public boolean isRealTime() {
		return isRealTime;
	}

	public void setRealTime(boolean isRealTime) {
		this.isRealTime = isRealTime;
	}

	public Currency getCurrency() {
		return TradistaModelUtil.clone(currency);
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public PricingParameter getPricingParameter() {
		return TradistaModelUtil.clone(pricingParameter);
	}

	public void setPricingParameter(PricingParameter pricingParameter) {
		this.pricingParameter = pricingParameter;
	}

	public LegalEntity getProcessingOrg() {
		return TradistaModelUtil.clone(processingOrg);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public PositionDefinition clone() {
		PositionDefinition positionDefinition = (PositionDefinition) super.clone();
		positionDefinition.book = TradistaModelUtil.clone(book);
		positionDefinition.product = TradistaModelUtil.clone(product);
		positionDefinition.counterparty = TradistaModelUtil.clone(counterparty);
		positionDefinition.currency = TradistaModelUtil.clone(currency);
		positionDefinition.pricingParameter = TradistaModelUtil.clone(pricingParameter);
		positionDefinition.processingOrg = TradistaModelUtil.clone(processingOrg);
		return positionDefinition;
	}

}