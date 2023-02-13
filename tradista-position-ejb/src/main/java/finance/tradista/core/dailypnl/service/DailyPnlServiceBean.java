package finance.tradista.core.dailypnl.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.calendar.service.CalendarBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.dailypnl.model.DailyPnl;
import finance.tradista.core.dailypnl.persistence.DailyPnlSQL;
import finance.tradista.core.position.model.Position;
import finance.tradista.core.position.model.PositionCalculationError;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.service.PositionCalculationErrorService;
import finance.tradista.core.position.service.PositionDefinitionProductScopeFilteringInterceptor;
import finance.tradista.core.position.service.PositionDefinitionService;
import finance.tradista.core.position.service.PositionService;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

/*
 * Copyright 2016 Olivier Asuncion
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
public class DailyPnlServiceBean implements DailyPnlService {

	@EJB
	private PositionDefinitionService positionDefinitionService;

	@EJB
	private PositionService positionService;

	@EJB
	private PositionCalculationErrorService positionCalculationErrorService;

	@Override
	@Interceptors(PositionDefinitionProductScopeFilteringInterceptor.class)
	public DailyPnl calculateDailyPnl(String positionDefinitionName, String calendarCode, LocalDate valueDate)
			throws TradistaBusinessException {
		PositionDefinition posDef = positionDefinitionService.getPositionDefinitionByName(positionDefinitionName);
		if (posDef == null) {
			throw new TradistaBusinessException(
					String.format("The position definition named '%s' cannot be found.", positionDefinitionName));
		}
		Calendar cal = new CalendarBusinessDelegate().getCalendarByCode(calendarCode);
		if (cal == null) {
			throw new TradistaBusinessException(
					String.format("The calendar with code '%s' cannot be found.", calendarCode));
		}

		// 1. Get the previous business day

		LocalDate previousDay = DateUtil.previousBusinessDay(valueDate, cal);

		Position previousDayPosition = positionService.getLastPositionByDefinitionNameAndValueDate(posDef.getName(),
				previousDay);
		Position position;

		// 2. If the position doesn't exist, we check is there was an unsolved
		// error
		if (previousDayPosition == null) {
			List<PositionCalculationError> errors = positionCalculationErrorService.getPositionCalculationErrors(
					posDef.getId(), PositionCalculationError.Status.UNSOLVED, 0, 0, previousDay, previousDay, null,
					null, null, null);
			if (errors != null && !errors.isEmpty()) {
				throw new TradistaBusinessException(String.format(
						"The position as of %tD must be calculated. The position calculation error must be solved.",
						previousDay));
			} else {
				// There was no error, so we calculate the position
				positionService.calculatePosition(posDef.getName(), LocalDateTime.of(previousDay, LocalTime.MIN));
				previousDayPosition = positionService.getLastPositionByDefinitionNameAndValueDate(posDef.getName(),
						previousDay);
				// 3. If the position still doesn't exist, it means there was an
				// unsolved error to be solved.
				if (previousDayPosition == null) {
					throw new TradistaBusinessException(String.format(
							"The position as of %tD must be calculated. The position calculation error must be solved.",
							previousDay));
				}
			}
		}

		positionService.calculatePosition(posDef.getName(), valueDate.atTime(LocalTime.MAX));

		List<PositionCalculationError> errors = positionCalculationErrorService.getPositionCalculationErrors(
				posDef.getId(), PositionCalculationError.Status.UNSOLVED, 0, 0, valueDate, valueDate, null, null, null,
				null);

		if (errors != null && !errors.isEmpty()) {
			throw new TradistaBusinessException(String.format(
					"The position as of %tD must be calculated. The position calculation error must be solved.",
					valueDate));
		} else {
			position = positionService.getLastPositionByDefinitionNameAndValueDate(posDef.getName(), valueDate);
		}

		// Check if there was already a daily pnl for this position definition,
		// calendar and value date
		// If yes, we update it. Otherwise, we create a new one.

		DailyPnl dailyPnl = getDailyPnlByPositionDefinitionCalendarAndValueDate(posDef, cal, valueDate);

		if (dailyPnl == null) {
			dailyPnl = new DailyPnl(positionDefinitionService.getPositionDefinitionByName(posDef.getName()), cal, valueDate);
		}
		dailyPnl.setPnl(position.getPnl().subtract(previousDayPosition.getPnl()));
		dailyPnl.setRealizedPnl(position.getRealizedPnl().subtract(previousDayPosition.getRealizedPnl()));
		dailyPnl.setUnrealizedPnl(position.getUnrealizedPnl().subtract(previousDayPosition.getUnrealizedPnl()));

		return dailyPnl;

	}

	private DailyPnl getDailyPnlByPositionDefinitionCalendarAndValueDate(PositionDefinition positionDefinition,
			Calendar calendar, LocalDate valueDate) {
		return DailyPnlSQL.getDailyPnlByPositionDefinitionCalendarAndValueDate(positionDefinition, calendar, valueDate);
	}

	@Override
	@Interceptors(DailyPnlProductScopeFilteringInterceptor.class)
	public long saveDailyPnl(DailyPnl dailyPnl) {
		return DailyPnlSQL.saveDailyPnl(dailyPnl);
	}

	@Interceptors(DailyPnlFilteringInterceptor.class)
	@Override
	public Set<DailyPnl> getDailyPnlsByDefinitionIdCalendarAndValueDates(long positionDefinitionId, String calendarCode,
			LocalDate valueDateFrom, LocalDate valueDateTo) throws TradistaBusinessException {
		return DailyPnlSQL.getDailyPnlsByPositionDefinitionCalendarAndValueDate(positionDefinitionId, calendarCode,
				valueDateFrom, valueDateTo);
	}

}