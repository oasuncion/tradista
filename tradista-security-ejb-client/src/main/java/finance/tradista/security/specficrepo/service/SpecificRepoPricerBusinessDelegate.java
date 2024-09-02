package finance.tradista.security.specficrepo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.specificrepo.model.SpecificRepoTrade;
import finance.tradista.security.specificrepo.service.SpecificRepoPricerService;
import finance.tradista.security.specificrepo.validator.SpecificRepoTradeValidator;

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

public class SpecificRepoPricerBusinessDelegate implements Serializable {

	private static final long serialVersionUID = -644716507703744185L;

	private SpecificRepoPricerService specificRepoPricerService;

	private SpecificRepoTradeValidator validator;

	private static final String PRICING_DATE_IS_MANDATORY = "The pricing date is mandatory.%n";

	private static final String TRADE_IS_MANDATORY = "The trade is mandatory.%n";

	private static final String PRICING_PARAMETERS_SET_IS_MANDATORY = "The Pricing Parameters Set is mandatory.%n";

	public SpecificRepoPricerBusinessDelegate() {
		specificRepoPricerService = TradistaServiceLocator.getInstance().getSpecificRepoPricerService();
		validator = new SpecificRepoTradeValidator();
	}

	public List<CashFlow> generateCashFlows(SpecificRepoTrade trade, PricingParameter pp, LocalDate pricingDate)
			throws TradistaBusinessException {
		StringBuilder errorMsg = new StringBuilder();
		if (trade == null) {
			errorMsg.append(String.format(TRADE_IS_MANDATORY));
		}
		if (pp == null) {
			errorMsg.append(String.format(PRICING_PARAMETERS_SET_IS_MANDATORY));
		}
		if (pricingDate == null) {
			errorMsg.append(String.format(PRICING_DATE_IS_MANDATORY));
		}
		if (errorMsg.length() > 0) {
			throw new TradistaBusinessException(errorMsg.toString());
		}
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> specificRepoPricerService.generateCashFlows(pp, trade, pricingDate));
	}

	public BigDecimal getCollateralMarketToMarket(SpecificRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (trade == null) {
			errMsg.append(String.format(TRADE_IS_MANDATORY));
		} else {
			if (trade.getId() == 0) {
				errMsg.append(String.format("The trade must be an existing one.%n"));
			}
			try {
				validator.validateTrade(trade);
			} catch (TradistaBusinessException tbe) {
				errMsg.append(tbe.getMessage());
			}
		}
		if (params == null) {
			errMsg.append(String.format(PRICING_PARAMETERS_SET_IS_MANDATORY));
		}
		if (pricingDate == null) {
			errMsg.append(String.format(PRICING_DATE_IS_MANDATORY));
		} else {
			if (pricingDate.isAfter(LocalDate.now())) {
				errMsg.append(String.format(
						"Pricing date (%tD) cannot be in the future to calculate Collateral Mark To Market",
						pricingDate));
			}
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		return SecurityUtil.runEx(
				() -> specificRepoPricerService.getCollateralMarketToMarket(trade, currency, pricingDate, params));
	}

	public BigDecimal getCurrentCollateralMarketToMarket(SpecificRepoTrade trade) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (trade == null) {
			errMsg.append(String.format(TRADE_IS_MANDATORY));
		} else {
			if (trade.getId() == 0) {
				errMsg.append(String.format("The trade must be an existing one.%n"));
			}
			try {
				validator.validateTrade(trade);
			} catch (TradistaBusinessException tbe) {
				errMsg.append(tbe.getMessage());
			}
		}
		if (!errMsg.isEmpty()) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		return SecurityUtil.runEx(() -> specificRepoPricerService.getCurrentCollateralMarketToMarket(trade));
	}

	public BigDecimal getCollateralMarketToMarket(Map<Security, Map<Book, BigDecimal>> securities, LegalEntity po,
			LocalDate pricingDate) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (po == null) {
			errMsg.append(String.format("The Processing Org is mandatory.%n"));
		}
		if (securities == null || securities.isEmpty()) {
			errMsg.append(String.format("Securities are mandatory.%n"));
		}
		if (pricingDate == null) {
			errMsg.append(String.format(PRICING_DATE_IS_MANDATORY));
		} else {
			if (pricingDate.isAfter(LocalDate.now())) {
				errMsg.append(String.format(
						"Pricing date (%tD) cannot be in the future to calculate the collateral Mark To Market",
						pricingDate));
			}
		}
		if (!errMsg.isEmpty()) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		return SecurityUtil
				.runEx(() -> specificRepoPricerService.getCollateralMarketToMarket(securities, po, pricingDate));
	}

	public BigDecimal getExposure(SpecificRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (trade == null) {
			throw new TradistaBusinessException(String.format(TRADE_IS_MANDATORY));
		} else {
			try {
				validator.validateTrade(trade);
			} catch (TradistaBusinessException tbe) {
				errMsg.append(tbe.getMessage());
			}
		}
		if (params == null) {
			errMsg.append(String.format(PRICING_PARAMETERS_SET_IS_MANDATORY));
		}
		if (currency == null) {
			errMsg.append(String.format("The currency is mandatory.%n"));
		}
		if (pricingDate == null) {
			errMsg.append(String.format(PRICING_DATE_IS_MANDATORY));
		} else {
			if (pricingDate.isBefore(trade.getSettlementDate())) {
				errMsg.append(String.format(
						"Pricing date (%tD) cannot be before the trade settlement date (%tD) to calculate the exposure",
						pricingDate, trade.getSettlementDate()));
			}
			if (pricingDate.isAfter(trade.getEndDate())) {
				return BigDecimal.ZERO;
			}
			if (pricingDate.isAfter(LocalDate.now())) {
				if (!trade.isFixedRepoRate()) {
					InterestRateCurve indexCurve = params.getIndexCurves().get(trade.getIndex());
					if (indexCurve == null) {
						throw new TradistaBusinessException(String.format(
								"Pricing Parameters Set '%s' doesn't contain an Index Curve for %s. please add it or change the Pricing Parameters Set.",
								params.getName(), trade.getIndex()));
					}
				}
			}
		}
		return SecurityUtil.runEx(() -> specificRepoPricerService.getExposure(trade, currency, pricingDate, params));
	}

	public BigDecimal getCurrentExposure(SpecificRepoTrade trade) throws TradistaBusinessException {
		if (trade == null) {
			throw new TradistaBusinessException(String.format(TRADE_IS_MANDATORY));
		}
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> specificRepoPricerService.getCurrentExposure(trade));
	}

	public BigDecimal pnlDefault(SpecificRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> specificRepoPricerService.pnlDefault(trade, currency, pricingDate, params));
	}

	public BigDecimal realizedPayments(SpecificRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> specificRepoPricerService.realizedPayments(trade, currency, pricingDate, params));
	}

	public BigDecimal discountedPayments(SpecificRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil
				.runEx(() -> specificRepoPricerService.discountedPayments(trade, currency, pricingDate, params));
	}
}