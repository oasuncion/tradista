package finance.tradista.security.equity.ui.view;

import java.util.Set;

import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.service.EquityBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

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

public class TradistaEquityComboBox extends ComboBox<Equity> {

	public TradistaEquityComboBox() {
		EquityBusinessDelegate equityBusinessDelegate = new EquityBusinessDelegate();
		Set<Equity> allEquities = equityBusinessDelegate.getAllEquities();
		if (allEquities != null && !allEquities.isEmpty()) {
			setItems(FXCollections.observableArrayList(allEquities));
		}
	}

}