package finance.tradista.core.legalentity.ui.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.legalentity.service.LegalEntityBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/*
 * Copyright 2014 Olivier Asuncion
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

public class LegalEntityReportController extends TradistaControllerAdapter {

	@FXML
	private TextField shortNameTextField;

	@FXML
	private TableView<LegalEntity> report;

	@FXML
	private TableColumn<LegalEntity, String> id;

	@FXML
	private TableColumn<LegalEntity, String> shortName;

	@FXML
	private TableColumn<LegalEntity, String> longName;

	@FXML
	private TableColumn<LegalEntity, String> description;

	@FXML
	private TableColumn<LegalEntity, LegalEntity.Role> role;

	@FXML
	private ComboBox<String> roleComboBox;

	private LegalEntityBusinessDelegate legalEntityBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		legalEntityBusinessDelegate = new LegalEntityBusinessDelegate();
		id.setCellValueFactory(new PropertyValueFactory<LegalEntity, String>("id"));
		shortName.setCellValueFactory(new PropertyValueFactory<LegalEntity, String>("shortName"));
		longName.setCellValueFactory(new PropertyValueFactory<LegalEntity, String>("longName"));
		description.setCellValueFactory(new PropertyValueFactory<LegalEntity, String>("description"));
		role.setCellValueFactory(new PropertyValueFactory<LegalEntity, LegalEntity.Role>("role"));
		TradistaGUIUtil.fillComboBox(
				Arrays.asList(LegalEntity.Role.values()).stream().map(t -> t.toString()).collect(Collectors.toList()),
				roleComboBox);
		roleComboBox.getItems().add(0, StringUtils.EMPTY);
		roleComboBox.getSelectionModel().selectFirst();
	}

	@FXML
	protected void load() {
		if (StringUtils.isEmpty(shortNameTextField.getText()) && roleComboBox.getValue().isEmpty()) {
			TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
			confirmation.setTitle("Load Legal Entities");
			confirmation.setHeaderText("Load Legal Entities");
			confirmation.setContentText(
					"You are loading all the legal entities present in the system, it can take time. Are you sure to continue?");

			Optional<ButtonType> result = confirmation.showAndWait();
			if (result.get() == ButtonType.OK) {
				fillReport();
			}
		} else {
			fillReport();
		}
	}

	private void fillReport() {
		ObservableList<LegalEntity> data = null;
		LegalEntity.Role role = null;
		if (!roleComboBox.getValue().isEmpty()) {
			role = LegalEntity.Role.getRole(roleComboBox.getValue());
		}
		Set<LegalEntity> legalEntities = legalEntityBusinessDelegate
				.getLegalEntitiesByShortNameAndRole("%" + shortNameTextField.getText() + "%", role);

		if (legalEntities != null) {
			data = FXCollections.observableArrayList(legalEntities);
		}
		report.setItems(data);
		report.refresh();
	}

	@FXML
	protected void export() {
		try {
			TradistaGUIUtil.export(report, "LegalEntities", report.getScene().getWindow());
		} catch (TradistaTechnicalException tte) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tte.getMessage());
			alert.showAndWait();
		}
	}

}