package finance.tradista.ai.reasoning.prm.probability.model;

import finance.tradista.ai.reasoning.prm.model.FunctionCall;
import finance.tradista.ai.reasoning.prm.model.Value;
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

public class Predicate extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7471677760825414676L;

	private FunctionCall functionCall;

	private Operator operator;

	private Value value;

	private boolean result;

	public FunctionCall getFunctionCall() {
		return functionCall;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public void setFunctionCall(FunctionCall functionCall) {
		this.functionCall = functionCall;
	}	
	

}