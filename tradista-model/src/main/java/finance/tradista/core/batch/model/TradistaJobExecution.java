package finance.tradista.core.batch.model;

import java.time.LocalDateTime;

import org.quartz.Trigger;

import finance.tradista.core.common.model.TradistaObject;

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

public class TradistaJobExecution extends TradistaObject {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1515249258612193803L;

	private Trigger trigger;

	private String status, errorCause, name, jobType;

	private LocalDateTime startTime, endTime;

	private TradistaJobInstance jobInstance;

	public TradistaJobInstance getJobInstance() {
		return jobInstance;
	}

	public TradistaJobExecution(Trigger trigger, TradistaJobInstance jobInstance) {
		super();
		this.jobInstance = jobInstance;
		this.trigger = trigger;
	}

	/**
	 * Note: the name field of this TradistaJobExecution instance is created in
	 * BatchSQL and is, in reality, the fireInstanceId data in the Quartz API.
	 * In quartz, fireInstanceId is different of name because a trigger can
	 * launch several job executions, each having its own fireInstanceId. But in
	 * Tradista (at least Tradista 1.0), a trigger launches only one immediate
	 * execution, so, the fireInstanceId identifies a trigger. FireInstanceId
	 * are used in the Quartz API to interrupt triggers.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}	

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public String getErrorCause() {
		return errorCause;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public String getJobInstanceName() {
		return trigger.getJobKey().getName();
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

}
