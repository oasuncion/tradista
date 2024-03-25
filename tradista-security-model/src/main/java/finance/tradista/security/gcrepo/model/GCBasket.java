package finance.tradista.security.gcrepo.model;

import java.util.Set;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.security.common.model.Security;

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

public class GCBasket extends TradistaObject {

	private static final long serialVersionUID = -7581319325631065360L;

	@Id
	private String name;

	private Set<Security> securities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public Set<Security> getSecurities() {
		return (Set<Security>) TradistaModelUtil.deepCopy(securities);
	}

	public void setSecurities(Set<Security> securities) {
		this.securities = securities;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GCBasket clone() {
		GCBasket gcBasket = (GCBasket) super.clone();
		gcBasket.setSecurities((Set<Security>) TradistaModelUtil.deepCopy(securities));
		return gcBasket;
	}

	@Override
	public String toString() {
		return name;
	}
}