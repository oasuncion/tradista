package finance.tradista.security.equity.service;

import java.time.LocalDate;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.validator.EquityValidator;
import jakarta.enterprise.inject.Default;

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

@Default
public class EquityBusinessDelegate {

	private EquityService equityService;

	private EquityValidator validator;

	public EquityBusinessDelegate() {
		equityService = TradistaServiceLocator.getInstance().getEquityService();
		validator = new EquityValidator();
	}

	public long saveEquity(Equity equity) throws TradistaBusinessException {
		validator.validateProduct(equity);
		return SecurityUtil.runEx(() -> equityService.saveEquity(equity));
	}

	public Set<Equity> getEquitiesByCreationDate(LocalDate date) {
		return SecurityUtil.run(() -> equityService.getEquitiesByCreationDate(date));
	}

	public Set<Equity> getAllEquities() {
		return SecurityUtil.run(() -> equityService.getAllEquities());
	}

	public Equity getEquityById(long id) {
		return SecurityUtil.run(() -> equityService.getEquityById(id));
	}

	public Set<Equity> getEquitiesByDates(LocalDate minCreationDate, LocalDate maxCreationDate, LocalDate minActiveDate,
			LocalDate maxActiveDate) throws TradistaBusinessException {
		StringBuilder errorMsg = new StringBuilder();
		if (minCreationDate != null && maxCreationDate != null) {
			if (maxCreationDate.isBefore(minCreationDate)) {
				errorMsg.append(String.format("'To' creation date cannot be before 'From' creation date.%n"));
			}
		}
		if (minActiveDate != null && maxActiveDate != null) {
			if (maxActiveDate.isBefore(minActiveDate)) {
				errorMsg.append("'To' active date cannot be before 'From' active date.");
			}
		}
		if (errorMsg.length() > 0) {
			throw new TradistaBusinessException(errorMsg.toString());
		}
		return SecurityUtil.run(
				() -> equityService.getEquitiesByDates(minCreationDate, maxCreationDate, minActiveDate, maxActiveDate));
	}

	public Set<Equity> getEquitiesByIsin(String isin) {
		return SecurityUtil.run(() -> equityService.getEquitiesByIsin(isin));
	}

	public Equity getEquityByIsinAndExchangeCode(String isin, String exchangeCode) {
		return SecurityUtil.run(() -> equityService.getEquityByIsinAndExchangeCode(isin, exchangeCode));
	}

}