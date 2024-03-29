package finance.tradista.core.workflow.model.mapping;

import java.util.HashSet;
import java.util.Set;

import finance.tradista.core.workflow.model.Action;
import finance.tradista.core.workflow.model.Status;
import finance.tradista.core.workflow.model.Workflow;

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

public final class WorkflowMapper {

	private WorkflowMapper() {
	}

	public static Workflow map(finance.tradista.flow.model.Workflow wkf) {
		Workflow workflow = null;
		if (wkf != null) {
			workflow = new Workflow();
			workflow.setId(wkf.getId());
			workflow.setName(wkf.getName());
			workflow.setDescription(wkf.getDescription());
			Set<Status> statusSet = new HashSet<>();
			Set<Action> actionsSet = new HashSet<>();
			for (finance.tradista.flow.model.Status status : wkf.getStatus()) {
				Status currentStatus = StatusMapper.map(status);
				currentStatus.setWorkflowName(workflow.getName());
				statusSet.add(currentStatus);
			}
			for (finance.tradista.flow.model.Action action : wkf.getActions()) {
				Action currentAction = ActionMapper.map(action);
				if (currentAction != null) {
					actionsSet.add(currentAction);
					currentAction.setWorkflowName(workflow.getName());
				}
			}
			workflow.setActions(actionsSet);
			workflow.setStatus(statusSet);
		}
		return workflow;
	}

}