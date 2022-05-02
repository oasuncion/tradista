package finance.tradista.core.batch.ui.controller;

import finance.tradista.core.batch.model.TradistaJobInstance;
import finance.tradista.core.batch.service.BatchBusinessDelegate;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class JobInstanceCreatorDialog extends
		TradistaDialog<TradistaJobInstance> {

	public JobInstanceCreatorDialog() {
		super();

		setTitle("Job Instance Creation");

		setHeaderText("Please specify a name and a job type for the Job Instance to create.");
		setResizable(false);
		Label nameLabel = new Label("Name: ");
		Label quoteSetLabel = new Label("Job type: ");
		TextField nameTextField = new TextField();
		ComboBox<String> jobTypeComboBox = new ComboBox<String>();
		ObservableList<String> jobTypes = FXCollections
				.observableArrayList(new BatchBusinessDelegate()
						.getAllJobTypes());
		jobTypeComboBox.setItems(jobTypes);
		jobTypeComboBox.getSelectionModel().selectFirst();
		GridPane grid = new GridPane();
		grid.setStyle("-fx-padding: 20; -fx-hgap: 20; -fx-vgap: 20;");
		grid.add(nameLabel, 1, 1);
		grid.add(nameTextField, 2, 1);
		grid.add(quoteSetLabel, 1, 2);
		grid.add(jobTypeComboBox, 2, 2);
		getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Create", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel",
				ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);
		setResultConverter(new Callback<ButtonType, TradistaJobInstance>() {
			@Override
			public TradistaJobInstance call(ButtonType b) {
				if (b == buttonTypeOk) {
					return new TradistaJobInstance(
							nameTextField.getText(), jobTypeComboBox.getValue());
				}
				return null;
			}
		});
		TradistaGUIUtil.resizeComponents((Stage) getDialogPane().getScene().getWindow(), 0);
	}

}
