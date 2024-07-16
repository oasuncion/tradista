package finance.tradista.fx.fxndf.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.fx.fxndf.model.FXNDFTrade;
import finance.tradista.fx.fxndf.service.FXNDFPricerBusinessDelegate;

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

	private FXNDFPricerBusinessDelegate fxNdfPricerBusinessDelegate;

	public PricerMeasureNPV() {
		super();
		fxNdfPricerBusinessDelegate = new FXNDFPricerBusinessDelegate();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3122350582050873923L;

	@Pricing
	public BigDecimal discountedLegsDiff(PricingParameter params, FXNDFTrade trade, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {

		return fxNdfPricerBusinessDelegate.npvDiscountedLegsDiff(params, (FXNDFTrade) trade, currency, pricingDate);
	}

	public String toString() {
		return "NPV";
	}

}