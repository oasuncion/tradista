package finance.tradista.core.error.service;

import java.time.LocalDate;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.error.model.Error.Status;
import finance.tradista.core.error.persistence.ErrorSQL;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;

/*
 * Copyright 2019 Olivier Asuncion
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
public class ErrorServiceBean implements ErrorService {

	@Override
	public void deleteErrors(String errorType, Status status,
			LocalDate errorDateFrom, LocalDate errorDateTo) {
		ErrorSQL.deleteErrors(errorType, status, errorDateFrom, errorDateTo);
	}

}
