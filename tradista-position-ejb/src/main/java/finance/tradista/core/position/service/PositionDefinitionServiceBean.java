package finance.tradista.core.position.service;

import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.persistence.PositionDefinitionSQL;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

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
public class PositionDefinitionServiceBean implements LocalPositionDefinitionService, PositionDefinitionService {

	@Interceptors(PositionDefinitionFilteringInterceptor.class)
	@Override
	public Set<PositionDefinition> getAllPositionDefinitions() {
		return PositionDefinitionSQL.getAllPositionDefinitions();
	}

	@Interceptors(PositionDefinitionFilteringInterceptor.class)
	@Override
	public PositionDefinition getPositionDefinitionByName(String name) {
		return PositionDefinitionSQL.getPositionDefinitionByName(name);
	}

	@Interceptors(PositionDefinitionFilteringInterceptor.class)
	@Override
	public PositionDefinition getPositionDefinitionById(long id) {
		return PositionDefinitionSQL.getPositionDefinitionById(id);
	}

	@Interceptors({ PositionDefinitionProductScopeFilteringInterceptor.class,
			PositionDefinitionFilteringInterceptor.class })
	@Override
	public long savePositionDefinition(PositionDefinition positionDefinition) throws TradistaBusinessException {
		if (positionDefinition.getId() == 0) {
			checkPositionDefinitionName(positionDefinition);
		} else {
			PositionDefinition oldPositionDefinition = PositionDefinitionSQL
					.getPositionDefinitionById(positionDefinition.getId());
			if (!oldPositionDefinition.getName().equals(oldPositionDefinition.getName())) {
				checkPositionDefinitionName(positionDefinition);
			}
		}
		return PositionDefinitionSQL.savePositionDefinition(positionDefinition);
	}

	private void checkPositionDefinitionName(PositionDefinition positionDefinition) throws TradistaBusinessException {
		if (getPositionDefinitionByName(positionDefinition.getName()) != null) {
			throw new TradistaBusinessException(String.format(
					"A position definition named %s already exists in the system.", positionDefinition.getName()));
		}
	}

	@Override
	public boolean deletePositionDefinition(String name) {
		return PositionDefinitionSQL.deletePositionDefinition(name);
	}

	@Override
	public Set<PositionDefinition> getAllRealTimePositionDefinitions() {
		return PositionDefinitionSQL.getAllRealTimePositionDefinitions();
	}

	@Override
	public Set<String> getPositionDefinitionsByPricingParametersSetId(long id) {
		return PositionDefinitionSQL.getPositionDefinitionsByPricingParametersSetId(id);
	}
}