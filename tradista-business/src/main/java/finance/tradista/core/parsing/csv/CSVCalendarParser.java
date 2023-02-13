package finance.tradista.core.parsing.csv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.csv.CSVRecord;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.parsing.parser.TradistaObjectParser;

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

public class CSVCalendarParser extends TradistaObjectParser<Calendar, CSVRecord> {

	@Override
	public Calendar parse(CSVRecord csvRecord) {
		Calendar calendar = new Calendar(csvRecord.get(2));
		Set<LocalDate> holidays = new HashSet<LocalDate>();
		holidays.add(LocalDate.parse(csvRecord.get(1), DateTimeFormatter.ISO_DATE));
		calendar.setHolidays(holidays);
		return calendar;
	}

}
