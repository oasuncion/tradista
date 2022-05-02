package finance.tradista.core.batch.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import finance.tradista.core.batch.jobproperty.JobProperty;
import finance.tradista.core.batch.model.TradistaJob;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.service.PositionBusinessDelegate;

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

public class PositionCalculationJob extends TradistaJob {

	@JobProperty(name = "PositionDefinition", type = "PositionDefinition")
	private PositionDefinition positionDefinition;

	@JobProperty(name = "ValueDateTime")
	private String valueDateTime;

	@Override
	public void executeTradistaJob(JobExecutionContext execContext) throws JobExecutionException, TradistaBusinessException {

		if (isInterrupted) {
			performInterruption(execContext);
		}

		new PositionBusinessDelegate().calculatePosition(positionDefinition.getName(),
				LocalDateTime.parse(valueDateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

		if (isInterrupted) {
			performInterruption(execContext);
		}
	}

	@Override
	public String getName() {
		return "PositionCalculation";
	}

	public void setPositionDefinition(PositionDefinition positionDefinition) {
		this.positionDefinition = positionDefinition;
	}

	public void setValueDateTime(String valueDateTime) {
		this.valueDateTime = valueDateTime;
	}

	@Override
	public void checkJobProperties() throws TradistaBusinessException {
		if (positionDefinition == null) {
			throw new TradistaBusinessException("The position definition is mandatory.");
		}
		if (StringUtils.isEmpty(valueDateTime)) {
			throw new TradistaBusinessException("The value date is mandatory.");
		}
		try {
			LocalDateTime.parse(valueDateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		} catch (DateTimeParseException dtpe) {
			throw new TradistaBusinessException(
					String.format("The value date must be a valid date: %s.", dtpe.getMessage()));
		}
	}
}
