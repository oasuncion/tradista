package finance.tradista.security.bond.ui.view;

import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaDialog;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.security.bond.model.Bond;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

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

public class BondCreatorDialog extends TradistaDialog<Bond> {

	public BondCreatorDialog(Exchange exchange) {
		super();
		setTitle("Bond Copy");
		setHeaderText("Please specify ISIN and exchange for the new Bond.");
		Label isinLabel = new Label("ISIN: ");
		TextField isinTextField = new TextField();
		Label exchangeLabel = new Label("Exchange: ");
		ComboBox<Exchange> exchangeComboBox = new ComboBox<Exchange>();
		TradistaGUIUtil.fillExchangeComboBox(exchangeComboBox);
		exchangeComboBox.setValue(exchange);
		GridPane grid = new GridPane();
		grid.setStyle("-fx-padding: 20; -fx-hgap: 20; -fx-vgap: 20;");
		grid.add(isinLabel, 1, 1);
		grid.add(isinTextField, 2, 1);
		grid.add(exchangeLabel, 1, 2);
		grid.add(exchangeComboBox, 2, 2);
		getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Copy", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);
		setResultConverter(new Callback<ButtonType, Bond>() {
			@Override
			public Bond call(ButtonType b) {
				if (b == buttonTypeOk) {
					Bond bond = new Bond(exchangeComboBox.getValue(), isinTextField.getText());
					return bond;
				}
				return null;
			}
		});
		TradistaGUIUtil.resizeComponents((Stage) getDialogPane().getScene().getWindow(), 0);
	}

}