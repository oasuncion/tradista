package finance.tradista.core.pricing.service;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.service.PositionDefinitionBusinessDelegate;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.service.TradeBusinessDelegate;

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

public class CashFlowPreFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private TradeBusinessDelegate tradeBusinessDelegate;

	private PositionDefinitionBusinessDelegate positionDefinitionBusinessDelegate;

	public CashFlowPreFilteringInterceptor() {
		super();
		tradeBusinessDelegate = new TradeBusinessDelegate();
		positionDefinitionBusinessDelegate = new PositionDefinitionBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		if (parameters[0] instanceof Long) {
			long tradeId = (long) parameters[0];
			if (tradeId != 0) {
				Trade<?> trade = tradeBusinessDelegate.getTradeById(tradeId, false);
				if (trade == null) {
					throw new TradistaBusinessException(String.format("The trade %d was not found.", tradeId));
				}
			}
		}
		if (parameters[0] instanceof PricingParameter) {
			PricingParameter pp = (PricingParameter) parameters[0];
			if (pp != null) {
				if (!pp.getProcessingOrg().equals(getCurrentUser().getProcessingOrg())) {
					throw new TradistaBusinessException(String.format("The Pricing Parameters Set %s was not found.", pp));
				}
			}
		}
		if (parameters[1] instanceof PricingParameter) {
			PricingParameter pp = (PricingParameter) parameters[1];
			if (pp != null) {
				if (!pp.getProcessingOrg().equals(getCurrentUser().getProcessingOrg())) {
					throw new TradistaBusinessException(String.format("The Pricing Parameters Set %s was not found.", pp));
				}
			}
		}
		if (parameters.length >= 3 && parameters[2] instanceof PositionDefinition) {
			long posDefId = (long) parameters[2];
			if (posDefId != 0) {
				PositionDefinition posDef = positionDefinitionBusinessDelegate.getPositionDefinitionById(posDefId);
				if (posDef == null) {
					throw new TradistaBusinessException(String.format("The Position Definition %s was not found.", posDef));
				}
			}
		}
	}

}