package finance.tradista.security.gcrepo.model;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaultsModule;

/*
 * Copyright 2024 Olivier Asuncion
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

public class ProcessingOrgDefaultsCollateralManagementModule extends ProcessingOrgDefaultsModule {

	private static final long serialVersionUID = -6779143569735077767L;

	public static final String COLLATERAL_MANAGEMENT = "Collateral Management";

	private QuoteSet quoteSet;

	public ProcessingOrgDefaultsCollateralManagementModule() {
		name = COLLATERAL_MANAGEMENT;
	}

	public QuoteSet getQuoteSet() {
		return TradistaModelUtil.clone(quoteSet);
	}

	public void setQuoteSet(QuoteSet qs) {
		quoteSet = qs;
	}

	@Override
	public ProcessingOrgDefaultsCollateralManagementModule clone() {
		ProcessingOrgDefaultsCollateralManagementModule module = (ProcessingOrgDefaultsCollateralManagementModule) super.clone();
		module.quoteSet = TradistaModelUtil.clone(quoteSet);
		return module;
	}

}