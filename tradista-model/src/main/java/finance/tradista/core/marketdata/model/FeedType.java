package finance.tradista.core.marketdata.model;

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

public enum FeedType {

	BLOOMBERG_BPIPE, BLOOMBERG_SERVER_API, QUANDL;

	private static final String BLOOMBERG_BPIPE_NAME = "BloombergBPipe";

	private static final String BLOOMBERG_SERVER_API_NAME = "BloombergServerAPI";

	private static final String QUANDL_NAME = "Quandl";

	public String toString() {
		switch (this) {
		case BLOOMBERG_BPIPE:
			return BLOOMBERG_BPIPE_NAME;
		case BLOOMBERG_SERVER_API:
			return BLOOMBERG_SERVER_API_NAME;
		case QUANDL:
			return QUANDL_NAME;
		}
		return super.toString();
	}

	/**
	 * Gets a QuoteType from a display name. Display names are used in GUIs. A
	 * display name of a QuoteType is the result of hits toString() method.
	 * 
	 * @param type
	 * @return
	 */
	public static FeedType getFeedType(String displayName) {
		switch (displayName) {
		case BLOOMBERG_BPIPE_NAME:
			return BLOOMBERG_BPIPE;
		case BLOOMBERG_SERVER_API_NAME:
			return BLOOMBERG_SERVER_API;
		case QUANDL_NAME:
			return QUANDL;
		}
		return null;
	}

}