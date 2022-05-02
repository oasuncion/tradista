package finance.tradista.security.common.ui.util;

import java.util.Set;

import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.service.EquityBusinessDelegate;
import finance.tradista.security.equityoption.model.BlankEquityOption;
import finance.tradista.security.equityoption.model.EquityOption;
import finance.tradista.security.equityoption.model.EquityOptionContractSpecification;
import finance.tradista.security.equityoption.service.EquityOptionBusinessDelegate;
import finance.tradista.security.equityoption.service.EquityOptionContractSpecificationBusinessDelegate;
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

public final class TradistaSecurityGUIUtil {

	private static EquityOptionContractSpecificationBusinessDelegate equityOptionContractSpecificationBusinessDelegate = new EquityOptionContractSpecificationBusinessDelegate();

	private static EquityBusinessDelegate equityBusinessDelegate = new EquityBusinessDelegate();

	private static EquityOptionBusinessDelegate equityOptionBusinessDelegate = new EquityOptionBusinessDelegate();

	@SafeVarargs
	public static void fillEquityOptionContractSpecificationComboBox(
			ComboBox<EquityOptionContractSpecification>... comboBoxes) {
		Set<EquityOptionContractSpecification> specifications = equityOptionContractSpecificationBusinessDelegate
				.getAllEquityOptionContractSpecifications();
		ObservableList<EquityOptionContractSpecification> data = null;
		if (specifications != null && !specifications.isEmpty()) {
			data = FXCollections.observableArrayList(specifications);
		} else {
			data = FXCollections.emptyObservableList();
		}
		if (comboBoxes.length > 0) {
			for (ComboBox<EquityOptionContractSpecification> cb : comboBoxes) {
				EquityOptionContractSpecification element = cb.getValue();
				cb.setItems(data);
				if (element != null && data.contains(element)) {
					cb.getSelectionModel().select(element);
				} else {
					cb.getSelectionModel().selectFirst();
				}
			}
		}
	}

	@SafeVarargs
	public static void fillEquityComboBox(ComboBox<Equity>... comboBoxes) {
		Set<Equity> equities = equityBusinessDelegate.getAllEquities();
		ObservableList<Equity> data = null;
		if (equities != null && !equities.isEmpty()) {
			data = FXCollections.observableArrayList(equities);
		} else {
			data = FXCollections.emptyObservableList();
		}
		if (comboBoxes.length > 0) {
			for (ComboBox<Equity> cb : comboBoxes) {
				Equity element = cb.getValue();
				cb.setItems(data);
				if (element != null && data.contains(element)) {
					cb.getSelectionModel().select(element);
				} else {
					cb.getSelectionModel().selectFirst();
				}
			}
		}
	}

	@SafeVarargs
	public static void fillEquityOptionComboBox(ComboBox<EquityOption>... comboBoxes) {
		fillEquityOptionComboBox(false, comboBoxes);
	}

	@SafeVarargs
	public static void fillEquityOptionComboBox(boolean addBlank, ComboBox<EquityOption>... comboBoxes) {
		Set<EquityOption> equtyOptions = equityOptionBusinessDelegate.getAllEquityOptions();
		ObservableList<EquityOption> data = null;
		if (equtyOptions != null && !equtyOptions.isEmpty()) {
			data = FXCollections.observableArrayList(equtyOptions);
		} else {
			data = FXCollections.observableArrayList();
		}
		if (addBlank) {
			data.add(0, BlankEquityOption.getInstance());
		}
		if (comboBoxes.length > 0) {
			for (ComboBox<EquityOption> cb : comboBoxes) {
				EquityOption element = cb.getValue();
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