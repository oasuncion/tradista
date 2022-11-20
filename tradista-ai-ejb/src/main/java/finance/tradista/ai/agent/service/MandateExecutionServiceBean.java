package finance.tradista.ai.agent.service;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import finance.tradista.ai.agent.model.AssetManagerAgent;
import finance.tradista.core.common.exception.TradistaBusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

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

@Startup
@Singleton
public class MandateExecutionServiceBean {

	@EJB
	AssetManagerAgentService agentService;

	@PostConstruct
	public void init() {
		Set<AssetManagerAgent> agents = agentService.getAllStartedAssetManagerAgents();
		if (agents != null && !agents.isEmpty()) {
			ExecutorService executor = Executors.newScheduledThreadPool(agents.size());
			for (AssetManagerAgent a : agents) {
				executor.submit(() -> {
					try {
						agentService.executeMandate(a);
					} catch (TradistaBusinessException tbe) {
						// TODO Check what to do when an agent encounters an error
						tbe.printStackTrace();
					}
				});
			}
		}
	}

}