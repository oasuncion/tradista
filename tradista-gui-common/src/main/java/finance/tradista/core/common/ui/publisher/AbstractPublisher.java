package finance.tradista.core.common.ui.publisher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import finance.tradista.core.common.ui.subscriber.TradistaSubscriber;


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
/**
 * Class following the Observer Design Pattern with some adaptations to handle
 * subscriptions to specific data.
 * 
 * @author OA
 *
 */
public abstract class AbstractPublisher implements TradistaPublisher {

	protected Set<TradistaSubscriber> subscribers;
	
	private boolean error;

	protected AbstractPublisher() {
		subscribers = Collections.synchronizedSet(new HashSet<TradistaSubscriber>());
	}

	@Override
	public void publish() {
		if (subscribers != null && !subscribers.isEmpty()) {
			for (TradistaSubscriber sub : subscribers) {
				sub.update(this);
			}
		}
	}

	@Override
	public void addSubscriber(TradistaSubscriber subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void removeSubscriber(TradistaSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	@Override
	public boolean isError() {
		return error;
	}

	protected void setError(boolean error) {
		this.error = error;
	}
	
}