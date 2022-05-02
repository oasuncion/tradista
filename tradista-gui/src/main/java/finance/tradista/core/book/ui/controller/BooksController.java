package finance.tradista.core.book.ui.controller;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.common.ui.view.TradistaTextInputDialog;
import finance.tradista.core.common.util.ClientUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/*
 * Copyright 2015 Olivier Asuncion
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

public class BooksController extends TradistaControllerAdapter {

	@FXML
	private TextField name;

	@FXML
	private Label nameLabel;

	@FXML
	private TextArea description;

	private BookBusinessDelegate bookBusinessDelegate;

	@FXML
	private ComboBox<Book> load;

	@FXML
	private TradistaBookPieChart bookChartPane;

	private Book book;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		bookBusinessDelegate = new BookBusinessDelegate();
		TradistaGUIUtil.fillBookComboBox(load);
	}

	@FXML
	protected void save() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Save Book");
		confirmation.setHeaderText("Save Book");
		confirmation.setContentText("Do you want to save this Book?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				if (book == null) {
					book = new Book();
				}
				if (name.isVisible()) {
					book.setName(name.getText());
					nameLabel.setText(name.getText());
				} else {
					book.setName(nameLabel.getText());
				}
				book.setDescription(description.getText());
				book.setProcessingOrg(ClientUtil.getCurrentUser().getProcessingOrg());
				book.setId(bookBusinessDelegate.saveBook(book));
				name.setVisible(false);
				nameLabel.setVisible(true);
				bookChartPane.updateBookChart(book);
			} catch (TradistaBusinessException tbe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void copy() {
		long oldBookId = 0;
		try {
			TradistaTextInputDialog dialog = new TradistaTextInputDialog();
			dialog.setTitle("Book Copy");
			dialog.setHeaderText("Do you want to copy this Book ?");
			dialog.setContentText("Please enter the name of the new Book:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				if (book == null) {
					book = new Book();
				}
				book.setName(result.get());
				book.setDescription(description.getText());
				book.setProcessingOrg(ClientUtil.getCurrentUser().getProcessingOrg());
				book.setId(0);
				book.setId(bookBusinessDelegate.saveBook(book));
				name.setVisible(false);
				nameLabel.setVisible(true);
				nameLabel.setText(book.getName());
				bookChartPane.updateBookChart(book);
			}
		} catch (TradistaBusinessException tbe) {
			book.setId(oldBookId);
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	@FXML
	protected void load() {
		Book book = null;
		String bookName = null;
		try {

			if (load.getValue() != null) {
				bookName = load.getValue().getName();
			} else {
				throw new TradistaBusinessException("Please specify a name.");
			}

			book = bookBusinessDelegate.getBookByName(bookName);

			if (book == null) {
				throw new TradistaBusinessException(
						String.format("The book %s doesn't exist in the system.", load.getValue().getName()));
			}

			load(book);
		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	private void load(Book book) {
		this.book = book;
		description.setText(book.getDescription());
		name.setVisible(false);
		nameLabel.setText(book.getName());
		nameLabel.setVisible(true);
		bookChartPane.updateBookChart(book);
	}

	@Override
	@FXML
	public void clear() {
		book = null;
		name.clear();
		description.clear();
		nameLabel.setText(StringUtils.EMPTY);
		name.setVisible(true);
		nameLabel.setVisible(false);
		bookChartPane.clear();
	}

	@Override
	@FXML
	public void refresh() {
		TradistaGUIUtil.fillBookComboBox(load);
	}

}