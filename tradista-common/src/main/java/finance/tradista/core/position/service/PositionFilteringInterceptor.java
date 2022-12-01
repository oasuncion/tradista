package finance.tradista.core.position.service;

import java.util.List;
import java.util.stream.Collectors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.position.model.Position;
import finance.tradista.core.position.model.PositionDefinition;
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

public class PositionFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private PositionDefinitionBusinessDelegate positionDefinitionBusinessDelegate;

	public PositionFilteringInterceptor() {
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
				throw new TradistaBusinessException(
						String.format("The Position Definition %s was not found.", posDefId));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			if (value instanceof List) {
				List<Position> positions = (List<Position>) value;
				User user = getCurrentUser();
				value = positions.stream().filter(
						p -> p.getPositionDefinition().getBook().getProcessingOrg().equals(user.getProcessingOrg()))
						.collect(Collectors.toList());
			}
		}
		return value;
	}

}