package finance.tradista.core.exchange.ui.controller;

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
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.core.exchange.service.ExchangeBusinessDelegate;
import finance.tradista.core.exchange.ui.view.ExchangeCreatorDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

public class ExchangesController extends TradistaControllerAdapter {

	@FXML
	private TextField code;

	@FXML
	private Label codeLabel;

	@FXML
	private TextField name;

	@FXML
	private CheckBox isOtc;

	@FXML
	private ComboBox<Calendar> calendar;

	private ExchangeBusinessDelegate exchangeBusinessDelegate;

	private CalendarBusinessDelegate calendarBusinessDelegate;

	@FXML
	private ComboBox<Exchange> load;

	private Exchange exchange;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		exchangeBusinessDelegate = new ExchangeBusinessDelegate();
		calendarBusinessDelegate = new CalendarBusinessDelegate();
		TradistaGUIUtil.fillComboBox(calendarBusinessDelegate.getAllCalendars(), calendar);
		calendar.getItems().add(0, BlankCalendar.getInstance());
		calendar.getSelectionModel().selectFirst();
		TradistaGUIUtil.fillComboBox(exchangeBusinessDelegate.getAllExchanges(), load);
	}

	@FXML
	protected void save() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Save Exchange");
		confirmation.setHeaderText("Save Exchange");
		confirmation.setContentText("Do you want to save this Exchange?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				if (exchange == null) {
					exchange = new Exchange();
				}
				if (!calendar.getValue().equals(BlankCalendar.getInstance())) {
					exchange.setCalendar(calendar.getValue());
				}
				if (code.isVisible()) {
					exchange.setCode(code.getText());
					codeLabel.setText(code.getText());
				} else {
					exchange.setCode(codeLabel.getText());
				}
				exchange.setName(name.getText());
				exchange.setOtc(isOtc.isSelected());
				exchange.setId(exchangeBusinessDelegate.saveExchange(exchange));
				code.setVisible(false);
				codeLabel.setVisible(true);
			} catch (TradistaBusinessException tbe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void copy() {
		long oldExchangeId = 0;
		try {
			ExchangeCreatorDialog dialog = new ExchangeCreatorDialog();
			Optional<Exchange> result = dialog.showAndWait();
			if (result.isPresent()) {
				if (exchange == null) {
					exchange = new Exchange();
				}
				exchange.setCode(result.get().getCode());
				exchange.setName(result.get().getName());
				if (!calendar.getValue().equals(BlankCalendar.getInstance())) {
					exchange.setCalendar(calendar.getValue());
				}
				exchange.setOtc(isOtc.isSelected());
				oldExchangeId = exchange.getId();
				exchange.setId(0);
				exchange.setId(exchangeBusinessDelegate.saveExchange(exchange));
				code.setVisible(false);
				codeLabel.setVisible(true);
				codeLabel.setText(exchange.getCode());
				name.setText(exchange.getName());
			}
		} catch (TradistaBusinessException tbe) {
			exchange.setId(oldExchangeId);
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	@FXML
	protected void load() {
		Exchange exchange = null;
		String exchangeCode = null;
		try {

			if (load.getValue() != null) {
				exchangeCode = load.getValue().getCode();
			} else {
				throw new TradistaBusinessException("Please specify a short name.");
			}

			exchange = exchangeBusinessDelegate.getExchangeByCode(exchangeCode);

			if (exchange == null) {
				throw new TradistaBusinessException(
						String.format("The exchange %s doesn't exist in the system.", load.getValue().getCode()));
			}

			load(exchange);
		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	private void load(Exchange exchange) {
		this.exchange = exchange;
		calendar.setValue(exchange.getCalendar());
		code.setVisible(false);
		codeLabel.setText(exchange.getCode());
		codeLabel.setVisible(true);
		isOtc.setSelected(exchange.isOtc());
		name.setText(exchange.getName());
	}

	@Override
	@FXML
	public void clear() {
		exchange = null;
		code.clear();
		codeLabel.setText("");
		code.setVisible(true);
		codeLabel.setVisible(false);
		name.clear();
	}

	@Override
	@FXML
	public void refresh() {
		List<Calendar> calendars = new ArrayList<Calendar>();
		calendars.add(BlankCalendar.getInstance());
		calendars.addAll(calendarBusinessDelegate.getAllCalendars());
		TradistaGUIUtil.fillComboBox(calendars, calendar);
		TradistaGUIUtil.fillComboBox(exchangeBusinessDelegate.getAllExchanges(), load);
	}

}