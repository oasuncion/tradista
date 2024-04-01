package finance.tradista.core.processingorgdefaults.service;

import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaults;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaultsModule;

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

public class ProcessingOrgDefaultsBusinessDelegate {

	private ProcessingOrgDefaultsService poDefaultsService;

	private Map<String, ProcessingOrgDefaultsModuleValidator> validators;

	public ProcessingOrgDefaultsBusinessDelegate() {
		poDefaultsService = TradistaServiceLocator.getInstance().getProcessingOrgDefaultsService();
		validators = new HashMap<>();
		ProcessingOrgDefaultsModuleValidator validator = null;
		try {
			validator = TradistaUtil.getInstance(ProcessingOrgDefaultsModuleValidator.class,
					"finance.tradista.security.gcrepo.validator.ProcessingOrgDefaultsCollateralManagementModuleValidator");
			validators.put("finance.tradista.security.gcrepo.model.ProcessingOrgDefaultsCollateralManagementModule",
					validator);
		} catch (TradistaTechnicalException tte) {
			// TODO Add log info
		}
	}

	public ProcessingOrgDefaults getProcessingOrgDefaultsByPoId(long poId) throws TradistaBusinessException {
		if (poId <= 0) {
			throw new TradistaBusinessException("The po id must be positive.");
		}
		return SecurityUtil.run(() -> poDefaultsService.getProcessingOrgDefaultsByPoId(poId));
	}

	public long saveProcessingOrgDefaults(ProcessingOrgDefaults poDefaults) throws TradistaBusinessException {
		if (poDefaults == null) {
			throw new TradistaBusinessException("The Processing Org Defaults cannot be null.");
		}
		StringBuilder errMsg = new StringBuilder();
		if (poDefaults.getModules() != null && poDefaults.getModules().isEmpty()) {
			for (ProcessingOrgDefaultsModule module : poDefaults.getModules()) {
				ProcessingOrgDefaultsModuleValidator validator = getValidator(module);
				try {
					validator.validateModule(module, poDefaults.getProcessingOrg());
				} catch (TradistaBusinessException abe) {
					errMsg.append(abe.getMessage());
				}
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		return SecurityUtil.runEx(() -> poDefaultsService.saveProcessingOrgDefaults(poDefaults));
	}

	ProcessingOrgDefaultsModuleValidator getValidator(ProcessingOrgDefaultsModule module) {
		return validators.get(module.getClass().getName());
	}

}