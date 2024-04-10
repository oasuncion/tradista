package finance.tradista.security.gcrepo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.configuration.service.ConfigurationService;
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
import finance.tradista.core.processingorgdefaults.service.ProcessingOrgDefaultsService;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.model.ProcessingOrgDefaultsCollateralManagementModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

/*
 * Copyright 2023 Olivier Asuncion
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

@SecurityDomain(value = "other")
@PermitAll
@Stateless
@Interceptors(GCRepoProductScopeFilteringInterceptor.class)
public class GCRepoPricerServiceBean implements GCRepoPricerService {

	@EJB
	protected ConfigurationService configurationService;

	@EJB
	protected GCRepoTradeService gcRepoTradeService;

	@EJB
	protected ProcessingOrgDefaultsService poDefaultsService;

	private QuoteBusinessDelegate quoteBusinessDelegate;

	@PostConstruct
	private void initialize() {
		quoteBusinessDelegate = new QuoteBusinessDelegate();
	}

	@Override
	public BigDecimal getCollateralMarketToMarket(GCRepoTrade trade, Currency currency, LocalDate pricingDate,
			PricingParameter params) throws TradistaBusinessException {
		BigDecimal mtm;
		CurrencyPair pair = new CurrencyPair(trade.getCurrency(), currency);
		FXCurve paramTradeCcyPricingCcyFXCurve = params.getFxCurves().get(pair);
		if (paramTradeCcyPricingCcyFXCurve == null) {
			// TODO Add log warn
		}

		// 1. Get the current collateral
		Map<Security, Map<Book, BigDecimal>> securities = gcRepoTradeService.getAllocatedCollateral(trade);

		// 2. Get the MTM of the current collateral as of pricing date
		mtm = getCollateralMarketToMarket(securities, trade.getBook().getProcessingOrg(), pricingDate);

		if (!currency.equals(trade.getCurrency())) {
			mtm = PricerUtil.convertAmount(mtm, trade.getCurrency(), currency, pricingDate,
					params.getQuoteSet().getId(),
					paramTradeCcyPricingCcyFXCurve != null ? paramTradeCcyPricingCcyFXCurve.getId() : 0);
		}

		return mtm;
	}

	@Override
	public BigDecimal getCurrentCollateralMarketToMarket(GCRepoTrade trade) throws TradistaBusinessException {
		// 1. Get the current collateral
		Map<Security, Map<Book, BigDecimal>> securities = gcRepoTradeService.getAllocatedCollateral(trade);

		// 2. Get the MTM of the current collateral as of pricing date
		return getCollateralMarketToMarket(securities, trade.getBook().getProcessingOrg(), LocalDate.now());
	}

	@Override
	public BigDecimal getCollateralMarketToMarket(Map<Security, Map<Book, BigDecimal>> securities, LegalEntity po,
			LocalDate pricingDate) throws TradistaBusinessException {

		BigDecimal mtm = BigDecimal.ZERO;

		ProcessingOrgDefaults poDefaults = poDefaultsService.getProcessingOrgDefaultsByPoId(po.getId());
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

	@Override
	public BigDecimal getExposure(GCRepoTrade trade, Currency currency, LocalDate pricingDate, PricingParameter params)
			throws TradistaBusinessException {
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

	@Override
	public BigDecimal getCurrentExposure(GCRepoTrade trade) throws TradistaBusinessException {
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

	private BigDecimal getFloatingRate(GCRepoTrade trade, LocalDate date) throws TradistaBusinessException {
		BigDecimal rate;
		ProcessingOrgDefaults poDefaults = poDefaultsService
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

	@Override
	public List<CashFlow> generateCashFlows(PricingParameter params, GCRepoTrade trade, LocalDate pricingDate)
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
					repoRate = repoRate.divide(new BigDecimal(100), configurationService.getScale(),
							configurationService.getRoundingMode());
					interestAmount = pt.getValue().multiply(repoRate).multiply(PricerUtil
							.daysToYear(new DayCountConvention("ACT/360"), trade.getSettlementDate(), pt.getKey()));
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
			BigDecimal repoRate = trade.getRepoRate().divide(new BigDecimal(100), configurationService.getScale(),
					configurationService.getRoundingMode());
			BigDecimal interestAmount = trade.getAmount().multiply(repoRate).multiply(PricerUtil
					.daysToYear(new DayCountConvention("ACT/360"), trade.getSettlementDate(), trade.getEndDate()));
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
			repoRate = repoRate.divide(new BigDecimal(100), configurationService.getScale(),
					configurationService.getRoundingMode());

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

}