package finance.tradista.ir.irswapoption.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.pricing.pricer.PricingParameterModule;
import finance.tradista.core.pricing.service.PricingParameterModuleValidator;
import finance.tradista.ir.irswapoption.model.PricingParameterVolatilitySurfaceModule;
import finance.tradista.ir.irswapoption.model.SwaptionVolatilitySurface;
import finance.tradista.ir.irswapoption.service.SwaptionVolatilitySurfaceBusinessDelegate;

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

public class PricingParameterVolatilitySurfaceModuleValidator implements PricingParameterModuleValidator {

	private SwaptionVolatilitySurfaceBusinessDelegate swaptionVolatilitySurfaceBusinessDelegate;

	public PricingParameterVolatilitySurfaceModuleValidator() {
		swaptionVolatilitySurfaceBusinessDelegate = new SwaptionVolatilitySurfaceBusinessDelegate();
	}

	@Override
	public void validateModule(PricingParameterModule module, LegalEntity po) throws TradistaBusinessException {
		PricingParameterVolatilitySurfaceModule mod = (PricingParameterVolatilitySurfaceModule) module;
		StringBuilder errMsg = new StringBuilder();
		if (mod.getVolatilitySurfaces() != null && !mod.getVolatilitySurfaces().isEmpty()) {
			for (SwaptionVolatilitySurface surface : mod.getVolatilitySurfaces().values()) {
				if (po != null && surface.getProcessingOrg() != null && !surface.getProcessingOrg().equals(po)) {
					errMsg.append(String.format(
							"the Pricing Parameters Set's PO and the Swaption Volatility Surface %s's PO should be the same.%n",
							surface));
				}
				if (po == null && surface.getProcessingOrg() != null) {
					errMsg.append(String.format(
							"If the Pricing Parameters Set is a global one, the Swaption Volatility Surface %s must also be global.%n",
							surface));
				}
				if (po != null && surface.getProcessingOrg() == null) {
					errMsg.append(String.format(
							"If the Swaption Volatility Surface %s is a global one, the Pricing Parameters Set must also be global.%n",
							surface));
				}
			}
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	@Override
	public void checkAccess(PricingParameterModule module, StringBuilder errMsg) {
		PricingParameterVolatilitySurfaceModule mod = (PricingParameterVolatilitySurfaceModule) module;
		if (mod.getVolatilitySurfaces() != null && !mod.getVolatilitySurfaces().isEmpty()) {
			for (SwaptionVolatilitySurface surface : mod.getVolatilitySurfaces().values()) {
				SwaptionVolatilitySurface vol = null;
				try {
					vol = swaptionVolatilitySurfaceBusinessDelegate.getSwaptionVolatilitySurfaceById(surface.getId());
				} catch (TradistaBusinessException tbe) {
					// Not expected here.
				}
				if (vol == null) {
					errMsg.append(String.format("the fx volatility surface %s was not found.%n", surface.getName()));
				}
			}
		}
	}

}