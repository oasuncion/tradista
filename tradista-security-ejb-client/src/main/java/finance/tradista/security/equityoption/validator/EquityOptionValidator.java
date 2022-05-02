package finance.tradista.security.equityoption.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;
import finance.tradista.security.common.validator.DefaultSecurityValidator;
import finance.tradista.security.equityoption.model.EquityOption;

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

public class EquityOptionValidator extends DefaultSecurityValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3801876371578875255L;

	@Override
	public void validateProduct(Product product) throws TradistaBusinessException {
		EquityOption equityOption = (EquityOption) product;
		StringBuilder errMsg = new StringBuilder();
		if (equityOption.getUnderlying() == null) {
			errMsg.append(String.format("The underlying is mandatory.%n"));
		}

		if (equityOption.getStyle() == null) {
			errMsg.append(String.format("The style is mandatory.%n"));
		}

		if (equityOption.getCode() == null) {
			errMsg.append(String.format("The code is mandatory.%n"));
		}
		if (equityOption.getMaturityDate() == null) {
			errMsg.append(String.format("The maturity date is mandatory.%n"));
		}
		if (equityOption.getEquityOptionContractSpecification() == null) {
			errMsg.append(String.format("The equity option contract specification is mandatory.%n"));
		}
		if (equityOption.getStrike() == null) {
			errMsg.append(String.format("The strike is mandatory.%n"));
		} else {
			if (equityOption.getStrike().doubleValue() <= 0) {
				errMsg.append(
						String.format("The strike (%s) must be positive.%n", equityOption.getStrike().doubleValue()));
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}