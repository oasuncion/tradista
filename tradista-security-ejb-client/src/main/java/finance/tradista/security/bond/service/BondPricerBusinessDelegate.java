package finance.tradista.security.bond.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.model.BondTrade;
import finance.tradista.security.bond.service.BondPricerService;
import finance.tradista.security.bond.validator.BondTradeValidator;
import finance.tradista.security.bond.validator.BondValidator;

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

public class BondPricerBusinessDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9015095965433017363L;

	private BondPricerService bondPricerService;

	private BondTradeValidator tradeValidator;

	private BondValidator bondValidator;

	public BondPricerBusinessDelegate() {
		bondPricerService = TradistaServiceLocator.getInstance().getBondPricerService();
		tradeValidator = new BondTradeValidator();
		bondValidator = new BondValidator();
	}

	public BigDecimal ytmNewtonRaphson(PricingParameter params, BondTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> bondPricerService.ytmNewtonRaphson(params, trade, currency, pricingDate));
	}

	public BigDecimal parYieldParYield(PricingParameter params, BondTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> bondPricerService.parYieldParYield(params, trade, currency, pricingDate));
	}

	public BigDecimal npvDiscountedCashFlow(PricingParameter params, BondTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> bondPricerService.npvDiscountedCashFlow(params, trade, currency, pricingDate));
	}

	public BigDecimal pvDiscountedCashFlow(PricingParameter params, BondTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> bondPricerService.npvDiscountedCashFlow(params, trade, currency, pricingDate));
	}

	public BigDecimal cleanPriceDiscountedCashFlow(PricingParameter params, BondTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> bondPricerService.cleanPriceDiscountedCashFlow(params, trade, currency, pricingDate));
	}

	public BigDecimal dirtyPriceDiscountedCashFlow(PricingParameter params, BondTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> bondPricerService.dirtyPriceDiscountedCashFlow(params, trade, currency, pricingDate));
	}

	public BigDecimal pnlDefault(PricingParameter params, Bond bond, Book book, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		bondValidator.validateProduct(bond);
		return SecurityUtil.runEx(() -> bondPricerService.pnlDefault(params, bond, book, currency, pricingDate));
	}

	public BigDecimal realizedPnlDefault(PricingParameter params, Bond bond, Book book, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		bondValidator.validateProduct(bond);
		return SecurityUtil
				.runEx(() -> bondPricerService.realizedPnlDefault(params, bond, book, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlDefault(PricingParameter params, Bond bond, Book book, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		bondValidator.validateProduct(bond);
		return SecurityUtil
				.runEx(() -> bondPricerService.unrealizedPnlDefault(params, bond, book, currency, pricingDate));
	}

	public List<CashFlow> generateCashFlows(BondTrade trade, PricingParameter pp, LocalDate pricingDate)
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
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> bondPricerService.generateCashFlows(pp, trade, pricingDate));
	}

}