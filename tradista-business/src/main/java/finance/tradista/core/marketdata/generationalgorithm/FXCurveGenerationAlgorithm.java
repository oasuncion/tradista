package finance.tradista.core.marketdata.generationalgorithm;

import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.marketdata.interpolator.UnivariateInterpolator;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.RatePoint;

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

public interface FXCurveGenerationAlgorithm {

	List<RatePoint> generate(String instance, LocalDate quoteDate, QuoteSet quoteSet, Currency primaryCurrency,
			Currency quoteCurrency, InterestRateCurve primaryCurrencyIRCurve, InterestRateCurve quoteCurrencyIRCurve,
			UnivariateInterpolator interpolator) throws TradistaBusinessException;
}
