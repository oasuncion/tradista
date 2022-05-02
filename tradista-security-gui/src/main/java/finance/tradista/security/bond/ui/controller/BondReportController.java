package finance.tradista.security.bond.ui.controller;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.service.BondBusinessDelegate;
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

public class BondReportController extends TradistaControllerAdapter {

	@FXML
	private DatePicker creationDateFromDatePicker;

	@FXML
	private DatePicker creationDateToDatePicker;

	@FXML
	private DatePicker maturityDateFromDatePicker;

	@FXML
	private DatePicker maturityDateToDatePicker;

	@FXML
	private TextField idTextField;

	@FXML
	private TextField isinTextField;

	@FXML
	private TableView<Bond> report;

	@FXML
	private TableColumn<Bond, String> coupon;

	@FXML
	private TableColumn<Bond, String> maturity;

	@FXML
	private TableColumn<Bond, String> principal;

	@FXML
	private TableColumn<Bond, String> creationDate;

	@FXML
	private TableColumn<Bond, String> datedDate;

	@FXML
	private TableColumn<Bond, String> id;

	@FXML
	private TableColumn<Bond, String> issuerId;

	@FXML
	private TableColumn<Bond, String> isin;

	private BondBusinessDelegate bondBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {

		bondBusinessDelegate = new BondBusinessDelegate();

		id.setCellValueFactory(new PropertyValueFactory<Bond, String>("id"));

		coupon.setCellValueFactory(new Callback<CellDataFeatures<Bond, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Bond, String> p) {
				String coupon = StringUtils.EMPTY;
				if (p.getValue().getCoupon() != null) {
					coupon = TradistaGUIUtil.formatAmount(p.getValue().getCoupon());
				}
				return new ReadOnlyObjectWrapper<String>(coupon);
			}
		});

		maturity.setCellValueFactory(new PropertyValueFactory<Bond, String>("maturityDate"));

		principal.setCellValueFactory(new Callback<CellDataFeatures<Bond, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Bond, String> p) {
				return new ReadOnlyObjectWrapper<String>(TradistaGUIUtil.formatAmount(p.getValue().getPrincipal()));
			}
		});

		creationDate.setCellValueFactory(new PropertyValueFactory<Bond, String>("creationDate"));

		datedDate.setCellValueFactory(new PropertyValueFactory<Bond, String>("datedDate"));

		issuerId.setCellValueFactory(new PropertyValueFactory<Bond, String>("issuerId"));

		isin.setCellValueFactory(new PropertyValueFactory<Bond, String>("isin"));

	}

	@FXML
	protected void load() {
		ObservableList<Bond> data = null;
		if (!idTextField.getText().isEmpty()) {
			Bond bond = bondBusinessDelegate.getBondById(Long.parseLong(idTextField.getText()));
			if (bond != null) {
				data = FXCollections.observableArrayList(bond);
			}
			report.setItems(data);
			report.refresh();

		} else if (!isinTextField.getText().isEmpty()) {
			Set<Bond> bonds = bondBusinessDelegate.getBondsByIsin(isinTextField.getText());
			if (bonds != null) {
				data = FXCollections.observableArrayList(bonds);
			}
			report.setItems(data);
			report.refresh();

		} else {
			if (maturityDateFromDatePicker.getValue() == null && maturityDateToDatePicker.getValue() == null
					&& creationDateFromDatePicker.getValue() == null && creationDateToDatePicker.getValue() == null) {
				TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
				confirmation.setTitle("Load Bonds");
				confirmation.setHeaderText("Load Bonds");
				confirmation.setContentText(
						"You are loading all the bonds present in the system, it can take time. Are you sure to continue?");

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
		ObservableList<Bond> data = null;
		Set<Bond> bonds;
		try {
			bonds = bondBusinessDelegate.getBondsByDates(creationDateFromDatePicker.getValue(),
					creationDateToDatePicker.getValue(), maturityDateFromDatePicker.getValue(),
					maturityDateToDatePicker.getValue());
			if (bonds != null) {
				data = FXCollections.observableArrayList(bonds);
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
			TradistaGUIUtil.export(report, "Bonds", report.getScene().getWindow());
		} catch (TradistaTechnicalException ate) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, ate.getMessage());
			alert.showAndWait();
		}
	}
}