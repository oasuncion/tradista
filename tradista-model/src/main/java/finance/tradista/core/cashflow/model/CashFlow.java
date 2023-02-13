package finance.tradista.core.cashflow.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.transfer.model.TransferPurpose;

/*
 * Copyright 2014 Olivier Asuncion
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

public class CashFlow extends TradistaObject implements Comparable<CashFlow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7017846262097274047L;

	private BigDecimal amount;

	private BigDecimal discountedAmount;

	private BigDecimal discountFactor;

	@Id
	private LocalDate date;

	@Id
	private Currency currency;

	@Id
	private TransferPurpose purpose;

	private Direction direction;

	public static enum Direction {
		PAY, RECEIVE;

		public String toString() {
			switch (this) {
			case PAY:
				return "Pay";
			case RECEIVE:
				return "Receive";
			}
			return super.toString();
		}
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Currency getCurrency() {
		return TradistaModelUtil.clone(currency);
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public TransferPurpose getPurpose() {
		return purpose;
	}

	public void setPurpose(TransferPurpose purpose) {
		this.purpose = purpose;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public BigDecimal getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(BigDecimal discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public BigDecimal getDiscountFactor() {
		return discountFactor;
	}

	public void setDiscountFactor(BigDecimal discountFactor) {
		this.discountFactor = discountFactor;
	}

	@Override
	public int compareTo(CashFlow cf) {
		if (cf == null) {
			return 1;
		}
		return date.compareTo(cf.getDate());
	}

	@Override
	public CashFlow clone() {
		CashFlow cf = (CashFlow) super.clone();
		cf.currency = TradistaModelUtil.clone(currency);
		return cf;
	}

}