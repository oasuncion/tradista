package finance.tradista.ai.agent.service;

import finance.tradista.ai.agent.model.AssetManagerAgent;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;

/*
 * Copyright 2018 Olivier Asuncion
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

public class AssetManagerAgentBusinessDelegate {

	private AssetManagerAgentService assetManagerAgentService;

	public AssetManagerAgentBusinessDelegate() {
		assetManagerAgentService = TradistaServiceLocator.getInstance().getAssetManagerAgentService();
	}

	public long saveAssetManagerAgent(AssetManagerAgent agent) throws TradistaBusinessException {
		if (agent == null) {
			throw new TradistaBusinessException("The agent cannot be null.");
		}
		StringBuilder errMsg = new StringBuilder();
		if (agent.getName() == null) {
			errMsg.append(String.format("The name is mandatory.%n"));
		}
		if (agent.getMandate() == null) {
			errMsg.append(String.format("The mandate is mandatory.%n"));
		}
		if (agent.getPricingParameter() == null) {
			errMsg.append(String.format("The pricing parameter set is mandatory.%n"));
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

		return SecurityUtil.runEx(() -> assetManagerAgentService.saveAssetManagerAgent(agent));
	}

	public void executeMandate(AssetManagerAgent agent) throws TradistaBusinessException {
		if (agent == null) {
			throw new TradistaBusinessException("The agent cannot be null.");
		}
		SecurityUtil.runEx(() -> assetManagerAgentService.executeMandate(agent));
	}

}