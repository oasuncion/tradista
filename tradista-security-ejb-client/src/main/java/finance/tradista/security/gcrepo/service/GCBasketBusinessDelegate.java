package finance.tradista.security.gcrepo.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.security.gcrepo.model.GCBasket;

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

public class GCBasketBusinessDelegate {

	private GCBasketService gcBasketService;

	public GCBasketBusinessDelegate() {
		gcBasketService = TradistaServiceLocator.getInstance().getGCBasketService();
	}

	public long saveGCBasket(GCBasket gcBasket) throws TradistaBusinessException {
		if (gcBasket == null) {
			throw new TradistaBusinessException("the GC Basket cannot be null.");
		} else {
			if (StringUtils.isEmpty(gcBasket.getName())) {
				throw new TradistaBusinessException("the GC Basket name is mandatory.");
			}
		}
		return SecurityUtil.runEx(() -> gcBasketService.saveGCBasket(gcBasket));
	}

	public GCBasket getGCBasketById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("the GC Basket id must be positive.");
		}
		return SecurityUtil.run(() -> gcBasketService.getGCBasketById(id));
	}

	public Set<GCBasket> getAllGCBaskets() {
		return SecurityUtil.run(() -> gcBasketService.getAllGCBaskets());
	}

	public GCBasket getGCBasketByName(String name) throws TradistaBusinessException {
		if (StringUtils.isEmpty(name)) {
			throw new TradistaBusinessException("the GC Basket name is mandatory.");
		}
		return SecurityUtil.run(() -> gcBasketService.getGCBasketByName(name));
	}

}