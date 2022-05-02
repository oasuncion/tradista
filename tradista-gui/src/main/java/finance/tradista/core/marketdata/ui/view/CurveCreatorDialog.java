package finance.tradista.core.marketdata.ui.view;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaDialog;
import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.marketdata.model.Curve;
import finance.tradista.core.marketdata.model.FXCurve;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.ZeroCouponCurve;
import finance.tradista.core.marketdata.service.CurveBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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

public class CurveCreatorDialog extends TradistaDialog<Curve<LocalDate, BigDecimal>> {

	public CurveCreatorDialog() {
		super();
		setTitle("Curve Creation");
		setHeaderText("Please specify a name and a type for the Curve to create.");
		Label nameLabel = new Label("Name: ");
		TextField nameTextField = new TextField();
		Label typeLabel = new Label("Type: ");
		ComboBox<String> typeComboBox = new ComboBox<String>();
		typeComboBox.setItems(FXCollections.observableArrayList(new CurveBusinessDelegate().getAllCurveTypes()));
		typeComboBox.getSelectionModel().selectFirst();
		GridPane grid = new GridPane();
		grid.setStyle("-fx-padding: 20; -fx-hgap: 20; -fx-vgap: 20;");
		grid.add(nameLabel, 1, 1);
		grid.add(nameTextField, 2, 1);
		grid.add(typeLabel, 1, 2);
		grid.add(typeComboBox, 2, 2);
		getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Create", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);
		setResultConverter(new Callback<ButtonType, Curve<LocalDate, BigDecimal>>() {
			@Override
			public Curve<LocalDate, BigDecimal> call(ButtonType b) {
				if (b == buttonTypeOk) {
					if (typeComboBox.getValue().equals(InterestRateCurve.INTEREST_RATE_CURVE)) {
						return new InterestRateCurve(nameTextField.getText(), ClientUtil.getCurrentUser().getProcessingOrg());
					} else if (typeComboBox.getValue().equals(ZeroCouponCurve.ZERO_COUPON_CURVE)) {
						return new ZeroCouponCurve(nameTextField.getText(), ClientUtil.getCurrentUser().getProcessingOrg());
					} else {
						return new FXCurve(nameTextField.getText(), ClientUtil.getCurrentUser().getProcessingOrg());
					}
				}
				return null;
			}
		});
		TradistaGUIUtil.resizeComponents((Stage) getDialogPane().getScene().getWindow(), 0);
	}

}
