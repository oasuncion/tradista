package finance.tradista.core.cashflow.ui.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import javafx.beans.property.SimpleStringProperty;

/*
 * Copyright 2018 Olivier Asuncion
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

public class CashFlowProperty {

	private final SimpleStringProperty date;
	private final SimpleStringProperty direction;
	private final SimpleStringProperty purpose;
	private final SimpleStringProperty amount;
	private final SimpleStringProperty currency;
	private final SimpleStringProperty discountedAmount;
	private final SimpleStringProperty discountFactor;

	public CashFlowProperty(String date, String direction, String purpose, String amount, String currency,
			String discountedAmount, String discountFactor) {
		this.date = new SimpleStringProperty(date);
		this.direction = new SimpleStringProperty(direction);
		this.purpose = new SimpleStringProperty(purpose);
		this.amount = new SimpleStringProperty(amount);
		this.currency = new SimpleStringProperty(currency);
		this.discountedAmount = new SimpleStringProperty(discountedAmount);
		this.discountFactor = new SimpleStringProperty(discountFactor);
	}

	public static List<CashFlowProperty> toCashFlowPropertyList(Collection<CashFlow> data) {
		List<CashFlowProperty> cfPropertyList = new ArrayList<CashFlowProperty>();
		for (CashFlow cf : data) {
			cfPropertyList.add(new CashFlowProperty(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(cf.getDate()),
					cf.getDirection().toString(),
					cf.getPurpose() == null ? StringUtils.EMPTY : cf.getPurpose().toString(),
					TradistaGUIUtil.formatAmount(cf.getAmount()), cf.getCurrency().toString(),
					cf.getDiscountedAmount() == null ? StringUtils.EMPTY
							: TradistaGUIUtil.formatAmount(cf.getDiscountedAmount()),
					cf.getDiscountFactor() == null ? StringUtils.EMPTY
							: TradistaGUIUtil.formatAmount(cf.getDiscountFactor())));
		}

		return cfPropertyList;
	}

	public String getDate() {
		return date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public String getDirection() {
		return direction.get();
	}

	public void setDirection(String direction) {
		this.direction.set(direction);
	}

	public String getPurpose() {
		return purpose.get();
	}

	public void setPurpose(String purpose) {
		this.purpose.set(purpose);
	}

	public String getAmount() {
		return amount.get();
	}

	public void setAmount(String amount) {
		this.amount.set(amount);
	}

	public String getCurrency() {
		return currency.get();
	}

	public void setCurrency(String currency) {
		this.currency.set(currency);
	}

	public String getDiscountedAmount() {
		return discountedAmount.get();
	}

	public void setDiscountedAmount(String discountedAmount) {
		this.discountedAmount.set(discountedAmount);
	}

	public String getDiscountFactor() {
		return discountFactor.get();
	}

	public void setDiscountFactor(String discountFactor) {
		this.discountFactor.set(discountFactor);
	}

}