package finance.tradista.core.position.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.position.model.Position;

/*
 * Copyright 2016 Olivier Asuncion
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
public interface PositionService {

	long savePosition(Position position);

	void savePositions(List<Position> positions);

	List<Position> getPositionsByDefinitionIdAndValueDates(long positionDefinitionId, LocalDate valueDateFrom,
			LocalDate valueDateTo) throws TradistaBusinessException;

	void calculatePosition(String positionDefinition, LocalDateTime valueDateTime) throws TradistaBusinessException;

	Position getLastPositionByDefinitionNameAndValueDate(String positionDefinitionName, LocalDate valueDate);

}