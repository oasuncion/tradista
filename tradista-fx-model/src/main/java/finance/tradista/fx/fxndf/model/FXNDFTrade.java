package finance.tradista.fx.fxndf.model;

import java.math.BigDecimal;

import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.product.model.Product;
import finance.tradista.fx.common.model.AbstractFXTrade;

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

public class FXNDFTrade extends AbstractFXTrade<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4927177189578884165L;

	public static String FX_NDF = "FXNDF";

	private Currency nonDeliverableCurrency;

	private BigDecimal ndfRate;

	public Currency getNonDeliverableCurrency() {
		return nonDeliverableCurrency;
	}

	public void setNonDeliverableCurrency(Currency nonDeliverableCurrency) {
		this.nonDeliverableCurrency = nonDeliverableCurrency;
	}

	public BigDecimal getNdfRate() {
		return ndfRate;
	}

	public void setNdfRate(BigDecimal ndfRate) {
		this.ndfRate = ndfRate;
	}

	public String getProductType() {
		return FX_NDF;
	}

}
