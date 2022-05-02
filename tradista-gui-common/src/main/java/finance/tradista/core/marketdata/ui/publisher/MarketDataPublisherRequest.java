package finance.tradista.core.marketdata.ui.publisher;

import java.util.Set;

import finance.tradista.core.common.ui.publisher.PublisherRequest;

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

public class MarketDataPublisherRequest implements PublisherRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5635333331539746621L;

	private String quoteSetName;

	private Set<String> quoteNames;

	public String getQuoteSetName() {
		return quoteSetName;
	}

	public void setQuoteSetName(String quoteSetName) {
		this.quoteSetName = quoteSetName;
	}

	public Set<String> getQuoteNames() {
		return quoteNames;
	}

	public void setQuoteNames(Set<String> quoteNames) {
		this.quoteNames = quoteNames;
	}

}