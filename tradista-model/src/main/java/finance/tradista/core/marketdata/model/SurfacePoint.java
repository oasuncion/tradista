package finance.tradista.core.marketdata.model;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaObject;

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

public class SurfacePoint<X extends Number, Y extends Number, Z extends Number> extends TradistaObject
		implements Comparable<SurfacePoint<X, Y, Z>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3013306908811949446L;

	@Id
	private X xAxis;

	@Id
	private Y yAxis;

	@Id
	private Z zAxis;

	public SurfacePoint(X x, Y y, Z z) {
		this.xAxis = x;
		this.yAxis = y;
		this.zAxis = z;
	}

	public X getxAxis() {
		return xAxis;
	}

	public Y getyAxis() {
		return yAxis;
	}

	public Z getzAxis() {
		return zAxis;
	}

	@Override
	public int compareTo(SurfacePoint<X, Y, Z> o) {
		return (xAxis.toString() + yAxis.toString() + zAxis.toString())
				.compareTo((o.xAxis.toString() + o.yAxis.toString() + o.zAxis.toString()));
	}

}