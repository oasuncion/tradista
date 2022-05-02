package finance.tradista.core.batch.job;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import finance.tradista.core.batch.jobproperty.JobProperty;
import finance.tradista.core.batch.model.TradistaJob;
import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.calendar.service.CalendarBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.ParserUtil;

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

public class CalendarJob extends TradistaJob {

	@JobProperty(name = "FilePath")
	private String filePath;

	@JobProperty(name = "replaceCalendar")
	private boolean replaceCalendar;

	@JobProperty(name = "fieldSeparator")
	private String fieldSeparator;

	@SuppressWarnings({ "unchecked" })
	@Override
	public void executeTradistaJob(JobExecutionContext execContext)
			throws JobExecutionException, TradistaBusinessException {

		CalendarBusinessDelegate calendarBusinessDelegate;
		Map<String, String> config = null;
		if (fieldSeparator != null && !fieldSeparator.isEmpty()) {
			config = new HashMap<String, String>();
			config.put("fieldSeparator", fieldSeparator);
		}

		if (isInterrupted) {
			performInterruption(execContext);
		}

		// 1. Get the file
		File file = new File(filePath);

		if (isInterrupted) {
			performInterruption(execContext);
		}

		// 2. Parse the file
		List<Calendar> calendars = (List<Calendar>) ParserUtil.parse(file,
				"Calendar", config);

		if (isInterrupted) {
			performInterruption(execContext);
		}

		// 3. Transform the list of Calendars into a Set
		Set<Calendar> calSet = toSet(calendars);

		if (isInterrupted) {
			performInterruption(execContext);
		}

		calendarBusinessDelegate = new CalendarBusinessDelegate();

		// 4. Drop the existing calendar (only if replaceCalendar was selected)
		if (replaceCalendar) {
			calendarBusinessDelegate.saveCalendars(calSet);
			return;
		}

		calendarBusinessDelegate.addHolidays(calSet);

	}

	private Set<Calendar> toSet(List<Calendar> calendars) {
		Set<Calendar> calSet = new HashSet<Calendar>();
		Map<String, Calendar> calMap = new HashMap<String, Calendar>();
		for (Calendar cal : calendars) {
			if (calMap.containsKey(cal.getCode())) {
				Calendar tmpCal = calMap.remove(cal.getCode());
				tmpCal.addHolidays(cal.getHolidays());
				calMap.put(cal.getCode(), tmpCal);
			} else {
				calMap.put(cal.getCode(), cal);
			}
		}

		// Now the map contains all the calendars, we will transform it as a set
		for (Calendar cal : calMap.values()) {
			calSet.add(cal);
		}

		return calSet;
	}

	public void setReplaceCalendar(boolean replaceCalendar) {
		this.replaceCalendar = replaceCalendar;
	}

	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	@Override
	public String getName() {
		return "Calendar";
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void checkJobProperties() throws TradistaBusinessException {
		if (StringUtils.isEmpty(fieldSeparator)) {
			throw new TradistaBusinessException("The field separator is mandatory.");
		}
		if (StringUtils.isEmpty(filePath)) {
			throw new TradistaBusinessException("The file path is mandatory.");
		}
		File file = new File(filePath);
		if (!file.exists()) {
			throw new TradistaBusinessException("The file path must exist.");
		}
	}

}
