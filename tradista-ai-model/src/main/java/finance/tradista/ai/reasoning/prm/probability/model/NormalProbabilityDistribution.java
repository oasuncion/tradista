package finance.tradista.ai.reasoning.prm.probability.model;

import org.apache.commons.math3.distribution.NormalDistribution;

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

public class NormalProbabilityDistribution extends ContinuousProbabilityDistribution {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3118439260355422888L;

	public NormalProbabilityDistribution() {
		super();
		realDistributon = new NormalDistribution();
	}

	public NormalProbabilityDistribution(short mean, short standardDeviation) {
		super();
		realDistributon = new NormalDistribution(mean, standardDeviation);
	}

	private short mean;

	private short standardDeviation;

	public short getMean() {
		return mean;
	}

	public void setMean(short mean) {
		this.mean = mean;
	}

	public short getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(short standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

}