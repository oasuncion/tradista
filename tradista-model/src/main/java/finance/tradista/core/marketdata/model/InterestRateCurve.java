package finance.tradista.core.marketdata.model;

import finance.tradista.core.legalentity.model.LegalEntity;

/*
 * Copyright 2014 Olivier Asuncion
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

public class InterestRateCurve extends GenerableCurve {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String INTEREST_RATE_CURVE = "InterestRateCurve";

	public InterestRateCurve(String name, LegalEntity po) {
		super(name, po);
	}

	public String getType() {
		return INTEREST_RATE_CURVE;
	}

	public String toString() {
		return this.getName();
	}

}