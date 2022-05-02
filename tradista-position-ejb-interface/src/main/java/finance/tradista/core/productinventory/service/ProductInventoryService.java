package finance.tradista.core.productinventory.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.inventory.model.ProductInventory;
import finance.tradista.core.transfer.model.ProductTransfer;

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
public interface ProductInventoryService {

	void updateProductInventory(ProductTransfer transfer) throws TradistaBusinessException;

	Set<ProductInventory> getProductInventoriesBeforeDateByProductAndBookIds(long productId, long bookId,
			LocalDate date);

	Set<ProductInventory> getOpenPositionsFromProductInventoryByProductAndBookIds(long productId, long bookId);

	BigDecimal getQuantityByDateProductAndBookIds(long productId, long bookId, LocalDate date);

	BigDecimal getAveragePriceByDateProductAndBookIds(long productId, long bookId, LocalDate date);

	Set<ProductInventory> getProductInventories(LocalDate from, LocalDate to, String productType, long productId,
			long bookId, boolean onlyOpenPositions) throws TradistaBusinessException;

	void closeExpiredProductsPositions();

	Map<String, BigDecimal> getBookProductContent(LocalDate date, long bookId);

}
