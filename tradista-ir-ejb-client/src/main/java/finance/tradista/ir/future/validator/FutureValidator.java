package finance.tradista.ir.future.validator;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.product.validator.DefaultProductValidator;
import finance.tradista.ir.future.model.Future;
import finance.tradista.ir.future.service.FutureBusinessDelegate;

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

public class FutureValidator extends DefaultProductValidator {

	/**
	* 
	*/
	private static final long serialVersionUID = 1121603261939982187L;

	@Override
	public void validateProduct(Product product) throws TradistaBusinessException {
		Future future = (Future) product;
		StringBuilder errMsg = validateProductBasics(product);

		try {
			validateSymbol(future.getSymbol());
		} catch (TradistaBusinessException abe) {
			errMsg.append(String.format(abe.getMessage() + "%n"));
		}

		if (future.getContractSpecification() == null) {
			errMsg.append(String.format("The contract specification is mandatory.%n"));
		}

		if (future.getMaturityDate() == null) {
			errMsg.append(String.format("The maturity date is mandatory.%n"));
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

	}

	public void validateSymbol(String symbol) throws TradistaBusinessException {
		if (StringUtils.isEmpty(symbol)) {
			throw new TradistaBusinessException("The symbol is mandatory.");
		}
		// Checking symbol format.
		if (symbol.length() != 5) {
			throw new TradistaBusinessException(String.format("The symbol (%s)'s length must be 5 characters.", symbol));
		}

		// Checking the year.
		try {
			Integer.parseInt(symbol.substring(3));
		} catch (NumberFormatException nfe) {
			throw new TradistaBusinessException(
					String.format("The symbol's year ('%s') is not correct", symbol.substring(3)));
		}
		// checking the month.
		new FutureBusinessDelegate().getMonth(symbol.substring(0, 3));
	}

}
