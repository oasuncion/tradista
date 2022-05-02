package finance.tradista.security.equity.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;
import finance.tradista.security.common.validator.DefaultSecurityValidator;
import finance.tradista.security.equity.model.Equity;

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

public class EquityValidator extends DefaultSecurityValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7682359998420318639L;

	@Override
	public void validateProduct(Product product) throws TradistaBusinessException {
		Equity equity = (Equity) product;
		StringBuilder errMsg = validateProductBasics(product);
		if (equity.getActiveFrom() == null) {
			errMsg.append(String.format("Active From is mandatory.%n"));
		} else {
			if (equity.getActiveTo() == null) {
				errMsg.append(String.format("Active To is mandatory.%n"));
			} else {
				if (equity.getActiveFrom().isAfter(equity.getActiveTo())) {
					errMsg.append(String.format("Active From cannot be after Active To.%n"));
				}
			}
		}

		if (equity.getTotalIssued() <= 0) {
			errMsg.append(String.format("Total issued (%s) must be positive.%n", equity.getTotalIssued()));
		}

		if (equity.getTradingSize() < 0) {
			errMsg.append(String.format("Trding size (%s) must be positive.%n", equity.getTradingSize()));
		}

		if (equity.isPayDividend()) {
			if (equity.getDividendCurrency() == null) {
				errMsg.append(String.format("The dividend currency is mandatory when Pay Dividend is selected.%n"));
			}
			if (equity.getDividendFrequency() == null) {
				errMsg.append(String.format("The dividend frequency is mandatory when Pay Dividend is selected.%n"));
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

}
