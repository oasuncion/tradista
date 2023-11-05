package finance.tradista.core.workflow.model;

import java.util.Objects;

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

public class SimpleAction extends Action {

	private static final long serialVersionUID = 1L;

	private Process process;

	private Status arrivalStatus;

	public Process getProcess() {
		return TradistaModelUtil.clone(process);
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Status getArrivalStatus() {
		return TradistaModelUtil.clone(arrivalStatus);
	}

	public void setArrivalStatus(Status arrivalStatus) {
		this.arrivalStatus = arrivalStatus;
	}

	@Override
	public SimpleAction clone() {
		SimpleAction action = (SimpleAction) super.clone();
		action.arrivalStatus = TradistaModelUtil.clone(arrivalStatus);
		action.process = TradistaModelUtil.clone(process);
		return action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arrivalStatus, getDepartureStatus(), getName(), getWorkflowName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleAction other = (SimpleAction) obj;
		return Objects.equals(arrivalStatus, other.arrivalStatus)
				&& Objects.equals(getDepartureStatus(), other.getDepartureStatus())
				&& Objects.equals(getName(), other.getName())
				&& Objects.equals(getWorkflowName(), other.getWorkflowName());
	}

}