package finance.tradista.core.currency.model;

import finance.tradista.core.calendar.model.Calendar;
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

	private String isoCode;
	
	private String name;
	
	private boolean nonDeliverable;
	
	private int fixingDateOffset;
	
	private Calendar calendar;

	public Calendar getCalendar() {
		return calendar;
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

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return isoCode;
	}
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof Currency)) {
			return false;
		}
		
		return isoCode.equals(((Currency)o).getIsoCode());
	}
	
	public int hashCode() {
		return isoCode.hashCode();
	}

}