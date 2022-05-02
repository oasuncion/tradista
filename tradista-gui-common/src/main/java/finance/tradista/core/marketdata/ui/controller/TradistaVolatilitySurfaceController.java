package finance.tradista.core.marketdata.ui.controller;

import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.marketdata.model.Quote;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/*
 * Copyright 2020 Olivier Asuncion
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

public class TradistaVolatilitySurfaceController extends TradistaControllerAdapter {

	@FXML
	protected ComboBox<QuoteSet> quoteSet;

	@FXML
	protected Label marketDataMessage;

	@FXML
	protected TextField quoteNameTextField;

	@FXML
	protected Button searchButton;

	@FXML
	protected ListView<Quote> selectedQuotesList;

	@FXML
	protected ListView<Quote> quotesList;

	protected QuoteBusinessDelegate quoteBusinessDelegate;

	protected boolean quoteSetExists = false;

	protected boolean isQuoteSetServiceError = false;

	protected boolean isQuoteServiceError = false;

	public void initialize() {
		quoteBusinessDelegate = new QuoteBusinessDelegate();
		try {
			TradistaGUIUtil.fillComboBox(quoteBusinessDelegate.getAllQuoteSets(), quoteSet);
			quoteSetExists = (quoteSet.getValue() != null);
		} catch (TradistaTechnicalException tte) {
			// TODO log tte
			isQuoteSetServiceError = true;
		}

		if (!quoteSetExists) {
			quoteSet.setDisable(true);
			TradistaGUIUtil.applyWarningStyle(marketDataMessage);
			marketDataMessage.setText("There is no quote set, please create one.");
			marketDataMessage.setVisible(true);
		}

		if (isQuoteSetServiceError) {
			quoteSet.setDisable(true);
			TradistaGUIUtil.applyErrorStyle(marketDataMessage);
			marketDataMessage.setText("Cannot get quote sets, please contact support.");
			marketDataMessage.setVisible(true);
		}
	}

	protected void fillQuoteNames(String quoteName) {
		try {
			List<Quote> quotes = quoteBusinessDelegate.getQuotesByName(quoteName);
			if (quotes != null && !quotes.isEmpty() && !selectedQuotesList.getItems().isEmpty()) {
				List<Quote> notAlreadySelectedQuotes = new ArrayList<Quote>();
				for (Quote quote : quotes) {
					if (!selectedQuotesList.getItems().contains(quote)) {
						notAlreadySelectedQuotes.add(quote);
					}
				}
				quotes = notAlreadySelectedQuotes;
			}
			if (quotes != null) {
				quotesList.setItems(FXCollections.observableArrayList(quotes));
			} else {
				quotesList.setItems(FXCollections.emptyObservableList());
			}
		} catch (TradistaTechnicalException tte) {
			isQuoteServiceError = true;
		}
		if (isQuoteServiceError) {
			TradistaGUIUtil.unapplyWarningStyle(marketDataMessage);
			TradistaGUIUtil.applyErrorStyle(marketDataMessage);
			searchButton.setDisable(true);
			quoteNameTextField.setDisable(true);
			if (!isQuoteServiceError) {
				marketDataMessage.setText("Cannot get quote sets, please contact support.");
			} else {
				marketDataMessage.setText("Cannot get quote sets, cannot get quotes, please contact support.");
			}
			marketDataMessage.setVisible(true);
		}

	}

	@Override
	@FXML
	public void refresh() {
		try {
			TradistaGUIUtil.fillComboBox(quoteBusinessDelegate.getAllQuoteSets(), quoteSet);
			quoteSetExists = (quoteSet.getValue() != null);
			isQuoteSetServiceError = false;
		} catch (TradistaTechnicalException tte) {
			// TODO log tte
			isQuoteSetServiceError = true;
		}

		if (!quoteSetExists) {
			TradistaGUIUtil.unapplyErrorStyle(marketDataMessage);
			TradistaGUIUtil.applyWarningStyle(marketDataMessage);
			marketDataMessage.setText("There is no quote set, please create one.");
		}

		if (isQuoteServiceError) {
			try {
				quoteBusinessDelegate.getQuotesByName("Ping");
				isQuoteServiceError = false;
			} catch (TradistaTechnicalException tte) {
			}
		}

		quoteSet.setDisable(!quoteSetExists || isQuoteSetServiceError);
		quoteNameTextField.setDisable(isQuoteServiceError);
		searchButton.setDisable(isQuoteServiceError);

		if (isQuoteSetServiceError) {
			TradistaGUIUtil.unapplyWarningStyle(marketDataMessage);
			TradistaGUIUtil.applyErrorStyle(marketDataMessage);
			if (!isQuoteServiceError) {
				marketDataMessage.setText("Cannot get quote sets, please contact support.");
			} else {
				marketDataMessage.setText("Cannot get quote sets, cannot get quotes, please contact support.");
			}
		}

		marketDataMessage.setVisible(!quoteSetExists || isQuoteSetServiceError || isQuoteServiceError);
	}

}