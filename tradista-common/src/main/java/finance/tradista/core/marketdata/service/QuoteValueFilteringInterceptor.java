package finance.tradista.core.marketdata.service;

import java.util.List;
import java.util.stream.Collectors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.user.model.User;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

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

public class QuoteValueFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private QuoteBusinessDelegate quoteBusinessDelegate;

	public QuoteValueFilteringInterceptor() {
		super();
		quoteBusinessDelegate = new QuoteBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		long quoteSetId = (long) parameters[0];
		StringBuilder errMsg = new StringBuilder();
		if (quoteSetId != 0) {
			QuoteSet qs = quoteBusinessDelegate.getQuoteSetById(quoteSetId);
			if (qs == null) {
				errMsg.append(String.format("The quote set %d was not found.%n", quoteSetId));
			} else if (qs.getProcessingOrg() == null) {
				errMsg.append(String.format(
						"This QuoteSet %d is a global one and you are not allowed to add quote values on it.",
						quoteSetId));
			}
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			if (value instanceof List) {
				List<QuoteValue> qvs = (List<QuoteValue>) value;
				if (!qvs.isEmpty()) {
					User user = getCurrentUser();
					value = qvs.stream()
							.filter(qv -> (qv.getQuoteSet().getProcessingOrg() == null)
									|| (qv.getQuoteSet().getProcessingOrg().equals(user.getProcessingOrg())))
							.collect(Collectors.toList());
				}
			}
		}
		return value;
	}

}