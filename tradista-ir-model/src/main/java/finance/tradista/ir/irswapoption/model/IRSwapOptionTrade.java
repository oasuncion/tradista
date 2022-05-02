package finance.tradista.ir.irswapoption.model;

import java.math.BigDecimal;

import finance.tradista.core.index.model.Index;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.trade.model.VanillaOptionTrade;
import finance.tradista.ir.irswap.model.SingleCurrencyIRSwapTrade;

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

public class IRSwapOptionTrade extends VanillaOptionTrade<SingleCurrencyIRSwapTrade> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8952352869490542697L;
	
	public static final String IR_SWAP_OPTION = "IRSwapOption";
	
	private BigDecimal cashSettlementAmount;
	
	private Index alternativeCashSettlementReferenceRateIndex;
	
	private Tenor alternativeCashSettlementReferenceRateIndexTenor;
	
	public BigDecimal getCashSettlementAmount() {
		return cashSettlementAmount;
	}

	public void setCashSettlementAmount(BigDecimal cashSettlementAmount) {
		this.cashSettlementAmount = cashSettlementAmount;
	}

	public Index getAlternativeCashSettlementReferenceRateIndex() {
		return alternativeCashSettlementReferenceRateIndex;
	}

	public void setAlternativeCashSettlementReferenceRateIndex(Index alternativeCashSettlementReferenceRateIndex) {
		this.alternativeCashSettlementReferenceRateIndex = alternativeCashSettlementReferenceRateIndex;
	}

	public Tenor getAlternativeCashSettlementReferenceRateIndexTenor() {
		return alternativeCashSettlementReferenceRateIndexTenor;
	}

	public void setAlternativeCashSettlementReferenceRateIndexTenor(
			Tenor alternativeCashSettlementReferenceRateIndexTenor) {
		this.alternativeCashSettlementReferenceRateIndexTenor = alternativeCashSettlementReferenceRateIndexTenor;
	}

	@Override
	public String getProductType() {
		return IR_SWAP_OPTION;
	}
	
}