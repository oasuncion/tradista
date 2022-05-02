package finance.tradista.ir.irswap.model;

import finance.tradista.core.marketdata.model.Instrument;

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

public class SingleCurrencyIRSwapTrade extends IRSwapTrade implements Instrument{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6188291608649255466L;
	
	public static final String IR_SWAP = "IRSwap";
		
	public String getProductType() {
		return IR_SWAP;
	}

	@Override
	public String getInstrumentName() {
		return IR_SWAP;
	}
	

}
