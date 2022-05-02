package finance.tradista.fx.fxswap.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.ejb.Remote;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.fx.fxswap.model.FXSwapTrade;

/*
 * Copyright 2015 Olivier Asuncion
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

@Remote
public interface FXSwapPricerService {

	BigDecimal fwdLegNpvDiscountedLegsDiff(PricingParameter params, FXSwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException;

	BigDecimal npvDiscountedLegsDiff(PricingParameter params, FXSwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException;

	BigDecimal spotLegNpvDiscountedLegsDiff(PricingParameter params, FXSwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException;

	BigDecimal unrealizedPnlLegsDiff(PricingParameter params, FXSwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException;

	BigDecimal defaultPNL(PricingParameter params, FXSwapTrade trade, Currency currency, LocalDate pricingDate)
			throws TradistaBusinessException;

	List<CashFlow> generateCashFlows(PricingParameter params, FXSwapTrade trade, LocalDate pricingDate)
			throws TradistaBusinessException;

	BigDecimal unrealizedPnlMarkToMarket(PricingParameter params, FXSwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException;

	BigDecimal realizedPnlMarkToMarket(PricingParameter params, FXSwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException;
}
