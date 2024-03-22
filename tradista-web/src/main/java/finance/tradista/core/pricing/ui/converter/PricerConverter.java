package finance.tradista.core.pricing.ui.converter;

import java.io.Serializable;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.pricing.pricer.Parameterizable;
import finance.tradista.core.pricing.pricer.Pricer;
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
@FacesConverter("pricerConverter")
public class PricerConverter implements Serializable, Converter<Pricer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PricerConverter() {
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Pricer pricer) {
		return pricer.getClass().getAnnotation(Parameterizable.class).name();
	}

	@Override
	public Pricer getAsObject(FacesContext context, UIComponent component, String value) {
		Pricer pricer = null;
		try {
			pricer = (Pricer) TradistaUtil.getInstance(TradistaUtil
					.getAllClassesByTypeAndAnnotation(Pricer.class, Parameterizable.class, "finance.tradista").stream()
					.filter(p -> p.getClass().getAnnotation(Parameterizable.class).name().equals(value)).findFirst()
					.get());
		} catch (TradistaTechnicalException tte) {
			throw new ConverterException(String.format("Could not convert pricer %s", value), tte);
		}
		return pricer;
	}

}