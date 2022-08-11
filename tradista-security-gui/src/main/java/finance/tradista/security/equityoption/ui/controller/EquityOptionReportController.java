package finance.tradista.security.equityoption.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.security.equityoption.model.EquityOption;
import finance.tradista.security.equityoption.service.EquityOptionBusinessDelegate;
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

public class EquityOptionReportController extends TradistaControllerAdapter {

	@FXML
	private DatePicker creationDateFromDatePicker;

	@FXML
	private DatePicker creationDateToDatePicker;

	@FXML
	private TextField idTextField;

	@FXML
	private TextField codeTextField;

	@FXML
	private TableView<EquityOptionProperty> report;

	@FXML
	private TableColumn<EquityOptionProperty, Number> id;

	@FXML
	private TableColumn<EquityOptionProperty, String> code;

	@FXML
	private TableColumn<EquityOptionProperty, String> equity;

	@FXML
	private TableColumn<EquityOptionProperty, String> quantity;

	@FXML
	private TableColumn<EquityOptionProperty, String> style;

	@FXML
	private TableColumn<EquityOptionProperty, String> exchange;

	private EquityOptionBusinessDelegate equityOptionBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {

		equityOptionBusinessDelegate = new EquityOptionBusinessDelegate();

		id.setCellValueFactory(cellData -> cellData.getValue().getId());
		code.setCellValueFactory(cellData -> cellData.getValue().getCode());
		equity.setCellValueFactory(cellData -> cellData.getValue().getEquity());
		quantity.setCellValueFactory(cellData -> cellData.getValue().getQuantity());
		style.setCellValueFactory(cellData -> cellData.getValue().getStyle());
		exchange.setCellValueFactory(cellData -> cellData.getValue().getExchange());
	}

	@FXML
	protected void load() {
		ObservableList<EquityOptionProperty> data = null;
		if (!idTextField.getText().isEmpty()) {
			EquityOption equityOption = equityOptionBusinessDelegate
					.getEquityOptionById(Long.parseLong(idTextField.getText()));
			if (equityOption != null) {
				EquityOptionProperty eop = new EquityOptionProperty(equityOption);
				data = FXCollections.observableArrayList(eop);
			}
			report.setItems(data);
			report.refresh();

		} else if (!codeTextField.getText().isEmpty()) {
			try {
				Set<EquityOption> equityOptions = equityOptionBusinessDelegate
						.getEquityOptionsByCode(codeTextField.getText());
				if (equityOptions != null) {
					if (!equityOptions.isEmpty()) {
						List<EquityOptionProperty> eopList = new ArrayList<>(equityOptions.size());
						for (EquityOption equityOption : equityOptions) {
							eopList.add(new EquityOptionProperty(equityOption));
						}
						data = FXCollections.observableArrayList(eopList);
					}
				}
				report.setItems(data);
				report.refresh();
			} catch (TradistaBusinessException tbe) {
			}
		} else {
			if (creationDateFromDatePicker.getValue() == null && creationDateToDatePicker.getValue() == null) {
				TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
				confirmation.setTitle("Load Equity Options");
				confirmation.setHeaderText("Load Equity Options");
				confirmation.setContentText(
						"You are loading all the equity options present in the system, it can take time. Are you sure to continue?");

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
		ObservableList<EquityOptionProperty> data = null;
		Set<EquityOption> equityOptions;
		try {
			equityOptions = equityOptionBusinessDelegate.getEquityOptionsByCreationDate(
					creationDateFromDatePicker.getValue(), creationDateToDatePicker.getValue());
			if (equityOptions != null) {
				if (!equityOptions.isEmpty()) {
					List<EquityOptionProperty> eopList = new ArrayList<>(equityOptions.size());
					for (EquityOption equityOption : equityOptions) {
						eopList.add(new EquityOptionProperty(equityOption));
					}
					data = FXCollections.observableArrayList(eopList);
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
			TradistaGUIUtil.export(report, "EquityOptions", report.getScene().getWindow());
		} catch (TradistaTechnicalException tte) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tte.getMessage());
			alert.showAndWait();
		}
	}

}