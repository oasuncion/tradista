package finance.tradista.security.gcrepo.service;

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
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.specificrepo.model.SpecificRepoTrade;
import jakarta.ejb.Remote;

/********************************************************************************
 * Copyright (c) 2023 Olivier Asuncion
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
public interface GCRepoPricerService {

	List<CashFlow> generateCashFlows(PricingParameter params, GCRepoTrade trade, LocalDate pricingDate)
			throws TradistaBusinessException;

	BigDecimal getCollateralMarketToMarket(GCRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException;

	BigDecimal getCollateralMarketToMarket(Map<Security, Map<Book, BigDecimal>> securities, LegalEntity po,
			LocalDate pricingDate) throws TradistaBusinessException;

	BigDecimal getExposure(GCRepoTrade trade, Currency currency, LocalDate pricingDate, PricingParameter params)
			throws TradistaBusinessException;

	BigDecimal getCurrentCollateralMarketToMarket(GCRepoTrade trade) throws TradistaBusinessException;

	BigDecimal getCurrentExposure(GCRepoTrade trade) throws TradistaBusinessException;

	BigDecimal pnlDefault(GCRepoTrade trade, Currency currency, LocalDate pricingDate, PricingParameter params)
			throws TradistaBusinessException;

	BigDecimal realizedPayments(GCRepoTrade trade, Currency currency, LocalDate pricingDate, PricingParameter params)
			throws TradistaBusinessException;

	BigDecimal discountedPayments(GCRepoTrade trade, Currency currency, LocalDate pricingDate, PricingParameter params)
			throws TradistaBusinessException;

	BigDecimal getDelta(GCRepoTrade trade, Currency currency, LocalDate pricingDate, PricingParameter params)
			throws TradistaBusinessException;

	BigDecimal getApproximatedConvexity(GCRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException;

	BigDecimal getCurrentCollateralValue(GCRepoTrade trade) throws TradistaBusinessException;

	BigDecimal getPendingCollateralValue(GCRepoTrade trade, Map<Security, Map<Book, BigDecimal>> addedSecurities,
			Map<Security, Map<Book, BigDecimal>> removedSecurities) throws TradistaBusinessException;

	BigDecimal getCurrentCashValue(GCRepoTrade trade) throws TradistaBusinessException;

}