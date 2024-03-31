package finance.tradista.core.marketdata.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
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

public class QuoteSet extends TradistaObject implements Comparable<QuoteSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8675483478779089653L;

	private Map<Quote, Map<LocalDate, QuoteValue>> quoteValues;

	@Id
	private String name;

	@Id
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

	@SuppressWarnings("unchecked")
	public Map<Quote, Map<LocalDate, QuoteValue>> getQuoteValues() {
		if (quoteValues == null) {
			return null;
		}
		Map<Quote, Map<LocalDate, QuoteValue>> quoteValues = new HashMap<Quote, Map<LocalDate, QuoteValue>>();
		for (Map.Entry<Quote, Map<LocalDate, QuoteValue>> entry : this.quoteValues.entrySet()) {
			if (entry.getValue() != null) {
				quoteValues.put(TradistaModelUtil.clone(entry.getKey()),
						(Map<LocalDate, QuoteValue>) TradistaModelUtil.deepCopy(entry.getValue()));
			}
		}
		return quoteValues;
	}

	public void setQuoteValues(Map<Quote, Map<LocalDate, QuoteValue>> quoteValues) {
		this.quoteValues = quoteValues;
	}

	public String getName() {
		return name;
	}

	public LegalEntity getProcessingOrg() {
		return TradistaModelUtil.clone(processingOrg);
	}

	public QuoteValue getQuoteValueByQuoteAndDate(Quote quote, LocalDate date) {
		return quoteValues.get(quote).get(date);
	}

	public String toString() {
		return name;
	}

	@Override
	public int compareTo(QuoteSet qs) {
		return name.compareTo(qs.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public QuoteSet clone() {
		QuoteSet quoteSet = (QuoteSet) super.clone();
		if (this.quoteValues != null) {
			Map<Quote, Map<LocalDate, QuoteValue>> quoteValues = new HashMap<Quote, Map<LocalDate, QuoteValue>>();
			for (Map.Entry<Quote, Map<LocalDate, QuoteValue>> entry : this.quoteValues.entrySet()) {
				if (entry.getValue() != null) {
					quoteValues.put(TradistaModelUtil.clone(entry.getKey()),
							(Map<LocalDate, QuoteValue>) TradistaModelUtil.deepCopy(entry.getValue()));
				}
			}
			quoteSet.quoteValues = quoteValues;
		}
		quoteSet.processingOrg = TradistaModelUtil.clone(processingOrg);
		return quoteSet;
	}

}