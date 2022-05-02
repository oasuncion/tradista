package finance.tradista.core.marketdata.ui.publisher;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.publisher.AbstractPublisher;
import finance.tradista.core.common.ui.subscriber.TradistaSubscriber;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.MarketDataBusinessDelegate;
import finance.tradista.core.marketdata.service.MarketDataConfigurationBusinessDelegate;

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

public class MarketDataPublisher extends AbstractPublisher {

	private int refreshFrequency;

	private boolean isStopped = true;

	private Timer timer;

	private TimerTask task;

	private MarketDataBusinessDelegate marketDataBusinessDelegate;

	private static MarketDataPublisher instance;

	private Map<TradistaSubscriber, QuoteSet> quoteSets;

	private Set<QuoteValue> quoteValues;

	private MarketDataPublisher() {
		super();
		quoteSets = Collections.synchronizedMap(new HashMap<TradistaSubscriber, QuoteSet>());
		quoteValues = new HashSet<QuoteValue>();
		// Retrieve Market Data refresh frequency
		refreshFrequency = new MarketDataConfigurationBusinessDelegate().getFrequency();
		marketDataBusinessDelegate = new MarketDataBusinessDelegate();
		timer = new Timer(true);
		task = new TimerTask() {
			@Override
			public void run() {
				// EJB call with a list of quotesets
				Set<QuoteSet> quoteSets = getQuoteSets();
				try {
					if (!subscribers.isEmpty()) {
						boolean wasError = isError();
						boolean updated = false;
						List<QuoteValue> quoteValues = marketDataBusinessDelegate.getMarketData(quoteSets);
						setError(false);
						if (quoteValues != null && !quoteValues.isEmpty()) {
							updateQuoteValues(quoteValues);
							updated = true;
						}
						if (wasError || updated) {
							publish();
						}
					}
				} catch (TradistaBusinessException | TradistaTechnicalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					setError(true);
					publish();
				}
			}

		};

		start();
	}

	public void start() {
		if (isStopped) {
			timer.schedule(task, Calendar.getInstance().getTime(), refreshFrequency * 1000);
			isStopped = false;
		}
	}

	private void updateQuoteValues(List<QuoteValue> quoteValues) {
		this.quoteValues = new HashSet<QuoteValue>(quoteValues);

	}

	public void stop() {
		if (!isStopped) {
			timer.cancel();
			isStopped = true;
		}
	}

	private Set<QuoteSet> getQuoteSets() {
		return new HashSet<QuoteSet>(quoteSets.values());
	}

	public Set<QuoteValue> getQuoteValues() {
		return quoteValues;
	}

	/**
	 * Returns the singleton but if initial instantiation failed, try again.
	 * 
	 * @return the MarketDataPublisher singleton
	 */
	public static synchronized MarketDataPublisher getInstance() {
		if (instance == null) {
			instance = new MarketDataPublisher();
		}
		return instance;
	}

	public void addSubscriber(TradistaSubscriber subscriber, QuoteSet quoteSet) {
		super.addSubscriber(subscriber);
		quoteSets.put(subscriber, quoteSet);
	}

}