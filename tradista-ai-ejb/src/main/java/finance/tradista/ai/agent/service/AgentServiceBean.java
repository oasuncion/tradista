package finance.tradista.ai.agent.service;

import java.util.Set;

import javax.ejb.Stateless;

import finance.tradista.ai.agent.model.Agent;
import finance.tradista.ai.agent.persistence.AgentSQL;
import finance.tradista.ai.reasoning.prm.persistence.FunctionSQL;
import finance.tradista.core.common.exception.TradistaBusinessException;

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

@Stateless
public class AgentServiceBean implements AgentService {

	@Override
	public long saveAgent(Agent agent) throws TradistaBusinessException {
		if (agent.getId() == 0) {
			checkNameExistence(agent);
			return AgentSQL.saveAgent(agent);
		} else {
			Agent oldAgent = AgentSQL.getAgentById(agent.getId());
			if (!agent.getName().equals(oldAgent.getName())) {
				checkNameExistence(agent);
			}
			return AgentSQL.saveAgent(agent);
		}
	}

	private void checkNameExistence(Agent agent) throws TradistaBusinessException {
		if (FunctionSQL.getFunctionByName(agent.getName()) != null) {
			throw new TradistaBusinessException(String.format("This agent '%s' already exists.", agent.getName()));
		}
	}

	@Override
	public Set<Agent> getAllStartedAgents() {
		return AgentSQL.getAllStartedAgents();
	}

}