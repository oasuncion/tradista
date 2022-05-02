package finance.tradista.core.calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
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

public class Calendar extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8619460945695027871L;
	private String code;
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private Set<DayOfWeek> weekEnd;

	private Set<LocalDate> holidays;

	public Set<DayOfWeek> getWeekEnd() {
		return weekEnd;
	}

	public void setWeekEnd(Set<DayOfWeek> weekEnd) {
		this.weekEnd = weekEnd;
	}

	public Set<LocalDate> getHolidays() {
		return holidays;
	}

	public void setHolidays(Set<LocalDate> holidays) {
		this.holidays = holidays;
	}

	public String toString() {
		return code;
	}

	public boolean isBusinessDay(LocalDate date) throws TradistaBusinessException {
		if (date == null) {
			throw new TradistaBusinessException("The date cannot be null");
		}
		// Check if it is a weekend
		if (weekEnd != null) {
			if (weekEnd.contains(date.getDayOfWeek())) {
				return false;
			}
		}
		// Check if it is a holiday
		if (holidays != null) {
			if (holidays.contains(date)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object o) {
		Calendar cal = null;
		if (o == null) {
			return false;
		}
		if (!(o instanceof Calendar)) {
			return false;
		}
		cal = (Calendar) o;
		if (cal == this) {
			return true;
		}
		
		if (cal.getCode() == null) {
			return (code == null);
		}
		
		return cal.getCode().equals(code);
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	public void addHolidays(Set<LocalDate> holidays) {
		if (this.holidays == null) {
			this.holidays = holidays;
		} else {
			this.holidays.addAll(holidays);
		}
	}

}