package finance.tradista.security.equityoption.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.trade.model.OptionTrade;
import finance.tradista.security.equityoption.model.EquityOption;
import finance.tradista.security.equityoption.validator.EquityOptionValidator;

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

public class EquityOptionBusinessDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5129022391357676738L;
	private EquityOptionService equityOptionService;

	private EquityOptionValidator validator;

	public EquityOptionBusinessDelegate() {
		equityOptionService = TradistaServiceLocator.getInstance().getEquityOptionService();
		validator = new EquityOptionValidator();
	}

	public long saveEquityOption(EquityOption product) throws TradistaBusinessException {
		validator.validateProduct(product);
		return SecurityUtil.runEx(() -> equityOptionService.saveEquityOption(product));
	}

	public Set<EquityOption> getEquityOptionsByCreationDate(LocalDate date) {
		return SecurityUtil.run(() -> equityOptionService.getEquityOptionsByCreationDate(date));
	}

	public Set<EquityOption> getEquityOptionsByCreationDate(LocalDate minDate, LocalDate maxDate)
			throws TradistaBusinessException {
		if (minDate != null && maxDate != null) {
			if (maxDate.isBefore(minDate)) {
				throw new TradistaBusinessException("'To' creation date cannot be before 'From' creation date.");
			}
		}
		return SecurityUtil.run(() -> equityOptionService.getEquityOptionsByCreationDate(minDate, maxDate));
	}

	public Set<EquityOption> getAllEquityOptions() {
		return SecurityUtil.run(() -> equityOptionService.getAllEquityOptions());
	}

	public EquityOption getEquityOptionById(long id) {
		return SecurityUtil.run(() -> equityOptionService.getEquityOptionById(id));
	}

	public EquityOption getEquityOptionByCodeTypeStrikeMaturityDateAndContractSpecificationName(String code,
			OptionTrade.Type type, BigDecimal strike, LocalDate maturityDate, String contractSpecificationName)
			throws TradistaBusinessException {
		if (code == null) {
			throw new TradistaBusinessException("The code is mandatory.");
		}
		if (type == null) {
			throw new TradistaBusinessException("The type is mandatory.");
		}
		if (contractSpecificationName == null) {
			throw new TradistaBusinessException("The contract specification name is mandatory.");
		}
		if (maturityDate == null) {
			throw new TradistaBusinessException("The maturity date code is mandatory.");
		}
		if (strike == null) {
			throw new TradistaBusinessException("The strike is mandatory.");
		}
		return SecurityUtil.run(
				() -> equityOptionService.getEquityOptionByCodeTypeStrikeMaturityDateAndContractSpecificationName(code,
						type, strike, maturityDate, contractSpecificationName));
	}

	public Set<EquityOption> getEquityOptionsByCode(String code) throws TradistaBusinessException {
		if (code == null) {
			throw new TradistaBusinessException("The code is mandatory.");
		}
		return SecurityUtil.run(() -> equityOptionService.getEquityOptionsByCode(code));
	}

}