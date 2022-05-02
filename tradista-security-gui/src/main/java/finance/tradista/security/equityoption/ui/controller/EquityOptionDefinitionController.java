package finance.tradista.security.equityoption.ui.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.controller.TradistaController;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.common.ui.view.TradistaChoiceDialog;
import finance.tradista.core.daterule.service.DateRuleBusinessDelegate;
import finance.tradista.core.trade.model.OptionTrade;
import finance.tradista.security.common.ui.util.TradistaSecurityGUIUtil;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.service.EquityBusinessDelegate;
import finance.tradista.security.equityoption.model.EquityOption;
import finance.tradista.security.equityoption.model.EquityOptionContractSpecification;
import finance.tradista.security.equityoption.service.EquityOptionBusinessDelegate;
import finance.tradista.security.equityoption.service.EquityOptionContractSpecificationBusinessDelegate;
import finance.tradista.security.equityoption.ui.view.EquityOptionCreatorDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

public class EquityOptionDefinitionController implements TradistaController {

	@FXML
	private TextField code;

	@FXML
	private ComboBox<OptionTrade.Type> type;

	@FXML
	private TextField strike;

	@FXML
	private ComboBox<Equity> equity;

	@FXML
	private ComboBox<EquityOptionContractSpecification> contractSpecification;

	private EquityBusinessDelegate equityBusinessDelegate;

	private EquityOptionBusinessDelegate equityOptionBusinessDelegate;

	private EquityOptionContractSpecificationBusinessDelegate equityOptionContractSpecificationBusinessDelegate;

	@FXML
	private ComboBox<String> loadingCriterion;

	@FXML
	private TextField load;

	private EquityOption equityOption;

	@FXML
	private Label equityOptionId;

	@FXML
	private DatePicker maturityDate;

	@FXML
	private Label productType;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {

		productType.setText("Equity Option");
		loadingCriterion.getItems().add("id");
		loadingCriterion.getItems().add("code");
		loadingCriterion.getSelectionModel().selectFirst();

		equityBusinessDelegate = new EquityBusinessDelegate();
		equityOptionBusinessDelegate = new EquityOptionBusinessDelegate();
		equityOptionContractSpecificationBusinessDelegate = new EquityOptionContractSpecificationBusinessDelegate();

		contractSpecification.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<EquityOptionContractSpecification>() {
					@Override
					public void changed(ObservableValue<? extends EquityOptionContractSpecification> arg0,
							EquityOptionContractSpecification arg1, EquityOptionContractSpecification newEocs) {
						if (newEocs != null) {
							maturityDate.setValue(null);
							LocalDate startDate = LocalDate.now().minusYears(10);
							Set<LocalDate> maturityDates = new DateRuleBusinessDelegate()
									.generateDates(newEocs.getMaturityDatesDateRule(), startDate, Period.ofYears(100));
							final Callback<DatePicker, DateCell> maturityDayCellFactory = new Callback<DatePicker, DateCell>() {
								public DateCell call(final DatePicker datePicker) {
									return new DateCell() {

										private boolean isAvailable(LocalDate date) {
											if (contractSpecification.getValue() != null) {
												return maturityDates.contains(date);
											}
											return true;
										}

										@Override
										public void updateItem(LocalDate item, boolean empty) {
											super.updateItem(item, empty);
											if (!isAvailable(item)) {
												setDisable(true);
											}
										}
									};
								}
							};
							maturityDate.setDayCellFactory(maturityDayCellFactory);
						}
					}
				});

		TradistaGUIUtil.fillOptionTypeComboBox(type);
		TradistaSecurityGUIUtil.fillEquityOptionContractSpecificationComboBox(contractSpecification);
		TradistaGUIUtil.fillComboBox(equityBusinessDelegate.getAllEquities(), equity);
	}

	private void buildProduct() {
		if (equityOption == null) {
			equityOption = new EquityOption();
			equityOption.setCreationDate(LocalDate.now());
		}
		try {
			equityOption.setEquityOptionContractSpecification(contractSpecification.getValue());
			equityOption.setType(type.getValue());
			equityOption.setCode(code.getText());
			equityOption.setUnderlying(equity.getValue());
			if (!strike.getText().isEmpty()) {
				equityOption.setStrike(TradistaGUIUtil.parseAmount(strike.getText(), "Strike"));
			}
			equityOption.setMaturityDate(maturityDate.getValue());
		} catch (TradistaBusinessException tbe) {
			// Should not appear here.
		}
	}

	@FXML
	protected void save() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Save Equity Option");
		confirmation.setHeaderText("Save Equity Option");
		confirmation.setContentText("Do you want to save this Equity Option?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				checkAmounts();

				buildProduct();

				equityOption.setId(equityOptionBusinessDelegate.saveEquityOption(equityOption));
				equityOptionId.setText(String.valueOf(equityOption.getId()));

			} catch (TradistaBusinessException tbe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void copy() {
		EquityOptionCreatorDialog dialog = new EquityOptionCreatorDialog(equityOption);
		Optional<EquityOption> result = dialog.showAndWait();
		long oldEquityOptionId = 0;
		;

		if (result.isPresent()) {
			try {
				buildProduct();
				oldEquityOptionId = equityOption.getId();
				equityOption.setCode(result.get().getCode());
				equityOption.setType(result.get().getType());
				equityOption.setMaturityDate(result.get().getMaturityDate());
				equityOption.setEquityOptionContractSpecification(result.get().getEquityOptionContractSpecification());
				equityOption.setStrike(result.get().getStrike());

				equityOption.setId(0);
				equityOption.setId(equityOptionBusinessDelegate.saveEquityOption(equityOption));
				equityOptionId.setText(String.valueOf(equityOption.getId()));

				code.setText(equityOption.getCode());
				type.setValue(equityOption.getType());
				contractSpecification.setValue(equityOption.getEquityOptionContractSpecification());
				maturityDate.setValue(equityOption.getMaturityDate());
				strike.setText(TradistaGUIUtil.formatAmount(equityOption.getStrike()));

			} catch (TradistaBusinessException tbe) {
				equityOption.setId(oldEquityOptionId);
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void load() {
		Set<EquityOption> equityOptions = null;
		long equityOptionId = 0;
		String equityOptionCode = null;
		try {
			try {
				if (!load.getText().isEmpty()) {
					if (loadingCriterion.getValue().equals("id")) {
						equityOptionId = Long.parseLong(load.getText());
					} else {
						equityOptionCode = load.getText();
					}
				} else {
					throw new TradistaBusinessException("Please specify a product id or code.");
				}
			} catch (NumberFormatException nfe) {
				throw new TradistaBusinessException(String.format("The product id is incorrect: %s", load.getText()));
			}

			if (loadingCriterion.getValue().equals("id")) {
				equityOptions = new HashSet<EquityOption>(1);
				EquityOption equityOption = equityOptionBusinessDelegate.getEquityOptionById(equityOptionId);
				if (equityOption != null) {
					equityOptions.add(equityOption);
				}
			} else {
				equityOptions = equityOptionBusinessDelegate.getEquityOptionsByCode(equityOptionCode);
			}
			if (equityOptions == null || equityOptions.isEmpty()) {
				throw new TradistaBusinessException(
						String.format("The equity option %s doesn't exist in the system.", load.getText()));
			}

			if (equityOptions.size() > 1) {
				TradistaChoiceDialog<EquityOption> dialog = new TradistaChoiceDialog<EquityOption>(
						(EquityOption) equityOptions.toArray()[0], equityOptions);
				dialog.setTitle("Equity Option Selection");
				dialog.setHeaderText("Please choose an Equity Option");
				dialog.setContentText("Selected Equity Option:");

				Optional<EquityOption> result = dialog.showAndWait();
				result.ifPresent(equityOption -> load(equityOption));
			} else {
				load((EquityOption) equityOptions.toArray()[0]);
			}
		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	private void load(EquityOption equityOption) {
		this.equityOption = equityOption;
		equityOptionId.setText(Long.toString(equityOption.getId()));
		equity.setValue(equityOption.getUnderlying());
		contractSpecification.setValue(equityOption.getEquityOptionContractSpecification());
		type.setValue(equityOption.getType());
		code.setText(equityOption.getCode());
		strike.setText(TradistaGUIUtil.formatAmount(equityOption.getStrike()));
		maturityDate.setValue(equityOption.getMaturityDate());
	}

	@Override
	@FXML
	public void clear() {
		equityOption = null;
		equityOptionId.setText("");
		code.clear();
		strike.clear();
		maturityDate.setValue(null);
	}

	@Override
	@FXML
	public void refresh() {
		LocalDate matDate = maturityDate.getValue();
		TradistaGUIUtil.fillComboBox(
				equityOptionContractSpecificationBusinessDelegate.getAllEquityOptionContractSpecifications(),
				contractSpecification);
		TradistaGUIUtil.fillComboBox(equityBusinessDelegate.getAllEquities(), equity);
		maturityDate.setValue(matDate);
	}

	@Override
	public void checkAmounts() throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		try {
			TradistaGUIUtil.checkAmount(strike.getText(), "Strike");
		} catch (TradistaBusinessException abe) {
			errMsg.append(abe.getMessage());
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}