package finance.tradista.ai.reasoning.prm.probability.model;

import java.util.Set;

import finance.tradista.core.common.model.TradistaModelUtil;

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

public class ComplexProbabilityDistribution extends ProbabilityDistribution {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8607385455578856099L;

	private Set<IfExpression> ifExpressions;

	@SuppressWarnings("unchecked")
	public Set<IfExpression> getIfExpressions() {
		return (Set<IfExpression>) TradistaModelUtil.deepCopy(ifExpressions);
	}

	public void setIfExpressions(Set<IfExpression> ifExpressions) {
		this.ifExpressions = ifExpressions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComplexProbabilityDistribution clone() {
		ComplexProbabilityDistribution complexProbabilityDistribution = (ComplexProbabilityDistribution) super.clone();
		complexProbabilityDistribution.ifExpressions = (Set<IfExpression>) TradistaModelUtil.deepCopy(ifExpressions);
		return complexProbabilityDistribution;
	}

}