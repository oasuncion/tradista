package finance.tradista.core.batch.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import finance.tradista.core.batch.jobproperty.JobProperty;
import finance.tradista.core.batch.model.TradistaJob;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;

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

public class CashTransferFixingJob extends TradistaJob {

	@JobProperty(name = "QuoteSet", type = "QuoteSet")
	private QuoteSet quoteSet;

	@Override
	public void executeTradistaJob(JobExecutionContext execContext) throws JobExecutionException, TradistaBusinessException {
		TransferBusinessDelegate transferBusinessDelegate = new TransferBusinessDelegate();
		transferBusinessDelegate.fixCashTransfers(quoteSet.getId());
	}

	@Override
	public String getName() {
		return "CashTransferFixing";
	}

	public void setQuoteSet(QuoteSet quoteSet) {
		this.quoteSet = quoteSet;
	}

	@Override
	public void checkJobProperties() throws TradistaBusinessException {
		if (quoteSet == null) {
			throw new TradistaBusinessException("The quote set is mandatory.");
		}
	}

}