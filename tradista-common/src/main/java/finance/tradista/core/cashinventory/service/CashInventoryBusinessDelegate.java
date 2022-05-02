package finance.tradista.core.cashinventory.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.inventory.model.CashInventory;
import finance.tradista.core.transfer.model.CashTransfer;

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

public class CashInventoryBusinessDelegate {

	private CashInventoryService cashInventoryService;

	public CashInventoryBusinessDelegate() {
		cashInventoryService = TradistaServiceLocator.getInstance().getCashInventoryService();
	}

	public void updateCashInventory(CashTransfer transfer) throws TradistaBusinessException {

		if (transfer == null) {
			throw new TradistaBusinessException("The CashTransfer cannot be null.");
		}

		StringBuffer errMsg = new StringBuffer();

		// TODO Should we validate the transfer here

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

		SecurityUtil.runEx(() -> cashInventoryService.updateCashInventory(transfer));
	}

	public Set<CashInventory> getCashInventoriesBeforeDateByCurrencyAndBookIds(long currencyId, long bookId,
			LocalDate date) throws TradistaBusinessException {
		if (date == null) {
			throw new TradistaBusinessException("The date cannot be null.");
		}

		return SecurityUtil.run(
				() -> cashInventoryService.getCashInventoriesBeforeDateByCurrencyAndBookIds(currencyId, bookId, date));
	}

	public Set<CashInventory> getOpenPositionsFromCashInventoryByCurrencyAndBookIds(long currencyId, long bookId)
			throws TradistaBusinessException {
		return SecurityUtil.run(
				() -> cashInventoryService.getOpenPositionsFromCashInventoryByCurrencyAndBookIds(currencyId, bookId));
	}

	public BigDecimal getAmountByDateCurrencyAndBookIds(long currencyId, long bookId, LocalDate date)
			throws TradistaBusinessException {
		if (date == null) {
			throw new TradistaBusinessException("The date is mandatory.");
		}
		return SecurityUtil.run(() -> cashInventoryService.getAmountByDateCurrencyAndBookIds(currencyId, bookId, date));
	}

	public Set<CashInventory> getCashInventories(LocalDate from, LocalDate to, long currencyId, long bookId,
			boolean onlyOpenPositions) throws TradistaBusinessException {

		if (onlyOpenPositions && to != null) {
			throw new TradistaBusinessException(
					String.format("'Only Open positions' and 'To' cannot be selected together.%n"));
		}
		if (from != null && to != null) {
			if (to.isBefore(from)) {
				throw new TradistaBusinessException(String.format("'To' date cannot be before 'From' date.%n"));
			}
		}
		return SecurityUtil
				.runEx(() -> cashInventoryService.getCashInventories(from, to, currencyId, bookId, onlyOpenPositions));
	}

}