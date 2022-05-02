package finance.tradista.core.currency.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import finance.tradista.core.calendar.model.BlankCalendar;
import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.calendar.service.CalendarBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.common.ui.view.TradistaTextInputDialog;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.service.CurrencyBusinessDelegate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

public class CurrenciesController extends TradistaControllerAdapter {

	@FXML
	private TextField isoCode;

	@FXML
	private Label isoCodeLabel;

	@FXML
	private TextField name;

	@FXML
	private TextField fixingDateOffset;

	@FXML
	private CheckBox nonDeliverable;

	@FXML
	private Label fixingDateOffsetLabel;

	private CurrencyBusinessDelegate currencyBusinessDelegate;

	private CalendarBusinessDelegate calendarBusinessDelegate;

	@FXML
	private ComboBox<Calendar> calendar;

	@FXML
	private ComboBox<Currency> load;

	private Currency currency;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		calendarBusinessDelegate = new CalendarBusinessDelegate();
		currencyBusinessDelegate = new CurrencyBusinessDelegate();
		nonDeliverable.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (arg2) {
					fixingDateOffsetLabel.setVisible(true);
					fixingDateOffset.setVisible(true);

				} else {
					fixingDateOffsetLabel.setVisible(false);
					fixingDateOffset.setVisible(false);
				}
			}
		});
		TradistaGUIUtil.fillComboBox(calendarBusinessDelegate.getAllCalendars(), calendar);
		calendar.getItems().add(0, BlankCalendar.getInstance());
		calendar.getSelectionModel().selectFirst();
		TradistaGUIUtil.fillComboBox(currencyBusinessDelegate.getAllCurrencies(), load);
	}

	@Override
	public void checkAmounts() throws TradistaBusinessException {
		if (fixingDateOffset.getText().isEmpty() && nonDeliverable.isSelected()) {
			throw new TradistaBusinessException(
					String.format("The fixing date offset cannot be empty when the currency is non deliverable."));
		}
		if (!fixingDateOffset.getText().isEmpty()) {
			try {
				Integer.parseInt(fixingDateOffset.getText());
			} catch (NumberFormatException nfe) {
				throw new TradistaBusinessException(
						String.format("The fixing date offset (%) is not correct.", fixingDateOffset.getText()));
			}
		}
	}

	@FXML
	protected void save() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Save Currency");
		confirmation.setHeaderText("Save Currency");
		confirmation.setContentText("Do you want to save this Currency?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				checkAmounts();
				if (currency == null) {
					currency = new Currency();
				}
				if (calendar.getValue() != null && !calendar.getValue().equals(BlankCalendar.getInstance())) {
					currency.setCalendar(calendar.getValue());
				}
				if (isoCode.isVisible()) {
					currency.setIsoCode(isoCode.getText());
					isoCodeLabel.setText(isoCode.getText());
				} else {
					currency.setIsoCode(isoCodeLabel.getText());
				}
				currency.setName(name.getText());
				if (nonDeliverable.isSelected()) {
					currency.setNonDeliverable(true);
					currency.setFixingDateOffset(Integer.parseInt(fixingDateOffset.getText()));
				} else {
					currency.setNonDeliverable(false);
				}
				currency.setId(currencyBusinessDelegate.saveCurrency(currency));
				isoCode.setVisible(false);
				isoCodeLabel.setVisible(true);
			} catch (TradistaBusinessException tbe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void copy() {
		long oldCurrencyId = 0;
		try {
			TradistaTextInputDialog dialog = new TradistaTextInputDialog();
			dialog.setTitle("Currency Copy");
			dialog.setHeaderText("Do you want to copy this Currency ?");
			dialog.setContentText("Please enter the ISO code of the new Currency:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				checkAmounts();
				if (currency == null) {
					currency = new Currency();
				}
				if (calendar.getValue() != null && !calendar.getValue().equals(BlankCalendar.getInstance())) {
					currency.setCalendar(calendar.getValue());
				}
				currency.setIsoCode(result.get());
				currency.setName(name.getText());
				if (nonDeliverable.isSelected()) {
					currency.setNonDeliverable(true);
					currency.setFixingDateOffset(Integer.parseInt(fixingDateOffset.getText()));
				} else {
					currency.setNonDeliverable(false);
				}
				oldCurrencyId = currency.getId();
				currency.setId(0);
				currency.setId(currencyBusinessDelegate.saveCurrency(currency));
				isoCode.setVisible(false);
				isoCodeLabel.setVisible(true);
				isoCodeLabel.setText(currency.getIsoCode());
			}
		} catch (TradistaBusinessException tbe) {
			currency.setId(oldCurrencyId);
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	@FXML
	protected void load() {
		Currency currency = null;
		String currencyIsoCode = null;
		try {

			if (load.getValue() != null) {
				currencyIsoCode = load.getValue().getIsoCode();
			} else {
				throw new TradistaBusinessException("Please specify an ISO code.");
			}

			currency = currencyBusinessDelegate.getCurrencyByIsoCode(currencyIsoCode);

			if (currency == null) {
				throw new TradistaBusinessException(
						String.format("The currency %s doesn't exist in the system.", load.getValue().getIsoCode()));
			}

			load(currency);
		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	private void load(Currency currency) {
		this.currency = currency;
		calendar.setValue(currency.getCalendar());
		fixingDateOffset.setText(Integer.toString(currency.getFixingDateOffset()));
		isoCode.setText(currency.getIsoCode());
		name.setText(currency.getName());
		nonDeliverable.setSelected(currency.isNonDeliverable());
		isoCode.setVisible(false);
		isoCodeLabel.setText(currency.getIsoCode());
		isoCodeLabel.setVisible(true);
	}

	@Override
	@FXML
	public void clear() {
		currency = null;
		isoCode.clear();
		isoCodeLabel.setText("");
		isoCode.setVisible(true);
		isoCodeLabel.setVisible(false);
		name.clear();
		fixingDateOffset.setText("-2");
	}

	@Override
	@FXML
	public void refresh() {
		List<Calendar> calendars = new ArrayList<Calendar>();
		calendars.add(BlankCalendar.getInstance());
		calendars.addAll(calendarBusinessDelegate.getAllCalendars());
		TradistaGUIUtil.fillComboBox(calendars, calendar);
		TradistaGUIUtil.fillComboBox(currencyBusinessDelegate.getAllCurrencies(), load);
	}

}