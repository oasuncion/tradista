package finance.tradista.core.legalentity.service;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.legalentity.persistence.LegalEntitySQL;
import finance.tradista.core.legalentity.service.LegalEntityService;
import finance.tradista.legalentity.service.LegalEntityFilteringInterceptor;

/*
 * Copyright 2019 Olivier Asuncion
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
public class LegalEntityServiceBean implements LegalEntityService {

	@Override
	public Set<LegalEntity> getAllLegalEntities() {
		return LegalEntitySQL.getAllLegalEntities();
	}

	@Interceptors(LegalEntityFilteringInterceptor.class)
	@Override
	public Set<LegalEntity> getAllProcessingOrgs() {
		return LegalEntitySQL.getAllProcessingOrgs();
	}

	@Interceptors(LegalEntityFilteringInterceptor.class)
	@Override
	public long saveLegalEntity(LegalEntity legalEntity) throws TradistaBusinessException {
		if (legalEntity.getId() == 0) {
			checkShortNameExistence(legalEntity);
			checkLongNameExistence(legalEntity);
		} else {
			LegalEntity oldLegalEntity = LegalEntitySQL.getLegalEntityById(legalEntity.getId());
			if (!oldLegalEntity.getShortName().equals(legalEntity.getShortName())) {
				checkShortNameExistence(legalEntity);
			}
			if (!oldLegalEntity.getLongName().equals(legalEntity.getLongName())) {
				checkLongNameExistence(legalEntity);
			}
		}
		return LegalEntitySQL.saveLegalEntity(legalEntity);
	}

	private void checkShortNameExistence(LegalEntity legalEntity) throws TradistaBusinessException {
		if (getLegalEntityByShortName(legalEntity.getShortName()) != null) {
			throw new TradistaBusinessException(String.format(
					"A legal entity with short name %s already exists in the system.", legalEntity.getShortName()));
		}
	}

	private void checkLongNameExistence(LegalEntity legalEntity) throws TradistaBusinessException {
		if (getLegalEntityByLongName(legalEntity.getLongName()) != null) {
			throw new TradistaBusinessException(String.format(
					"A legal entity with long name %s already exists in the system.", legalEntity.getLongName()));
		}
	}

	@Interceptors(LegalEntityFilteringInterceptor.class)
	@Override
	public Set<LegalEntity> getLegalEntitiesByShortNameAndRole(String shortName, LegalEntity.Role role) {
		return LegalEntitySQL.getLegalEntitiesByShortNameAndRole(shortName, role);
	}

	@Interceptors(LegalEntityFilteringInterceptor.class)
	@Override
	public LegalEntity getLegalEntityByShortName(String shortName) {
		return LegalEntitySQL.getLegalEntityByShortName(shortName);
	}

	@Override
	public LegalEntity getLegalEntityByLongName(String longName) {
		return LegalEntitySQL.getLegalEntityByLongName(longName);
	}

	@Interceptors(LegalEntityFilteringInterceptor.class)
	@Override
	public LegalEntity getLegalEntityById(long id) {
		return LegalEntitySQL.getLegalEntityById(id);
	}

	@Override
	public Set<LegalEntity> getAllCounterparties() {
		return LegalEntitySQL.getAllCounterparties();
	}
}