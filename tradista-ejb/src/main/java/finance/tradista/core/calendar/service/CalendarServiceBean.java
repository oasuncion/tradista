package finance.tradista.core.calendar.service;

import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.calendar.persistence.CalendarSQL;
import finance.tradista.core.common.exception.TradistaBusinessException;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;

/*
 * Copyright 2019 Olivier Asuncion
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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class CalendarServiceBean implements CalendarService {

	@Override
	public Set<Calendar> getAllCalendars() {
		return CalendarSQL.getAllCalendars();
	}

	@Override
	public Calendar getCalendarById(long id) {
		return CalendarSQL.getCalendarById(id);
	}

	@Override
	public Calendar getCalendarByCode(String code) {
		return CalendarSQL.getCalendarByCode(code);
	}

	@Override
	public long saveCalendar(Calendar calendar) throws TradistaBusinessException {
		if (calendar.getId() == 0) {
			checkCodeExistence(calendar);
			checkNameExistence(calendar);
		} else {
			Calendar oldCalendar = CalendarSQL.getCalendarById(calendar
					.getId());
			if (!oldCalendar.getCode().equals(calendar.getCode())) {
				checkCodeExistence(calendar);
			}
			if (!oldCalendar.getName().equals(calendar.getName())) {
				checkNameExistence(calendar);
			}
		}
		return CalendarSQL.saveCalendar(calendar);
	}

	private void checkCodeExistence(Calendar calendar)
			throws TradistaBusinessException {
		if (getCalendarByCode(calendar.getCode()) != null) {
			throw new TradistaBusinessException(
					String.format(
							"A calendar with the code '%s' already exists in the system.",
							calendar.getCode()));
		}
	}
	
	private void checkNameExistence(Calendar calendar)
			throws TradistaBusinessException {
		if (getCalendarByName(calendar.getName()) != null) {
			throw new TradistaBusinessException(
					String.format(
							"A calendar with the name '%s' already exists in the system.",
							calendar.getName()));
		}
	}

	public Calendar getCalendarByName(String name) {
		return CalendarSQL.getCalendarByName(name);
	}

	@Override
	public long[] saveCalendars(Set<Calendar> calendars) {
		return CalendarSQL.saveCalendars(calendars);
	}

	@Override
	public long[] addHolidays(Set<Calendar> calendars) {
		return CalendarSQL.addHolidays(calendars);
	}

	@Override
	public Set<String> getAllCalendarCodes() {
		return CalendarSQL.getAllCalendarCodes();
	}

}
