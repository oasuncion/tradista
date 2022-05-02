package finance.tradista.core.common.ui.view;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
 * Copyright 2018 Olivier Asuncion
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

public class TradistaAlert extends Alert {

	public void init(String style) {
		initStyle(StageStyle.UNDECORATED);
		TradistaGUIUtil.setTradistaIcons((Stage) getDialogPane().getScene().getWindow());
		if (style == null) {
			try {
				style = new ConfigurationBusinessDelegate().getUIConfiguration(ClientUtil.getCurrentUser()).getStyle();
			} catch (TradistaBusinessException abe) {
			}
		}
		getDialogPane().getStylesheets().add("/" + style + "Style.css");
		getDialogPane().getStyleClass().add("root");
		TradistaGUIUtil.resizeComponents((Stage) getDialogPane().getScene().getWindow(), 0);
	}

	public TradistaAlert(AlertType alertType) {
		super(alertType);
		init(null);
	}

	public TradistaAlert(AlertType alertType, String contentText, ButtonType... buttons) {
		super(alertType, contentText, buttons);
		init(null);
	}

	public TradistaAlert(AlertType alertType, String contentText, String style, ButtonType... buttons) {
		super(alertType, contentText, buttons);
		init(style);
	}

}