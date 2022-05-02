package finance.tradista.core.product.ui.view;

import java.util.Set;

import finance.tradista.core.product.service.ProductBusinessDelegate;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.beans.NamedArg;

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

public class TradistaProductTypeComboBox extends ComboBox<String> {
	
	public TradistaProductTypeComboBox() {
		this(null);
	}

	public TradistaProductTypeComboBox(@NamedArg("productFamily") String productFamily) {
		ProductBusinessDelegate productBusinessDelegate = new ProductBusinessDelegate();
		Set<String> allProductTypes = null;
		if (productFamily == null) {
			allProductTypes = productBusinessDelegate.getAvailableProductTypes();
		} else if (productFamily.equals("fx")) {
			allProductTypes = productBusinessDelegate.getAvailableFXProductTypes();
		}
		if (allProductTypes != null && !allProductTypes.isEmpty()) {
			setItems(FXCollections.observableArrayList(allProductTypes));
		}
	}

}