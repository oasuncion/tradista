package finance.tradista.security.specificrepo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.specificrepo.model.SpecificRepoTrade;
import jakarta.ejb.Remote;

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

@Remote
public interface SpecificRepoPricerService {

	List<CashFlow> generateCashFlows(PricingParameter params, SpecificRepoTrade trade, LocalDate pricingDate)
			throws TradistaBusinessException;

	BigDecimal getCollateralMarketToMarket(SpecificRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException;

	BigDecimal getCollateralMarketToMarket(Map<Security, Map<Book, BigDecimal>> securities, LegalEntity po,
			LocalDate pricingDate) throws TradistaBusinessException;

	BigDecimal getExposure(SpecificRepoTrade trade, Currency currency, LocalDate pricingDate, PricingParameter params)
			throws TradistaBusinessException;

	BigDecimal getCurrentCollateralMarketToMarket(SpecificRepoTrade trade) throws TradistaBusinessException;

	BigDecimal getCurrentExposure(SpecificRepoTrade trade) throws TradistaBusinessException;

}
