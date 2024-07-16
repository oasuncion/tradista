package finance.tradista.ir.fra.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.ir.fra.model.FRATrade;
import finance.tradista.ir.fra.service.FRAPricerBusinessDelegate;

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

public class PricerMeasureUNREALIZED_PNL extends PricerMeasure {

	private static final long serialVersionUID = -7320823523034043035L;

	private FRAPricerBusinessDelegate fraPricerBusinessDelegate;

	public PricerMeasureUNREALIZED_PNL() {
		super();
		fraPricerBusinessDelegate = new FRAPricerBusinessDelegate();
	}

	@Pricing(defaultUNREALIZED_PNL = true)
	public BigDecimal unrealizedPnl(PricingParameter params, FRATrade trade, Currency currency, LocalDate pricingDate)
			throws PricerException, TradistaBusinessException {

		return fraPricerBusinessDelegate.unrealizedPnlDefault(params, trade, currency, pricingDate);
	}

	public String toString() {
		return "UNREALIZED_PNL";
	}

}