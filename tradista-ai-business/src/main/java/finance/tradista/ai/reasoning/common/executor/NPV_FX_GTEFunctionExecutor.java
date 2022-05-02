package finance.tradista.ai.reasoning.common.executor;

/*
 * Copyright 2019 Olivier Asuncion
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

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.ai.reasoning.common.model.Function;
import finance.tradista.ai.reasoning.common.model.FunctionExecutor;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.fx.common.util.FXUtil;

public class NPV_FX_GTEFunctionExecutor extends FunctionExecutor<Boolean> {

	@Override
	/**
	 * Returns the NPV of a FX on the given currencies and settlement date. Both
	 * amounts are calculated to be equivalent of one unit of the mandate currency
	 */
	public Boolean call(Function<?> function, Object... parameters) throws TradistaBusinessException {
		BigDecimal npv;
		Currency primaryCurrency = (Currency) parameters[0];
		Currency quoteCurrency = (Currency) parameters[1];
		Currency valueCurrency = (Currency) parameters[2];
		LocalDate date = (LocalDate) parameters[3];
		PricingParameter pp = (PricingParameter) parameters[4];
		BigDecimal threshold = (BigDecimal) parameters[5];
		npv = FXUtil.getNPV(primaryCurrency, quoteCurrency, valueCurrency, date, pp);
		return npv.compareTo(threshold) >= 0;
	}

}
