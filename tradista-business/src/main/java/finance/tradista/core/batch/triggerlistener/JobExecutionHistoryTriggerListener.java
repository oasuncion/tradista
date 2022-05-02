package finance.tradista.core.batch.triggerlistener;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.util.StringUtils;

import finance.tradista.core.batch.model.TradistaJob;
import finance.tradista.core.batch.service.BatchBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;

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

public class JobExecutionHistoryTriggerListener extends TriggerListenerSupport {

	BatchBusinessDelegate batchBusinessDelegate;

	public JobExecutionHistoryTriggerListener() {
		batchBusinessDelegate = new BatchBusinessDelegate();
	}

	@Override
	public String getName() {
		return "JobExecutionHistoryTriggerListener";
	}

	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		String jobType = null;
		try {
			jobType = batchBusinessDelegate
					.getJobTypeByClass((Class<? extends TradistaJob>) context.getJobDetail().getJobClass());
		} catch (TradistaBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		batchBusinessDelegate.saveJobExecution(context.getFireInstanceId(), trigger.getJobKey().getGroup(),
				"IN PROGRESS", LocalDateTime.ofInstant(context.getFireTime().toInstant(), ZoneId.systemDefault()), null,
				null, trigger.getJobKey().getName(), jobType);
	}

	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			Trigger.CompletedExecutionInstruction triggerInstructionCode) {
		String status;
		String errorCause = (String) context.get(TradistaJob.ERROR_CAUSE);
		if (StringUtils.isEmpty(errorCause)) {
			status = "SUCCESS";
		} else {
			status = "FAILED";
		}
		if (!StringUtils.isEmpty(context.get(TradistaJob.STOPPED))) {
			status = "STOPPED";
		}
		String jobType = null;
		try {
			jobType = batchBusinessDelegate
					.getJobTypeByClass((Class<? extends TradistaJob>) context.getJobDetail().getJobClass());
		} catch (TradistaBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		batchBusinessDelegate.saveJobExecution(context.getFireInstanceId(), trigger.getJobKey().getGroup(), status,
				null, LocalDateTime.now(), errorCause, trigger.getJobKey().getName(), jobType);
	}

}