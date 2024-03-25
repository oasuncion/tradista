package finance.tradista.security.ui.converter;

import java.io.Serializable;

import finance.tradista.security.bond.service.BondBusinessDelegate;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.equity.service.EquityBusinessDelegate;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

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

@FacesConverter("securityConverter")
public class SecurityConverter implements Serializable, Converter<Security> {

	private static final long serialVersionUID = 3469069244088871255L;
	private BondBusinessDelegate bondBusinessDelegate;
	private EquityBusinessDelegate equityBusinessDelegate;

	public SecurityConverter() {
		bondBusinessDelegate = new BondBusinessDelegate();
		equityBusinessDelegate = new EquityBusinessDelegate();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Security security) {
		String value = null;
		if (security != null) {
			value = security.toString();
		}
		return value;
	}

	@Override
	public Security getAsObject(FacesContext context, UIComponent component, String value) {
		Security security = null;
		if (value != null) {
			String[] values = value.split(" - ");
			String isin = values[0];
			String exchange = values[1];
			security = bondBusinessDelegate.getBondByIsinAndExchangeCode(isin, exchange);
			if (security == null) {
				security = equityBusinessDelegate.getEquityByIsinAndExchangeCode(isin, exchange);
			}
		}
		return security;
	}

}