package finance.tradista.security.gcrepo.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaultsModule;
import finance.tradista.core.processingorgdefaults.service.ProcessingOrgDefaultsModuleValidator;
import finance.tradista.security.gcrepo.model.ProcessingOrgDefaultsCollateralManagementModule;

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

public class ProcessingOrgDefaultsCollateralManagementModuleValidator implements ProcessingOrgDefaultsModuleValidator {

	private QuoteBusinessDelegate quoteBusinessDelegate;

	public ProcessingOrgDefaultsCollateralManagementModuleValidator() {
		quoteBusinessDelegate = new QuoteBusinessDelegate();
	}

	@Override
	public void validateModule(ProcessingOrgDefaultsModule module, LegalEntity po) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		QuoteSet qs = ((ProcessingOrgDefaultsCollateralManagementModule) module).getQuoteSet();
		if (qs != null) {
			if (po != null && qs.getProcessingOrg() != null && !qs.getProcessingOrg().equals(po)) {
				errMsg.append(String.format(
						"the Processing Org Defaults's PO and the Collateral Management Quote Set %s's PO should be the same.%n",
						qs));
			}
			if (po == null && qs.getProcessingOrg() != null) {
				errMsg.append(String.format(
						"If the Processing Org Defaults is a global one, the Collateral Management Quote Set %s must also be global.%n",
						qs));
			}
			if (po != null && qs.getProcessingOrg() == null) {
				errMsg.append(String.format(
						"If the Collateral Management Quote Set %s is a global one, the Processing Org Defaults must also be global.%n",
						qs));
			}
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	@Override
	public void checkAccess(ProcessingOrgDefaultsModule module, StringBuilder errMsg) throws TradistaBusinessException {
		QuoteSet qs = ((ProcessingOrgDefaultsCollateralManagementModule) module).getQuoteSet();
		if (qs != null) {
			QuoteSet checkQs = quoteBusinessDelegate.getQuoteSetById(qs.getId());
			if (checkQs == null) {
				errMsg.append(String.format("the Collateral Management Quote Set %s was not found.%n", qs));
			}
		}
	}

}