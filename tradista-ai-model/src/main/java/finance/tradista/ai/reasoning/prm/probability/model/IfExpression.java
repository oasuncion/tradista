package finance.tradista.ai.reasoning.prm.probability.model;

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

public class IfExpression extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7393724119422401024L;

	private Predicate condition;

	private ProbabilityDistribution ifClause;

	private ProbabilityDistribution elseClause;

	private ComplexProbabilityDistribution complexProbabilityDistribution;

	private short position;

	public Predicate getCondition() {
		return TradistaModelUtil.clone(condition);
	}

	public ProbabilityDistribution getIfClause() {
		return TradistaModelUtil.clone(ifClause);
	}

	public ProbabilityDistribution getElseClause() {
		return TradistaModelUtil.clone(elseClause);
	}

	public ComplexProbabilityDistribution getComplexProbabilityDistribution() {
		return TradistaModelUtil.clone(complexProbabilityDistribution);
	}

	public void setComplexProbabilityDistribution(ComplexProbabilityDistribution complexProbabilityDistribution) {
		this.complexProbabilityDistribution = complexProbabilityDistribution;
	}

	public short getPosition() {
		return position;
	}

	public void setPosition(short position) {
		this.position = position;
	}

	public void setCondition(Predicate condition) {
		this.condition = condition;
	}

	public void setIfClause(ProbabilityDistribution ifClause) {
		this.ifClause = ifClause;
	}

	public void setElseClause(ProbabilityDistribution elseClause) {
		this.elseClause = elseClause;
	}

	@Override
	public IfExpression clone() {
		IfExpression ifExpression = (IfExpression) super.clone();
		ifExpression.condition = TradistaModelUtil.clone(condition);
		ifExpression.ifClause = TradistaModelUtil.clone(ifClause);
		ifExpression.elseClause = TradistaModelUtil.clone(elseClause);
		ifExpression.complexProbabilityDistribution = TradistaModelUtil.clone(complexProbabilityDistribution);
		return ifExpression;
	}

}