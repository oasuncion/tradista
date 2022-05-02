package finance.tradista.ir.irswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.daycountconvention.model.DayCountConvention;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.interestpayment.model.InterestPayment;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.trade.model.Trade;

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

public abstract class IRSwapTrade extends Trade<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6188291608649255466L;

	public static final String IR_SWAP = "IRSwap";

	protected LocalDate maturityDate;

	protected Tenor maturityTenor;

	protected Tenor paymentFrequency;

	protected Tenor receptionFrequency;
	
	protected InterestPayment paymentInterestPayment;

	protected InterestPayment receptionInterestPayment;
	
	protected InterestPayment paymentInterestFixing;

	protected InterestPayment receptionInterestFixing;

	protected Tenor paymentReferenceRateIndexTenor;

	protected Tenor receptionReferenceRateIndexTenor;

	protected Index receptionReferenceRateIndex;

	protected Index paymentReferenceRateIndex;

	protected BigDecimal paymentSpread;

	protected BigDecimal receptionSpread;

	protected BigDecimal paymentFixedInterestRate;

	protected boolean interestsToPayFixed;

	protected DayCountConvention paymentDayCountConvention;

	protected DayCountConvention receptionDayCountConvention;	

	public InterestPayment getPaymentInterestPayment() {
		return paymentInterestPayment;
	}

	public void setPaymentInterestPayment(InterestPayment paymentInterestPayment) {
		this.paymentInterestPayment = paymentInterestPayment;
	}

	public InterestPayment getReceptionInterestPayment() {
		return receptionInterestPayment;
	}

	public void setReceptionInterestPayment(InterestPayment receptionInterestPayment) {
		this.receptionInterestPayment = receptionInterestPayment;
	}

	public DayCountConvention getPaymentDayCountConvention() {
		return paymentDayCountConvention;
	}

	public void setPaymentDayCountConvention(DayCountConvention paymentDayCountConvention) {
		this.paymentDayCountConvention = paymentDayCountConvention;
	}

	public DayCountConvention getReceptionDayCountConvention() {
		return receptionDayCountConvention;
	}

	public void setReceptionDayCountConvention(DayCountConvention receptionDayCountConvention) {
		this.receptionDayCountConvention = receptionDayCountConvention;
	}

	public boolean isInterestsToPayFixed() {
		return interestsToPayFixed;
	}

	public void setInterestsToPayFixed(boolean interestsToPayFixed) {
		this.interestsToPayFixed = interestsToPayFixed;
	}

	public LocalDate getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(LocalDate maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Tenor getMaturityTenor() {
		return maturityTenor;
	}

	public void setMaturityTenor(Tenor maturityTenor) {
		this.maturityTenor = maturityTenor;
	}

	public BigDecimal getPaymentFixedInterestRate() {
		return paymentFixedInterestRate;
	}

	public Tenor getPaymentFrequency() {
		return paymentFrequency;
	}

	public void setPaymentFrequency(Tenor paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}

	public Tenor getReceptionFrequency() {
		return receptionFrequency;
	}

	public void setReceptionFrequency(Tenor receptionFrequency) {
		this.receptionFrequency = receptionFrequency;
	}

	public Tenor getPaymentReferenceRateIndexTenor() {
		return paymentReferenceRateIndexTenor;
	}

	public void setPaymentReferenceRateIndexTenor(Tenor paymentReferenceRateIndexTenor) {
		this.paymentReferenceRateIndexTenor = paymentReferenceRateIndexTenor;
	}

	public Tenor getReceptionReferenceRateIndexTenor() {
		return receptionReferenceRateIndexTenor;
	}

	public void setReceptionReferenceRateIndexTenor(Tenor receptionReferenceRateIndexTenor) {
		this.receptionReferenceRateIndexTenor = receptionReferenceRateIndexTenor;
	}

	public Index getReceptionReferenceRateIndex() {
		return receptionReferenceRateIndex;
	}

	public void setReceptionReferenceRateIndex(Index receptionReferenceRateIndex) {
		this.receptionReferenceRateIndex = receptionReferenceRateIndex;
	}

	public Index getPaymentReferenceRateIndex() {
		return paymentReferenceRateIndex;
	}

	public void setPaymentReferenceRateIndex(Index paymentReferenceRateIndex) {
		this.paymentReferenceRateIndex = paymentReferenceRateIndex;
	}

	public BigDecimal getPaymentSpread() {
		return paymentSpread;
	}

	public void setPaymentSpread(BigDecimal paymentSpread) {
		this.paymentSpread = paymentSpread;
	}

	public BigDecimal getReceptionSpread() {
		return receptionSpread;
	}

	public void setReceptionSpread(BigDecimal receptionSpread) {
		this.receptionSpread = receptionSpread;
	}

	public void setPaymentFixedInterestRate(BigDecimal paymentFixedInterestRate) {
		this.paymentFixedInterestRate = paymentFixedInterestRate;
	}

	public InterestPayment getPaymentInterestFixing() {
		return paymentInterestFixing;
	}

	public void setPaymentInterestFixing(InterestPayment paymentInterestFixing) {
		this.paymentInterestFixing = paymentInterestFixing;
	}

	public InterestPayment getReceptionInterestFixing() {
		return receptionInterestFixing;
	}

	public void setReceptionInterestFixing(InterestPayment receptionInterestFixing) {
		this.receptionInterestFixing = receptionInterestFixing;
	}
	
}