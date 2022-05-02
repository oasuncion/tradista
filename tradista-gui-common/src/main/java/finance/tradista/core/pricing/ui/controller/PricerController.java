package finance.tradista.core.pricing.ui.controller;

import java.io.IOException;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.ui.controller.TradistaController;
import finance.tradista.core.common.ui.controller.TradistaControllerAdapter;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.pricing.pricer.Pricer;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.service.PricerBusinessDelegate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

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

public class PricerController extends TradistaControllerAdapter implements TradistaController {

	@FXML
	private ComboBox<LegalEntity> counterparty;

	@FXML
	private ComboBox<PricingParameter> pricingParameter;

	@FXML
	private ComboBox<PricerMeasure> pricingMeasure;

	@FXML
	private ComboBox<String> pricingMethod;

	@FXML
	private ComboBox<Currency> pricingCurrency;

	@FXML
	private DatePicker pricingDate;

	@FXML
	private Label result;

	public PricerController() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Pricer.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void initialize() {
		TradistaGUIUtil.fillComboBox(new PricerBusinessDelegate().getAllPricingParameters(), pricingParameter);
		try {
			Pricer pricer = new PricerBusinessDelegate().getPricer("FX", pricingParameter.getValue());
			TradistaGUIUtil.fillComboBox(pricer.getPricerMeasures(), pricingMeasure);
		} catch (TradistaBusinessException tbe) {
			// Will never happen in this case.
		}

		pricingMeasure.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PricerMeasure>() {
			@Override
			public void changed(ObservableValue<? extends PricerMeasure> arg0, PricerMeasure arg1, PricerMeasure arg2) {
				TradistaGUIUtil.fillComboBox(new PricerBusinessDelegate().getAllPricingMethods(arg2), pricingMethod);
			}
		});

	}

	public void initPricerMeasures(String productType) {
		try {
			Pricer pricer = new PricerBusinessDelegate().getPricer(productType, pricingParameter.getValue());
			TradistaGUIUtil.fillComboBox(pricer.getPricerMeasures(), pricingMeasure);
		} catch (TradistaBusinessException tbe) {
			// Will never happen in this case.
		}
	}

	@FXML
	protected void price() {
	}

}