package finance.tradista.security.equity.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.equity.model.EquityTrade;
import finance.tradista.security.equity.service.EquityPricerBusinessDelegate;

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

public class PricerMeasureEXPECTED_RETURN extends PricerMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7565285618234200040L;

	private EquityPricerBusinessDelegate equityPricerBusinessDelegate;

	public PricerMeasureEXPECTED_RETURN() {
		super();
		equityPricerBusinessDelegate = new EquityPricerBusinessDelegate();
	}

	public String toString() {
		return "EXPECTED_RETURN";
	}

	/**
	 * Returns the equity expected return rate using CAPM : ERR = Rf + B(Rm -
	 * Rf) Rf : the Risk free rate B : the equity Beta Rm : the expected market
	 * return rate
	 * 
	 * @param params
	 * @param trade
	 * @param currency
	 * @return
	 * @throws PricerException
	 * @throws TradistaBusinessException
	 */
	@Pricing
	public BigDecimal capm(PricingParameter params, EquityTrade trade, Currency currency, LocalDate pricingDate)
			throws PricerException, TradistaBusinessException {
		return equityPricerBusinessDelegate.expectedReturnCapm(params, trade, currency, pricingDate);
	}

}