package finance.tradista.security.gcrepo.service;

import java.math.BigDecimal;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.security.common.model.Security;
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

public class GCRepoTradeBusinessDelegate {

	private GCRepoTradeService gcRepoTradeService;

	private GCRepoTradeValidator validator;

	private static final String TRADE_ID_MUST_BE_POSITIVE = "The trade id must be positive.";

	public GCRepoTradeBusinessDelegate() {
		gcRepoTradeService = TradistaServiceLocator.getInstance().getGCRepoTradeService();
		validator = new GCRepoTradeValidator();
	}

	public long saveGCRepoTrade(GCRepoTrade trade, String action) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> gcRepoTradeService.saveGCRepoTrade(trade, action));
	}

	public GCRepoTrade getGCRepoTradeById(long tradeId) throws TradistaBusinessException {
		if (tradeId <= 0) {
			throw new TradistaBusinessException(TRADE_ID_MUST_BE_POSITIVE);
		}
		return SecurityUtil.run(() -> gcRepoTradeService.getGCRepoTradeById(tradeId));
	}

	public Map<Security, Map<Book, BigDecimal>> getAllocatedCollateral(GCRepoTrade trade)
			throws TradistaBusinessException {
		if (trade == null) {
			throw new TradistaBusinessException("The trade is mandatory.");
		}
		return SecurityUtil.runEx(() -> gcRepoTradeService.getAllocatedCollateral(trade));
	}

}