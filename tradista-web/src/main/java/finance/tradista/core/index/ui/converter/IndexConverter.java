package finance.tradista.core.index.ui.converter;

import java.io.Serializable;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.index.service.IndexBusinessDelegate;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

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

@FacesConverter("indexConverter")
public class IndexConverter implements Serializable, Converter<Index> {

	private static final long serialVersionUID = 1302621501579077205L;
	private IndexBusinessDelegate indexBusinessDelegate;

	public IndexConverter() {
		indexBusinessDelegate = new IndexBusinessDelegate();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Index index) {
		return index.toString();
	}

	@Override
	public Index getAsObject(FacesContext context, UIComponent component, String value) {
		Index index = null;
		try {
			index = indexBusinessDelegate.getIndexByName(value);
		} catch (TradistaBusinessException tbe) {
			throw new ConverterException(String.format("Could not convert index %s", value), tbe);
		}
		return index;
	}

}