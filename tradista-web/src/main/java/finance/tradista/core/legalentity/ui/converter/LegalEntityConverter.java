package finance.tradista.core.legalentity.ui.converter;

import java.io.Serializable;

import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.legalentity.service.LegalEntityBusinessDelegate;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

/*
 * Copyright 2022 Olivier Asuncion
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

@FacesConverter("legalEntityConverter")
public class LegalEntityConverter implements Serializable, Converter<LegalEntity> {

	private static final long serialVersionUID = 3802860683043711768L;

	private LegalEntityBusinessDelegate legalEntityBusinessDelegate;

	public LegalEntityConverter() {
		legalEntityBusinessDelegate = new LegalEntityBusinessDelegate();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, LegalEntity legalEntity) {
		return legalEntity.toString();
	}

	@Override
	public LegalEntity getAsObject(FacesContext context, UIComponent component, String value) {
		return legalEntityBusinessDelegate.getLegalEntityByShortName(value);
	}

}