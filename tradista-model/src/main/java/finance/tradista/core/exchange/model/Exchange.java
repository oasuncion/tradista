package finance.tradista.core.exchange.model;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;

/*
 * Copyright 2015 Olivier Asuncion
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
/**
 * Designed to represent both OTC and organized markets.
 * 
 * @author OA
 *
 */
public class Exchange extends TradistaObject implements Comparable<Exchange> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 77518843854472245L;

	private String name;

	@Id
	private String code;

	private Calendar calendar;

	public Exchange(String code) {
		this.code = code;
	}

	// The Exchange class can also represents OTC markets
	private boolean isOtc;

	public Calendar getCalendar() {
		return TradistaModelUtil.clone(calendar);
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public boolean isOtc() {
		return isOtc;
	}

	public void setOtc(boolean isOtc) {
		this.isOtc = isOtc;
	}

	public String toString() {
		return code;
	}

	@Override
	public int compareTo(Exchange exchange) {
		return code.compareTo(exchange.getCode());
	}

	@Override
	public Exchange clone() {
		Exchange exchange = (Exchange) super.clone();
		exchange.calendar = TradistaModelUtil.clone(calendar);
		return exchange;
	}

}