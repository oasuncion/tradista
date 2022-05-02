package finance.tradista.core.batch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import javax.ejb.Remote;

import finance.tradista.core.batch.model.TradistaJob;
import finance.tradista.core.batch.model.TradistaJobExecution;
import finance.tradista.core.batch.model.TradistaJobInstance;
import finance.tradista.core.common.exception.TradistaBusinessException;

/*
 * Copyright 2015 Olivier Asuncion
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

@Remote
public interface BatchService {

	void saveJobInstance(TradistaJobInstance jobInstance) throws TradistaBusinessException;

	Set<String> getAllJobTypes();

	String getJobTypeByClass(Class<? extends TradistaJob> klass) throws TradistaBusinessException;

	Class<? extends TradistaJob> getJobClassByType(String jobType) throws TradistaBusinessException;

	void deleteJobInstance(String jobInstanceName, String po);

	Set<TradistaJobInstance> getAllJobInstances(String po) throws TradistaBusinessException;

	TradistaJobInstance getJobInstanceByNameAndPo(String jobInstanceName, String po) throws TradistaBusinessException;

	Set<TradistaJobExecution> getJobExecutions(LocalDate date, String po);

	void runJobInstance(String jobInstanceName, String po);

	void stopJobExecution(String jobExecutionId);

	long saveJobExecution(String name, String po, String status, LocalDateTime startTime, LocalDateTime endTime,
			String errorCause, String jobInstanceName, String jobType);

	TradistaJobExecution getJobExecutionById(String jobExecutionId);

}
