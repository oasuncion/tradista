package finance.tradista.core.index.ui.controller;

import java.util.Optional;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.common.ui.view.TradistaTextInputDialog;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.index.model.Index.Fixing;
import finance.tradista.core.index.service.IndexBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

public class IndexesController extends TradistaControllerAdapter {

	@FXML
	private TextField name;

	@FXML
	private Label nameLabel;

	@FXML
	private TextArea description;

	@FXML
	private ComboBox<Fixing> fixing;

	private IndexBusinessDelegate indexBusinessDelegate;

	@FXML
	private ComboBox<Index> load;

	private Index index;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		indexBusinessDelegate = new IndexBusinessDelegate();
		TradistaGUIUtil.fillComboBox(indexBusinessDelegate.getAllIndexes(), load);
		fixing.setItems(FXCollections.observableArrayList(Fixing.values()));
		fixing.setValue(Fixing.PREFIXED);
	}

	@FXML
	protected void save() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Save Index");
		confirmation.setHeaderText("Save Index");
		confirmation.setContentText("Do you want to save this Index?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				if (index == null) {
					index = new Index(name.getText());
				}
				if (name.isVisible()) {
					index.setName(name.getText());
					nameLabel.setText(name.getText());
				} else {
					index.setName(nameLabel.getText());
				}
				index.setDescription(description.getText());
				index.setPrefixed(fixing.getValue().equals(Fixing.PREFIXED));
				index.setId(indexBusinessDelegate.saveIndex(index));
				name.setVisible(false);
				nameLabel.setVisible(true);
			} catch (TradistaBusinessException tbe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void copy() {
		long oldIndexId = 0;
		try {
			TradistaTextInputDialog dialog = new TradistaTextInputDialog();
			dialog.setTitle("Index Copy");
			dialog.setHeaderText("Do you want to copy this Index ?");
			dialog.setContentText("Please enter the name of the new Index:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				if (index == null) {
					index = new Index(name.getText());
				}

				index.setName(result.get());
				index.setDescription(description.getText());
				index.setPrefixed(fixing.getValue().equals(Fixing.PREFIXED));
				oldIndexId = index.getId();
				index.setId(0);
				index.setId(indexBusinessDelegate.saveIndex(index));
				name.setVisible(false);
				nameLabel.setVisible(true);
				nameLabel.setText(index.getName());
			}
		} catch (TradistaBusinessException tbe) {
			index.setId(oldIndexId);
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	@FXML
	protected void load() {
		Index index = null;
		String indexName = null;
		try {

			if (load.getValue() != null) {
				indexName = load.getValue().getName();
			} else {
				throw new TradistaBusinessException("Please specify a name.");
			}

			index = indexBusinessDelegate.getIndexByName(indexName);

			if (index == null) {
				throw new TradistaBusinessException(
						String.format("The index %s doesn't exist in the system.", load.getValue().getName()));
			}

			load(index);
		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	private void load(Index index) {
		this.index = index;
		description.setText(index.getDescription());
		fixing.setValue(index.isPrefixed() ? Fixing.PREFIXED : Fixing.POSTFIXED);
		name.setVisible(false);
		nameLabel.setText(index.getName());
		nameLabel.setVisible(true);
	}

	@Override
	@FXML
	public void clear() {
		index = null;
		name.clear();
		description.clear();
		nameLabel.setText("");
		name.setVisible(true);
		nameLabel.setVisible(false);
	}

	@Override
	@FXML
	public void refresh() {
		TradistaGUIUtil.fillComboBox(indexBusinessDelegate.getAllIndexes(), load);
	}

}