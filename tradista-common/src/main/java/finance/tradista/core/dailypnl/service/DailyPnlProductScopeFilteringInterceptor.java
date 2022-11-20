package finance.tradista.core.dailypnl.service;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.dailypnl.model.DailyPnl;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.product.service.ProductBusinessDelegate;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

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

public class DailyPnlProductScopeFilteringInterceptor {

	protected ProductBusinessDelegate productBusinessDelegate;

	public DailyPnlProductScopeFilteringInterceptor() {
		productBusinessDelegate = new ProductBusinessDelegate();
	}

	@AroundInvoke
	protected Object proceed(InvocationContext ic) throws TradistaBusinessException, Exception {
		preFilter(ic);
		return ic.proceed();
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		if (ic.getParameters()[0] instanceof DailyPnl) {
			DailyPnl dailyPnl = (DailyPnl) ic.getParameters()[0];
			// Only object creations are controlled.
			if (dailyPnl.getId() == 0) {
				PositionDefinition posDef = dailyPnl.getPositionDefinition();
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
