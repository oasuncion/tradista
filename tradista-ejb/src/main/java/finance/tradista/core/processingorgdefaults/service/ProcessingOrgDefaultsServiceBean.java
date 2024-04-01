package finance.tradista.core.processingorgdefaults.service;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaults;
import finance.tradista.core.processingorgdefaults.persistence.ProcessingOrgDefaultsSQL;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

/*
 * Copyright 2024 Olivier Asuncion
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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class ProcessingOrgDefaultsServiceBean implements ProcessingOrgDefaultsService {

	@Override
	@Interceptors(ProcessingOrgDefaultsFilteringInterceptor.class)
	public ProcessingOrgDefaults getProcessingOrgDefaultsByPoId(long poId) {
		return ProcessingOrgDefaultsSQL.getProcessingOrgDefaultsByPoId(poId);
	}

	@Override
	@Interceptors(ProcessingOrgDefaultsFilteringInterceptor.class)
	public long saveProcessingOrgDefaults(ProcessingOrgDefaults poDefaults) {
		return ProcessingOrgDefaultsSQL.saveProcessingOrgDefaults(poDefaults);
	}

}