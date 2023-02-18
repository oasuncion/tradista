package finance.tradista.core.marketdata.model;

import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
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

public abstract class Curve<X, Y> extends TradistaObject implements MarketData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2297522690673041454L;

	protected Map<X, Y> points;

	@Id
	private String name;

	@Id
	private LegalEntity processingOrg;

	public Curve(String name, LegalEntity po) {
		this.name = name;
		processingOrg = po;
	}

	public String getName() {
		return name;
	}

	public Map<X, Y> getPoints() {
		if (points == null) {
			return null;
		}
		return new HashMap<>(points);
	}

	public void setPoints(Map<X, Y> points) {
		this.points = points;
	}

	public LegalEntity getProcessingOrg() {
		return TradistaModelUtil.clone(processingOrg);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Curve<X, Y> clone() {
		Curve<X, Y> curve = (Curve<X, Y>) super.clone();
		curve.processingOrg = TradistaModelUtil.clone(processingOrg);
		return curve;
	}

}