package finance.tradista.core.position.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.service.PricerBusinessDelegate;

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

public class PositionDefinitionFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private BookBusinessDelegate bookBusinessDelegate;

	private PricerBusinessDelegate pricerBusinessDelegate;

	private PositionDefinitionBusinessDelegate positionDefinitionBusinessDelegate;

	public PositionDefinitionFilteringInterceptor() {
		super();
		bookBusinessDelegate = new BookBusinessDelegate();
		pricerBusinessDelegate = new PricerBusinessDelegate();
		positionDefinitionBusinessDelegate = new PositionDefinitionBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		if (parameters.length > 0 && parameters[0] instanceof PositionDefinition) {
			PositionDefinition posDef = (PositionDefinition) parameters[0];
			StringBuilder errMsg = new StringBuilder();
			if (posDef.getId() != 0) {
				PositionDefinition pd = positionDefinitionBusinessDelegate.getPositionDefinitionById(posDef.getId());
				if (pd == null) {
					errMsg.append(
							String.format("The position definition %s was not found.%n", posDef.getBook().getName()));
				} else {
					if (pd.getProcessingOrg() == null) {
						errMsg.append(String.format(
								"This Position Definition %d is a global one and you are not allowed to delete it.",
								pd.getId()));
					}
				}
			}
			if (posDef.getProcessingOrg() != null
					&& !posDef.getProcessingOrg().equals(getCurrentUser().getProcessingOrg())) {
				errMsg.append(String.format("The processing org %s was not found.", posDef.getProcessingOrg()));
			}
			if (posDef.getBook() != null) {
				Book book = bookBusinessDelegate.getBookById(posDef.getBook().getId());
				if (book == null) {
					errMsg.append(String.format("The book %s was not found.%n", posDef.getBook().getName()));
				}
			}
			PricingParameter pp = pricerBusinessDelegate.getPricingParameterById(posDef.getPricingParameter().getId());
			if (pp == null) {
				errMsg.append(String.format("The pricing parameters set %s was not found.",
						posDef.getPricingParameter().getName()));
			}
			if (errMsg.length() > 0) {
				throw new TradistaBusinessException(errMsg.toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			if (value instanceof Set) {
				Set<PositionDefinition> pds = (Set<PositionDefinition>) value;
				if (!pds.isEmpty()) {
					value = pds.stream()
							.filter(b -> (b.getProcessingOrg() == null)
									|| (b.getProcessingOrg().equals(getCurrentUser().getProcessingOrg())))
							.collect(Collectors.toSet());
				}
			}
			if (value instanceof PositionDefinition) {
				PositionDefinition posDef = (PositionDefinition) value;
				if ((posDef.getProcessingOrg() != null)
						&& (!posDef.getProcessingOrg().equals(getCurrentUser().getProcessingOrg()))) {
					value = null;
				}
			}
		}
		return value;
	}

}