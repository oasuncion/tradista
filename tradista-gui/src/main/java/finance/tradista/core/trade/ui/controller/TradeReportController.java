package finance.tradista.core.trade.ui.controller;

import java.util.List;
import java.util.Optional;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.service.TradeBusinessDelegate;
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

public class TradeReportController extends TradistaControllerAdapter {

	@FXML
	private DatePicker creationDateFromDatePicker;

	@FXML
	private DatePicker creationDateToDatePicker;

	@FXML
	private DatePicker tradeDateFromDatePicker;

	@FXML
	private DatePicker tradeDateToDatePicker;

	@FXML
	private TextField idTextField;

	@FXML
	private TableView<Trade<? extends Product>> report;

	@FXML
	private TableColumn<Trade<? extends Product>, String> tradeDate;

	@FXML
	private TableColumn<Trade<? extends Product>, String> productType;

	@FXML
	private TableColumn<Trade<? extends Product>, String> creationDate;

	@FXML
	private TableColumn<Trade<? extends Product>, String> id;

	@FXML
	private TableColumn<Trade<? extends Product>, String> productId;

	@FXML
	private TableColumn<Trade<? extends Product>, String> counterpartyId;

	private TradeBusinessDelegate tradeBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {

		tradeBusinessDelegate = new TradeBusinessDelegate();

		id.setCellValueFactory(new PropertyValueFactory<Trade<? extends Product>, String>("id"));

		tradeDate.setCellValueFactory(new PropertyValueFactory<Trade<? extends Product>, String>("tradeDate"));

		productId.setCellValueFactory(new PropertyValueFactory<Trade<? extends Product>, String>("productId"));

		productType.setCellValueFactory(new PropertyValueFactory<Trade<? extends Product>, String>("productType"));

		creationDate.setCellValueFactory(new PropertyValueFactory<Trade<? extends Product>, String>("creationDate"));

		counterpartyId.setCellValueFactory(new PropertyValueFactory<Trade<? extends Product>, String>("counterparty"));
	}

	@SuppressWarnings("unchecked")
	@FXML
	protected void load() {
		ObservableList<Trade<? extends Product>> data = null;

		if (!idTextField.getText().isEmpty()) {
			try {
				Trade<? extends Product> trade = tradeBusinessDelegate
						.getTradeById(Long.parseLong(idTextField.getText()));
				if (trade != null) {
					data = FXCollections.observableArrayList(trade);
				}
				report.setItems(data);
				report.refresh();
			} catch (NumberFormatException e) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR,
						String.format("The trade id: %s is incorrect.", idTextField.getText()));
				alert.showAndWait();
			} catch (TradistaBusinessException abe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, abe.getMessage());
				alert.showAndWait();
			}

		} else {

			if (tradeDateFromDatePicker.getValue() == null && tradeDateToDatePicker.getValue() == null
					&& creationDateFromDatePicker.getValue() == null && creationDateToDatePicker.getValue() == null) {
				TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
				confirmation.setTitle("Load Trades");
				confirmation.setHeaderText("Load Trades");
				confirmation.setContentText(
						"You are loading all the trades present in the system, it can take time. Are you sure to continue?");

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
		ObservableList<Trade<? extends Product>> data = null;
		try {
			List<Trade<? extends Product>> trades = tradeBusinessDelegate.getTradesByDates(
					creationDateFromDatePicker.getValue(), creationDateToDatePicker.getValue(),
					tradeDateFromDatePicker.getValue(), tradeDateToDatePicker.getValue());
			if (trades != null) {
				data = FXCollections.observableArrayList(trades);
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
			TradistaGUIUtil.export(report, "Trades", report.getScene().getWindow());
		} catch (TradistaTechnicalException tte) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tte.getMessage());
			alert.showAndWait();
		}
	}

}