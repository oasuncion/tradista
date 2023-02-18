package finance.tradista.ai.agent.model;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaObject;

/*
 * Copyright 2017 Olivier Asuncion
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

public class Agent extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -242917493894323568L;

	@Id
	private String name;

	private boolean onlyInformative;
	
	private boolean started;
	
	public Agent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isOnlyInformative() {
		return onlyInformative;
	}

	public void setOnlyInformative(boolean onlyInformative) {
		this.onlyInformative = onlyInformative;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

}