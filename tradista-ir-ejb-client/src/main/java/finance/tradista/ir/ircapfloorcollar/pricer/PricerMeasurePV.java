package finance.tradista.ir.ircapfloorcollar.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.ir.ircapfloorcollar.model.IRCapFloorCollarTrade;
import finance.tradista.ir.ircapfloorcollar.service.IRCapFloorCollarPricerBusinessDelegate;

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

public class PricerMeasurePV extends PricerMeasure {

	private static final long serialVersionUID = -5843221620138434048L;

	private IRCapFloorCollarPricerBusinessDelegate irCapFloorCollarPricerBusinessDelegate;

	public PricerMeasurePV() {
		super();
		irCapFloorCollarPricerBusinessDelegate = new IRCapFloorCollarPricerBusinessDelegate();
	}

	@Pricing
	/**
	 * Use of the Black formula. Cf Hull
	 * 
	 * @param params
	 * @param trade
	 * @return
	 * @throws PricerException
	 */
	public BigDecimal black(PricingParameter params, IRCapFloorCollarTrade trade, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {

		return irCapFloorCollarPricerBusinessDelegate.pvBlack(params, trade, currency, pricingDate);

	}

	public String toString() {
		return "PV";
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof PricerMeasurePV)) {
			return false;
		}

		return getClass().equals(getClass());
	}

	public int hashCode() {
		return getClass().hashCode();
	}

}
