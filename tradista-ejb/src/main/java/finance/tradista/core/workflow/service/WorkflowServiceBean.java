package finance.tradista.core.workflow.service;

import java.util.HashSet;
import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.workflow.model.Action;
import finance.tradista.core.workflow.model.Status;
import finance.tradista.core.workflow.model.Workflow;
import finance.tradista.core.workflow.model.mapping.ActionMapper;
import finance.tradista.core.workflow.model.mapping.StatusMapper;
import finance.tradista.core.workflow.model.mapping.WorkflowMapper;
import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.service.WorkflowManager;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;

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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class WorkflowServiceBean implements WorkflowService {

    @Override
    public Workflow getWorkflowByName(String name) throws TradistaBusinessException {
	finance.tradista.flow.model.Workflow workflow;
	Workflow workflowResult = null;
	try {
	    workflow = WorkflowManager.getWorkflowByName(name);
	} catch (TradistaFlowBusinessException tfbe) {
	    throw new TradistaBusinessException(tfbe);
	}
	if (workflow != null) {
	    workflowResult = WorkflowMapper.map(workflow);
	}
	return workflowResult;
    }

    @Override
    public Set<Action> getAvailableActionsFromStatus(String workflowName, Status status)
	    throws TradistaBusinessException {
	finance.tradista.flow.model.Workflow workflow;
	Set<finance.tradista.flow.model.Action> actions = null;
	Set<Action> actionsResult = null;
	try {
	    workflow = WorkflowManager.getWorkflowByName(workflowName);
	} catch (TradistaFlowBusinessException tfbe) {
	    throw new TradistaBusinessException(tfbe);
	}
	if (workflow != null) {
	    actions = workflow.getAvailableActionsFromStatus(StatusMapper.map(status, workflow));
	    if (actions != null) {
		actionsResult = new HashSet<>(actions.size());
		for (finance.tradista.flow.model.Action action : actions) {
		    Action currentAction = ActionMapper.map(action);
		    if (currentAction != null) {
			currentAction.setWorkflowName(workflowName);
			actionsResult.add(currentAction);
		    }
		}
	    }
	} else {
	    throw new TradistaBusinessException(String.format("The workflow %s cannot be found", workflowName));
	}
	return actionsResult;
    }

    @Override
    public Status getInitialStatus(String workflowName) throws TradistaBusinessException {
	finance.tradista.flow.model.Workflow workflow;
	try {
	    workflow = WorkflowManager.getWorkflowByName(workflowName);
	} catch (TradistaFlowBusinessException tfbe) {
	    throw new TradistaBusinessException(tfbe);
	}
	if (workflow != null) {
	    return StatusMapper.map(workflow.getInitialStatus());
	} else {
	    throw new TradistaBusinessException(String.format("The workflow %s cannot be found", workflowName));
	}
    }

}