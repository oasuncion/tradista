package finance.tradista.ai.reasoning.prm.model;

import java.util.Set;

import finance.tradista.core.common.model.TradistaModelUtil;
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

public class FunctionCall extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3250890461242436439L;

	private Function function;

	private Set<Value> parameters;

	public Function getFunction() {
		return TradistaModelUtil.clone(function);
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	@SuppressWarnings("unchecked")
	public Set<Value> getParameters() {
		return (Set<Value>) TradistaModelUtil.deepCopy(parameters);
	}

	public void setParameters(Set<Value> parameters) {
		this.parameters = parameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FunctionCall clone() {
		FunctionCall functionCall = (FunctionCall) super.clone();
		functionCall.function = TradistaModelUtil.clone(function);
		functionCall.parameters = (Set<Value>) TradistaModelUtil.deepCopy(parameters);
		return functionCall;
	}

}