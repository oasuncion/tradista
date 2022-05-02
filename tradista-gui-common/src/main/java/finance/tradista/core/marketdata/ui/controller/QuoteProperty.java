package finance.tradista.core.marketdata.ui.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.marketdata.model.Quote;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

public class QuoteProperty {

	private final SimpleStringProperty date;
	private final SimpleStringProperty name;
	private final SimpleStringProperty type;
	private final SimpleStringProperty bid;
	private final SimpleStringProperty ask;
	private final SimpleStringProperty open;
	private final SimpleStringProperty close;
	private final SimpleStringProperty high;
	private final SimpleStringProperty low;
	private final SimpleStringProperty last;
	private final SimpleStringProperty enteredDate;
	private final SimpleStringProperty sourceName;

	public QuoteProperty(String date, String name, String type, String bid, String ask, String open, String close,
			String high, String low, String last, String enteredDate, String sourceName) {
		this.date = new SimpleStringProperty(date);
		this.name = new SimpleStringProperty(name);
		this.type = new SimpleStringProperty(type);
		this.bid = new SimpleStringProperty(bid);
		this.ask = new SimpleStringProperty(ask);
		this.open = new SimpleStringProperty(open);
		this.close = new SimpleStringProperty(close);
		this.high = new SimpleStringProperty(high);
		this.low = new SimpleStringProperty(low);
		this.last = new SimpleStringProperty(last);
		this.enteredDate = new SimpleStringProperty(enteredDate);
		this.sourceName = new SimpleStringProperty(sourceName);
	}

	public static ObservableList<QuoteProperty> buildTableContent(List<QuoteValue> data, Month month, Year year,
			String quoteName, QuoteType quoteType) {

		if (data == null) {
			data = new ArrayList<QuoteValue>();
		}

		// Get the number of days in that month
		int daysInMonth = month.length(year.isLeap());

		for (int i = 1; i <= daysInMonth; i++) {
			LocalDate cal = LocalDate.of(year.getValue(), month, i);
			if (quoteType == null) {
				List<QuoteType> quoteTypes = new QuoteBusinessDelegate().getQuoteTypesByQuoteName(quoteName);
				for (QuoteType type : quoteTypes) {
					QuoteValue quoteValue = new QuoteValue(cal);
					quoteValue.setQuote(new QuoteBusinessDelegate().getQuoteByNameAndType(quoteName, type));
					if (!data.contains(quoteValue)) {
						data.add(quoteValue);
					}
				}
			} else {
				QuoteValue quoteValue = new QuoteValue(cal);
				Quote quote = new QuoteBusinessDelegate().getQuoteByNameAndType(quoteName, quoteType);
				if (quote != null) {
					quoteValue.setQuote(quote);
					if (!data.contains(quoteValue)) {
						data.add(quoteValue);
					}
				}
			}

		}

		Collections.sort(data);

		return FXCollections.observableArrayList(toQuotePropertyList(data));

	}

	public static List<QuoteProperty> toQuotePropertyList(Collection<QuoteValue> data) {
		List<QuoteProperty> quotePropertyList = new ArrayList<QuoteProperty>();
		for (QuoteValue quoteValue : data) {
			quotePropertyList
					.add(new QuoteProperty(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(quoteValue.getDate()),
							quoteValue.getQuote().getName(), quoteValue.getQuote().getType().toString(),
							quoteValue.getBid() == null ? "" : TradistaGUIUtil.formatAmount(quoteValue.getBid()),
							quoteValue.getAsk() == null ? "" : TradistaGUIUtil.formatAmount(quoteValue.getAsk()),
							quoteValue.getOpen() == null ? "" : TradistaGUIUtil.formatAmount(quoteValue.getOpen()),
							quoteValue.getClose() == null ? "" : TradistaGUIUtil.formatAmount(quoteValue.getClose()),
							quoteValue.getHigh() == null ? "" : TradistaGUIUtil.formatAmount(quoteValue.getHigh()),
							quoteValue.getLow() == null ? "" : TradistaGUIUtil.formatAmount(quoteValue.getLow()),
							quoteValue.getLast() == null ? "" : TradistaGUIUtil.formatAmount(quoteValue.getLast()),
							quoteValue.getEnteredDate() == null ? "" : quoteValue.getEnteredDate().toString(),
							quoteValue.getSourceName() == null ? "" : quoteValue.getSourceName()));
		}

		return quotePropertyList;
	}

	public String getDate() {
		return date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public String getBid() {
		return bid.get();
	}

	public void setBid(String bid) {
		this.bid.set(bid);
	}

	public String getAsk() {
		return ask.get();
	}

	public void setAsk(String ask) {
		this.ask.set(ask);
	}

	public String getOpen() {
		return open.get();
	}

	public void setOpen(String open) {
		this.open.set(open);
	}

	public String getClose() {
		return close.get();
	}

	public void setClose(String close) {
		this.close.set(close);
	}

	public String getHigh() {
		return high.get();
	}

	public void setHigh(String high) {
		this.high.set(high);
	}

	public String getLow() {
		return low.get();
	}

	public void setLow(String low) {
		this.low.set(low);
	}

	public String getLast() {
		return last.get();
	}

	public void setLast(String last) {
		this.last.set(last);
	}

	public String getEnteredDate() {
		return enteredDate.get();
	}

	public void setEnteredDate(String enteredDate) {
		this.enteredDate.set(enteredDate);
	}

	public String getSourceName() {
		return sourceName.get();
	}

	public void setSourceName(String sourceName) {
		this.sourceName.set(sourceName);
	}

}
