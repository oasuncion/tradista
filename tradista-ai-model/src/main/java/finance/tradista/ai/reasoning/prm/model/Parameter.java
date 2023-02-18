package finance.tradista.ai.reasoning.prm.model;

import finance.tradista.core.common.model.Id;
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

public class Parameter extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2523448749272012352L;

	@Id
	private short position;

	private Type type;

	@Id
	private Function function;

	public Parameter(Function function, short position) {
		super();
		this.function = function;
		this.position = position;
	}

	public short getPosition() {
		return position;
	}

	public void setPosition(short position) {
		this.position = position;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Function getFunction() {
		return TradistaModelUtil.clone(function);
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	@Override
	public Parameter clone() {
		Parameter parameter = (Parameter) super.clone();
		parameter.function = TradistaModelUtil.clone(function);
		return parameter;
	}

}