package finance.tradista.core.marketdata.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.model.TradistaObject;

/*
 * Copyright 2014 Olivier Asuncion
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

public class RatePoint extends TradistaObject implements Comparable<RatePoint>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3013332431720131491L;

	public RatePoint(LocalDate date, BigDecimal rate) {
		this.date = date;
		this.rate = rate;
	}
	
	private BigDecimal rate;
	
	private LocalDate date;

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	public boolean equals(Object o) {	
		
	if (o == null) {
		return false;
	}	
	
	if (!(o instanceof RatePoint)) {
		return false;
	}
	
	if (o == this) {
		return true;
	}
	
	return ((RatePoint)o).getDate().isEqual(date);
	}
	
	public int hashCode() {
		return date.hashCode();
	}

	@Override
	public int compareTo(RatePoint p) {
		return date.compareTo((p).getDate());
	}

}
