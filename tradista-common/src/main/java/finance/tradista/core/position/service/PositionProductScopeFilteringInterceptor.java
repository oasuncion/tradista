package finance.tradista.core.position.service;

import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.position.model.Position;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.product.service.ProductBusinessDelegate;

/*
 * Copyright 2021 Olivier Asuncion
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

public class PositionProductScopeFilteringInterceptor {

	protected ProductBusinessDelegate productBusinessDelegate;

	public PositionProductScopeFilteringInterceptor() {
		productBusinessDelegate = new ProductBusinessDelegate();
	}

	@AroundInvoke
	protected Object proceed(InvocationContext ic) throws TradistaBusinessException, Exception {
		preFilter(ic);
		return ic.proceed();
	}

	@SuppressWarnings("unchecked")
	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		if (ic.getParameters()[0] instanceof Position) {
			Position position = (Position) ic.getParameters()[0];
			// Only object creations are controlled.
			if (position.getId() == 0) {
				PositionDefinition posDef = position.getPositionDefinition();
				if (posDef.getProductType() != null)
					if (!productBusinessDelegate.getAllProducts().contains(posDef.getProduct())) {
						throw new TradistaBusinessException(String.format(
								"%s is not found among the allowed product types. Please contact your administrator.",
								posDef.getProductType()));
					}
			}
		} else if (ic.getParameters()[0] instanceof List<?>) {
			List<Position> positions = (List<Position>) ic.getParameters()[0];
			// Only object creations are controlled.
			for (Position position : positions) {
				if (position.getId() == 0) {
					PositionDefinition posDef = position.getPositionDefinition();
					if (posDef.getProductType() != null)
						if (!productBusinessDelegate.getAllProducts().contains(posDef.getProduct())) {
							throw new TradistaBusinessException(String.format(
									"%s is not found among the allowed product types. Please contact your administrator.",
									posDef.getProductType()));
						}
				}
			}
		}

	}
}
