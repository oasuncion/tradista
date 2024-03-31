package finance.tradista.core.processingorgdefaults.service;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaults;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaultsModule;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

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

public class ProcessingOrgDefaultsFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {
	
	private ProcessingOrgDefaultsBusinessDelegate poDefaultsBusinessDelegate;

	public ProcessingOrgDefaultsFilteringInterceptor() {
		poDefaultsBusinessDelegate = new ProcessingOrgDefaultsBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	@Override
	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		if (parameters.length > 0) {
			StringBuilder errMsg = new StringBuilder();
			if (parameters[0] instanceof Long poId) {
				if (getCurrentUser().getProcessingOrg().getId() != 0) {
					if (poId != getCurrentUser().getProcessingOrg().getId()) {
						errMsg.append(String.format(
								"You are not allowed to access the Prcessing Org Defaults of this Processing Org.%n"));
					}
				}
			}
			if (parameters[0] instanceof ProcessingOrgDefaults pod) {
				if (pod.getModules() != null && pod.getModules().isEmpty()) {
					for (ProcessingOrgDefaultsModule module : pod.getModules()) {
						ProcessingOrgDefaultsModuleValidator validator = poDefaultsBusinessDelegate.getValidator(module);
						validator.checkAccess(module, errMsg);
					}
				}
			}
			if (errMsg.length() > 0) {
				throw new TradistaBusinessException(errMsg.toString());
			}
		}
	}

}