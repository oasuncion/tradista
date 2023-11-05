package finance.tradista.core.marketdata.ui;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.MarketDataConfigurationBusinessDelegate;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/*
 * Copyright 2023 Olivier Asuncion
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

@Named
@ViewScoped
public class QuotesView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set<QuoteValue> quoteValues;

	private QuoteSet quoteSet;

	private Set<QuoteSet> allQuoteSets;

	private LocalDate quoteDate;

	private boolean realTime;

	private QuoteBusinessDelegate quoteBusinessDelegate;

	private MarketDataConfigurationBusinessDelegate marketDataConfigurationBusinessDelegate;

	private int frequency;

	@PostConstruct
	public void init() {
		quoteBusinessDelegate = new QuoteBusinessDelegate();
		quoteDate = LocalDate.now();
		allQuoteSets = quoteBusinessDelegate.getAllQuoteSets();
		quoteValues = Collections.synchronizedSet(new HashSet<QuoteValue>());
		if (allQuoteSets != null) {
			quoteSet = allQuoteSets.stream().findFirst().get();
		}
		marketDataConfigurationBusinessDelegate = new MarketDataConfigurationBusinessDelegate();
	}

	public void setQuoteValues(Set<QuoteValue> quoteValues) {
		this.quoteValues = quoteValues;
	}

	public Set<QuoteValue> getQuoteValues() {
		return quoteValues;
	}

	public QuoteSet getQuoteSet() {
		return quoteSet;
	}

	public void setQuoteSet(QuoteSet quoteSet) {
		this.quoteSet = quoteSet;
	}

	public LocalDate getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(LocalDate quoteDate) {
		this.quoteDate = quoteDate;
	}

	public boolean getRealTime() {
		return realTime;
	}

	public void setRealTime(boolean realTime) {
		this.realTime = realTime;
		if (realTime) {
			frequency = marketDataConfigurationBusinessDelegate.getFrequency();
		}
	}

	public Set<QuoteSet> getAllQuoteSets() {
		return allQuoteSets;
	}

	public void setAllQuoteSets(Set<QuoteSet> allQuoteSets) {
		this.allQuoteSets = allQuoteSets;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void refresh(LocalDate quoteDate, QuoteSet quoteSet, String... quoteNames) {
		quoteValues.clear();
		if (quoteSet != null && quoteDate != null && quoteNames != null) {
			for (String quoteName : quoteNames) {
				if (!StringUtils.isEmpty(quoteName)) {
					List<QuoteValue> quoteValues = quoteBusinessDelegate
							.getQuoteValuesByQuoteSetIdQuoteNameAndDate(quoteSet.getId(), quoteName, quoteDate);
					if (quoteValues != null) {
						this.quoteValues.addAll(quoteValues);
					}
				}
			}
		}
	}

}