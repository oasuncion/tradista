package finance.tradista.core.batch.job;

import java.time.LocalDate;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import finance.tradista.core.batch.jobproperty.JobProperty;
import finance.tradista.core.batch.model.TradistaJob;
import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.dailypnl.model.DailyPnl;
import finance.tradista.core.dailypnl.service.DailyPnlBusinessDelegate;
import finance.tradista.core.position.model.PositionDefinition;

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

public class DailyPnlCalculationJob extends TradistaJob {

	@JobProperty(name = "PositionDefinition", type = "PositionDefinition")
	private PositionDefinition positionDefinition;

	@JobProperty(name = "ValueDate", type = "Date")
	private LocalDate valueDate;

	@JobProperty(name = "Calendar", type = "Calendar")
	private Calendar calendar;

	@Override
	public void executeTradistaJob(JobExecutionContext execContext) throws JobExecutionException, TradistaBusinessException {

		if (isInterrupted) {
			performInterruption(execContext);
		}

		DailyPnlBusinessDelegate dailyPnlBusinessDelegate = new DailyPnlBusinessDelegate();

		DailyPnl dailyPnl = dailyPnlBusinessDelegate.calculateDailyPnl(positionDefinition.getName(), calendar.getCode(),
				valueDate);

		dailyPnlBusinessDelegate.saveDailyPnl(dailyPnl);

		if (isInterrupted) {
			performInterruption(execContext);
		}
	}

	@Override
	public String getName() {
		return "DailyPnlCalculation";
	}

	public void setPositionDefinition(PositionDefinition positionDefinition) {
		this.positionDefinition = positionDefinition;
	}

	public void setValueDate(LocalDate valueDate) {
		this.valueDate = valueDate;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	@Override
	public void checkJobProperties() throws TradistaBusinessException {
		if (positionDefinition == null) {
			throw new TradistaBusinessException("The position definition is mandatory.");
		}
		if (valueDate == null) {
			throw new TradistaBusinessException("The value date is mandatory.");
		}
		if (calendar == null) {
			throw new TradistaBusinessException("The calendar is mandatory.");
		}
	}
}