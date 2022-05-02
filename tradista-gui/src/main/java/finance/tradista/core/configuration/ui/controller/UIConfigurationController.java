package finance.tradista.core.configuration.ui.controller;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.configuration.model.UIConfiguration;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/*
 * Copyright 2017 Olivier Asuncion
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

public class UIConfigurationController extends TradistaControllerAdapter {

	@FXML
	private TextField decimalSeparator;

	@FXML
	private TextField groupingSeparator;

	@FXML
	private ComboBox<RoundingMode> roundingMode;

	@FXML
	private ComboBox<Short> decimalDigits;

	@FXML
	private ComboBox<String> styles;

	private ConfigurationBusinessDelegate configurationBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		configurationBusinessDelegate = new ConfigurationBusinessDelegate();
		UIConfiguration uiConfiguration = null;
		try {
			uiConfiguration = configurationBusinessDelegate.getUIConfiguration(ClientUtil.getCurrentUser());
		} catch (TradistaBusinessException tbe) {
		}
		decimalDigits
				.setItems(FXCollections.observableArrayList((short) 1, (short) 2, (short) 3, (short) 4, (short) 5));
		roundingMode.setItems(FXCollections.observableArrayList(RoundingMode.values()));
		roundingMode.getItems().remove(RoundingMode.UNNECESSARY);

		if (uiConfiguration.getDecimalFormat().getDecimalFormatSymbols() != null) {
			decimalSeparator.setText(
					String.valueOf(uiConfiguration.getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator()));
			groupingSeparator.setText(String
					.valueOf(uiConfiguration.getDecimalFormat().getDecimalFormatSymbols().getGroupingSeparator()));
		}
		roundingMode.getSelectionModel().select(uiConfiguration.getDecimalFormat().getRoundingMode());
		decimalDigits.getSelectionModel()
				.select(Short.valueOf((short) uiConfiguration.getDecimalFormat().getMaximumFractionDigits()));
		styles.setItems(FXCollections.observableArrayList(configurationBusinessDelegate.getAllStyles()));
		styles.setValue(uiConfiguration.getStyle());
	}

	@FXML
	protected void save() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Save UI Configuration");
		confirmation.setHeaderText("Save UI Configuration");
		confirmation.setContentText("Do you want to save this UI Configuration?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			DecimalFormat uiDecimalFormat = new DecimalFormat();
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			UIConfiguration uiConfiguration = new UIConfiguration();
			uiConfiguration.setUser(ClientUtil.getCurrentUser());
			if (!StringUtils.isEmpty(decimalSeparator.getText())) {
				dfs.setGroupingSeparator(decimalSeparator.getText().charAt(0));
			} else {
				dfs.setGroupingSeparator(Character.MIN_VALUE);
			}
			if (!StringUtils.isEmpty(groupingSeparator.getText())) {
				dfs.setGroupingSeparator(groupingSeparator.getText().charAt(0));
			} else {
				dfs.setGroupingSeparator(Character.MIN_VALUE);
			}
			uiDecimalFormat.setDecimalFormatSymbols(dfs);
			if (roundingMode.getValue() != null) {
				uiDecimalFormat.setRoundingMode(roundingMode.getValue());
			}
			if (decimalDigits.getValue() != null) {
				uiDecimalFormat.setMaximumFractionDigits(decimalDigits.getValue());
			}
			uiConfiguration.setDecimalFormat(uiDecimalFormat);
			uiConfiguration.setStyle(styles.getValue());
			try {
				configurationBusinessDelegate.saveUIConfiguration(uiConfiguration);
			} catch (TradistaBusinessException tbe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@Override
	@FXML
	public void refresh() {
		UIConfiguration uiConfiguration = null;
		try {
			uiConfiguration = configurationBusinessDelegate.getUIConfiguration(ClientUtil.getCurrentUser());
		} catch (TradistaBusinessException abe) {
		}

		if (uiConfiguration.getDecimalFormat().getDecimalFormatSymbols() != null) {
			decimalSeparator.setText(
					String.valueOf(uiConfiguration.getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator()));
			groupingSeparator.setText(String
					.valueOf(uiConfiguration.getDecimalFormat().getDecimalFormatSymbols().getGroupingSeparator()));
		}
		roundingMode.getSelectionModel().select(uiConfiguration.getDecimalFormat().getRoundingMode());
		decimalDigits.getSelectionModel()
				.select(Short.valueOf((short) uiConfiguration.getDecimalFormat().getMaximumFractionDigits()));
		styles.setValue(uiConfiguration.getStyle());
	}

}