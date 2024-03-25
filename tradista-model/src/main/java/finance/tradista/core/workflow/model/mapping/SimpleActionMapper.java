package finance.tradista.core.workflow.model.mapping;

import finance.tradista.core.workflow.model.SimpleAction;
import finance.tradista.flow.model.Workflow;

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

public final class SimpleActionMapper {

	private SimpleActionMapper() {
	}

	public static SimpleAction map(finance.tradista.flow.model.SimpleAction action) {
		SimpleAction actionResult = null;
		if (action != null) {
			actionResult = new SimpleAction();
			actionResult.setId(action.getId());
			actionResult.setName(action.getName());
			actionResult.setArrivalStatus(StatusMapper.map(action.getArrivalStatus()));
			actionResult.setDepartureStatus(StatusMapper.map(action.getDepartureStatus()));
			actionResult.setGuard(GuardMapper.map(action.getGuard()));
			actionResult.setProcess(ProcessMapper.map(action.getProcess()));
		}
		return actionResult;
	}

	public static finance.tradista.flow.model.SimpleAction map(SimpleAction action, Workflow workflow) {
		finance.tradista.flow.model.SimpleAction actionResult = null;
		if (action != null) {
			actionResult = new finance.tradista.flow.model.SimpleAction();
			actionResult.setId(action.getId());
			actionResult.setName(action.getName());
			actionResult.setArrivalStatus(StatusMapper.map(action.getArrivalStatus(), workflow));
			actionResult.setDepartureStatus(StatusMapper.map(action.getDepartureStatus(), workflow));
			actionResult.setGuard(GuardMapper.map(action.getGuard()));
			actionResult.setProcess(ProcessMapper.map(action.getProcess()));
			actionResult.setWorkflow(workflow);
		}
		return actionResult;
	}

}