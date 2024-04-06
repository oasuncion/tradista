package finance.tradista.security.gcrepo.service;

import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.security.gcrepo.model.AllocationConfiguration;
import finance.tradista.security.gcrepo.persistence.AllocationConfigurationSQL;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

/*
 * Copyright 2024 Olivier Asuncion
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
public class AllocationConfigurationServiceBean implements AllocationConfigurationService {

	@Override
	@Interceptors({ GCRepoProductScopeFilteringInterceptor.class, AllocationConfigurationFilteringInterceptor.class })
	public long saveAllocationConfiguration(AllocationConfiguration allocationConfiguration)
			throws TradistaBusinessException {
		if (allocationConfiguration.getId() == 0) {
			checkNameExistence(allocationConfiguration);
			return AllocationConfigurationSQL.saveAllocationConfiguration(allocationConfiguration);
		} else {
			AllocationConfiguration oldAllocationConfiguration = AllocationConfigurationSQL
					.getAllocationConfigurationById(allocationConfiguration.getId());
			if (!allocationConfiguration.getName().equals(oldAllocationConfiguration.getName())
					|| !allocationConfiguration.getProcessingOrg()
							.equals(oldAllocationConfiguration.getProcessingOrg())) {
				checkNameExistence(allocationConfiguration);
			}
			return AllocationConfigurationSQL.saveAllocationConfiguration(allocationConfiguration);
		}
	}

	private void checkNameExistence(AllocationConfiguration allocationConfiguration) throws TradistaBusinessException {
		if (AllocationConfigurationSQL.getAllocationConfigurationByNameAndPoId(allocationConfiguration.getName(),
				allocationConfiguration.getProcessingOrg().getId()) != null) {
			throw new TradistaBusinessException(
					String.format("This Allocation Configuration '%s' already exists in the system for the PO %s.",
							allocationConfiguration.getName(), allocationConfiguration.getProcessingOrg().getId()));
		}
	}

	@Override
	@Interceptors(AllocationConfigurationFilteringInterceptor.class)
	public AllocationConfiguration getAllocationConfigurationByName(String name) {
		return AllocationConfigurationSQL.getAllocationConfigurationByName(name);
	}

	@Override
	@Interceptors(AllocationConfigurationFilteringInterceptor.class)
	public AllocationConfiguration getAllocationConfigurationById(long id) {
		return AllocationConfigurationSQL.getAllocationConfigurationById(id);
	}

	@Override
	@Interceptors(AllocationConfigurationFilteringInterceptor.class)
	public Set<AllocationConfiguration> getAllAllocationConfigurations() {
		return AllocationConfigurationSQL.getAllAllocationConfigurations();
	}

}