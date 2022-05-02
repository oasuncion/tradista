package finance.tradista.security.common.validator;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.product.validator.DefaultProductValidator;
import finance.tradista.legalentity.service.LegalEntityBusinessDelegate;
import finance.tradista.security.common.model.Security;

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

public class DefaultSecurityValidator extends DefaultProductValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6255982705139838320L;

	@Override
	public void validateProduct(Product product) throws TradistaBusinessException {
		StringBuilder errMsg = validateProductBasics(product);
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	/*
	 * Making issue date and issue price optional. (non-Javadoc)
	 * 
	 * @see
	 * finance.tradista.core.validator.DefaultProductValidator#validateProductBasics
	 * (finance.tradista.core.product.Product)
	 */
	protected StringBuilder validateProductBasics(Product product) throws TradistaBusinessException {
		Security security = (Security) product;

		StringBuilder errMsg = super.validateProductBasics(security);

		if (security.getCurrency() == null) {
			errMsg.append(String.format("The currency is mandatory.%n"));
		}

		if (StringUtils.isBlank(security.getIsin())) {
			errMsg.append(String.format("The ISIN is mandatory.%n"));
		}

		if (security.getIssuer() == null) {
			errMsg.append(String.format("The issuer is mandatory.%n"));
		} else {
			LegalEntity issuer = new LegalEntityBusinessDelegate()
					.getLegalEntityByShortName(security.getIssuer().getShortName());
			if (issuer == null) {
				errMsg.append(String.format("The issuer (%s) must exist in the system.%n",
						security.getIssuer().getShortName()));
			}
		}

		if (security.getIssueDate() == null) {
			errMsg.append(String.format("The issue date is mandatory.%n"));
		}

		if (security.getIssuePrice() == null) {
			errMsg.append(String.format("The issue price is mandatory.%n"));
		} else {
			if (security.getIssuePrice().doubleValue() <= 0) {
				errMsg.append(
						String.format("The issue price (%s) is mandatory.%n", security.getIssuePrice().doubleValue()));
			}
		}

		return errMsg;
	}

}
