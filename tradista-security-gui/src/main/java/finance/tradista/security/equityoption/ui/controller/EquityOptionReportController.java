package finance.tradista.security.equityoption.ui.controller;

import java.util.Optional;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.trade.model.VanillaOptionTrade;
import finance.tradista.security.equityoption.model.EquityOption;
import finance.tradista.security.equityoption.service.EquityOptionBusinessDelegate;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private TableView<EquityOption> report;

	@FXML
	private TableColumn<EquityOption, String> id;

	@FXML
	private TableColumn<EquityOption, String> code;

	@FXML
	private TableColumn<EquityOption, String> equity;

	@FXML
	private TableColumn<EquityOption, String> quantity;

	@FXML
	private TableColumn<EquityOption, VanillaOptionTrade.Style> style;

	@FXML
	private TableColumn<EquityOption, String> exchange;

	private EquityOptionBusinessDelegate equityOptionBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {

		equityOptionBusinessDelegate = new EquityOptionBusinessDelegate();

		id.setCellValueFactory(new PropertyValueFactory<EquityOption, String>("id"));

		code.setCellValueFactory(new PropertyValueFactory<EquityOption, String>("code"));

		equity.setCellValueFactory(new PropertyValueFactory<EquityOption, String>("underlying"));

		quantity.setCellValueFactory(new Callback<CellDataFeatures<EquityOption, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<EquityOption, String> p) {
				return new ReadOnlyObjectWrapper<String>(TradistaGUIUtil.formatAmount(p.getValue().getQuantity()));
			}
		});

		style.setCellValueFactory(new PropertyValueFactory<EquityOption, VanillaOptionTrade.Style>("style"));

		exchange.setCellValueFactory(new PropertyValueFactory<EquityOption, String>("exchange"));

	}

	@FXML
	protected void load() {
		ObservableList<EquityOption> data = null;
		if (!idTextField.getText().isEmpty()) {
			EquityOption equityOption = equityOptionBusinessDelegate
					.getEquityOptionById(Long.parseLong(idTextField.getText()));
			if (equityOption != null) {
				data = FXCollections.observableArrayList(equityOption);
			}
			report.setItems(data);
			report.refresh();

		} else if (!codeTextField.getText().isEmpty()) {
			try {
				Set<EquityOption> equityOptions = equityOptionBusinessDelegate
						.getEquityOptionsByCode(codeTextField.getText());
				if (equityOptions != null) {
					data = FXCollections.observableArrayList(equityOptions);
				}
				report.setItems(data);
				report.refresh();
			} catch (TradistaBusinessException abe) {
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
		ObservableList<EquityOption> data = null;
		Set<EquityOption> equityOptions;
		try {
			equityOptions = equityOptionBusinessDelegate.getEquityOptionsByCreationDate(
					creationDateFromDatePicker.getValue(), creationDateToDatePicker.getValue());
			if (equityOptions != null) {
				data = FXCollections.observableArrayList(equityOptions);
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
			TradistaGUIUtil.export(report, "EquityOptions", report.getScene().getWindow());
		} catch (TradistaTechnicalException ate) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, ate.getMessage());
			alert.showAndWait();
		}
	}

}