package finance.tradista.security.gcrepo.ui.view;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import finance.tradista.core.marketdata.model.BlankQuoteSet;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/*
 * Copyright 2024 Olivier Asuncion
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
public class ProcessingOrgDefaultsCollateralManagementView implements Serializable {

	private static final long serialVersionUID = -2740894244286229939L;

	private SortedSet<QuoteSet> allQuoteSets;

	@PostConstruct
	public void init() {
		QuoteBusinessDelegate quoteBusinessDelegate = new QuoteBusinessDelegate();
		Set<QuoteSet> allQs = quoteBusinessDelegate.getAllQuoteSets();
		allQuoteSets = new TreeSet<>();
		allQuoteSets.add(BlankQuoteSet.getInstance());
		if (allQs != null && !allQs.isEmpty()) {
			allQuoteSets.addAll(allQs);
		}
	}

	public SortedSet<QuoteSet> getAllQuoteSets() {
		return allQuoteSets;
	}

	public void setAllQuoteSets(SortedSet<QuoteSet> quoteSets) {
		this.allQuoteSets = quoteSets;
	}

}