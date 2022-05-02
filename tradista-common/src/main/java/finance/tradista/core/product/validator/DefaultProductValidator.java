package finance.tradista.core.product.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.product.model.Product;

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

public class DefaultProductValidator implements ProductValidator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7230180363740521856L;

	@Override
	public void validateProduct(Product product)
			throws TradistaBusinessException {
		StringBuilder errMsg = validateProductBasics(product);
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	protected StringBuilder validateProductBasics(Product product)
			throws TradistaBusinessException {
		// Existence controls
		if (product == null) {
			throw new TradistaBusinessException("The product cannot be null.");
		}
		StringBuilder errMsg = new StringBuilder();
		if (product.getExchange() == null) {
			errMsg.append(String.format("The exchange is mandatory.%n"));
		}
		
		return errMsg;
	}

}
