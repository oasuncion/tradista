package finance.tradista.core.dailypnl.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.dailypnl.model.DailyPnl;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.service.PositionDefinitionBusinessDelegate;
import finance.tradista.core.user.model.User;

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

public class DailyPnlFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private PositionDefinitionBusinessDelegate positionDefinitionBusinessDelegate;

	public DailyPnlFilteringInterceptor() {
		super();
		positionDefinitionBusinessDelegate = new PositionDefinitionBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		long posDefId = (long) parameters[0];
		if (posDefId != 0) {
			PositionDefinition posDef = positionDefinitionBusinessDelegate.getPositionDefinitionById(posDefId);
			if (posDef == null) {
				throw new TradistaBusinessException(String.format("The Book %s was not found.", posDefId));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			if (value instanceof Set) {
				Set<DailyPnl> dailyPnls = (Set<DailyPnl>) value;
				User user = getCurrentUser();
				value = dailyPnls.stream().filter(
						pnl -> pnl.getPositionDefinition().getBook().getProcessingOrg().equals(user.getProcessingOrg()))
						.collect(Collectors.toSet());
			}
		}
		return value;
	}

}