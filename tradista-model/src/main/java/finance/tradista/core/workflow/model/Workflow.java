package finance.tradista.core.workflow.model;

import java.util.Objects;
import java.util.Set;

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

public class Workflow extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private String description;

	private Set<Action> actions;

	private Set<Status> status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@SuppressWarnings("unchecked")
	public Set<Action> getActions() {
		return (Set<Action>) TradistaModelUtil.deepCopy(actions);
	}

	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}

	@SuppressWarnings("unchecked")
	public Set<Status> getStatus() {
		return (Set<Status>) TradistaModelUtil.deepCopy(status);
	}

	public void setStatus(Set<Status> status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Workflow other = (Workflow) obj;
		return Objects.equals(name, other.name);
	}

}