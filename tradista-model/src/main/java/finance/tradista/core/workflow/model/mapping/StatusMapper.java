package finance.tradista.core.workflow.model.mapping;

import finance.tradista.core.workflow.model.PseudoStatus;
import finance.tradista.core.workflow.model.Status;
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

public final class StatusMapper {

    private StatusMapper() {
    }

    public static Status map(finance.tradista.flow.model.Status status) {
	Status statusResult = null;
	if (status != null) {
	    if (status instanceof finance.tradista.flow.model.PseudoStatus) {
		statusResult = new PseudoStatus();
	    } else {
		statusResult = new Status();
	    }
	    statusResult.setId(status.getId());
	    statusResult.setName(status.getName());
	}
	return statusResult;
    }

    public static finance.tradista.flow.model.Status map(Status status, Workflow workflow) {
	finance.tradista.flow.model.Status statusResult = null;
	if (status != null) {
	    if (status instanceof PseudoStatus) {
		statusResult = new finance.tradista.flow.model.PseudoStatus();
	    } else {
		statusResult = new finance.tradista.flow.model.Status();
	    }
	    statusResult.setId(status.getId());
	    statusResult.setName(status.getName());
	    statusResult.setWorkflow(workflow);
	}
	return statusResult;
    }

}