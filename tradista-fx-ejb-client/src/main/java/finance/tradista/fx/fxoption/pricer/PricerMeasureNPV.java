package finance.tradista.fx.fxoption.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.fx.fxoption.model.FXOptionTrade;
import finance.tradista.fx.fxoption.service.FXOptionPricerBusinessDelegate;

/********************************************************************************
 * Copyright (c) 2018 Olivier Asuncion
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

public class PricerMeasureNPV extends PricerMeasure {

	private FXOptionPricerBusinessDelegate fxOptionPricerBusinessDelegate;

	public PricerMeasureNPV() {
		super();
		fxOptionPricerBusinessDelegate = new FXOptionPricerBusinessDelegate();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7664440676294772708L;

	@Pricing
	/**
	 * Use of the Black & Scholes formula.
	 * 
	 * @param params
	 * @param trade
	 * @return
	 * @throws PricerException
	 */
	public BigDecimal blackAndScholes(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {
		return fxOptionPricerBusinessDelegate.npvBlacBkAndScholes(params, trade, currency, pricingDate);
	}

	@Pricing
	/**
	 * Use of the Black & Scholes formula.
	 * 
	 * @param params
	 * @param trade
	 * @return
	 * @throws PricerException
	 */
	public BigDecimal coxRossRubinstein(PricingParameter params, FXOptionTrade trade, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {
		return fxOptionPricerBusinessDelegate.npvCoxRossRubinstein(params, trade, currency, pricingDate);

	}

	public String toString() {
		return "NPV";
	}

}