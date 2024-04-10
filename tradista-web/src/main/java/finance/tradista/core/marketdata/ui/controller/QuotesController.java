package finance.tradista.core.marketdata.ui.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.component.poll.Poll;
import org.springframework.util.CollectionUtils;

import finance.tradista.core.index.model.Index;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.MarketDataConfigurationBusinessDelegate;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.tenor.model.Tenor;
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
public class QuotesController implements Serializable {

	private static final long serialVersionUID = -1925132958947152812L;

	private List<QuoteValue> quoteValues;

	private QuoteSet quoteSet;

	private Set<QuoteSet> allQuoteSets;

	private LocalDate quoteDate;

	private boolean realTime;

	private QuoteBusinessDelegate quoteBusinessDelegate;

	private MarketDataConfigurationBusinessDelegate marketDataConfigurationBusinessDelegate;

	private int frequency;

	private Set<String> quoteNames;

	private Poll poll;

	@PostConstruct
	public void init() {
		quoteBusinessDelegate = new QuoteBusinessDelegate();
		quoteDate = LocalDate.now();
		allQuoteSets = quoteBusinessDelegate.getAllQuoteSets();
		if (allQuoteSets != null) {
			quoteSet = allQuoteSets.stream().findFirst().get();
		}
		marketDataConfigurationBusinessDelegate = new MarketDataConfigurationBusinessDelegate();
	}

	public void setQuoteValues(List<QuoteValue> quoteValues) {
		this.quoteValues = quoteValues;
	}

	public List<QuoteValue> getQuoteValues() {
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
		String method;
		if (realTime) {
			frequency = marketDataConfigurationBusinessDelegate.getFrequency();
		}
		if (realTime) {
			method = "start();";
		} else {
			method = "stop();";
		}
		PrimeFaces.current().executeScript("PF('poll')." + method);
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

	public Set<String> getQuoteNames() {
		return quoteNames;
	}

	public void setQuoteNames(Set<String> quoteNames) {
		this.quoteNames = quoteNames;
	}

	public Poll getPoll() {
		return poll;
	}

	public void setPoll(Poll poll) {
		this.poll = poll;
	}

	public void updateQuoteNames(Index index, Tenor tenor, Set<String> secQuoteNames) {
		if (quoteNames != null) {
			quoteNames.clear();
		}
		if (index != null && tenor != null) {
			if (quoteNames == null) {
				quoteNames = new HashSet<>();
			}
			quoteNames.add(Index.INDEX + "." + index.getName() + "." + tenor);
		}
		if (!CollectionUtils.isEmpty(secQuoteNames)) {
			if (quoteNames == null) {
				quoteNames = new HashSet<>();
			}
			quoteNames.addAll(secQuoteNames);
		}
	}

	public void refresh() {
		if (quoteValues != null) {
			quoteValues.clear();
		}
		if (quoteSet != null && quoteDate != null && quoteNames != null) {
			for (String quoteName : quoteNames) {
				if (!StringUtils.isEmpty(quoteName)) {
					List<QuoteValue> quoteValues = quoteBusinessDelegate
							.getQuoteValuesByQuoteSetIdQuoteNameAndDate(quoteSet.getId(), quoteName, quoteDate);
					if (quoteValues != null) {
						if (this.quoteValues == null) {
							this.quoteValues = new ArrayList<>();
						}
						this.quoteValues.addAll(quoteValues);
					}
				}
			}
		}
	}

	public void clear() {
		quoteValues = null;
	}

}