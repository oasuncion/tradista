package finance.tradista.core.workflow.model;

import java.util.Objects;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;

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

public abstract class Action extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String NEW = "NEW";

	private String name;

	private String workflowName;

	private Status departureStatus;

	private Guard guard;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Status getDepartureStatus() {
		return departureStatus;
	}

	public void setDepartureStatus(Status departureStatus) {
		this.departureStatus = departureStatus;
	}

	public Guard getGuard() {
		return TradistaModelUtil.clone(guard);
	}

	public void setGuard(Guard guard) {
		this.guard = guard;
	}

	@Override
	public Action clone() {
		Action action = (Action) super.clone();
		action.departureStatus = TradistaModelUtil.clone(departureStatus);
		action.guard = TradistaModelUtil.clone(guard);
		return action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(departureStatus, name, workflowName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		return Objects.equals(departureStatus, other.departureStatus) && Objects.equals(name, other.name)
				&& Objects.equals(workflowName, other.workflowName);
	}

	@Override
	public String toString() {
		return name;
	}

}