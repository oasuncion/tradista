package finance.tradista.security.gcrepo.service;

import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.validator.GCRepoTradeValidator;

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

public class GCRepoPricerBusinessDelegate {

	private GCRepoPricerService gcRepoPricerService;

	private GCRepoTradeValidator validator;

	public GCRepoPricerBusinessDelegate() {
		gcRepoPricerService = TradistaServiceLocator.getInstance().getGCRepoPricerService();
		validator = new GCRepoTradeValidator();
	}

	public List<CashFlow> generateCashFlows(GCRepoTrade trade, PricingParameter pp, LocalDate pricingDate)
			throws TradistaBusinessException {
		StringBuffer errorMsg = new StringBuffer();
		if (trade == null) {
			errorMsg.append(String.format("The trade cannot be null.%n"));
		}
		if (pp == null) {
			errorMsg.append(String.format("The pricing parameters cannot be null.%n"));
		}
		if (pricingDate == null) {
			errorMsg.append(String.format("The pricing date cannot be null.%n"));
		}
		if (errorMsg.length() > 0) {
			throw new TradistaBusinessException(errorMsg.toString());
		}
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> gcRepoPricerService.generateCashFlows(pp, trade, pricingDate));
	}

}