package finance.tradista.core.position.ui.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.error.model.Error.Status;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.position.model.BlankPositionDefinition;
import finance.tradista.core.position.model.PositionCalculationError;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.service.PositionCalculationErrorBusinessDelegate;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
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
import javafx.scene.control.TextField;
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

public class PositionCalculationErrorReportController extends TradistaControllerAdapter {

	@FXML
	private DatePicker valueDateFromDatePicker;

	@FXML
	private DatePicker valueDateToDatePicker;

	@FXML
	private DatePicker errorDateFromDatePicker;

	@FXML
	private DatePicker errorDateToDatePicker;

	@FXML
	private DatePicker solvingDateFromDatePicker;

	@FXML
	private DatePicker solvingDateToDatePicker;

	@FXML
	private ComboBox<PositionDefinition> positionDefinitionComboBox;

	@FXML
	private ComboBox<String> statusComboBox;

	@FXML
	private TextField tradeIdTextField;

	@FXML
	private TextField productIdTextField;

	@FXML
	private TableView<PositionCalculationError> report;

	@FXML
	private TableColumn<PositionCalculationError, String> errorDate;

	@FXML
	private TableColumn<PositionCalculationError, String> valueDate;

	@FXML
	private TableColumn<PositionCalculationError, String> solvingDate;

	@FXML
	private TableColumn<PositionCalculationError, String> book;

	@FXML
	private TableColumn<PositionCalculationError, String> productType;

	@FXML
	private TableColumn<PositionCalculationError, String> productId;

	@FXML
	private TableColumn<PositionCalculationError, String> counterparty;

	@FXML
	private TableColumn<PositionCalculationError, String> tradeId;

	@FXML
	private TableColumn<PositionCalculationError, String> message;

	@FXML
	private TableColumn<PositionCalculationError, String> status;

	private PositionCalculationErrorBusinessDelegate positionCalculationErrorBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {

		positionCalculationErrorBusinessDelegate = new PositionCalculationErrorBusinessDelegate();

		errorDate.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						return new ReadOnlyObjectWrapper<String>(p.getValue().getErrorDate().toString());

					}
				});

		valueDate.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						return new ReadOnlyObjectWrapper<String>(p.getValue().getValueDate().toString());
					}
				});

		solvingDate.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						String solvingDateString = "";
						LocalDateTime solvDate = p.getValue().getSolvingDate();
						if (solvDate != null) {
							solvingDateString = solvDate.toString();
						}
						return new ReadOnlyObjectWrapper<String>(solvingDateString);
					}
				});

		book.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						return new ReadOnlyObjectWrapper<String>(p.getValue().getBook().getName());
					}
				});

		productType.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						Trade<? extends Product> trade = p.getValue().getTrade();
						if (trade != null) {
							return new ReadOnlyObjectWrapper<String>(trade.getProductType());
						} else {
							Product product = p.getValue().getProduct();
							if (product != null) {
								return new ReadOnlyObjectWrapper<String>(product.getProductType());
							}
						}
						return null;
					}
				});

		productId.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						Product product = p.getValue().getPositionDefinition().getProduct();
						if (product != null) {
							return new ReadOnlyObjectWrapper<String>(Long.toString(product.getId()));
						} else {
							product = p.getValue().getProduct();
							if (product != null) {
								return new ReadOnlyObjectWrapper<String>(Long.toString(product.getId()));
							}
						}
						return new ReadOnlyObjectWrapper<String>("");
					}
				});

		counterparty.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						String shortName = "";
						LegalEntity cpty = p.getValue().getPositionDefinition().getCounterparty();
						if (cpty != null) {
							shortName = cpty.getShortName();
						}
						return new ReadOnlyObjectWrapper<String>(shortName);
					}
				});

		tradeId.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						if (p.getValue().getTrade() != null) {
							return new ReadOnlyObjectWrapper<String>(Long.toString(p.getValue().getTrade().getId()));
						} else {
							return new ReadOnlyObjectWrapper<String>("");
						}
					}
				});

		message.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						return new ReadOnlyObjectWrapper<String>(p.getValue().getMessage());
					}
				});

		status.setCellValueFactory(
				new Callback<CellDataFeatures<PositionCalculationError, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<PositionCalculationError, String> p) {
						return new ReadOnlyObjectWrapper<String>(p.getValue().getStatus().toString());
					}
				});

		TradistaGUIUtil.fillPositionDefinitionComboBox(true, positionDefinitionComboBox);
		TradistaGUIUtil.fillErrorStatusComboBox(statusComboBox);

	}

	@FXML
	protected void load() {
		try {
			checkAmounts();

			if (positionDefinitionComboBox.getValue().equals(BlankPositionDefinition.getInstance())
					&& StringUtils.isEmpty(statusComboBox.getValue()) && tradeIdTextField.getText().isEmpty()
					&& tradeIdTextField.getText().isEmpty() && productIdTextField.getText().isEmpty()
					&& productIdTextField.getText().isEmpty() && errorDateFromDatePicker.getValue() == null
					&& errorDateToDatePicker.getValue() == null && valueDateFromDatePicker.getValue() == null
					&& valueDateToDatePicker.getValue() == null && solvingDateFromDatePicker.getValue() == null
					&& solvingDateToDatePicker.getValue() == null) {
				TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
				confirmation.setTitle("Load Position Calculation Errors");
				confirmation.setHeaderText("Load Position Calculation Errors");
				confirmation.setContentText(
						"You are loading all the position calculation errors present in the system, it can take time. Are you sure to continue?");

				Optional<ButtonType> result = confirmation.showAndWait();
				if (result.get() == ButtonType.OK) {
					fillReport();
				}
			} else {
				fillReport();
			}

		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}

	}

	@Override
	public void checkAmounts() throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		try {
			if (!tradeIdTextField.getText().isEmpty()) {
				Long.parseLong(tradeIdTextField.getText());
			}
		} catch (NumberFormatException nfe) {
			errMsg.append(String.format("The trade id is incorrect: %s.%n", tradeIdTextField.getText()));
		}
		try {
			if (!productIdTextField.getText().isEmpty()) {
				Long.parseLong(productIdTextField.getText());
			}
		} catch (NumberFormatException nfe) {
			errMsg.append(String.format("The product id is incorrect: %s.%n", productIdTextField.getText()));
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

	private void fillReport() {
		ObservableList<PositionCalculationError> data = null;
		List<PositionCalculationError> errors;
		try {
			errors = positionCalculationErrorBusinessDelegate.getPositionCalculationErrors(
					positionDefinitionComboBox.getValue().equals(BlankPositionDefinition.getInstance()) ? 0
							: positionDefinitionComboBox.getValue().getId(),
					Status.getStatus(statusComboBox.getValue()),
					tradeIdTextField.getText().isEmpty() ? 0 : Long.parseLong(tradeIdTextField.getText()),
					productIdTextField.getText().isEmpty() ? 0 : Long.parseLong(productIdTextField.getText()),
					errorDateFromDatePicker.getValue(), errorDateToDatePicker.getValue(),
					valueDateFromDatePicker.getValue(), valueDateToDatePicker.getValue(),
					solvingDateFromDatePicker.getValue(), solvingDateToDatePicker.getValue());

			if (errors != null) {
				data = FXCollections.observableArrayList(errors);
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
			TradistaGUIUtil.export(report, "PositionCalculationErrors", report.getScene().getWindow());
		} catch (TradistaTechnicalException tte) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tte.getMessage());
			alert.showAndWait();
		}
	}

}