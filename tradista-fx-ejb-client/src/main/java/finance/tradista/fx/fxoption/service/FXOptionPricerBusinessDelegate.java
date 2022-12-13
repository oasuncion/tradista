package finance.tradista.fx.fxoption.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.fx.fxoption.model.FXOptionTrade;
import finance.tradista.fx.fxoption.validator.FXOptionTradeValidator;

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

public class FXOptionPricerBusinessDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7137084591554434226L;

	private FXOptionPricerService fxOptionPricerService;

	private FXOptionTradeValidator fxOptionTradeValidator;

	public FXOptionPricerBusinessDelegate() {
		fxOptionPricerService = TradistaServiceLocator.getInstance().getFXOptionPricerService();
		fxOptionTradeValidator = new FXOptionTradeValidator();
	}

	public BigDecimal npvBlacBkAndScholes(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> fxOptionPricerService.npvBlackAndScholes(params, trade, currency, pricingDate));
	}

	public BigDecimal npvCoxRossRubinstein(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> fxOptionPricerService.npvCoxRossRubinstein(params, trade, currency, pricingDate));
	}

	public BigDecimal pvBlacBkAndScholes(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> fxOptionPricerService.pvBlackAndScholes(params, trade, currency, pricingDate));
	}

	public BigDecimal pvCoxRossRubinstein(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> fxOptionPricerService.pvCoxRossRubinstein(params, trade, currency, pricingDate));
	}

	public BigDecimal realizedPnlMarkToMarket(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> fxOptionPricerService.realizedPnlMarkToMarket(params, trade, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlBlackAndScholes(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> fxOptionPricerService.unrealizedPnlBlackAndScholes(params, trade, currency, pricingDate));
	}

	public BigDecimal pnlDefault(PricingParameter params, FXOptionTrade trade, Currency currency, LocalDate pricingDate)
			throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> fxOptionPricerService.pnlDefault(params, trade, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlMarkToMarket(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		fxOptionTradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> fxOptionPricerService.unrealizedPnlMarkToMarket(params, trade, currency, pricingDate));
	}

}