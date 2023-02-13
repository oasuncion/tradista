package finance.tradista.core.product.model;

import java.time.LocalDate;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.exchange.model.Exchange;

/*
 * Copyright 2014 Olivier Asuncion
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

public abstract class Product extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 518407081850145938L;
	private LocalDate creationDate;

	@Id
	private Exchange exchange;
	
	public Product(Exchange exchange) {
		super();
		this.exchange = exchange;
	}

	public Exchange getExchange() {
		return TradistaModelUtil.clone(exchange);
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public abstract String getProductType();

	@Override
	public Product clone() {
		Product product = (Product) super.clone();
		product.exchange = TradistaModelUtil.clone(exchange);
		return product;
	}

}