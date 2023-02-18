package finance.tradista.ai.agent.model;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.pricing.pricer.PricingParameter;

/*
 * Copyright 2017 Olivier Asuncion
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

public class AssetManagerAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 497961030733496375L;

	private Mandate mandate;

	private PricingParameter pricingParameter;

	public AssetManagerAgent(String name) {
		super(name);
	}

	public Mandate getMandate() {
		return TradistaModelUtil.clone(mandate);
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public PricingParameter getPricingParameter() {
		return TradistaModelUtil.clone(pricingParameter);
	}

	public void setPricingParameter(PricingParameter pricingParameter) {
		this.pricingParameter = pricingParameter;
	}

	@Override
	public AssetManagerAgent clone() {
		AssetManagerAgent agent = (AssetManagerAgent) super.clone();
		agent.mandate = TradistaModelUtil.clone(mandate);
		agent.pricingParameter = TradistaModelUtil.clone(pricingParameter);
		return agent;
	}

}