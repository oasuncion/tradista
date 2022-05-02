package finance.tradista.core.marketdata.model;

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

	private X xAxis;

	private Y yAxis;

	private Z zAxis;

	public SurfacePoint(X x, Y y, Z z) {
		this.xAxis = x;
		this.yAxis = y;
		this.zAxis = z;
	}

	public X getxAxis() {
		return xAxis;
	}

	public void setxAxis(X xAxis) {
		this.xAxis = xAxis;
	}

	public Y getyAxis() {
		return yAxis;
	}

	public void setyAxis(Y yAxis) {
		this.yAxis = yAxis;
	}

	public Z getzAxis() {
		return zAxis;
	}

	public void setzAxis(Z zAxis) {
		this.zAxis = zAxis;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof SurfacePoint)) {
			return false;
		}
		return ((SurfacePoint<?, ?, ?>) o).getxAxis().equals(this.getxAxis())
				&& ((SurfacePoint<?, ?, ?>) o).getyAxis().equals(this.getyAxis());
	}

	public int hashCode() {
		return (getxAxis().toString() + getyAxis().toString()).hashCode();
	}

	@Override
	public int compareTo(SurfacePoint<X, Y, Z> o) {
		return (getxAxis().toString() + getyAxis().toString())
				.compareTo((o.getxAxis().toString() + o.getyAxis().toString()));
	}

	public String toString() {
		return "(X=" + getxAxis().toString() + ",Y=" + getyAxis().toString() + ",Z=" + getzAxis().toString() + ")";
	}
}