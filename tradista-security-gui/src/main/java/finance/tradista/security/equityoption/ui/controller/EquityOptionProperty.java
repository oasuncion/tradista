package finance.tradista.security.equityoption.ui.controller;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.security.equityoption.model.EquityOption;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

public class EquityOptionProperty {

	private LongProperty id = new SimpleLongProperty();
	private StringProperty code = new SimpleStringProperty();
	private StringProperty equity = new SimpleStringProperty();
	private StringProperty quantity = new SimpleStringProperty();
	private StringProperty style = new SimpleStringProperty();
	private StringProperty exchange = new SimpleStringProperty();

	public EquityOptionProperty(EquityOption equityOption) {
		this.id.set(equityOption.getId());
		this.code.set(equityOption.getCode());
		this.equity
				.set(equityOption.getUnderlying() == null ? StringUtils.EMPTY : equityOption.getUnderlying().getIsin());
		this.quantity.set(TradistaGUIUtil.formatAmount(equityOption.getQuantity()));
		this.style.set(equityOption.getStyle().name());
		this.exchange.set(equityOption.getExchange().getCode());
	}

	public LongProperty getId() {
		return id;
	}

	public StringProperty getCode() {
		return code;
	}

	public StringProperty getEquity() {
		return equity;
	}

	public StringProperty getQuantity() {
		return quantity;
	}

	public StringProperty getStyle() {
		return style;
	}

	public StringProperty getExchange() {
		return exchange;
	}

}