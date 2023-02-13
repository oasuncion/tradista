package finance.tradista.fx.fxoption.model;

import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.currency.model.CurrencyPair;
import finance.tradista.core.pricing.pricer.PricingParameterModule;

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

public class PricingParameterVolatilitySurfaceModule extends PricingParameterModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6630400213476177724L;

	private Map<CurrencyPair, FXVolatilitySurface> volatilitySurfaces;

	public PricingParameterVolatilitySurfaceModule() {
		volatilitySurfaces = new HashMap<CurrencyPair, FXVolatilitySurface>();
	}

	@Override
	public String getProductFamily() {
		return "fx";
	}

	@Override
	public String getProductType() {
		return FXOptionTrade.FX_OPTION;
	}

	@SuppressWarnings("unchecked")
	public Map<CurrencyPair, FXVolatilitySurface> getVolatilitySurfaces() {
		return (Map<CurrencyPair, FXVolatilitySurface>) TradistaModelUtil.deepCopy(volatilitySurfaces);
	}

	public void setVolatilitySurfaces(Map<CurrencyPair, FXVolatilitySurface> volatilitySurfaces) {
		this.volatilitySurfaces = volatilitySurfaces;
	}

	public FXVolatilitySurface getFXVolatilitySurface(CurrencyPair currencyPair) {
		if (currencyPair == null || volatilitySurfaces == null) {
			return null;
		}
		return TradistaModelUtil.clone(volatilitySurfaces.get(currencyPair));
	}

	@SuppressWarnings("unchecked")
	@Override
	public PricingParameterVolatilitySurfaceModule clone() {
		PricingParameterVolatilitySurfaceModule pricingParameterVolatilitySurfaceModule = (PricingParameterVolatilitySurfaceModule) super.clone();
		pricingParameterVolatilitySurfaceModule.volatilitySurfaces = (Map<CurrencyPair, FXVolatilitySurface>) TradistaModelUtil
				.deepCopy(volatilitySurfaces);
		return pricingParameterVolatilitySurfaceModule;
	}

}