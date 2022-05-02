package finance.tradista.core.marketdata.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.FXCurve;

/*
 * Copyright 2018 Olivier Asuncion
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

public class DefaultFXCurveValidator implements FXCurveValidator {

	@Override
	public void validateCurve(FXCurve curve) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (curve == null) {
			throw new TradistaBusinessException("The curve cannot be null.");
		}
		if (curve.getName() == null) {
			errMsg.append(String.format("The curve name is mandatory.%n"));
		}

		if (curve.isGenerated()) {
			if (curve.getAlgorithm() == null) {
				errMsg.append(String.format("The algorithm is mandatory when the curve is generated.%n"));
			}
			if (curve.getInstance() == null) {
				errMsg.append(String.format("The instance is mandatory when the curve is generated.%n"));
			}
			if (curve.getQuoteDate() == null) {
				errMsg.append(String.format("The quote date is mandatory when the curve is generated.%n"));
			}
			if (curve.getQuoteSet() == null) {
				errMsg.append(String.format("The quote set is mandatory when the curve is generated.%n"));
			}
			if (curve.getPrimaryCurrency() == null) {
				errMsg.append(String.format("The primary currency is mandatory when the curve is generated.%n"));
			}
			if (curve.getQuoteCurrency() == null) {
				errMsg.append(String.format("The quote currency is mandatory when the curve is generated.%n"));
			}
			if (curve.getPrimaryCurrencyIRCurve() == null) {
				errMsg.append(
						String.format("The primary currency IR Curve is mandatory when the curve is generated.%n"));
			}
			if (curve.getQuoteCurrencyIRCurve() == null) {
				errMsg.append(String.format("The quote currency IR Curve is mandatory when the curve is generated.%n"));
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}