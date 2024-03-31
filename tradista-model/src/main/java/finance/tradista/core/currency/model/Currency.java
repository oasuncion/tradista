package finance.tradista.core.currency.model;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
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

public class Currency extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5174185101451156191L;

	@Id
	private String isoCode;

	private String name;

	private boolean nonDeliverable;

	private int fixingDateOffset;

	private Calendar calendar;

	public Currency(String isoCode) {
		this.isoCode = isoCode;
	}

	public Calendar getCalendar() {
		return TradistaModelUtil.clone(calendar);
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public boolean isNonDeliverable() {
		return nonDeliverable;
	}

	public void setNonDeliverable(boolean nonDeliverable) {
		this.nonDeliverable = nonDeliverable;
	}

	public int getFixingDateOffset() {
		return fixingDateOffset;
	}

	public void setFixingDateOffset(int fixingDateOffset) {
		this.fixingDateOffset = fixingDateOffset;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return isoCode;
	}

	@Override
	public Currency clone() {
		Currency currency = (Currency) super.clone();
		currency.calendar = TradistaModelUtil.clone(calendar);
		return currency;
	}

}