package finance.tradista.fx.common.validator;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.pricer.PricingParameterModule;
import finance.tradista.core.pricing.service.PricingParameterModuleValidator;
import finance.tradista.fx.common.model.PricingParameterUnrealizedPnlCalculationModule;
import finance.tradista.fx.common.model.PricingParameterUnrealizedPnlCalculationModule.BookProductTypePair;

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

public class PricingParameterUnrealizedPnlCalculationModuleValidator implements PricingParameterModuleValidator {

	private BookBusinessDelegate bookBusinessDelegate;

	public PricingParameterUnrealizedPnlCalculationModuleValidator() {
		bookBusinessDelegate = new BookBusinessDelegate();
	}

	@Override
	public void validateModule(PricingParameterModule module, PricingParameter param) throws TradistaBusinessException {
		PricingParameterUnrealizedPnlCalculationModule mod = (PricingParameterUnrealizedPnlCalculationModule) module;
		StringBuilder errMsg = new StringBuilder();
		if (mod.getUnrealizedPnlCalculations() != null && !mod.getUnrealizedPnlCalculations().isEmpty()) {
			for (BookProductTypePair bookProd : mod.getUnrealizedPnlCalculations().keySet()) {
				if (param.getProcessingOrg() != null && bookProd.getBook() != null
						&& bookProd.getBook().getProcessingOrg() != null
						&& !bookProd.getBook().getProcessingOrg().equals(param.getProcessingOrg())) {
					errMsg.append(
							String.format("the Pricing Parameters Set's PO and the book %s's PO should be the same.%n",
									bookProd.getBook()));
				}
				if (param.getProcessingOrg() == null && bookProd.getBook() != null
						&& bookProd.getBook().getProcessingOrg() != null) {
					errMsg.append(String.format(
							"If the Pricing Parameters Set is a global one, the book %s must also be global.%n",
							bookProd.getBook()));
				}
				if (param.getProcessingOrg() != null && bookProd.getBook() != null
						&& bookProd.getBook().getProcessingOrg() == null) {
					errMsg.append(String.format(
							"If the book %s is a global one, the Pricing Parameters Set must also be global.%n",
							bookProd.getBook()));
				}
			}
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	@Override
	public void checkAccess(PricingParameterModule module, StringBuilder errMsg) {
		PricingParameterUnrealizedPnlCalculationModule mod = (PricingParameterUnrealizedPnlCalculationModule) module;
		if (mod.getUnrealizedPnlCalculations() != null && !mod.getUnrealizedPnlCalculations().isEmpty()) {
			for (BookProductTypePair bookProd : mod.getUnrealizedPnlCalculations().keySet()) {
				Book b = null;
				try {
					b = bookBusinessDelegate.getBookById(bookProd.getBook().getId());
				} catch (TradistaBusinessException abe) {
				}
				if (b == null) {
					errMsg.append(String.format("the book %s was not found.%n", bookProd.getBook()));
				}
			}
		}
	}

}