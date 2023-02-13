package finance.tradista.core.marketdata.model;

import finance.tradista.core.common.model.Id;
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

public class Quote extends TradistaObject implements MarketData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868004835569574694L;

	@Id
	private String name;

	@Id
	private QuoteType type;

	public Quote(long id, String name, QuoteType type) {
		super(id);
		this.name = name;
		this.type = type;
	}

	public Quote(String name, QuoteType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public QuoteType getType() {
		return type;
	}

	public String toString() {
		return this.getName();
	}

}