package finance.tradista.core.common.util;

import java.util.Properties;

import finance.tradista.core.common.exception.TradistaBusinessException;

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

public final class TradistaProperties {

	private TradistaProperties() {
	}

	private static String marketDataProvider;

	private static String tradistaAppServer;

	private static String tradistaAppPort;

	private static String tradistaAppProtocol;

	public static void load(Properties prop) throws TradistaBusinessException {
		if (prop == null) {
			throw new TradistaBusinessException("The properties cannot be null.");
		}
		marketDataProvider = prop.getProperty("marketDataProvider");
		tradistaAppServer = prop.getProperty("tradistaAppServer");
		tradistaAppPort = prop.getProperty("tradistaAppPort");
		tradistaAppProtocol = prop.getProperty("tradistaAppProtocol");
	}

	public static String getMarketDataProvider() {
		return marketDataProvider;
	}

	public static String getTradistaAppServer() {
		return tradistaAppServer;
	}

	public static String getTradistaAppPort() {
		return tradistaAppPort;
	}

	public static String getTradistaAppProtocol() {
		return tradistaAppProtocol;
	}

}