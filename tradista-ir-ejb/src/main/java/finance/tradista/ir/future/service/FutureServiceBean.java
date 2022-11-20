package finance.tradista.ir.future.service;

import java.util.Set;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.ir.future.model.Future;
import finance.tradista.ir.future.persistence.FutureSQL;
import finance.tradista.ir.future.service.FutureService;

/*
 * Copyright 2015 Olivier Asuncion
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

@Stateless
public class FutureServiceBean implements FutureService {

	@Override
	public Set<Future> getAllFutures() {
		return FutureSQL.getAllFutures();
	}

	@Override
	public Future getFutureById(long id) {
		return FutureSQL.getFutureById(id);
	}

	@Override
	public Future getFutureByContractSpecificationAndSymbol(String contractSpecification, String symbol) {
		return FutureSQL.getFutureByContractSpecificationAndSymbol(contractSpecification, symbol);
	}

	@Override
	@Interceptors(FutureProductScopeFilteringInterceptor.class)
	public long saveFuture(Future future) throws TradistaBusinessException {
		if (future.getId() == 0) {
			checkSymbolExistence(future);
			return FutureSQL.saveFuture(future);
		} else {
			Future oldFuture = FutureSQL.getFutureById(future.getId());
			if (!future.getSymbol().equals(oldFuture.getSymbol())) {
				checkSymbolExistence(future);
			}
			return FutureSQL.saveFuture(future);
		}
	}

	private void checkSymbolExistence(Future future) throws TradistaBusinessException {
		if (new FutureServiceBean().getFutureByContractSpecificationAndSymbol(
				future.getContractSpecification().getName(), future.getSymbol()) != null) {
			throw new TradistaBusinessException(String.format("This future '%s' already exists for the contract %s.",
					future.getSymbol(), future.getContractSpecification().getName()));
		}
	}

}