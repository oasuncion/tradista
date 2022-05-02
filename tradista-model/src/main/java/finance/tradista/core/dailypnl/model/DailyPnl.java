package finance.tradista.core.dailypnl.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.position.model.PositionDefinition;

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

public class DailyPnl extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8562519011584539331L;
	
	private PositionDefinition positionDefinition;
	
	private Calendar calendar;
	
	private LocalDate valueDate;
	
	private BigDecimal pnl;
	
	private BigDecimal realizedPnl;
	
	private BigDecimal unrealizedPnl;

	public PositionDefinition getPositionDefinition() {
		return positionDefinition;
	}

	public void setPositionDefinition(PositionDefinition positionDefinition) {
		this.positionDefinition = positionDefinition;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public LocalDate getValueDate() {
		return valueDate;
	}

	public void setValueDate(LocalDate valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getPnl() {
		return pnl;
	}

	public void setPnl(BigDecimal pnl) {
		this.pnl = pnl;
	}

	public BigDecimal getRealizedPnl() {
		return realizedPnl;
	}

	public void setRealizedPnl(BigDecimal realizedPnl) {
		this.realizedPnl = realizedPnl;
	}

	public BigDecimal getUnrealizedPnl() {
		return unrealizedPnl;
	}

	public void setUnrealizedPnl(BigDecimal unrealizedPnl) {
		this.unrealizedPnl = unrealizedPnl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
		result = prime * result + ((positionDefinition == null) ? 0 : positionDefinition.hashCode());
		result = prime * result + ((valueDate == null) ? 0 : valueDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DailyPnl other = (DailyPnl) obj;
		if (calendar == null) {
			if (other.calendar != null)
				return false;
		} else if (!calendar.equals(other.calendar))
			return false;
		if (positionDefinition == null) {
			if (other.positionDefinition != null)
				return false;
		} else if (!positionDefinition.equals(other.positionDefinition))
			return false;
		if (valueDate == null) {
			if (other.valueDate != null)
				return false;
		} else if (!valueDate.equals(other.valueDate))
			return false;
		return true;
	}		
	
}