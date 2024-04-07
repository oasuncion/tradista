package finance.tradista.security.equityoption.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.service.InterestRateCurveBusinessDelegate;
import finance.tradista.core.pricing.pricer.PricingParameterModule;
import finance.tradista.core.pricing.service.PricingParameterModuleValidator;
import finance.tradista.security.equityoption.model.PricingParameterDividendYieldCurveModule;

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

public class PricingParameterDividendYieldCurveModuleValidator implements PricingParameterModuleValidator {

	private InterestRateCurveBusinessDelegate interestRateCurveBusinessDelegate;

	public PricingParameterDividendYieldCurveModuleValidator() {
		interestRateCurveBusinessDelegate = new InterestRateCurveBusinessDelegate();
	}

	@Override
	public void validateModule(PricingParameterModule module, LegalEntity po) throws TradistaBusinessException {
		PricingParameterDividendYieldCurveModule mod = (PricingParameterDividendYieldCurveModule) module;
		StringBuilder errMsg = new StringBuilder();
		if (mod.getDividendYieldCurves() != null && !mod.getDividendYieldCurves().isEmpty()) {
			for (InterestRateCurve curve : mod.getDividendYieldCurves().values()) {
				if (po != null && curve.getProcessingOrg() != null && !curve.getProcessingOrg().equals(po)) {
					errMsg.append(String.format(
							"the Pricing Parameters Set's PO and the Dividend Yield curve %s's PO should be the same.%n",
							curve));
				}
				if (po == null && curve.getProcessingOrg() != null) {
					errMsg.append(String.format(
							"If the Pricing Parameters Set is a global one, the Dividend Yield curve %s must also be global.%n",
							curve));
				}
				if (po != null && curve.getProcessingOrg() == null) {
					errMsg.append(String.format(
							"If the Dividend Yield curve %s is a global one, the Pricing Parameters Set must also be global.%n",
							curve));
				}
			}
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	@Override
	public void checkAccess(PricingParameterModule module, StringBuilder errMsg) {
		PricingParameterDividendYieldCurveModule mod = (PricingParameterDividendYieldCurveModule) module;
		if (mod.getDividendYieldCurves() != null && !mod.getDividendYieldCurves().isEmpty()) {
			for (InterestRateCurve curve : mod.getDividendYieldCurves().values()) {
				InterestRateCurve c = null;
				try {
					c = interestRateCurveBusinessDelegate.getInterestRateCurveById(curve.getId());
				} catch (TradistaBusinessException tbe) {
					// Not expected here.
				}
				if (c == null) {
					errMsg.append(String.format("the Dividend Yield curve %s was not found.%n", curve));
				}
			}
		}
	}

}