package finance.tradista.security.gcrepo.service;

import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.security.gcrepo.model.GCBasket;
import finance.tradista.security.gcrepo.persistence.GCBasketSQL;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

/*
 * Copyright 2023 Olivier Asuncion
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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class GCBasketServiceBean implements GCBasketService {

	@Override
	@Interceptors(GCRepoProductScopeFilteringInterceptor.class)
	public long saveGCBasket(GCBasket gcBasket) throws TradistaBusinessException {
		if (gcBasket.getId() == 0) {
			checkNameExistence(gcBasket);
			return GCBasketSQL.saveGCBasket(gcBasket);
		} else {
			GCBasket oldGCBasket = GCBasketSQL.getGCBasketById(gcBasket.getId());
			if (!gcBasket.getName().equals(oldGCBasket.getName())) {
				checkNameExistence(gcBasket);
			}
			return GCBasketSQL.saveGCBasket(gcBasket);
		}
	}

	private void checkNameExistence(GCBasket gcBasket) throws TradistaBusinessException {
		if (getGCBasketByName(gcBasket.getName()) != null) {
			throw new TradistaBusinessException(
					String.format("This GC Basket '%s' already exists in the system.", gcBasket.getName()));
		}
	}

	@Override
	public GCBasket getGCBasketByName(String name) {
		return GCBasketSQL.getGCBasketByName(name);
	}

	@Override
	public GCBasket getGCBasketById(long id) {
		return GCBasketSQL.getGCBasketById(id);
	}

	@Override
	public Set<GCBasket> getAllGCBaskets() {
		return GCBasketSQL.getAllGCBaskets();
	}

}