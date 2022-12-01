package finance.tradista.core.configuration.service;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.configuration.model.UIConfiguration;
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

public class ConfigurationPreFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		if (parameters.length > 0) {
			if (parameters[0] instanceof UIConfiguration) {
				UIConfiguration uiConfiguration = (UIConfiguration) parameters[0];
				StringBuilder errMsg = new StringBuilder();
				User user = getCurrentUser();
				if (!user.equals(uiConfiguration.getUser())) {
					errMsg.append(
							String.format("This UI configuration is not yours, you are not allowed to update it."));
				}
				if (errMsg.length() > 0) {
					throw new TradistaBusinessException(errMsg.toString());
				}
			}
			if (parameters[0] instanceof User) {
				User user = (User) parameters[0];
				StringBuilder errMsg = new StringBuilder();
				User currentUser = getCurrentUser();
				if (user.getId() != 0) {
					User u = userBusinessDelegate.getUserById(user.getId());
					if (u == null) {
						errMsg.append(String.format("The User %s was not found.%n", user.getSurname()));
					} else if (!u.equals(currentUser)) {
						errMsg.append(
								String.format("This UI configuration is not yours, you are not allowed to update it."));
					}
				}
				if (!currentUser.equals(user)) {
					errMsg.append(
							String.format("This UI configuration is not yours, you are not allowed to update it."));
				}
				if (errMsg.length() > 0) {
					throw new TradistaBusinessException(errMsg.toString());
				}
			}
		}
	}

}