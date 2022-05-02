package finance.tradista.core.user.ui.controller;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.user.model.User;
import finance.tradista.core.user.service.UserBusinessDelegate;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/*
 * Copyright 2019 Olivier Asuncion
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

public class LoginController extends TradistaControllerAdapter {

	@FXML
	private TextField login;

	@FXML
	private PasswordField password;

	private UserBusinessDelegate userBusinessDelegate;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		userBusinessDelegate = new UserBusinessDelegate();
	}

	@FXML
	protected void login() {
		try {
			User user = userBusinessDelegate.login(login.getText(), password.getText());
			((Stage) login.getScene().getWindow()).close();
		} catch (TradistaBusinessException | TradistaTechnicalException e) {
			login.clear();
			password.clear();
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, e.getMessage(), "Default");
			alert.showAndWait();
		}
	}

	@FXML
	protected void cancel() {
		System.exit(0);
	}

}