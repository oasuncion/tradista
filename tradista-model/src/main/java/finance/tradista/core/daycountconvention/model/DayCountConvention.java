package finance.tradista.core.daycountconvention.model;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaObject;

/*
 * Copyright 2018 Olivier Asuncion
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

public class DayCountConvention extends TradistaObject implements Comparable<DayCountConvention> {

	private static final long serialVersionUID = -3809902839449814333L;

	public static final String ACT_365 = "ACT/365";

	public static final String ACT_360 = "ACT/360";

	@Id
	private String name;

	public DayCountConvention(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(DayCountConvention dcc) {
		return name.compareTo(dcc.getName());
	}

}