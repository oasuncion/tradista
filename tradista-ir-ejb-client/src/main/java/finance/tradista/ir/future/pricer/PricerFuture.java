package finance.tradista.ir.future.pricer;

import finance.tradista.core.pricing.pricer.Parameterizable;
import finance.tradista.core.pricing.pricer.Pricer;

/*
 * Copyright 2018 Olivier Asuncion
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

@Parameterizable(name="Default Future Pricer")
public class PricerFuture extends Pricer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2854712819456528291L;

	public PricerFuture() {
		super();
		getPricerMeasures().add(new PricerMeasurePV());
		getPricerMeasures().add(new PricerMeasureNPV());
		
		getProductPricerMeasures().add(new PricerMeasurePNL());
		getProductPricerMeasures().add(new PricerMeasureREALIZED_PNL());
		getProductPricerMeasures().add(new PricerMeasureUNREALIZED_PNL());
	}

}
