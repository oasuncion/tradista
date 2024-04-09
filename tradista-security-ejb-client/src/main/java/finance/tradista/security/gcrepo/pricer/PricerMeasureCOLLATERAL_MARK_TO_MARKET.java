package finance.tradista.security.gcrepo.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.service.GCRepoPricerBusinessDelegate;

/*
 * Copyright 2024 Olivier Asuncion
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

public class PricerMeasureCOLLATERAL_MARK_TO_MARKET extends PricerMeasure {

	private static final long serialVersionUID = -2138609489728203598L;

	private GCRepoPricerBusinessDelegate gcRepoPricerBusinessDelegate;

	public PricerMeasureCOLLATERAL_MARK_TO_MARKET() {
		gcRepoPricerBusinessDelegate = new GCRepoPricerBusinessDelegate();
	}

	public String toString() {
		return "COLLATERAL_MARK_TO_MARKET";
	}

	@Pricing
	public BigDecimal mtm(PricingParameter params, GCRepoTrade trade, Currency currency, LocalDate pricingDate)
			throws TradistaBusinessException {
		return gcRepoPricerBusinessDelegate.getCollateralMarketToMarket(trade, currency, pricingDate, params);
	}
}