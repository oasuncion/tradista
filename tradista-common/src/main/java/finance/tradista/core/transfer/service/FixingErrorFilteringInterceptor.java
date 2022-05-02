package finance.tradista.core.transfer.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.transfer.model.FixingError;
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

public class FixingErrorFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private TransferBusinessDelegate transferBusinessDelegate;

	public FixingErrorFilteringInterceptor() {
		super();
		transferBusinessDelegate = new TransferBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		long transferId = (long) parameters[0];
		if (transferId != 0) {
			Transfer transfer = transferBusinessDelegate.getTransferById(transferId);
			if (transfer == null) {
				throw new TradistaBusinessException(String.format("The Transfer %s was not found.", transferId));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			if (value instanceof List) {
				List<FixingError> fixingErrors = (List<FixingError>) value;
				User user = getCurrentUser();
				value = fixingErrors.stream()
						.filter(fe -> fe.getCashTransfer().getBook().getProcessingOrg().equals(user.getProcessingOrg()))
						.collect(Collectors.toList());
			}
		}
		return value;
	}

}