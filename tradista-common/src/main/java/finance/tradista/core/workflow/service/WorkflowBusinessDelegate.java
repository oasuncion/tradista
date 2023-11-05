package finance.tradista.core.workflow.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
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

public class WorkflowBusinessDelegate {

    private WorkflowService workflowService;

    public WorkflowBusinessDelegate() {
	workflowService = TradistaServiceLocator.getInstance().getWorkflowService();
    }

    public Workflow getWorkflowByName(String name) throws TradistaBusinessException {
	if (StringUtils.isEmpty(name)) {
	    throw new TradistaBusinessException("The name is mandatory");
	}
	return SecurityUtil.runEx(() -> workflowService.getWorkflowByName(name));
    }

    public Set<Action> getAvailableActionsFromStatus(String workflowName, Status status)
	    throws TradistaBusinessException {
	StringBuilder errMsg = new StringBuilder();
	if (StringUtils.isEmpty(workflowName)) {
	    errMsg.append("The workflow name is mandatory.");
	}
	if (status == null) {
	    errMsg.append("The status is mandatory.");
	}
	if (!errMsg.isEmpty()) {
	    throw new TradistaBusinessException(errMsg.toString());
	}
	return SecurityUtil.runEx(() -> workflowService.getAvailableActionsFromStatus(workflowName, status));
    }
    
    public Status getInitialStatus(String workflowName) throws TradistaBusinessException {
	StringBuilder errMsg = new StringBuilder();
	if (StringUtils.isEmpty(workflowName)) {
	    errMsg.append("The workflow name is mandatory.");
	}
	if (!errMsg.isEmpty()) {
	    throw new TradistaBusinessException(errMsg.toString());
	}
	return SecurityUtil.runEx(() -> workflowService.getInitialStatus(workflowName));
    }

}