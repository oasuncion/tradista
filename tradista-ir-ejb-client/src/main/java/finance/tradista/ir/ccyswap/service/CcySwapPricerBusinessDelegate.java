package finance.tradista.ir.ccyswap.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.ir.ccyswap.model.CcySwapTrade;
import finance.tradista.ir.ccyswap.validator.CcySwapTradeValidator;

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

public class CcySwapPricerBusinessDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7137084591554434226L;

	private CcySwapPricerService ccySwapPricerService;

	private CcySwapTradeValidator validator;

	public CcySwapPricerBusinessDelegate() {
		ccySwapPricerService = TradistaServiceLocator.getInstance().getCcySwapPricerService();
		validator = new CcySwapTradeValidator();
	}

	public BigDecimal fixedLegPvDiscountedCashFlow(PricingParameter params, CcySwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> ccySwapPricerService.fixedLegPvDiscountedCashFlow(params, trade, currency, pricingDate));
	}

	public BigDecimal floatingLegPvDiscountedCashFlow(PricingParameter params, CcySwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(
				() -> ccySwapPricerService.floatingLegPvDiscountedCashFlow(params, trade, currency, pricingDate));
	}

	public BigDecimal forwardSwapRateForwardSwapRate(PricingParameter params, CcySwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> ccySwapPricerService.forwardSwapRateForwardSwapRate(params, trade, currency, pricingDate));
	}

	public BigDecimal npvDiscountedLegsDiff(PricingParameter params, CcySwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> ccySwapPricerService.npvDiscountedLegsDiff(params, trade, currency, pricingDate));
	}

	public BigDecimal pnlDefault(PricingParameter params, CcySwapTrade trade, Currency currency, LocalDate pricingDate)
			throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> ccySwapPricerService.pnlDefault(params, trade, currency, pricingDate));
	}

	public BigDecimal realizedPnlLegsCashFlows(PricingParameter params, CcySwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> ccySwapPricerService.realizedPnlLegsCashFlows(params, trade, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlLegsDiff(PricingParameter params, CcySwapTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> ccySwapPricerService.unrealizedPnlLegsDiff(params, trade, currency, pricingDate));
	}

	public List<CashFlow> generateCashFlows(CcySwapTrade trade, PricingParameter pp, LocalDate pricingDate)
			throws TradistaBusinessException {
		StringBuffer errorMsg = new StringBuffer();
		if (trade == null) {
			errorMsg.append(String.format("The trade cannot be null.%n"));
		}
		if (pp == null) {
			errorMsg.append(String.format("The pricing parameters cannot be null.%n"));
		}
		if (pricingDate == null) {
			errorMsg.append(String.format("The pricing date cannot be null.%n"));
		}
		if (errorMsg.length() > 0) {
			throw new TradistaBusinessException(errorMsg.toString());
		}
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> ccySwapPricerService.generateCashFlows(pp, trade, pricingDate));
	}
}