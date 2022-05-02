package finance.tradista.security.equityoption.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.equityoption.model.EquityOption;
import finance.tradista.security.equityoption.model.EquityOptionTrade;
import finance.tradista.security.equityoption.service.EquityOptionPricerBusinessDelegate;

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

public class PricerMeasureREALIZED_PNL extends PricerMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2035526009627518046L;

	private EquityOptionPricerBusinessDelegate equityOptionPricerBusinessDelegate;

	public PricerMeasureREALIZED_PNL() {
		super();
		equityOptionPricerBusinessDelegate = new EquityOptionPricerBusinessDelegate();
	}

	public BigDecimal optionExercise(PricingParameter params, EquityOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {

		return equityOptionPricerBusinessDelegate.realizedPnlOptionExercise(params, trade, currency, pricingDate);
	}

	@Pricing(defaultREALIZED_PNL = true)
	public BigDecimal optionExercise(PricingParameter params, EquityOption product, Book book, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {

		return equityOptionPricerBusinessDelegate.realizedPnlDefault(params, product, book, currency, pricingDate);
	}

	public String toString() {
		return "REALIZED_PNL";
	}

}