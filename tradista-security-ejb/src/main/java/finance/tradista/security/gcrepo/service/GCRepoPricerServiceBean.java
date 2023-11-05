package finance.tradista.security.gcrepo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.configuration.service.ConfigurationService;
import finance.tradista.core.daycountconvention.model.DayCountConvention;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

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
public class GCRepoPricerServiceBean implements GCRepoPricerService {

	@EJB
	protected ConfigurationService configurationService;

	@Override
	public List<CashFlow> generateCashFlows(PricingParameter params, GCRepoTrade trade, LocalDate pricingDate)
			throws TradistaBusinessException {

		List<CashFlow> cfs = new ArrayList<CashFlow>();

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

		// CashFlow for cash of opening leg
		CashFlow cashOpeningLeg = new CashFlow();
		cashOpeningLeg.setDate(trade.getSettlementDate());
		cashOpeningLeg.setAmount(trade.getAmount());
		cashOpeningLeg.setCurrency(trade.getCurrency());
		cashOpeningLeg.setPurpose(TransferPurpose.CASH_SETTLEMENT);
		cashOpeningLeg.setDirection(trade.isBuy() ? CashFlow.Direction.RECEIVE : CashFlow.Direction.PAY);
		cfs.add(cashOpeningLeg);

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
			List<LocalDate> dates = trade.getSettlementDate().datesUntil(trade.getEndDate())
					.collect(Collectors.toList());
			BigDecimal repoRate = BigDecimal.ZERO;
			StringBuilder errorMsg = new StringBuilder();

			for (LocalDate date : dates) {
				try {
					repoRate.add(PricerUtil.getInterestRateAsOfDate(trade.getIndex() + "." + trade.getIndexTenor(),
							params.getQuoteSet().getId(), indexCurve.getId(), trade.getIndexTenor(), null, date));
				} catch (PricerException pe) {
					errorMsg.append(String.format("%tD ", date));
				}
			}
			if (errorMsg.length() > 0) {
				errorMsg = new StringBuilder(String.format(
						"Repo closing leg cashfow cannot be calculated. Impossible to calculate the rate for dates : "))
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
		cashClosingLeg.setDate(trade.getSettlementDate());
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