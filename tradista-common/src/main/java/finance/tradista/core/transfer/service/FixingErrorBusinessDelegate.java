package finance.tradista.core.transfer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.error.model.Error.Status;
import finance.tradista.core.transfer.model.FixingError;

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

public class FixingErrorBusinessDelegate {

	private FixingErrorService fixingErrorService;

	public FixingErrorBusinessDelegate() {
		fixingErrorService = TradistaServiceLocator.getInstance().getFixingErrorService();
	}

	public boolean saveFixingErrors(List<FixingError> errors) throws TradistaBusinessException {
		if (errors == null || errors.isEmpty()) {
			throw new TradistaBusinessException("The errors list is null or empty.");
		}
		return SecurityUtil.run(() -> fixingErrorService.saveFixingErrors(errors));
	}

	public void solveFixingError(Set<Long> solved, LocalDate date) throws TradistaBusinessException {
		if (solved == null || solved.isEmpty()) {
			throw new TradistaBusinessException("The solved transfer ids list is null or empty.");
		}
		if (date == null) {
			throw new TradistaBusinessException("The date cannot be null.");
		}
		SecurityUtil.run(() -> fixingErrorService.solveFixingError(solved, date));
	}

	public void solveFixingError(long transferId, LocalDate date) throws TradistaBusinessException {
		if (transferId <= 0) {
			throw new TradistaBusinessException("The transfer id must be positive.");
		}
		if (date == null) {
			throw new TradistaBusinessException("The date cannot be null.");
		}
		SecurityUtil.runEx(() -> fixingErrorService.solveFixingError(transferId, date));
	}

	public List<FixingError> getFixingErrors(long transferId, Status status, LocalDate errorDateFrom,
			LocalDate errorDateTo, LocalDate solvingDateFrom, LocalDate solvingDateTo) throws TradistaBusinessException {
		StringBuilder errorMsg = new StringBuilder();
		if (errorDateFrom != null && errorDateTo != null) {
			if (errorDateTo.isBefore(errorDateFrom)) {
				errorMsg.append(String.format("'To' error date cannot be before 'From' error date.%n"));
			}
		}
		if (solvingDateFrom != null && solvingDateTo != null) {
			if (solvingDateTo.isBefore(solvingDateFrom)) {
				errorMsg.append(String.format("'To' solving date cannot be before 'From' solving date.%n"));
			}
		}
		if (errorMsg.length() > 0) {
			throw new TradistaBusinessException(errorMsg.toString());
		}
		return SecurityUtil.runEx(() -> fixingErrorService.getFixingErrors(transferId, status, errorDateFrom,
				errorDateTo, solvingDateFrom, solvingDateTo));
	}

}