package finance.tradista.core.pricing.ui.converter;

import java.io.Serializable;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.service.PricerBusinessDelegate;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
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
public class PricingParameterConverter implements Serializable, Converter<PricingParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PricerBusinessDelegate pricerBusinessDelegate;

	public PricingParameterConverter() {
		pricerBusinessDelegate = new PricerBusinessDelegate();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, PricingParameter pricingParameter) {
		return pricingParameter.getName();
	}

	@Override
	public PricingParameter getAsObject(FacesContext context, UIComponent component, String value) {
		PricingParameter pricingParameter = null;
		try {
			LegalEntity currentPo = ClientUtil.getCurrentUser().getProcessingOrg();
			long poId = currentPo != null ? currentPo.getId() : 0;
			pricingParameter = pricerBusinessDelegate.getPricingParameterByNameAndPoId(value, poId);
		} catch (TradistaBusinessException tbe) {
			throw new ConverterException(String.format("Could not convert index %s", value), tbe);
		}
		return pricingParameter;
	}

}