package finance.tradista.core.workflow.model.mapping;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.workflow.model.Condition;

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

public final class ConditionMapper {

	private ConditionMapper() {
	}

	public static <X extends finance.tradista.flow.model.WorkflowObject> Condition map(
			finance.tradista.flow.model.Condition<X> condition) {
		Condition conditionResult = null;
		if (condition != null) {
			conditionResult = new Condition();
			conditionResult.setId(condition.getId());
			conditionResult.setName(condition.getName());
			conditionResult.setLongName(condition.getClass().getName());
		}
		return conditionResult;
	}

	@SuppressWarnings("unchecked")
	public static <X extends finance.tradista.flow.model.WorkflowObject> finance.tradista.flow.model.Condition<X> map(
			Condition condition) {
		finance.tradista.flow.model.Condition<X> conditionResult = null;
		if (condition != null) {
			conditionResult = TradistaModelUtil.getInstance(finance.tradista.flow.model.Condition.class,
					condition.getLongName());
			conditionResult.setId(condition.getId());
		}
		return conditionResult;
	}

}