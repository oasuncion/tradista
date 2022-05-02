package finance.tradista.fx.fxndf.service;

import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.fx.fx.service.AbstractFXTradeBusinessDelegate;
import finance.tradista.fx.fxndf.model.FXNDFTrade;
import finance.tradista.fx.fxndf.service.FXNDFTradeService;
import finance.tradista.fx.fxndf.validator.FXNDFTradeValidator;

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

public class FXNDFTradeBusinessDelegate extends AbstractFXTradeBusinessDelegate<FXNDFTrade> {

	private FXNDFTradeService fxNDFTradeService;

	private FXNDFTradeValidator validator;

	public FXNDFTradeBusinessDelegate() {
		fxNDFTradeService = TradistaServiceLocator.getInstance().getFXNDFTradeService();
		validator = new FXNDFTradeValidator();
	}

	public long saveFXNDFTrade(FXNDFTrade trade) throws TradistaBusinessException {

		validator.validateTrade(trade);

		return SecurityUtil.runEx(() -> fxNDFTradeService.saveFXNDFTrade(trade));
	}

	@Override
	public boolean isBusinessDay(FXNDFTrade fxTrade, LocalDate date) throws TradistaBusinessException {
		Currency nonDeliverableCurrency = fxTrade.getNonDeliverableCurrency();
		Calendar nonDeliverableCurrencyCalendar;
		if (nonDeliverableCurrency == null) {
			throw new TradistaBusinessException("The FX NDF trade non deliverable currency cannot be null");
		}
		nonDeliverableCurrencyCalendar = nonDeliverableCurrency.getCalendar();
		if (nonDeliverableCurrencyCalendar == null) {
			// TODO Add a warning log
		}
		boolean isBusinessDay = super.isBusinessDay(fxTrade, date);
		if (nonDeliverableCurrencyCalendar != null) {
			return isBusinessDay && nonDeliverableCurrencyCalendar.isBusinessDay(date);
		} else {
			return isBusinessDay;
		}
	}

	public LocalDate getFixingDate(FXNDFTrade trade) throws TradistaBusinessException {
		if (trade.getSettlementDate() == null) {
			throw new TradistaBusinessException("The settlement date must exist to calculate the fixing date.");
		}
		if (trade.getNonDeliverableCurrency() == null) {
			throw new TradistaBusinessException("The non deliverable currency must exist to calculate the fixing date.");
		}
		if (trade.getCurrency() == null) {
			throw new TradistaBusinessException("The deliverable currency must exist to calculate the fixing date.");
		}
		boolean fixingDateFound = false;
		LocalDate fixingDate = DateUtil.addBusinessDay(trade.getSettlementDate(), getFXExchange().getCalendar(),
				trade.getNonDeliverableCurrency().getFixingDateOffset());
		while (!fixingDateFound) {
			if ((trade.getCurrency().getCalendar() != null ? trade.getCurrency().getCalendar().isBusinessDay(fixingDate)
					: true)
					&& (trade.getNonDeliverableCurrency().getCalendar() != null
							? trade.getNonDeliverableCurrency().getCalendar().isBusinessDay(fixingDate)
							: true)) {
				fixingDateFound = true;
			} else {
				fixingDate = DateUtil.addBusinessDay(trade.getSettlementDate(), getFXExchange().getCalendar(), -1);
			}
		}
		return fixingDate;
	}

	public FXNDFTrade getFXNDFTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> fxNDFTradeService.getFXNDFTradeById(id));
	}

}