package finance.tradista.fx.fxoption.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.pricer.PricingParameterModule;
import finance.tradista.core.pricing.service.PricingParameterModuleValidator;
import finance.tradista.fx.fxoption.model.FXVolatilitySurface;
import finance.tradista.fx.fxoption.model.PricingParameterVolatilitySurfaceModule;
import finance.tradista.fx.fxoption.service.FXVolatilitySurfaceBusinessDelegate;

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

	private FXVolatilitySurfaceBusinessDelegate fxVolatilitySurfaceBusinessDelegate;

	public PricingParameterVolatilitySurfaceModuleValidator() {
		fxVolatilitySurfaceBusinessDelegate = new FXVolatilitySurfaceBusinessDelegate();
	}

	@Override
	public void validateModule(PricingParameterModule module, PricingParameter param) throws TradistaBusinessException {
		PricingParameterVolatilitySurfaceModule mod = (PricingParameterVolatilitySurfaceModule) module;
		StringBuilder errMsg = new StringBuilder();
		if (mod.getVolatilitySurfaces() != null && !mod.getVolatilitySurfaces().isEmpty()) {
			for (FXVolatilitySurface surface : mod.getVolatilitySurfaces().values()) {
				if (param.getProcessingOrg() != null && surface.getProcessingOrg() != null
						&& !surface.getProcessingOrg().equals(param.getProcessingOrg())) {
					errMsg.append(String.format(
							"the Pricing Parameters Set's PO and the FX Volatility Surface %s's PO should be the same.%n",
							surface));
				}
				if (param.getProcessingOrg() == null && surface.getProcessingOrg() != null) {
					errMsg.append(String.format(
							"If the Pricing Parameters Set is a global one, the FX Volatility Surface %s must also be global.%n",
							surface));
				}
				if (param.getProcessingOrg() != null && surface.getProcessingOrg() == null) {
					errMsg.append(String.format(
							"If the FX Volatility Surface %s is a global one, the Pricing Parameters Set must also be global.%n",
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
			for (FXVolatilitySurface surface : mod.getVolatilitySurfaces().values()) {
				FXVolatilitySurface vol = null;
				try {
					vol = fxVolatilitySurfaceBusinessDelegate.getFXVolatilitySurfaceById(surface.getId());
				} catch (TradistaBusinessException abe) {
				}
				if (vol == null) {
					errMsg.append(String.format("the fx volatility surface %s was not found.%n", surface.getName()));
				}
			}
		}
	}

}