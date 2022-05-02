package finance.tradista.security.equity.service;

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
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.model.EquityTrade;
import finance.tradista.security.equity.service.EquityPricerService;
import finance.tradista.security.equity.validator.EquityTradeValidator;
import finance.tradista.security.equity.validator.EquityValidator;

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

public class EquityPricerBusinessDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9015095965433017363L;

	private EquityPricerService equityPricerService;

	private EquityTradeValidator tradeValidator;

	private EquityValidator equityValidator;

	public EquityPricerBusinessDelegate() {
		equityPricerService = TradistaServiceLocator.getInstance().getEquityPricerService();
		tradeValidator = new EquityTradeValidator();
		equityValidator = new EquityValidator();
	}

	public BigDecimal pvMonteCarloSimulation(PricingParameter params, EquityTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> equityPricerService.pvMonteCarloSimulation(params, trade, currency, pricingDate));
	}

	public BigDecimal npvMontecarloSimulation(PricingParameter params, EquityTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> equityPricerService.npvMontecarloSimulation(params, trade, currency, pricingDate));
	}

	public BigDecimal expectedReturnCapm(PricingParameter params, EquityTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> equityPricerService.expectedReturnCapm(params, trade, currency, pricingDate));
	}

	public BigDecimal pnlDefault(PricingParameter params, Equity equity, Book book, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		equityValidator.validateProduct(equity);
		return equityPricerService.pnlDefault(params, equity, book, currency, pricingDate);
	}

	public BigDecimal realizedPnlDefault(PricingParameter params, Equity equity, Book book, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		equityValidator.validateProduct(equity);
		return SecurityUtil
				.runEx(() -> equityPricerService.realizedPnlDefault(params, equity, book, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlMarkToMarket(PricingParameter params, Equity equity, Book book, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		equityValidator.validateProduct(equity);
		return SecurityUtil.runEx(
				() -> equityPricerService.unrealizedPnlMarkToMarket(params, equity, book, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlMarkToModel(PricingParameter params, Equity equity, Book book, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {
		equityValidator.validateProduct(equity);
		return SecurityUtil
				.runEx(() -> equityPricerService.unrealizedPnlMarkToModel(params, equity, book, currency, pricingDate));
	}

	public List<CashFlow> generateCashFlows(EquityTrade trade, PricingParameter pp, LocalDate pricingDate)
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
		return SecurityUtil.runEx(() -> equityPricerService.generateCashFlows(pp, trade, pricingDate));
	}

}