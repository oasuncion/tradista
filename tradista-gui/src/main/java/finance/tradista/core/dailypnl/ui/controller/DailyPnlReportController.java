package finance.tradista.core.dailypnl.ui.controller;

import java.util.Optional;
import java.util.Set;

import finance.tradista.core.calendar.model.BlankCalendar;
import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.calendar.service.CalendarBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.dailypnl.model.DailyPnl;
import finance.tradista.core.dailypnl.service.DailyPnlBusinessDelegate;
import finance.tradista.core.position.model.BlankPositionDefinition;
import finance.tradista.core.position.model.PositionDefinition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/*
 * Copyright 2016 Olivier Asuncion
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

public class DailyPnlReportController extends TradistaControllerAdapter {

	@FXML
	private DatePicker valueDateFromDatePicker;

	@FXML
	private DatePicker valueDateToDatePicker;

	@FXML
	private ComboBox<PositionDefinition> positionDefinitionComboBox;

	@FXML
	private ComboBox<Calendar> calendarComboBox;

	@FXML
	private TableView<DailyPnl> report;

	@FXML
	private TableColumn<DailyPnl, String> date;

	@FXML
	private TableColumn<DailyPnl, String> positionDefinition;

	@FXML
	private TableColumn<DailyPnl, String> calendar;

	@FXML
	private TableColumn<DailyPnl, String> pnl;

	@FXML
	private TableColumn<DailyPnl, String> realizedPnl;

	@FXML
	private TableColumn<DailyPnl, String> unrealizedPnl;

	DailyPnlBusinessDelegate dailyPnlBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {

		dailyPnlBusinessDelegate = new DailyPnlBusinessDelegate();

		date.setCellValueFactory(new Callback<CellDataFeatures<DailyPnl, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DailyPnl, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getValueDate().toString());
			}
		});

		positionDefinition
				.setCellValueFactory(new Callback<CellDataFeatures<DailyPnl, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DailyPnl, String> p) {
						return new ReadOnlyObjectWrapper<String>(p.getValue().getPositionDefinition().getName());
					}
				});

		calendar.setCellValueFactory(new Callback<CellDataFeatures<DailyPnl, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DailyPnl, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getCalendar().getCode());
			}
		});

		pnl.setCellValueFactory(new Callback<CellDataFeatures<DailyPnl, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DailyPnl, String> p) {
				return new ReadOnlyObjectWrapper<String>(TradistaGUIUtil.formatAmount(p.getValue().getPnl()));
			}
		});

		realizedPnl.setCellValueFactory(new Callback<CellDataFeatures<DailyPnl, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DailyPnl, String> p) {
				return new ReadOnlyObjectWrapper<String>(TradistaGUIUtil.formatAmount(p.getValue().getRealizedPnl()));
			}
		});

		unrealizedPnl.setCellValueFactory(new Callback<CellDataFeatures<DailyPnl, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DailyPnl, String> p) {
				return new ReadOnlyObjectWrapper<String>(TradistaGUIUtil.formatAmount(p.getValue().getUnrealizedPnl()));
			}
		});

		TradistaGUIUtil.fillPositionDefinitionComboBox(true, positionDefinitionComboBox);
		TradistaGUIUtil.fillComboBox(new CalendarBusinessDelegate().getAllCalendars(), calendarComboBox);
		calendarComboBox.getItems().add(0, BlankCalendar.getInstance());
		calendarComboBox.getSelectionModel().selectFirst();

	}

	@FXML
	protected void load() {

		if ((calendarComboBox.getValue() == null || calendarComboBox.getValue().equals(BlankCalendar.getInstance()))
				&& (positionDefinitionComboBox.getValue().equals(BlankPositionDefinition.getInstance())
						&& this.valueDateFromDatePicker.getValue() == null
						&& valueDateToDatePicker.getValue() == null)) {
			TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
			confirmation.setTitle("Load Daily Pnls");
			confirmation.setHeaderText("Load Daily Pnls");
			confirmation.setContentText(
					"You are loading all the daily pnls present in the system, it can take time. Are you sure to continue?");
			Optional<ButtonType> result = confirmation.showAndWait();
			if (result.get() == ButtonType.OK) {
				fillReport();
			}
		} else {
			fillReport();
		}

	}

	private void fillReport() {
		ObservableList<DailyPnl> data = null;
		Set<DailyPnl> dailyPnls;

		try {
			String calendarCode = calendarComboBox.getValue() != null ? calendarComboBox.getValue().getCode() : null;
			dailyPnls = dailyPnlBusinessDelegate.getDailyPnlsByDefinitionIdCalendarAndValueDates(
					positionDefinitionComboBox.getValue().equals(BlankPositionDefinition.getInstance()) ? 0
							: positionDefinitionComboBox.getValue().getId(),
					calendarCode, valueDateFromDatePicker.getValue(), valueDateToDatePicker.getValue());

			if (dailyPnls != null) {
				data = FXCollections.observableArrayList(dailyPnls);
			} else {
				data = FXCollections.emptyObservableList();
			}

			report.setItems(data);
			report.refresh();

		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	@FXML
	protected void export() {
		try {
			TradistaGUIUtil.export(report, "DailyPnls", report.getScene().getWindow());
		} catch (TradistaTechnicalException tte) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tte.getMessage());
			alert.showAndWait();
		}
	}

}