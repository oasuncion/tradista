package finance.tradista.core.marketdata.service;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.marketdata.model.QuoteSet;
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

public class QuoteSetFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private QuoteBusinessDelegate quoteBusinessDelegate;

	public QuoteSetFilteringInterceptor() {
		super();
		quoteBusinessDelegate = new QuoteBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		if (parameters.length > 0) {
			if (parameters[0] instanceof QuoteSet) {
				QuoteSet quoteSet = (QuoteSet) parameters[0];
				StringBuilder errMsg = new StringBuilder();
				if (quoteSet.getId() != 0) {
					QuoteSet qs = quoteBusinessDelegate.getQuoteSetById(quoteSet.getId());
					if (qs == null) {
						errMsg.append(String.format("The quote set %s was not found.%n", quoteSet.getName()));
					} else if (qs.getProcessingOrg() == null) {
						errMsg.append(
								String.format("This QuoteSet %d is a global one and you are not allowed to update it.",
										quoteSet.getId()));
					}
				}
				if (quoteSet.getProcessingOrg() != null
						&& !quoteSet.getProcessingOrg().equals(getCurrentUser().getProcessingOrg())) {
					errMsg.append(String.format("The processing org %s was not found.", quoteSet.getProcessingOrg()));
				}
				if (errMsg.length() > 0) {
					throw new TradistaBusinessException(errMsg.toString());
				}
			}
			if (parameters[0] instanceof Long) {
				Method method = ic.getMethod();
				if (!method.getName().equals("getQuoteSetById")) {
					Long quoteSetId = (Long) parameters[0];
					StringBuilder errMsg = new StringBuilder();
					if (quoteSetId != 0) {
						QuoteSet qs = quoteBusinessDelegate.getQuoteSetById(quoteSetId);
						if (qs == null) {
							errMsg.append(String.format("The quote set %d was not found.%n", quoteSetId));
						} else if (method.getName().equals("deleteQuoteSet")) {
							if (qs.getProcessingOrg() == null) {
								errMsg.append(String.format(
										"This QuoteSet %d is a global one and you are not allowed to delete it.",
										qs.getId()));
							}
						}
					}
					if (errMsg.length() > 0) {
						throw new TradistaBusinessException(errMsg.toString());
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			if (value instanceof Set) {
				Set<QuoteSet> pps = (Set<QuoteSet>) value;
				if (!pps.isEmpty()) {
					User user = getCurrentUser();
					value = pps.stream()
							.filter(b -> (b.getProcessingOrg() == null)
									|| (b.getProcessingOrg().equals(user.getProcessingOrg())))
							.collect(Collectors.toSet());
				}
			}
			if (value instanceof QuoteSet) {
				QuoteSet qs = (QuoteSet) value;
				if (qs.getProcessingOrg() != null
						&& !qs.getProcessingOrg().equals(getCurrentUser().getProcessingOrg())) {
					value = null;
				}
			}
		}
		return value;
	}

}