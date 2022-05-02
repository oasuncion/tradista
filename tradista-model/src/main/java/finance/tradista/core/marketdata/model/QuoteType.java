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

public enum QuoteType {
	BETA, BOND_PRICE, DELTA, DIVIDEND_YIELD, EQUITY_OPTION_PRICE, EQUITY_PRICE, VOLATILITY, INTEREST_RATE,
	EXCHANGE_RATE, FUTURE_PRICE;

	public String toString() {
		switch (this) {
		case BETA:
			return "Beta";
		case BOND_PRICE:
			return "BondPrice";
		case DELTA:
			return "Delta";
		case DIVIDEND_YIELD:
			return "DividendYield";
		case EQUITY_OPTION_PRICE:
			return "EquityOptionPrice";
		case EQUITY_PRICE:
			return "EquityPrice";
		case VOLATILITY:
			return "Volatility";
		case INTEREST_RATE:
			return "InterestRate";
		case EXCHANGE_RATE:
			return "ExchangeRate";
		case FUTURE_PRICE:
			return "FuturePrice";
		}
		return super.toString();
	}

	/**
	 * Gets a QuoteType from a display name. Display names are used in GUIs. A
	 * display name of a QuoteType is the result of its toString() method.
	 * 
	 * @param type
	 * @return
	 */
	public static QuoteType getQuoteType(String displayName) {
		switch (displayName) {
		case "Beta":
			return BETA;
		case "BondPrice":
			return BOND_PRICE;
		case "Delta":
			return DELTA;
		case "DividendYield":
			return DIVIDEND_YIELD;
		case "EquityOptionPrice":
			return EQUITY_OPTION_PRICE;
		case "EquityPrice":
			return EQUITY_PRICE;
		case "Volatility":
			return VOLATILITY;
		case "InterestRate":
			return INTEREST_RATE;
		case "FuturePrice":
			return FUTURE_PRICE;
		case "ExchangeRate":
			return EXCHANGE_RATE;
		}
		return null;
	}

}
