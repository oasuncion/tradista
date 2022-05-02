package finance.tradista.security.equity.ui.controller;

import java.util.Optional;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.service.EquityBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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

public class EquityReportController extends TradistaControllerAdapter {

	@FXML
	private DatePicker creationDateFromDatePicker;

	@FXML
	private DatePicker creationDateToDatePicker;

	@FXML
	private DatePicker activeDateFromDatePicker;

	@FXML
	private DatePicker activeDateToDatePicker;

	@FXML
	private TextField idTextField;

	@FXML
	private TextField isinTextField;

	@FXML
	private TableView<Equity> report;

	@FXML
	private TableColumn<Equity, String> tradingSize;

	@FXML
	private TableColumn<Equity, String> totalIssued;

	@FXML
	private TableColumn<Equity, String> payDividend;

	@FXML
	private TableColumn<Equity, String> dividendCurrency;

	@FXML
	private TableColumn<Equity, String> activeFrom;

	@FXML
	private TableColumn<Equity, String> activeTo;

	@FXML
	private TableColumn<Equity, String> id;

	@FXML
	private TableColumn<Equity, String> issuerId;

	@FXML
	private TableColumn<Equity, String> isin;

	private EquityBusinessDelegate equityBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		equityBusinessDelegate = new EquityBusinessDelegate();
		id.setCellValueFactory(new PropertyValueFactory<Equity, String>("id"));

		tradingSize.setCellValueFactory(new PropertyValueFactory<Equity, String>("tradingSize"));

		totalIssued.setCellValueFactory(new PropertyValueFactory<Equity, String>("totalIssued"));

		payDividend.setCellValueFactory(new PropertyValueFactory<Equity, String>("payDividend"));

		dividendCurrency.setCellValueFactory(new PropertyValueFactory<Equity, String>("dividendCurrency"));

		activeFrom.setCellValueFactory(new PropertyValueFactory<Equity, String>("activeFrom"));

		activeTo.setCellValueFactory(new PropertyValueFactory<Equity, String>("activeTo"));

		issuerId.setCellValueFactory(new PropertyValueFactory<Equity, String>("issuerId"));

		isin.setCellValueFactory(new PropertyValueFactory<Equity, String>("isin"));
	}

	@FXML
	protected void load() {
		ObservableList<Equity> data = null;
		if (!idTextField.getText().isEmpty()) {
			Equity equity = equityBusinessDelegate.getEquityById(Long.parseLong(idTextField.getText()));
			if (equity != null) {
				data = FXCollections.observableArrayList(equity);
			}
			report.setItems(data);
			report.refresh();

		} else if (!isinTextField.getText().isEmpty()) {
			Set<Equity> equities = equityBusinessDelegate.getEquitiesByIsin(isinTextField.getText());
			if (equities != null) {
				data = FXCollections.observableArrayList(equities);
			}
			report.setItems(data);
			report.refresh();

		} else {
			if (activeDateFromDatePicker.getValue() == null && activeDateToDatePicker.getValue() == null
					&& creationDateFromDatePicker.getValue() == null && creationDateToDatePicker.getValue() == null) {
				TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
				confirmation.setTitle("Load Equities");
				confirmation.setHeaderText("Load Equities");
				confirmation.setContentText(
						"You are loading all the equities present in the system, it can take time. Are you sure to continue?");

				Optional<ButtonType> result = confirmation.showAndWait();
				if (result.get() == ButtonType.OK) {
					fillReport();
				}

			} else {
				fillReport();
			}
		}
	}

	private void fillReport() {
		ObservableList<Equity> data = null;
		Set<Equity> equities;
		try {
			equities = equityBusinessDelegate.getEquitiesByDates(creationDateFromDatePicker.getValue(),
					creationDateToDatePicker.getValue(), activeDateFromDatePicker.getValue(),
					activeDateToDatePicker.getValue());
			if (equities != null) {
				data = FXCollections.observableArrayList(equities);
			}
			report.setItems(data);
			report.refresh();
		} catch (TradistaBusinessException abe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, abe.getMessage());
			alert.showAndWait();
		}
	}
	
	@FXML
	protected void export() {
		try {
		TradistaGUIUtil.export(report, "Equities", report.getScene().getWindow());
		} catch (TradistaTechnicalException ate) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, ate.getMessage());
			alert.showAndWait();
		}
	}

}