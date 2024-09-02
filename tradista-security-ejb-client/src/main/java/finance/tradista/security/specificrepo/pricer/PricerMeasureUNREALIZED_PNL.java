package finance.tradista.security.specificrepo.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.specficrepo.service.SpecificRepoPricerBusinessDelegate;
import finance.tradista.security.specificrepo.model.SpecificRepoTrade;

/********************************************************************************
 * Copyright (c) 2024 Olivier Asuncion
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

public class PricerMeasureUNREALIZED_PNL extends PricerMeasure {

	private static final long serialVersionUID = -1160644913114120724L;

	private SpecificRepoPricerBusinessDelegate specificRepoPricerBusinessDelegate;

	public PricerMeasureUNREALIZED_PNL() {
		specificRepoPricerBusinessDelegate = new SpecificRepoPricerBusinessDelegate();
	}

	@Pricing(defaultUNREALIZED_PNL = true)
	public BigDecimal discountedPayments(PricingParameter params, SpecificRepoTrade trade, Currency currency,
			LocalDate pricingDate) throws TradistaBusinessException {

		return specificRepoPricerBusinessDelegate.discountedPayments(trade, currency, pricingDate, params);
	}

	public String toString() {
		return "UNREALIZED_PNL";
	}

}