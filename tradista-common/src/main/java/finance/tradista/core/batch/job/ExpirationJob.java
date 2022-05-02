package finance.tradista.core.batch.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import finance.tradista.core.batch.model.TradistaJob;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.productinventory.service.ProductInventoryBusinessDelegate;

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

public class ExpirationJob extends TradistaJob {

	@Override
	public void executeTradistaJob(JobExecutionContext execContext) throws JobExecutionException, TradistaBusinessException {
		ProductInventoryBusinessDelegate productInventoryBusinessDelegate = new ProductInventoryBusinessDelegate();
		productInventoryBusinessDelegate.closeExpiredProductsPositions();
	}

	@Override
	public String getName() {
		return "Expiration";
	}

	@Override
	public void checkJobProperties() throws TradistaBusinessException {
	}

}