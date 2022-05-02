package finance.tradista.core.transfer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.error.model.Error.Status;
import finance.tradista.core.transfer.model.FixingError;

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

@Remote
public interface FixingErrorService {

	boolean saveFixingErrors(List<FixingError> errors);

	void solveFixingError(Set<Long> solved, LocalDate date);

	void solveFixingError(long transferId, LocalDate date) throws TradistaBusinessException;

	List<FixingError> getFixingErrors(long transferId, Status status, LocalDate errorDateFrom, LocalDate errorDateTo,
			LocalDate solvingDateFrom, LocalDate solvingDateTo) throws TradistaBusinessException;

}