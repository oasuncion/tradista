package finance.tradista.ir.common.util;

import finance.tradista.ir.ircapfloorcollar.model.IRCapFloorCollarTrade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

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

public final class TradistaIRGUIUtil {

	@SafeVarargs
	public static void fillIRCapFloorCollarTypeComboBox(ComboBox<IRCapFloorCollarTrade.Type>... comboBoxes) {
		ObservableList<IRCapFloorCollarTrade.Type> data = FXCollections
				.observableArrayList(IRCapFloorCollarTrade.Type.values());
		if (comboBoxes.length > 0) {
			for (ComboBox<IRCapFloorCollarTrade.Type> cb : comboBoxes) {
				IRCapFloorCollarTrade.Type element = cb.getValue();
				cb.setItems(data);
				if (element != null && data.contains(element)) {
					cb.getSelectionModel().select(element);
				} else {
					cb.getSelectionModel().selectFirst();
				}
			}
		}
	}

}