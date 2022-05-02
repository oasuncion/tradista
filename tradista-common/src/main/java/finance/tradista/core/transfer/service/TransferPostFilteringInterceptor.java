package finance.tradista.core.transfer.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.transfer.model.Transfer;
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

public class TransferPostFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	@SuppressWarnings("unchecked")
	public void filter(Object value) throws Exception {
		if (value != null) {
			if (value instanceof Transfer) {
				Transfer transfer = (Transfer) value;
				User user = getCurrentUser();
				if (!transfer.getBook().getProcessingOrg().equals(user.getProcessingOrg())) {
					value = null;
				}
			}
			if (value instanceof List) {
				List<Transfer> transfers = (List<Transfer>) value;
				User user = getCurrentUser();
				value = transfers.stream().filter(t -> t.getBook().getProcessingOrg().equals(user.getProcessingOrg()))
						.collect(Collectors.toList());
			}
		}
	}

}