package finance.tradista.core.workflow.model;

import java.util.Map;
import java.util.Set;

import finance.tradista.core.common.model.TradistaModelUtil;

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

public class ConditionalAction extends Action {

	private static final long serialVersionUID = -7207658264977414731L;

	private Condition condition;

	private Map<Integer, Status> conditionalRouting;

	private Map<Status, Process> conditionalProcesses;

	private Status choicePseudoStatus;

	private Set<SimpleAction> conditionalActions;

	public Condition getCondition() {
		return TradistaModelUtil.clone(condition);
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Status> getConditionalRouting() {
		return (Map<Integer, Status>) TradistaModelUtil.deepCopy(conditionalRouting);
	}

	public void setConditionalRouting(Map<Integer, Status> conditionalRouting) {
		this.conditionalRouting = conditionalRouting;
	}

	@SuppressWarnings("unchecked")
	public Map<Status, Process> getConditionalProcesses() {
		return (Map<Status, Process>) TradistaModelUtil.deepCopy(conditionalProcesses);
	}

	public void setConditionalProcesses(Map<Status, Process> conditionalProcesses) {
		this.conditionalProcesses = conditionalProcesses;
	}

	public Status getChoicePseudoStatus() {
		return TradistaModelUtil.clone(choicePseudoStatus);
	}

	public void setChoicePseudoStatus(Status choicePseudoStatus) {
		this.choicePseudoStatus = choicePseudoStatus;
	}

	@SuppressWarnings("unchecked")
	public Set<SimpleAction> getConditionalActions() {
		return (Set<SimpleAction>) TradistaModelUtil.deepCopy(conditionalActions);
	}

	public void setConditionalActions(Set<SimpleAction> conditionalActions) {
		this.conditionalActions = conditionalActions;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ConditionalAction clone() {
		ConditionalAction action = (ConditionalAction) super.clone();
		action.condition = TradistaModelUtil.clone(condition);
		action.conditionalRouting = (Map<Integer, Status>) TradistaModelUtil.deepCopy(conditionalRouting);
		action.conditionalProcesses = (Map<Status, Process>) TradistaModelUtil.deepCopy(conditionalProcesses);
		action.conditionalActions = (Set<SimpleAction>) TradistaModelUtil.deepCopy(conditionalActions);
		return action;
	}

}