package finance.tradista.security.equity.ui.controller;

import java.util.ArrayList;
import java.util.List;
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
	private TableView<EquityProperty> report;

	@FXML
	private TableColumn<EquityProperty, Number> tradingSize;

	@FXML
	private TableColumn<EquityProperty, Number> totalIssued;

	@FXML
	private TableColumn<EquityProperty, Boolean> payDividend;

	@FXML
	private TableColumn<EquityProperty, String> dividendCurrency;

	@FXML
	private TableColumn<EquityProperty, String> activeFrom;

	@FXML
	private TableColumn<EquityProperty, String> activeTo;

	@FXML
	private TableColumn<EquityProperty, Number> id;

	@FXML
	private TableColumn<EquityProperty, String> issuer;

	@FXML
	private TableColumn<EquityProperty, String> isin;

	private EquityBusinessDelegate equityBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		equityBusinessDelegate = new EquityBusinessDelegate();

		id.setCellValueFactory(cellData -> cellData.getValue().getId());
		tradingSize.setCellValueFactory(cellData -> cellData.getValue().getTradingSize());
		totalIssued.setCellValueFactory(cellData -> cellData.getValue().getTotalIssued());
		payDividend.setCellValueFactory(cellData -> cellData.getValue().getPayDividend());
		dividendCurrency.setCellValueFactory(cellData -> cellData.getValue().getDividendCurrency());
		activeFrom.setCellValueFactory(cellData -> cellData.getValue().getActiveFrom());
		activeTo.setCellValueFactory(cellData -> cellData.getValue().getActiveTo());
		issuer.setCellValueFactory(cellData -> cellData.getValue().getIssuer());
		isin.setCellValueFactory(cellData -> cellData.getValue().getIsin());
	}

	@FXML
	protected void load() {
		ObservableList<EquityProperty> data = null;
		if (!idTextField.getText().isEmpty()) {
			Equity equity = equityBusinessDelegate.getEquityById(Long.parseLong(idTextField.getText()));
			if (equity != null) {
				EquityProperty ep = new EquityProperty(equity);
				data = FXCollections.observableArrayList(ep);
			}
			report.setItems(data);
			report.refresh();

		} else if (!isinTextField.getText().isEmpty()) {
			Set<Equity> equities = equityBusinessDelegate.getEquitiesByIsin(isinTextField.getText());
			if (equities != null) {
				if (!equities.isEmpty()) {
					List<EquityProperty> epList = new ArrayList<EquityProperty>(equities.size());
					for (Equity equity : equities) {
						epList.add(new EquityProperty(equity));
					}
					data = FXCollections.observableArrayList(epList);
				}
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
		ObservableList<EquityProperty> data = null;
		Set<Equity> equities;
		try {
			equities = equityBusinessDelegate.getEquitiesByDates(creationDateFromDatePicker.getValue(),
					creationDateToDatePicker.getValue(), activeDateFromDatePicker.getValue(),
					activeDateToDatePicker.getValue());
			if (equities != null) {
				if (!equities.isEmpty()) {
					List<EquityProperty> epList = new ArrayList<EquityProperty>(equities.size());
					for (Equity equity : equities) {
						epList.add(new EquityProperty(equity));
					}
					data = FXCollections.observableArrayList(epList);
				}
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
			TradistaGUIUtil.export(report, "Equities", report.getScene().getWindow());
		} catch (TradistaTechnicalException tte) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tte.getMessage());
			alert.showAndWait();
		}
	}

}