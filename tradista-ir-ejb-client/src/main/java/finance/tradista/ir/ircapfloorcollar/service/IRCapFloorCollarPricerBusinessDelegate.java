package finance.tradista.ir.ircapfloorcollar.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.ir.ircapfloorcollar.model.IRCapFloorCollarTrade;
import finance.tradista.ir.ircapfloorcollar.validator.IRCapFloorCollarTradeValidator;

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

public class IRCapFloorCollarPricerBusinessDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7137084591554434226L;

	private IRCapFloorCollarPricerService irCapFloorCollarPricerService;

	private IRCapFloorCollarTradeValidator validator;

	public IRCapFloorCollarPricerBusinessDelegate() {
		irCapFloorCollarPricerService = TradistaServiceLocator.getInstance().getIRCapFloorCollarPricerService();
		validator = new IRCapFloorCollarTradeValidator();
	}

	public BigDecimal npvBlack(PricingParameter params, IRCapFloorCollarTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {

		validator.validateTrade(trade);

		return SecurityUtil.runEx(() -> irCapFloorCollarPricerService.npvBlack(params, trade, currency, pricingDate));
	}

	public BigDecimal pvBlack(PricingParameter params, IRCapFloorCollarTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {

		validator.validateTrade(trade);

		return SecurityUtil.runEx(() -> irCapFloorCollarPricerService.pvBlack(params, trade, currency, pricingDate));
	}

	public BigDecimal pnlDefault(PricingParameter params, IRCapFloorCollarTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {

		validator.validateTrade(trade);

		return SecurityUtil.runEx(() -> irCapFloorCollarPricerService.pnlDefault(params, trade, currency, pricingDate));
	}

	public BigDecimal realizedPnlPaymentTriggers(PricingParameter params, IRCapFloorCollarTrade trade,
			Currency currency, LocalDate pricingDate) throws TradistaBusinessException {

		validator.validateTrade(trade);
		return SecurityUtil.runEx(
				() -> irCapFloorCollarPricerService.realizedPnlPaymentTriggers(params, trade, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlDefault(PricingParameter params, IRCapFloorCollarTrade trade, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> irCapFloorCollarPricerService.unrealizedPnlBlack(params, trade, currency, pricingDate));
	}

	public List<CashFlow> generateCashFlows(IRCapFloorCollarTrade trade, PricingParameter pp, LocalDate pricingDate)
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
		// Dummy method for the moment, cashflows generation of optional products is a
		// pending topic.
		return new ArrayList<CashFlow>();
	}
}