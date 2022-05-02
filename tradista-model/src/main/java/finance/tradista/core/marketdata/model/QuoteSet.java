package finance.tradista.core.marketdata.model;

import java.time.LocalDate;
import java.util.Map;

import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.legalentity.model.LegalEntity;

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

public class QuoteSet extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8675483478779089653L;

	private Map<Quote, Map<LocalDate, QuoteValue>> quoteValues;

	private String name;

	private LegalEntity processingOrg;

	public QuoteSet(long id, String name, LegalEntity processingOrg) {
		super(id);
		this.name = name;
		this.processingOrg = processingOrg;
	}

	public QuoteSet(String name, LegalEntity processingOrg) {
		super();
		this.name = name;
		this.processingOrg = processingOrg;
	}

	public Map<Quote, Map<LocalDate, QuoteValue>> getQuoteValues() {
		return quoteValues;
	}

	public void setQuoteValues(Map<Quote, Map<LocalDate, QuoteValue>> quoteValues) {
		this.quoteValues = quoteValues;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LegalEntity getProcessingOrg() {
		return processingOrg;
	}

	public void setProcessingOrg(LegalEntity processingOrg) {
		this.processingOrg = processingOrg;
	}

	public QuoteValue getQuoteValueByNameAndDate(String name, LocalDate date) {
		return quoteValues.get(name).get(date);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((processingOrg == null) ? 0 : processingOrg.hashCode());
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
		QuoteSet other = (QuoteSet) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (processingOrg == null) {
			if (other.processingOrg != null)
				return false;
		} else if (!processingOrg.equals(other.processingOrg))
			return false;
		return true;
	}

	public String toString() {
		return name;
	}

}