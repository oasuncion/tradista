package finance.tradista.security.equityoption.ui.view;

import java.math.BigDecimal;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.common.ui.view.TradistaDialog;
import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.security.equityoption.model.EquityOptionVolatilitySurface;
import finance.tradista.security.equityoption.ui.controller.EquityOptionVolatilitySurfacesController;
import finance.tradista.security.equityoption.ui.controller.EquityOptionVolatilitySurfacesController.StrikeProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class EquityOptionVolatilitySurfaceCreatorDialog extends TradistaDialog<EquityOptionVolatilitySurface> {

	public EquityOptionVolatilitySurfaceCreatorDialog() {
		super();
		setTitle("Equity Option Volatility Surface Creation");
		setHeaderText(
				"Please specify a name and Strike/Price ratios for the Equity Option Volatility Surface to create.");
		Label nameLabel = new Label("Name: ");
		TextField nameTextField = new TextField();
		Label addDeltaLabel = new Label("Add a Strike/Price ratio: ");
		TextField addStrikePriceRatioTextField = new TextField();
		TableView<StrikeProperty> selectedStrikePriceRatios = new TableView<StrikeProperty>();
		TableColumn<StrikeProperty, String> strikePriceRatioValue = new TableColumn<StrikeProperty, String>();
		selectedStrikePriceRatios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		strikePriceRatioValue.setText("Strike/Price ratio");
		selectedStrikePriceRatios.getColumns().add(strikePriceRatioValue);
		strikePriceRatioValue.setCellValueFactory(new PropertyValueFactory<StrikeProperty, String>("value"));
		GridPane grid = new GridPane();
		grid.setStyle("-fx-padding: 20; -fx-hgap: 20; -fx-vgap: 20;");
		grid.add(nameLabel, 1, 1);
		grid.add(nameTextField, 2, 1);
		grid.add(addDeltaLabel, 1, 2);
		grid.add(addStrikePriceRatioTextField, 2, 2);
		grid.add(selectedStrikePriceRatios, 1, 3);

		GridPane buttonsGrid = new GridPane();
		Button add = new Button("Add");
		Button delete = new Button("Delete");
		buttonsGrid.add(add, 1, 1);
		buttonsGrid.add(delete, 1, 2);
		buttonsGrid.setStyle("-fx-vgap: 20;");
		grid.add(buttonsGrid, 2, 3);
		getDialogPane().setContent(grid);

		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				selectedStrikePriceRatios.getItems()
						.remove(selectedStrikePriceRatios.getSelectionModel().getSelectedItem());
			}
		});

		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					BigDecimal strikePriceRatio;

					strikePriceRatio = TradistaGUIUtil.parseAmount(addStrikePriceRatioTextField.getText(),
							"Strike/Price Ratio");

					boolean strikeExists = false;
					if (selectedStrikePriceRatios.getItems() != null
							&& !selectedStrikePriceRatios.getItems().isEmpty()) {
						for (StrikeProperty prop : selectedStrikePriceRatios.getItems()) {
							if (TradistaGUIUtil.parseAmount(prop.getValue(), "Strike/Price Ratio").compareTo(TradistaGUIUtil
									.parseAmount(addStrikePriceRatioTextField.getText(), "Strike/Price Ratio")) == 0) {
								strikeExists = true;
								break;
							}
						}
					}
					if (!strikeExists) {
						selectedStrikePriceRatios.getItems()
								.add(new StrikeProperty(TradistaGUIUtil.formatAmount(strikePriceRatio)));
					}
				} catch (TradistaBusinessException abe) {
					TradistaAlert alert = new TradistaAlert(AlertType.ERROR, abe.getMessage());
					alert.showAndWait();

				}
			}
		});

		ButtonType buttonTypeOk = new ButtonType("Create", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);
		setResultConverter(new Callback<ButtonType, EquityOptionVolatilitySurface>() {
			@Override
			public EquityOptionVolatilitySurface call(ButtonType b) {
				if (b == buttonTypeOk) {
					try {
						EquityOptionVolatilitySurface surface = new EquityOptionVolatilitySurface(
								nameTextField.getText());
						surface.setProcessingOrg(ClientUtil.getCurrentUser().getProcessingOrg());
						surface.setStrikes(EquityOptionVolatilitySurfacesController
								.toStrikeList(selectedStrikePriceRatios.getItems()));
						return surface;
					} catch (TradistaBusinessException abe) {
						TradistaAlert alert = new TradistaAlert(AlertType.ERROR, abe.getMessage());
						alert.showAndWait();
					}
				}
				return null;
			}
		});
		TradistaGUIUtil.resizeComponents((Stage) getDialogPane().getScene().getWindow(), 0);
	}

}