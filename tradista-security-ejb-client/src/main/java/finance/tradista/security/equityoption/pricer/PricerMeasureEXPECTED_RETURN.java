package finance.tradista.security.equityoption.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.equityoption.model.EquityOptionTrade;
import finance.tradista.security.equityoption.service.EquityOptionPricerBusinessDelegate;

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

public class PricerMeasureEXPECTED_RETURN extends PricerMeasure {

	private static final long serialVersionUID = 4746586383555912490L;

	private EquityOptionPricerBusinessDelegate equityOptionPricerBusinessDelegate;

	public PricerMeasureEXPECTED_RETURN() {
		super();
		equityOptionPricerBusinessDelegate = new EquityOptionPricerBusinessDelegate();
	}

	public String toString() {
		return "EXPECTED_RETURN";
	}

	/**
	 * Returns the equity expected return rate using CAPM : ERR = Rf + B(Rm - Rf) Rf
	 * : the Risk free rate B : the equity Beta Rm : the expected market return rate
	 * 
	 * @param params
	 * @param trade
	 * @param currency
	 * @return
	 * @throws PricerException
	 */
	@Pricing
	public BigDecimal capm(PricingParameter params, EquityOptionTrade trade, Currency currency, LocalDate pricingDate)
			throws PricerException, TradistaBusinessException {
		return equityOptionPricerBusinessDelegate.expectedReturnCapm(params, trade, currency, pricingDate);
	}

}