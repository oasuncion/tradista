package finance.tradista.security.gcrepo.service;

import java.util.Set;
import java.util.stream.Collectors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.user.model.User;
import finance.tradista.security.gcrepo.model.AllocationConfiguration;
import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

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

public class AllocationConfigurationFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	@EJB
	protected AllocationConfigurationService allocationConfigurationService;

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	@Override
	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		if (parameters.length > 0) {
			StringBuilder errMsg = new StringBuilder();
			if (parameters[0] instanceof AllocationConfiguration allocConfig) {
				if (!allocConfig.getProcessingOrg().equals(getCurrentUser().getProcessingOrg())) {
					errMsg.append(String.format("You are not allowed to save this Allocation Configuration.%n"));
				}
			}
			if (errMsg.length() > 0) {
				throw new TradistaBusinessException(errMsg.toString());
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			User user = getCurrentUser();
			if (value instanceof AllocationConfiguration allocConfig) {
				if (!allocConfig.getProcessingOrg().equals(user.getProcessingOrg())) {
					value = null;
				}
			}
			if (value instanceof Set) {
				Set<AllocationConfiguration> allocConfigs = (Set<AllocationConfiguration>) value;
				value = allocConfigs.stream().filter(a -> (a.getProcessingOrg().equals(user.getProcessingOrg())))
						.collect(Collectors.toSet());
			}
		}
		return value;
	}

}