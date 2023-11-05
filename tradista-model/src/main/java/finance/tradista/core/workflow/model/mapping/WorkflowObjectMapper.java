package finance.tradista.core.workflow.model.mapping;

import finance.tradista.core.workflow.model.Status;
import finance.tradista.core.workflow.model.WorkflowObject;
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

public final class WorkflowObjectMapper {

    public static finance.tradista.flow.model.WorkflowObject map(WorkflowObject wo, Workflow wkf) {
	return new finance.tradista.flow.model.WorkflowObject() {

	    private finance.tradista.flow.model.Status status = StatusMapper.map(wo.getStatus(), wkf);

	    @Override
	    public finance.tradista.flow.model.Status getStatus() {
		return status;
	    }

	    @Override
	    public String getWorkflow() {
		return wo.getWorkflow();
	    }

	    @Override
	    public void setStatus(finance.tradista.flow.model.Status status) {
		this.status = status;
	    }

	    @Override
	    public finance.tradista.flow.model.WorkflowObject clone() throws java.lang.CloneNotSupportedException {
		return (finance.tradista.flow.model.WorkflowObject) super.clone();
	    }

	};
    }

    public static WorkflowObject map(finance.tradista.flow.model.WorkflowObject wo) {
	return new WorkflowObject() {

	    private Status status = StatusMapper.map(wo.getStatus());

	    @Override
	    public Status getStatus() {
		return status;
	    }

	    @Override
	    public String getWorkflow() {
		return wo.getWorkflow();
	    }

	    @Override
	    public void setStatus(Status status) {
		this.status = status;
	    }

	};
    }

}