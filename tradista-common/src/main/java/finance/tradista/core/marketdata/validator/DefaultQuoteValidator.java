package finance.tradista.core.marketdata.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;

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

public class DefaultQuoteValidator implements QuoteValidator {
	
	@Override
	public void validateQuoteName(String quoteName)
			throws TradistaBusinessException {
		validateQuoteBasics(quoteName);
	}

	protected void validateQuoteBasics(String quoteName)
			throws TradistaBusinessException {
		if (quoteName == null) {
			throw new TradistaBusinessException("The quote name cannot be null.");
		}
		if (quoteName.isEmpty()) {
			throw new TradistaBusinessException("The quote name cannot be empty.");
		}		
	}
}
