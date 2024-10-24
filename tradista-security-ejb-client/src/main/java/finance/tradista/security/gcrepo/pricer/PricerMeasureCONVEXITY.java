package finance.tradista.security.gcrepo.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.service.GCRepoPricerBusinessDelegate;

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

public class PricerMeasureCONVEXITY extends PricerMeasure {

	private static final long serialVersionUID = -6566300828951696665L;

	private GCRepoPricerBusinessDelegate gcRepoPricerBusinessDelegate;

	public PricerMeasureCONVEXITY() {
		gcRepoPricerBusinessDelegate = new GCRepoPricerBusinessDelegate();
	}

	public String toString() {
		return "CONVEXITY";
	}

	@Pricing
	public BigDecimal approximated(PricingParameter params, GCRepoTrade trade, Currency currency, LocalDate pricingDate)
			throws TradistaBusinessException {
		return gcRepoPricerBusinessDelegate.getApproximatedConvexity(trade, currency, pricingDate, params);
	}
}