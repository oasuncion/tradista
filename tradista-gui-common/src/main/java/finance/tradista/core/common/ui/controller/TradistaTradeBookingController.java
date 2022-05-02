package finance.tradista.core.common.ui.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.publisher.TradistaPublisher;
import finance.tradista.core.common.ui.subscriber.TradistaSubscriber;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.marketdata.ui.controller.QuoteProperty;
import finance.tradista.core.marketdata.ui.publisher.MarketDataPublisher;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

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

public abstract class TradistaTradeBookingController implements TradistaController, TradistaSubscriber {

	@FXML
	protected TableView<QuoteProperty> quotesTable;

	protected Set<QuoteValue> quoteValues;

	@FXML
	protected Label marketDataMessage;

	@FXML
	protected CheckBox isRealTime;

	@FXML
	protected ComboBox<QuoteSet> selectedQuoteSet;

	@FXML
	protected DatePicker selectedQuoteDate;

	protected MarketDataPublisher publisher;

	protected boolean isPublisherError = false;

	protected boolean isQuoteSetServiceError = false;

	protected boolean isQuoteValueServiceError = false;

	protected boolean quoteSetExists = false;

	protected QuoteBusinessDelegate quoteBusinessDelegate;

	public void initialize() {

		quoteBusinessDelegate = new QuoteBusinessDelegate();

		try {
			publisher = MarketDataPublisher.getInstance();
		} catch (TradistaTechnicalException tte) {
			// TODO log tte
			isPublisherError = true;
		}

		isRealTime.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue,
					Boolean newValue) {
				if (publisher != null) {
					if (newValue) {
						publisher.removeSubscriber(TradistaTradeBookingController.this);
						publisher.addSubscriber(TradistaTradeBookingController.this, selectedQuoteSet.getValue());

					} else {
						publisher.removeSubscriber(TradistaTradeBookingController.this);
					}
				}
			}
		});

		selectedQuoteSet.valueProperty().addListener(new ChangeListener<QuoteSet>() {
			@Override
			public void changed(ObservableValue<? extends QuoteSet> observableValue, QuoteSet oldValue,
					QuoteSet newValue) {
				if (isRealTime.isSelected()) {
					if (publisher != null) {
						if (newValue != null) {
							publisher.removeSubscriber(TradistaTradeBookingController.this);
							publisher.addSubscriber(TradistaTradeBookingController.this, selectedQuoteSet.getValue());

						} else {
							publisher.removeSubscriber(TradistaTradeBookingController.this);
						}
					}
				}
			}
		});

		selectedQuoteDate.setValue(LocalDate.now());

		try {
			TradistaGUIUtil.fillComboBox(quoteBusinessDelegate.getAllQuoteSets(), selectedQuoteSet);
			quoteSetExists = (selectedQuoteSet.getValue() != null);
		} catch (TradistaTechnicalException tte) {
			// TODO log tte
			isQuoteSetServiceError = true;
		}

		if (!quoteSetExists) {
			selectedQuoteSet.setDisable(true);
			selectedQuoteDate.setDisable(true);
			isRealTime.setDisable(true);
			TradistaGUIUtil.applyWarningStyle(marketDataMessage);
			marketDataMessage.setText("There is no quote set, please create one.");
			marketDataMessage.setVisible(true);
		}

		if (isQuoteSetServiceError) {
			selectedQuoteSet.setDisable(true);
			selectedQuoteDate.setDisable(true);
			isRealTime.setDisable(true);
			TradistaGUIUtil.applyErrorStyle(marketDataMessage);
			marketDataMessage.setText("Cannot get quote sets, please contact support.");
			marketDataMessage.setVisible(true);
		}
	}

	@Override
	@FXML
	public void refresh() {
		try {
			TradistaGUIUtil.fillComboBox(quoteBusinessDelegate.getAllQuoteSets(), selectedQuoteSet);
			isQuoteSetServiceError = false;
			quoteSetExists = (selectedQuoteSet.getValue() != null);
		} catch (TradistaTechnicalException tte) {
			// TODO log tte
			isQuoteSetServiceError = true;
			selectedQuoteSet.setItems(null);
		}
		isRealTime.setDisable(selectedQuoteSet.getValue() == null);
		selectedQuoteDate.setDisable(selectedQuoteSet.getValue() == null);
		selectedQuoteSet.setDisable(selectedQuoteSet.getValue() == null);
		if (isRealTime.isDisabled()) {
			isRealTime.setSelected(false);
		}

		if (publisher == null) {
			try {
				publisher = MarketDataPublisher.getInstance();
				isPublisherError = false;
				if (isRealTime.isSelected() && !isRealTime.isDisabled()) {
					publisher.addSubscriber(this, selectedQuoteSet.getValue());
				}
			} catch (TradistaTechnicalException tte) {
				// TODO log tte
				isPublisherError = true;
			}
		}
		if (!quoteSetExists) {
			TradistaGUIUtil.unapplyErrorStyle(marketDataMessage);
			TradistaGUIUtil.applyWarningStyle(marketDataMessage);
			marketDataMessage.setText("There is no quote set, please create one.");
		}
		if (isQuoteSetServiceError) {
			TradistaGUIUtil.unapplyWarningStyle(marketDataMessage);
			TradistaGUIUtil.applyErrorStyle(marketDataMessage);
			if (isPublisherError && isRealTime.isSelected()) {
				marketDataMessage.setText("Cannot get quote sets, cannot get quotes, please contact support.");
			} else {
				marketDataMessage.setText("Cannot get quote sets, please contact support.");
			}
		} else {
			if (isPublisherError && isRealTime.isSelected()) {
				TradistaGUIUtil.unapplyWarningStyle(marketDataMessage);
				TradistaGUIUtil.applyErrorStyle(marketDataMessage);
				marketDataMessage.setText("Cannot get quotes, please contact support.");
			}
		}

		marketDataMessage
				.setVisible(!quoteSetExists || isQuoteSetServiceError || (isPublisherError && isRealTime.isSelected()));
	}

	@Override
	public void update(TradistaPublisher publisher) {
		if (publisher instanceof MarketDataPublisher) {
			isPublisherError = publisher.isError();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isPublisherError) {
						TradistaGUIUtil.unapplyWarningStyle(marketDataMessage);
						TradistaGUIUtil.applyErrorStyle(marketDataMessage);
						marketDataMessage.setText("Cannot get quotes, please contact support.");
					}
					marketDataMessage.setVisible(isPublisherError);
				}
			});
		}
	}

	protected void fillQuotesTable(QuoteSet quoteSet, LocalDate date, String... quoteNames) {
		ObservableList<QuoteProperty> quotes = FXCollections.observableArrayList();
		this.quoteValues.clear();
		try {
			if (quoteSet != null && date != null) {
				for (String quoteName : quoteNames) {
					if (!StringUtils.isEmpty(quoteName)) {
						List<QuoteValue> quoteValues = quoteBusinessDelegate
								.getQuoteValuesByQuoteSetIdQuoteNameAndDate(quoteSet.getId(), quoteName, date);
						if (quoteValues != null) {
							this.quoteValues.addAll(quoteValues);
							quotes.addAll(QuoteProperty.toQuotePropertyList(quoteValues));
						}
					}
				}
			}
		} catch (TradistaTechnicalException tte) {
			isQuoteValueServiceError = true;
		}
		if (isQuoteValueServiceError) {
			TradistaGUIUtil.unapplyWarningStyle(marketDataMessage);
			TradistaGUIUtil.applyErrorStyle(marketDataMessage);
			marketDataMessage.setText("Cannot get quote values, please contact support.");
			marketDataMessage.setVisible(true);
		}
		quotesTable.setItems(quotes);
	}

}