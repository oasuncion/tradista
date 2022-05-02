package finance.tradista.security.bond.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.common.validator.DefaultSecurityValidator;

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

public class BondValidator extends DefaultSecurityValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458721944610540071L;

	@Override
	public void validateProduct(Product product) throws TradistaBusinessException {
		Bond bond = (Bond) product;
		StringBuilder errMsg = validateProductBasics(product);

		if (bond.getReferenceRateIndex() == null) {
			// Fixed rate Bond
			if (bond.getCoupon() == null) {
				errMsg.append(String.format("The coupon is mandatory.%n"));
			} else {
				if (bond.getCoupon().doubleValue() <= 0) {
					errMsg.append(String.format("The coupon (%s) must be positive.%n", bond.getCoupon().doubleValue()));
				}
			}
		} else {
			// FRN
			if (bond.isFloor()) {
				if (bond.getCap().compareTo(bond.getFloor()) >= 0) {
					errMsg.append(String.format("The Cap (%s) must be lower than the Floor (%s).%n", bond.getCap(),
							bond.getFloor()));
				}
			}
			if (bond.getLeverageFactor() != null) {
				if (bond.getLeverageFactor().signum() != 1) {
					errMsg.append(
							String.format("The Leverage Factor (%s) must be positive.%n", bond.getLeverageFactor()));
				}
			}
		}

		if (bond.getCouponFrequency() == null) {
			errMsg.append(String.format("The coupon frequency is mandatory.%n"));
		}

		if (bond.getCouponType() == null) {
			errMsg.append(String.format("The coupon type is mandatory.%n"));
		}

		if (bond.getDatedDate() == null) {
			errMsg.append(String.format("The dated date is mandatory.%n"));
		} else {
			if (bond.getIssueDate() != null) {
				if (bond.getDatedDate().isBefore(bond.getIssueDate())) {
					errMsg.append(String.format("The dated date cannot be before the issue date.%n"));
				}
			}
		}

		if (bond.getMaturityDate() == null) {
			errMsg.append(String.format("The maturity date is mandatory.%n"));
		} else {
			if (bond.getIssueDate() != null) {
				if (!bond.getMaturityDate().isAfter(bond.getIssueDate())) {
					errMsg.append(String.format("The maturity date must be after the issue date.%n"));
				}
			}

			if (bond.getDatedDate() != null) {
				if (!bond.getMaturityDate().isAfter(bond.getDatedDate())) {
					errMsg.append(String.format("The maturity date must be after the dated date.%n"));
				}
			}
		}

		if (bond.getPrincipal() == null) {
			errMsg.append(String.format("The principal is mandatory.%n"));
		} else {
			if (bond.getPrincipal().doubleValue() <= 0) {
				errMsg.append(
						String.format("The principal (%s) must be positive.%n", bond.getPrincipal().doubleValue()));
			}
		}

		if (bond.getRedemptionPrice() != null) {
			if (bond.getRedemptionPrice().doubleValue() <= 0) {
				errMsg.append(String.format("The redemption price (%s) must be positive.%n",
						bond.getPrincipal().doubleValue()));
			}
			if (bond.getRedemptionCurrency() == null) {
				errMsg.append(
						String.format("If the redemption price is defined, the redemption currency is mandatory.%n"));
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}
