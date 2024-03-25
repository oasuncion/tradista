package finance.tradista.core.workflow.model.mapping;

import finance.tradista.core.workflow.model.Action;
import finance.tradista.core.workflow.model.ConditionalAction;
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

public final class ActionMapper {

	private ActionMapper() {
	}

	public static Action map(finance.tradista.flow.model.Action action) {
		Action actionResult = null;
		if (action != null) {
			if (action instanceof finance.tradista.flow.model.SimpleAction simpleAction) {
				actionResult = SimpleActionMapper.map(simpleAction);
			} else {
				actionResult = ConditionalActionMapper.map((finance.tradista.flow.model.ConditionalAction) action);
			}
		}
		return actionResult;
	}

	public static finance.tradista.flow.model.Action map(Action action, Workflow workflow) {
		finance.tradista.flow.model.Action actionResult = null;
		if (action != null) {
			if (action instanceof SimpleAction simpleAction) {
				actionResult = SimpleActionMapper.map(simpleAction, workflow);
			} else {
				actionResult = ConditionalActionMapper.map((ConditionalAction) action, workflow);
			}
		}
		return actionResult;
	}

}