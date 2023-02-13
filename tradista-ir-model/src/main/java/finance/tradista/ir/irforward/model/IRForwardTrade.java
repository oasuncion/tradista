package finance.tradista.ir.irforward.model;

import java.time.LocalDate;

import finance.tradista.core.common.model.TradistaModelUtil;
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

public class IRForwardTrade<P extends Product> extends Trade<P> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 429312965675171534L;

	private LocalDate maturityDate;

	private Tenor frequency;

	private InterestPayment interestPayment;

	private InterestPayment interestFixing;

	private Index referenceRateIndex;

	private Tenor referenceRateIndexTenor;

	private DayCountConvention dayCountConvention;

	public static final String IR_FORWARD = "IRForward";

	public InterestPayment getInterestPayment() {
		return interestPayment;
	}

	public void setInterestPayment(InterestPayment interestPayment) {
		this.interestPayment = interestPayment;
	}

	public InterestPayment getInterestFixing() {
		return interestFixing;
	}

	public void setInterestFixing(InterestPayment interestFixing) {
		this.interestFixing = interestFixing;
	}

	public DayCountConvention getDayCountConvention() {
		return dayCountConvention;
	}

	public void setDayCountConvention(DayCountConvention dayCountConvention) {
		this.dayCountConvention = dayCountConvention;
	}

	public Tenor getFrequency() {
		return frequency;
	}

	public void setFrequency(Tenor frequency) {
		this.frequency = frequency;
	}

	public Index getReferenceRateIndex() {
		return TradistaModelUtil.clone(referenceRateIndex);
	}

	public Tenor getReferenceRateIndexTenor() {
		return referenceRateIndexTenor;
	}

	public LocalDate getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(LocalDate maturityDate) {
		this.maturityDate = maturityDate;
	}

	public void setReferenceRateIndex(Index referenceRateIndex) {
		this.referenceRateIndex = referenceRateIndex;
	}

	public void setReferenceRateIndexTenor(Tenor referenceRateIndexTenor) {
		this.referenceRateIndexTenor = referenceRateIndexTenor;
	}

	public String getProductType() {
		return IR_FORWARD;
	}

	@Override
	public IRForwardTrade<P> clone() {
		IRForwardTrade<P> irForwardTrade = (IRForwardTrade<P>) super.clone();
		irForwardTrade.referenceRateIndex = TradistaModelUtil.clone(referenceRateIndex);
		return irForwardTrade;
	}

}