package finance.tradista.fx.fxoption.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.fx.fxoption.model.FXOptionTrade;
import finance.tradista.fx.fxoption.service.FXOptionPricerBusinessDelegate;

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

public class PricerMeasurePNL extends PricerMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8763653903853022052L;

	private FXOptionPricerBusinessDelegate fxOptionPricerBusinessDelegate;

	public PricerMeasurePNL() {
		super();
		fxOptionPricerBusinessDelegate = new FXOptionPricerBusinessDelegate();
	}

	@Pricing(defaultPNL = true)
	public BigDecimal legsDiffAsOfSettlementDate(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {

		return fxOptionPricerBusinessDelegate.pnlDefault(params, trade, currency, pricingDate);
	}

	public String toString() {
		return "PNL";
	}

}
