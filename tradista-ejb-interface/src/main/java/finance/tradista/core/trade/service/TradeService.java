package finance.tradista.core.trade.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;
import jakarta.ejb.Remote;

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
public interface TradeService {

	List<Trade<? extends Product>> getTradesByCreationDate(LocalDate creationDate);

	List<Trade<? extends Product>> getTradesByDates(LocalDate startCreationDate, LocalDate endCreationDate,
			LocalDate startTradeDate, LocalDate endTradeDate);

	Trade<? extends Product> getTradeById(long id);

	Set<Trade<? extends Product>> getTrades(PositionDefinition posDef);

	Trade<?> getTradeById(long tradeId, boolean includeUnderlying);
}
