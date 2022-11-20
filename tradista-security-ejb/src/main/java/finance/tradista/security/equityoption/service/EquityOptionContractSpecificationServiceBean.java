package finance.tradista.security.equityoption.service;

import java.util.Set;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.security.equityoption.model.EquityOptionContractSpecification;
import finance.tradista.security.equityoption.persistence.EquityOptionContractSpecificationSQL;
import finance.tradista.security.equityoption.service.EquityOptionContractSpecificationService;

/*
 * Copyright 2017 Olivier Asuncion
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

@Stateless
public class EquityOptionContractSpecificationServiceBean implements EquityOptionContractSpecificationService {

	@Override
	@Interceptors(EquityOptionProductScopeFilteringInterceptor.class)
	public long saveEquityOptionContractSpecification(EquityOptionContractSpecification eocs)
			throws TradistaBusinessException {
		if (eocs.getId() == 0) {
			checkSpecificationExistence(eocs);
			return EquityOptionContractSpecificationSQL.saveEquityOptionContractSpecification(eocs);
		} else {
			EquityOptionContractSpecification oldEquityOptionSpecification = EquityOptionContractSpecificationSQL
					.getEquityOptionContractSpecificationById(eocs.getId());
			if (!oldEquityOptionSpecification.getName().equals(oldEquityOptionSpecification.getName())) {
				checkSpecificationExistence(oldEquityOptionSpecification);
			}
			return EquityOptionContractSpecificationSQL.saveEquityOptionContractSpecification(eocs);
		}
	}

	private void checkSpecificationExistence(EquityOptionContractSpecification eocs) throws TradistaBusinessException {
		if (getEquityOptionContractSpecificationByName(eocs.getName()) != null) {
			throw new TradistaBusinessException(
					String.format("This equity option specification '%s' already exists.", eocs.getName()));
		}
	}

	@Override
	public Set<EquityOptionContractSpecification> getAllEquityOptionContractSpecifications() {
		return EquityOptionContractSpecificationSQL.getAllEquityOptionContractSpecifications();
	}

	@Override
	public EquityOptionContractSpecification getEquityOptionContractSpecificationById(long id) {
		return EquityOptionContractSpecificationSQL.getEquityOptionContractSpecificationById(id);
	}

	@Override
	public EquityOptionContractSpecification getEquityOptionContractSpecificationByName(String name) {
		return EquityOptionContractSpecificationSQL.getEquityOptionContractSpecificationByName(name);
	}

}