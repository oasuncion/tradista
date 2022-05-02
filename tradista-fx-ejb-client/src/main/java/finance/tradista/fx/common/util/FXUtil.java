package finance.tradista.fx.common.util;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.book.model.BlankBook;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.model.CurrencyPair;
import finance.tradista.core.legalentity.model.BlankLegalEntity;
import finance.tradista.core.marketdata.model.FXCurve;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.fx.fx.model.FXTrade;
import finance.tradista.fx.fx.service.FXPricerBusinessDelegate;

/*
 * Copyright 2021 Olivier Asuncion
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

public final class FXUtil {

	/**
	 * Gets the NPV of a FX Spot/FX Fwd for one unit of each currency. This method
	 * uses a synthetic trade and then calls the NPV pricer measure ("NPV discounted
	 * legs diff" algorithm).
	 * 
	 * @param primaryCurrency the primary currency
	 * @param quoteCurrency   the quote currency
	 * @param valueCurrency   the value currency (currency of the returned npv)
	 * @param date            the value date
	 * @param pp              the pricing parameters
	 * @return the NPV of a FX Spot/FX Fwd for one unit of each currency.
	 * @throws TradistaBusinessException
	 */
	public static BigDecimal getNPV(Currency primaryCurrency, Currency quoteCurrency, Currency valueCurrency,
			LocalDate date, PricingParameter pp) throws TradistaBusinessException {
		FXTrade trade = new FXTrade();
		trade.setCurrency(quoteCurrency);
		trade.setCurrencyOne(primaryCurrency);
		trade.setTradeDate(LocalDate.now());
		trade.setSettlementDate(date);
		trade.setBook(BlankBook.getInstance());
		trade.setCounterparty(BlankLegalEntity.getInstance());
		// Calculate amounts equal to 1 unit of the mandate currency
		CurrencyPair pair = new CurrencyPair(valueCurrency, quoteCurrency);
		FXCurve ppQuoteFXCurve = pp.getFxCurves().get(pair);
		if (ppQuoteFXCurve == null) {
			// TODO add log warn
		}
		pair = new CurrencyPair(valueCurrency, primaryCurrency);
		FXCurve ppPrimaryFXCurve = pp.getFxCurves().get(pair);
		if (ppPrimaryFXCurve == null) {
			// TODO add log warn
		}
		BigDecimal convertedQuoteCurrency = PricerUtil.convertAmount(BigDecimal.ONE, quoteCurrency, valueCurrency,
				LocalDate.now(), pp.getQuoteSet().getId(), ppQuoteFXCurve != null ? ppQuoteFXCurve.getId() : 0);
		BigDecimal convertedPrimaryCurrency = PricerUtil.convertAmount(BigDecimal.ONE, primaryCurrency, valueCurrency,
				LocalDate.now(), pp.getQuoteSet().getId(), ppPrimaryFXCurve != null ? ppPrimaryFXCurve.getId() : 0);
		trade.setAmount(convertedQuoteCurrency);
		trade.setAmountOne(convertedPrimaryCurrency);

		return new FXPricerBusinessDelegate().npvDiscountedLegsDiff(pp, trade, valueCurrency, LocalDate.now());
	}

}