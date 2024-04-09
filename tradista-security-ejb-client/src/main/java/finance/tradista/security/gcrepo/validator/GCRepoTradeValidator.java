package finance.tradista.security.gcrepo.validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.validator.DefaultTradeValidator;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.gcrepo.model.GCRepoTrade;

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

public class GCRepoTradeValidator extends DefaultTradeValidator {

	private static final long serialVersionUID = 8195327334102296257L;

	@Override
	public void validateTrade(Trade<? extends Product> trade) throws TradistaBusinessException {
		GCRepoTrade gcRepoTrade = (GCRepoTrade) trade;
		StringBuilder errMsg = validateTradeBasics(trade);

		// Other business controls
		if (trade.getAmount() != null && trade.getAmount().doubleValue() <= 0) {
			errMsg.append(String.format("The amount (%s) must be positive.%n", trade.getAmount().doubleValue()));
		}

		if (trade.getSettlementDate() == null) {
			errMsg.append(String.format("The start date is mandatory.%n"));
		} else {
			if (gcRepoTrade.getEndDate() != null) {
				if (!gcRepoTrade.getEndDate().isAfter(trade.getSettlementDate())) {
					errMsg.append(String.format("The end date date (%s) cannot be before the start date (%s).%n",
							gcRepoTrade.getEndDate(), trade.getSettlementDate()));
				}
			}
		}

		if (gcRepoTrade.getMarginRate() == null) {
			errMsg.append(String.format("The margin rate is mandatory.%n"));
		} else if (gcRepoTrade.getMarginRate().doubleValue() <= 0) {
			errMsg.append(String.format("The margin rate (%s) must be positive.%n", trade.getAmount().doubleValue()));
		}

		if (gcRepoTrade.isTerminableOnDemand()) {
			if (gcRepoTrade.getNoticePeriod() < 0) {
				errMsg.append(
						String.format("The notice period (%s) must be positive.%n", gcRepoTrade.getNoticePeriod()));
			}
		} else {
			if (gcRepoTrade.getEndDate() == null) {
				errMsg.append(
						String.format("When the GC Repo is not terminable on demand, the end date is mandatory.%n"));
			}
		}

		if (gcRepoTrade.getGcBasket() == null) {
			errMsg.append(String.format("The GC basket is mandatory.%n"));
		}

		// For the moment, only GC Repos support Workflows.
		if (gcRepoTrade.getStatus() == null) {
			errMsg.append(String.format("The status is mandatory.%n"));
		}

		// surface checks of collateral to add
		if (gcRepoTrade.getCollateralToAdd() != null && !gcRepoTrade.getCollateralToAdd().isEmpty()) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : gcRepoTrade.getCollateralToAdd().entrySet()) {
				if (entry.getKey() == null) {
					errMsg.append("One of the securities to add as collateral is null, please check.%n");
					continue;
				}
				if (entry.getValue() == null) {
					errMsg.append(String.format(
							"Origin book and quantity of the security %s to add as collateral is null , please check.%n",
							entry.getKey()));
				} else {
					Map<Book, BigDecimal> bookMap = entry.getValue();
					for (Map.Entry<Book, BigDecimal> bookEntry : bookMap.entrySet()) {
						if (bookEntry.getKey() == null) {
							errMsg.append(String.format(
									"One origin book of the security %s to add as collateral is null , please check.%n",
									entry.getKey()));
							continue;
						}
						if (bookEntry.getValue() == null) {
							errMsg.append(String.format(
									"Quantity of the security %s with origin book %s to add as collateral is null , please check.%n",
									entry.getKey(), bookEntry.getKey()));
						}
					}

				}
			}
		}

		// surface checks of collateral to remove
		if (gcRepoTrade.getCollateralToRemove() != null && !gcRepoTrade.getCollateralToRemove().isEmpty()) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : gcRepoTrade.getCollateralToRemove().entrySet()) {
				if (entry.getKey() == null) {
					errMsg.append("One of the securities to remove as collateral is null, please check.%n");
					continue;
				}
				if (entry.getValue() == null) {
					errMsg.append(String.format(
							"Book and quantity of the security %s to remove as collateral is null , please check.%n",
							entry.getKey()));
				} else {
					Map<Book, BigDecimal> bookMap = entry.getValue();
					for (Map.Entry<Book, BigDecimal> bookEntry : bookMap.entrySet()) {
						if (bookEntry.getKey() == null) {
							errMsg.append(String.format(
									"Book of the security %s to remove as collateral is null , please check.%n",
									entry.getKey()));
							continue;
						}
						if (bookEntry.getValue() == null) {
							errMsg.append(String.format(
									"Quantity of the security %s with book %s to remove as collateral is null , please check.%n",
									entry.getKey(), bookEntry.getKey()));
						}
					}

				}
			}
		}

		// surface checks of partial terminations values
		if (gcRepoTrade.getPartialTerminations() != null && !gcRepoTrade.getPartialTerminations().isEmpty()) {
			for (Map.Entry<LocalDate, BigDecimal> entry : gcRepoTrade.getPartialTerminations().entrySet()) {
				if (entry.getKey() == null) {
					errMsg.append("One of the partial termination dates is null, please check.%n");
					continue;
				} else {
					if (entry.getKey().isBefore(gcRepoTrade.getSettlementDate())
							|| entry.getKey().isAfter(gcRepoTrade.getEndDate())) {
						errMsg.append(String.format(
								"One of the partial termination date (%tD) is not between trade settlement and end dates, please check.%n",
								entry.getKey()));
					}
				}
				if (entry.getValue() == null) {
					errMsg.append(
							String.format("Partial termination reduction amount is null for date %tD , please check.%n",
									entry.getKey()));
				} else {
					if (entry.getValue().signum() <= 0) {
						errMsg.append(String.format(
								"Partial termination amount (%s) for date %tD must be positive, please check.%n",
								entry.getValue(), entry.getKey()));
					}
				}
			}
		}

		if (!errMsg.isEmpty()) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}