package finance.tradista.core.position.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.error.model.Error.Status;
import finance.tradista.core.position.model.PositionCalculationError;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.persistence.PositionCalculationErrorSQL;
import finance.tradista.core.position.service.PositionCalculationErrorFilteringInterceptor;
import finance.tradista.core.position.service.PositionCalculationErrorService;
import finance.tradista.core.position.service.PositionDefinitionBusinessDelegate;

/*
 * Copyright 2016 Olivier Asuncion
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
public class PositionCalculationErrorServiceBean
		implements LocalPositionCalculationErrorService, PositionCalculationErrorService {

	@Override
	public boolean savePositionCalculationErrors(List<PositionCalculationError> errors) {
		return PositionCalculationErrorSQL.savePositionCalculationErrors(errors);
	}

	@Override
	public void solvePositionCalculationError(Set<Long> solved, LocalDate date) {
		PositionCalculationErrorSQL.solvePositionCalculationError(solved, date);
	}

	/**
	 * no existence controls on parameters here for performance reasons. Existence
	 * controls are important for saving methods but for getXXX methods like this
	 * one, not sure it is a good idea.
	 */
	@Interceptors(PositionCalculationErrorFilteringInterceptor.class)
	@Override
	public List<PositionCalculationError> getPositionCalculationErrors(long positionDefinitionId, Status status,
			long tradeId, long productId, LocalDate valueDateFrom, LocalDate valueDateTo, LocalDate errorDateFrom,
			LocalDate errorDateTo, LocalDate solvingDateFrom, LocalDate solvingDateTo)
			throws TradistaBusinessException {
		return PositionCalculationErrorSQL.getPositionCalculationErrors(positionDefinitionId, status, tradeId,
				productId, valueDateFrom, valueDateTo, errorDateFrom, errorDateTo, solvingDateFrom, solvingDateTo);
	}

	@Override
	public void solvePositionCalculationError(long positionDefinitionId, LocalDate date)
			throws TradistaBusinessException {
		PositionDefinition posDef = new PositionDefinitionBusinessDelegate()
				.getPositionDefinitionById(positionDefinitionId);
		if (posDef == null) {
			throw new TradistaBusinessException(String
					.format("The position definition with id %s does not exist in the system.", positionDefinitionId));
		}
		PositionCalculationErrorSQL.solvePositionCalculationError(positionDefinitionId, date);
	}

}
