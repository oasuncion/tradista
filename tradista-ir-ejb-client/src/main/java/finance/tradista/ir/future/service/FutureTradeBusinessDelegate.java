package finance.tradista.ir.future.service;

import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.ir.future.model.FutureTrade;
import finance.tradista.ir.future.validator.FutureTradeValidator;

/*
 * Copyright 2018 Olivier Asuncion
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

public class FutureTradeBusinessDelegate {

	private FutureTradeService futureTradeService;

	private FutureTradeValidator validator;

	public FutureTradeBusinessDelegate() {
		futureTradeService = TradistaServiceLocator.getInstance().getFutureTradeService();
		validator = new FutureTradeValidator();
	}

	public long saveFutureTrade(FutureTrade trade) throws TradistaBusinessException {
		validator.validateTrade(trade);
		return SecurityUtil.runEx(() -> futureTradeService.saveFutureTrade(trade));
	}

	public List<FutureTrade> getFutureTradesBeforeTradeDateByFutureAndBookIds(LocalDate date, long futureId,
			long bookId) throws TradistaBusinessException {
		if (date == null) {
			throw new TradistaBusinessException("The date is mandatory.");
		}
		return SecurityUtil
				.run(() -> futureTradeService.getFutureTradesBeforeTradeDateByFutureAndBookIds(date, futureId, bookId));
	}

	public FutureTrade getFutureTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> futureTradeService.getFutureTradeById(id));
	}

}