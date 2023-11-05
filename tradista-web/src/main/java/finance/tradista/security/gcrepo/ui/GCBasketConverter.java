package finance.tradista.security.gcrepo.ui;

import java.io.Serializable;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.security.gcrepo.model.GCBasket;
import finance.tradista.security.gcrepo.service.GCBasketBusinessDelegate;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/*
 * Copyright 2023 Olivier Asuncion
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

@Named
@ViewScoped
@FacesConverter("gcBasketConverter")
public class GCBasketConverter implements Serializable, Converter<GCBasket> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -19742544917495063L;
	private GCBasketBusinessDelegate gcBasketBusinessDelegate;

	public GCBasketConverter() {
		gcBasketBusinessDelegate = new GCBasketBusinessDelegate();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, GCBasket gcBasket) {
		String value = null;
		if (gcBasket != null) {
			value = gcBasket.getName();
		}
		return value;
	}

	@Override
	public GCBasket getAsObject(FacesContext context, UIComponent component, String value) {
		GCBasket gcBasket = null;
		if (value != null) {
			try {
				gcBasket = gcBasketBusinessDelegate.getGCBasketByName(value);
			} catch (TradistaBusinessException tbe) {
				throw new ConverterException(tbe.getMessage());
			}
		}
		return gcBasket;
	}

}