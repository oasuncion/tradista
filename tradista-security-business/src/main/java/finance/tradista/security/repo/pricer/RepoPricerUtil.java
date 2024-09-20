package finance.tradista.security.repo.pricer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.model.CurrencyPair;
import finance.tradista.core.daycountconvention.model.DayCountConvention;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.marketdata.model.FXCurve;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaults;
import finance.tradista.core.processingorgdefaults.service.ProcessingOrgDefaultsBusinessDelegate;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.model.BondTrade;
import finance.tradista.security.bond.service.BondPricerBusinessDelegate;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.pricer.PricerEquityUtil;
import finance.tradista.security.repo.model.ProcessingOrgDefaultsCollateralManagementModule;
import finance.tradista.security.repo.model.RepoTrade;
import finance.tradista.security.repo.trade.RepoTradeUtil;

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

public final class RepoPricerUtil {

	private static ProcessingOrgDefaultsBusinessDelegate poDefaultsBusinessDelegate = new ProcessingOrgDefaultsBusinessDelegate();

	private static QuoteBusinessDelegate quoteBusinessDelegate = new QuoteBusinessDelegate();

	private static ConfigurationBusinessDelegate configurationBusinessDelegate = new ConfigurationBusinessDelegate();

	private static BondPricerBusinessDelegate bondPricerBusinessDelegate = new BondPricerBusinessDelegate();

	private RepoPricerUtil() {
	}

	public static BigDecimal getCollateralMarketToMarket(RepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		BigDecimal mtm;
		CurrencyPair pair = new CurrencyPair(trade.getCurrency(), currency);
		FXCurve paramTradeCcyPricingCcyFXCurve = params.getFxCurves().get(pair);
		if (paramTradeCcyPricingCcyFXCurve == null) {
			// TODO Add log warn
		}

		// 1. Get the current collateral
		Map<Security, Map<Book, BigDecimal>> securities = RepoTradeUtil.getAllocatedCollateral(trade);

		// 2. Get the MTM of the current collateral as of pricing date
		mtm = getCollateralMarketToMarket(securities, trade.getBook().getProcessingOrg(), pricingDate);

		if (!currency.equals(trade.getCurrency())) {
			mtm = PricerUtil.convertAmount(mtm, trade.getCurrency(), currency, pricingDate,
					params.getQuoteSet().getId(),
					paramTradeCcyPricingCcyFXCurve != null ? paramTradeCcyPricingCcyFXCurve.getId() : 0);
		}

		return mtm;
	}

	public static BigDecimal getCurrentCollateralMarketToMarket(RepoTrade trade) throws TradistaBusinessException {
		// 1. Get the current collateral
		Map<Security, Map<Book, BigDecimal>> securities = RepoTradeUtil.getAllocatedCollateral(trade);

		// 2. Get the MTM of the current collateral as of pricing date
		return getCollateralMarketToMarket(securities, trade.getBook().getProcessingOrg(), LocalDate.now());
	}

	public static BigDecimal getCollateralMarketToMarket(Map<Security, Map<Book, BigDecimal>> securities,
			LegalEntity po, LocalDate pricingDate) throws TradistaBusinessException {

		BigDecimal mtm = BigDecimal.ZERO;

		ProcessingOrgDefaults poDefaults = poDefaultsBusinessDelegate.getProcessingOrgDefaultsByPoId(po.getId());
		QuoteSet qs = ((ProcessingOrgDefaultsCollateralManagementModule) poDefaults
				.getModuleByName(ProcessingOrgDefaultsCollateralManagementModule.COLLATERAL_MANAGEMENT)).getQuoteSet();

		if (qs == null) {
			throw new TradistaBusinessException(
					String.format("The Collateral Quote Set for Processing Org Defaults of PO %s has not been found.",
							po.getShortName()));
		}

		// Calculate the total MTM value of the collateral

		if (securities != null) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : securities.entrySet()) {
				String quoteName = entry.getKey().getProductType() + "." + entry.getKey().getIsin() + "."
						+ entry.getKey().getExchange();
				QuoteValue qv = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(qs.getId(),
						quoteName, entry.getKey().getProductType().equals(Bond.BOND) ? QuoteType.BOND_PRICE
								: QuoteType.EQUITY_PRICE,
						pricingDate);
				if (qv == null) {
					throw new TradistaBusinessException(
							String.format("The security price %s could not be found on quote set %s as of %tD",
									quoteName, qs, LocalDate.now()));
				}
				BigDecimal price = qv.getClose() != null ? qv.getClose() : qv.getLast();
				if (price == null) {
					throw new TradistaBusinessException(String.format(
							"The closing or last price of the product %s could not be found on quote set %s as of %tD",
							entry.getKey(), qs, LocalDate.now()));
				}
				// Assumption : all securities are priced in the same currency.
				for (BigDecimal qty : entry.getValue().values()) {
					mtm = mtm.add(price.multiply(qty));
				}
			}
		}

		return mtm;

	}

	public static BigDecimal getExposure(RepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		BigDecimal exposure;
		BigDecimal rate;
		CurrencyPair pair = new CurrencyPair(trade.getCurrency(), currency);
		FXCurve paramTradeCcyPricingCcyFXCurve = params.getFxCurves().get(pair);
		if (paramTradeCcyPricingCcyFXCurve == null) {
			// TODO Add log warn
		}

		// Calculate the required exposure

		if (trade.isFixedRepoRate()) {
			rate = trade.getRepoRate();
		} else {
			if (!pricingDate.isAfter(LocalDate.now())) {
				rate = getFloatingRate(trade, pricingDate);
			} else {
				InterestRateCurve indexCurve = params.getIndexCurves().get(trade.getIndex());
				try {
					rate = PricerUtil.getForwardRate(indexCurve.getId(), pricingDate,
							DateUtil.addTenor(pricingDate, trade.getIndexTenor()), null);
				} catch (PricerException | TradistaBusinessException e) {
					throw new TradistaBusinessException(e);
				}
			}
		}

		exposure = trade.getAmount()
				.multiply(rate.multiply(PricerUtil.daysToYear(LocalDate.now(), trade.getEndDate())));

		// Apply the margin rate (by convention, margin rate is noted as follows: 105
		// for 5%)
		BigDecimal marginRate = trade.getMarginRate().divide(BigDecimal.valueOf(100));
		exposure = exposure.multiply(marginRate);

		if (!currency.equals(trade.getCurrency())) {
			exposure = PricerUtil.convertAmount(exposure, trade.getCurrency(), currency, pricingDate,
					params.getQuoteSet().getId(),
					paramTradeCcyPricingCcyFXCurve != null ? paramTradeCcyPricingCcyFXCurve.getId() : 0);
		}

		return exposure;
	}

	private static BigDecimal getFloatingRate(RepoTrade trade, LocalDate date) throws TradistaBusinessException {
		BigDecimal rate;
		ProcessingOrgDefaults poDefaults = poDefaultsBusinessDelegate
				.getProcessingOrgDefaultsByPoId(trade.getBook().getProcessingOrg().getId());
		QuoteSet qs = ((ProcessingOrgDefaultsCollateralManagementModule) poDefaults
				.getModuleByName(ProcessingOrgDefaultsCollateralManagementModule.COLLATERAL_MANAGEMENT)).getQuoteSet();

		if (qs == null) {
			throw new TradistaBusinessException(
					String.format("The Collateral Quote Set for Processing Org Defaults of PO %s has not been found.",
							trade.getBook().getProcessingOrg().getShortName()));
		}
		String quoteName = Index.INDEX + "." + trade.getIndex() + "." + trade.getIndexTenor();
		QuoteValue qv = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(qs.getId(), quoteName,
				QuoteType.INTEREST_RATE, date);
		if (qv == null) {
			throw new TradistaBusinessException(String.format(
					"The index %s could not be found on quote set %s as of %tD", quoteName, qs, LocalDate.now()));
		}
		// the index is expected to be defined as quote closing value.
		rate = qv.getClose();
		if (rate == null) {
			throw new TradistaBusinessException(
					String.format("The index %s (closing value) could not be found on quote set %s as of %tD",
							quoteName, qs, LocalDate.now()));
		}
		return rate;
	}

	public static BigDecimal getCurrentExposure(RepoTrade trade) throws TradistaBusinessException {
		BigDecimal exposure;
		BigDecimal rate;

		// Calculate the required exposure

		if (trade.isFixedRepoRate()) {
			rate = trade.getRepoRate();
		} else {
			rate = getFloatingRate(trade, LocalDate.now());
		}

		exposure = trade.getAmount()
				.multiply(rate.multiply(PricerUtil.daysToYear(LocalDate.now(), trade.getEndDate())));

		// Apply the margin rate (by convention, margin rate is noted as follows: 105
		// for 5%)
		BigDecimal marginRate = trade.getMarginRate().divide(BigDecimal.valueOf(100));
		exposure = exposure.multiply(marginRate);

		return exposure;
	}

	public static List<CashFlow> generateCashFlows(PricingParameter params, RepoTrade trade, LocalDate pricingDate)
			throws TradistaBusinessException {

		List<CashFlow> cfs = new ArrayList<>();

		if (trade.getEndDate() == null) {
			throw new TradistaBusinessException("It is not possible to forecast cashflows of an open repo.");
		}

		if (!LocalDate.now().isBefore(trade.getEndDate())) {
			throw new TradistaBusinessException(
					"When the repo end date has passed, it is not possible to forecast cashflows.");
		}

		if (!pricingDate.isBefore(trade.getEndDate())) {
			throw new TradistaBusinessException(
					"When the pricing date is after the repo end date, it is not possible to forecast cashflows.");
		}

		if (!LocalDate.now().isBefore(trade.getSettlementDate())) {
			if (pricingDate.isBefore(LocalDate.now())) {
				throw new TradistaBusinessException(
						"When the trade settlement date has passed and a pricing date is in the past, it is not possible to forecast cashflows.");
			}
		}

		InterestRateCurve indexCurve = null;
		if (!trade.isFixedRepoRate()) {
			indexCurve = params.getIndexCurves().get(trade.getIndex());
			if (indexCurve == null) {
				throw new TradistaBusinessException(String.format(
						"%s Pricing Parameter doesn't contain an Index Curve for %s. please add it or change the Pricing Parameter.",
						params.getName(), trade.getIndex()));
			}
		}

		if (!trade.getSettlementDate().isBefore(pricingDate)) {
			// CashFlow for cash of opening leg
			CashFlow cashOpeningLeg = new CashFlow();
			cashOpeningLeg.setDate(trade.getSettlementDate());
			cashOpeningLeg.setAmount(trade.getAmount());
			cashOpeningLeg.setCurrency(trade.getCurrency());
			cashOpeningLeg.setPurpose(TransferPurpose.CASH_SETTLEMENT);
			cashOpeningLeg.setDirection(trade.isBuy() ? CashFlow.Direction.RECEIVE : CashFlow.Direction.PAY);
			cfs.add(cashOpeningLeg);
		}

		// Retrieve partial terminations after pricing date and generate cashflow for
		// each of them
		Map<LocalDate, BigDecimal> ptMap = trade.getPartialTerminations();
		if (ptMap != null && !ptMap.isEmpty()) {
			ptMap = ptMap.entrySet().stream().filter(e -> (!e.getKey().isBefore(pricingDate)))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			if (!ptMap.isEmpty()) {
				for (Map.Entry<LocalDate, BigDecimal> pt : ptMap.entrySet()) {
					BigDecimal interestAmount = null;
					BigDecimal repoRate = null;
					if (trade.isFixedRepoRate()) {
						repoRate = trade.getRepoRate();
					} else {
						try {
							repoRate = PricerUtil.getInterestRateAsOfDate(
									trade.getIndex().getName() + "." + trade.getIndexTenor(),
									params.getQuoteSet().getId(), indexCurve.getId(), trade.getIndexTenor(), null,
									pt.getKey());
							repoRate = repoRate.add(trade.getIndexOffset());
						} catch (PricerException pe) {
							throw new TradistaBusinessException(pe.getMessage());
						}
					}
					repoRate = repoRate.divide(new BigDecimal(100), configurationBusinessDelegate.getScale(),
							configurationBusinessDelegate.getRoundingMode());
					interestAmount = pt.getValue().multiply(repoRate)
							.multiply(PricerUtil.daysToYear(new DayCountConvention(DayCountConvention.ACT_360),
									trade.getSettlementDate(), pt.getKey()));
					CashFlow cashPt = new CashFlow();
					CashFlow.Direction direction;
					cashPt.setDate(pt.getKey());
					cashPt.setAmount(pt.getValue().add(interestAmount));
					cashPt.setCurrency(trade.getCurrency());
					cashPt.setPurpose(TransferPurpose.CASH_SETTLEMENT);
					if (trade.isBuy()) {
						if (cashPt.getAmount().signum() > 0) {
							direction = CashFlow.Direction.PAY;
						} else {
							direction = CashFlow.Direction.RECEIVE;
							cashPt.setAmount(cashPt.getAmount().negate());
						}
					} else {
						if (cashPt.getAmount().signum() > 0) {
							direction = CashFlow.Direction.RECEIVE;
						} else {
							direction = CashFlow.Direction.PAY;
							cashPt.setAmount(cashPt.getAmount().negate());
						}
					}
					cashPt.setDirection(direction);
					cfs.add(cashPt);
				}
			}

		}

		// Cashflow for cash of closing leg
		CashFlow cashClosingLeg = new CashFlow();
		BigDecimal amount;
		CashFlow.Direction direction;
		if (trade.isFixedRepoRate()) {
			BigDecimal repoRate = trade.getRepoRate().divide(new BigDecimal(100),
					configurationBusinessDelegate.getScale(), configurationBusinessDelegate.getRoundingMode());
			BigDecimal interestAmount = trade.getAmount().multiply(repoRate).multiply(PricerUtil.daysToYear(
					new DayCountConvention(DayCountConvention.ACT_360), trade.getSettlementDate(), trade.getEndDate()));
			amount = trade.getAmount().add(interestAmount);
		} else {
			List<LocalDate> dates = trade.getSettlementDate().datesUntil(trade.getEndDate()).toList();
			BigDecimal repoRate = BigDecimal.ZERO;
			StringBuilder errorMsg = new StringBuilder();

			for (LocalDate date : dates) {
				try {
					BigDecimal currentRate = PricerUtil.getInterestRateAsOfDate(
							trade.getIndex() + "." + trade.getIndexTenor(), params.getQuoteSet().getId(),
							indexCurve.getId(), trade.getIndexTenor(), null, date);
					if (currentRate != null) {
						repoRate = repoRate.add(currentRate);
					} else {
						errorMsg.append(String.format("%tD ", date));
					}
				} catch (PricerException pe) {
					errorMsg.append(String.format("%tD ", date));
				}
			}
			if (errorMsg.length() > 0) {
				errorMsg = new StringBuilder(
						"Repo closing leg cashflow cannot be calculated. Impossible to calculate the rate for dates : ")
						.append(errorMsg);
				throw new TradistaBusinessException(errorMsg.toString());
			}

			repoRate = repoRate.divide(new BigDecimal(dates.size()));
			repoRate = repoRate.add(trade.getIndexOffset());
			repoRate = repoRate.divide(new BigDecimal(100), configurationBusinessDelegate.getScale(),
					configurationBusinessDelegate.getRoundingMode());

			amount = trade.getAmount().multiply(repoRate);
		}

		if (trade.isBuy()) {
			if (amount.signum() > 0) {
				direction = CashFlow.Direction.PAY;
			} else {
				direction = CashFlow.Direction.RECEIVE;
				amount = amount.negate();
			}
		} else {
			if (amount.signum() > 0) {
				direction = CashFlow.Direction.RECEIVE;
			} else {
				direction = CashFlow.Direction.PAY;
				amount = amount.negate();
			}
		}

		cashClosingLeg.setAmount(amount);
		cashClosingLeg.setDirection(direction);
		cashClosingLeg.setDate(trade.getEndDate());
		cashClosingLeg.setCurrency(trade.getCurrency());
		cashClosingLeg.setPurpose(TransferPurpose.RETURNED_CASH_PLUS_INTEREST);

		cfs.add(cashClosingLeg);

		InterestRateCurve discountCurve = params.getDiscountCurves().get(trade.getCurrency());
		if (discountCurve == null) {
			// TODO Add log warn
		}

		if (discountCurve != null) {
			try {
				PricerUtil.discountCashFlows(cfs, pricingDate, discountCurve.getId(), null);
			} catch (PricerException pe) {
				throw new TradistaBusinessException(pe.getMessage());
			}
		}

		return cfs;
	}

	public static BigDecimal pnlDefault(RepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		return realizedPayments(trade, currency, pricingDate, params)
				.add(discountedPayments(trade, currency, pricingDate, params));
	}

	public static BigDecimal realizedPayments(RepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		BigDecimal pnl;
		Currency tradeCurrency = trade.getCurrency();
		CurrencyPair pair = new CurrencyPair(tradeCurrency, currency);
		FXCurve paramFXCurve = params.getFxCurves().get(pair);
		if (paramFXCurve == null) {
			// TODO Add log warn
		}

		// Payment for the opening leg
		if (pricingDate.isAfter(trade.getSettlementDate()) || pricingDate.equals(trade.getSettlementDate())) {

			BigDecimal openingLegAmount = trade.getAmount();

			// if there are partial terminations, rebuild the initial nominal from them
			if (trade.getPartialTerminations() != null && !trade.getPartialTerminations().isEmpty()) {
				openingLegAmount = openingLegAmount
						.add(trade.getPartialTerminations().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
			}

			// Finally apply the conversion to the pricing currency
			openingLegAmount = PricerUtil.convertAmount(openingLegAmount, tradeCurrency, currency, pricingDate,
					params.getQuoteSet().getId(), paramFXCurve != null ? paramFXCurve.getId() : 0);

			if (trade.isSell()) {
				openingLegAmount = openingLegAmount.negate();
			}

			pnl = openingLegAmount;

			// Payment for the closing leg
			if (pricingDate.isAfter(trade.getEndDate()) || pricingDate.equals(trade.getEndDate())) {

				// 1. Get the closing payment amount
				BigDecimal closingLegAmount = RepoTradeUtil.getClosingLegPayment(trade, params.getQuoteSet().getId(),
						new DayCountConvention(DayCountConvention.ACT_360));

				// Finally apply the conversion to the pricing currency
				closingLegAmount = PricerUtil.convertAmount(closingLegAmount, tradeCurrency, currency, pricingDate,
						params.getQuoteSet().getId(), paramFXCurve != null ? paramFXCurve.getId() : 0);

				if (trade.isBuy()) {
					closingLegAmount = closingLegAmount.negate();
				}

				pnl = pnl.add(closingLegAmount);

			}

			// Add the partial terminations
			if (trade.getPartialTerminations() != null && !trade.getPartialTerminations().isEmpty()) {
				BigDecimal partialTerminations = BigDecimal.ZERO;
				for (Map.Entry<LocalDate, BigDecimal> e : trade.getPartialTerminations().entrySet()) {
					if (!pricingDate.isAfter(e.getKey())) {
						partialTerminations = partialTerminations.add(e.getValue());
					}
				}
				if (trade.isBuy()) {
					partialTerminations = partialTerminations.negate();
				}
				pnl = pnl.add(partialTerminations);

			}

			return pnl;
		}

		// if pricing date is before settlement date, there is no realized pnl.
		return BigDecimal.ZERO;
	}

	public static BigDecimal discountedPayments(RepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		BigDecimal pnl = BigDecimal.ZERO;
		DayCountConvention dcc = new DayCountConvention(DayCountConvention.ACT_360);
		if (pricingDate.isAfter(trade.getEndDate()) || pricingDate.equals(trade.getEndDate())) {
			// if pricing date is equal to or after the repo end date,
			// the pnl is already realized
			return pnl;
		}

		// Payment for the opening leg

		if (LocalDate.now().isBefore(trade.getSettlementDate()) && pricingDate.isBefore(trade.getSettlementDate())) {
			try {
				Currency tradeCurrency = trade.getCurrency();
				CurrencyPair pair = new CurrencyPair(tradeCurrency, currency);
				FXCurve paramFXCurve = params.getFxCurves().get(pair);
				if (paramFXCurve == null) {
					// TODO Add log warn
				}
				// 1. Trade currency IR curve retrieval
				InterestRateCurve paramTradeCurrIRCurve = params.getDiscountCurves().get(tradeCurrency);
				if (paramTradeCurrIRCurve == null) {
					throw new TradistaBusinessException(String.format(
							"%s Pricing Parameter doesn't contain a discount curve for currency %s. please add it or change the Pricing Parameter.",
							params.getName(), tradeCurrency));
				}

				// 2. Discount the opening leg payment
				BigDecimal discountedOpeningLegPayment = PricerUtil.discountAmount(trade.getAmount(),
						paramTradeCurrIRCurve.getId(), pricingDate, trade.getSettlementDate(), dcc);

				// Finally apply the conversion to the pricing currency
				discountedOpeningLegPayment = PricerUtil.convertAmount(discountedOpeningLegPayment, tradeCurrency,
						currency, pricingDate, params.getQuoteSet().getId(),
						paramFXCurve != null ? paramFXCurve.getId() : 0);

				if (trade.isSell()) {
					discountedOpeningLegPayment = discountedOpeningLegPayment.negate();
				}

				pnl = discountedOpeningLegPayment;
			} catch (PricerException pe) {
				pe.printStackTrace();
				throw new TradistaBusinessException(pe.getMessage());
			}
		}

		// Payment for the closing leg
		if (LocalDate.now().isBefore(trade.getEndDate())) {
			try {
				Currency tradeCurrency = trade.getCurrency();
				CurrencyPair pair = new CurrencyPair(tradeCurrency, currency);
				FXCurve paramFXCurve = params.getFxCurves().get(pair);
				if (paramFXCurve == null) {
					// TODO Add log warn
				}
				// 1. Primary currency IR curve retrieval
				InterestRateCurve paramTradeCurrIRCurve = params.getDiscountCurves().get(tradeCurrency);
				if (paramTradeCurrIRCurve == null) {
					throw new TradistaBusinessException(String.format(
							"%s Pricing Parameter doesn't contain a discount curve for currency %s. please add it or change the Pricing Parameter.",
							params.getName(), tradeCurrency));
				}
				// 2. Get the closing payment amount
				BigDecimal amount = RepoTradeUtil.getClosingLegPayment(trade, params.getQuoteSet().getId(), dcc);

				// 3. Discount the closing leg payment
				BigDecimal discountedClosingLegPayment = PricerUtil.discountAmount(amount,
						paramTradeCurrIRCurve.getId(), pricingDate, trade.getSettlementDate(), dcc);

				// Finally apply the conversion to the pricing currency
				discountedClosingLegPayment = PricerUtil.convertAmount(discountedClosingLegPayment, tradeCurrency,
						currency, pricingDate, params.getQuoteSet().getId(),
						paramFXCurve != null ? paramFXCurve.getId() : 0);

				if (trade.isBuy()) {
					discountedClosingLegPayment = discountedClosingLegPayment.negate();
				}

				pnl = pnl.add(discountedClosingLegPayment);
			} catch (PricerException pe) {
				pe.printStackTrace();
				throw new TradistaBusinessException(pe.getMessage());
			}
		}

		return pnl;
	}

	public static BigDecimal getDelta(RepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {

		if (!pricingDate.isAfter(trade.getSettlementDate())) {
			throw new TradistaBusinessException(
					"Delta cannot be calculated when the pricing date is not after the repo trade settlement date");
		}
		// 1. Trade currency IR curve retrieval
		InterestRateCurve paramTradeCurrIRCurve = params.getDiscountCurves().get(trade.getCurrency());
		if (paramTradeCurrIRCurve == null) {
			throw new TradistaBusinessException(String.format(
					"%s Pricing Parameter doesn't contain a discount curve for currency %s. please add it or change the Pricing Parameter.",
					params.getName(), trade.getCurrency()));
		}
		// 2. Get collateral prices variations

		// 2.a Get the collaterals
		Map<Security, Map<Book, BigDecimal>> allocatedSecurities = RepoTradeUtil.getAllocatedCollateral(trade);

		BigDecimal collateralsPricesVariation = BigDecimal.ZERO;

		// 2.b browse the collaterals and calculate the prices variations
		if (!ObjectUtils.isEmpty(allocatedSecurities)) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : allocatedSecurities.entrySet()) {
				Security security = entry.getKey();
				BigDecimal quantity = entry.getValue().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
				String quoteName = security.getProductType() + "." + security.getIsin() + "." + security.getExchange();
				QuoteType quoteType = security.getProductType().equals(Bond.BOND) ? QuoteType.BOND_PRICE
						: QuoteType.EQUITY_PRICE;
				// Get price as of trade settlement date
				BigDecimal initialPrice = PricerUtil.getValueAsOfDateFromQuote(quoteName, params.getQuoteSet().getId(),
						quoteType, QuoteValue.CLOSE, trade.getSettlementDate());
				BigDecimal price = null;
				if (pricingDate.isBefore(LocalDate.now()) || pricingDate.isEqual(LocalDate.now())) {
					String quoteValueType = pricingDate.isBefore(LocalDate.now()) ? QuoteValue.CLOSE : QuoteValue.LAST;
					price = PricerUtil.getValueAsOfDateFromQuote(quoteName, params.getQuoteSet().getId(), quoteType,
							quoteValueType, pricingDate);
					if (price == null) {
						throw new TradistaBusinessException(String.format(
								"Price for security %s cannot be found as of %tD (quote name: %s, quote type: %s, quote value type: %s, quote set: %s)",
								security.getIsin(), pricingDate, quoteName, quoteType, quoteValueType,
								params.getQuoteSet()));
					}
				}
				if (pricingDate.isAfter(LocalDate.now())) {
					if (security.getProductType().equals(Bond.BOND)) {
						// Create a dummy bond trade for determination of the bond clean price
						BondTrade dummyTrade = new BondTrade();
						dummyTrade.setCounterparty(trade.getCounterparty());
						dummyTrade.setAmount(BigDecimal.ONE);
						dummyTrade.setQuantity(BigDecimal.ONE);
						dummyTrade.setTradeDate(trade.getTradeDate());
						dummyTrade.setSettlementDate(trade.getSettlementDate());
						dummyTrade.setBook(trade.getBook());
						dummyTrade.setBuySell(trade.isBuy());
						dummyTrade.setProduct((Bond) security);
						price = bondPricerBusinessDelegate.cleanPriceDiscountedCashFlow(params, dummyTrade, currency,
								pricingDate);
					} else {
						try {
							price = PricerEquityUtil.getEquityPrice(params, (Equity) security, pricingDate);
						} catch (PricerException pe) {
							throw new TradistaBusinessException(pe);
						}
					}
				}
				collateralsPricesVariation = collateralsPricesVariation
						.add((price.subtract(initialPrice)).multiply(quantity));
			}
		} else {
			throw new TradistaBusinessException(
					"Delta cannot be calculated: there is no allocated collateral for the repo trade.");
		}
		// 3. Convert the collateral prices variation in pricing currency
		collateralsPricesVariation = PricerUtil.convertAmount(collateralsPricesVariation, currency, currency,
				pricingDate, params.getQuoteSet().getId(), paramTradeCurrIRCurve.getId());

		// 4. Get the repo trade value variation
		// 4.a Get the repo trade value as of trade settlement date
		BigDecimal initialRepoTradePrice = discountedPayments(trade, currency, trade.getSettlementDate(), params);
		// 4.b Get the repo trade value as of pricing date
		BigDecimal repoTradePrice = discountedPayments(trade, currency, pricingDate, params);
		BigDecimal repoTradeValueVariation = repoTradePrice.subtract(initialRepoTradePrice);

		// 5. Convert the repo trade value variation in pricing currency
		repoTradeValueVariation = PricerUtil.convertAmount(repoTradeValueVariation, currency, currency, pricingDate,
				params.getQuoteSet().getId(), paramTradeCurrIRCurve.getId());

		// 6. Calculate the delta : repo trade value variation in pricing currency /
		// collateral prices variation in pricing currency
		return repoTradeValueVariation.divide(collateralsPricesVariation,
				configurationBusinessDelegate.getRoundingMode());
	}

}