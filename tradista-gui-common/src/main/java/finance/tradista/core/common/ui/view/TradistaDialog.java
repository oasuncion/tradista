package finance.tradista.core.common.ui.view;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

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

public class TradistaDialog<X> extends Dialog<X> {

	public TradistaDialog() {
		super();
		TradistaGUIUtil.setTradistaIcons((Stage) getDialogPane().getScene().getWindow());
		setResizable(false);
		try {
			getDialogPane().getStylesheets().add("/" + new ConfigurationBusinessDelegate().getUIConfiguration(ClientUtil.getCurrentUser()).getStyle() + "Style.css");
		} catch (TradistaBusinessException abe) {
		}
		getDialogPane().getStyleClass().add("root");
	}

}