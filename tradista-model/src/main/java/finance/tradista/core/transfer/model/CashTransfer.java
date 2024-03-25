package finance.tradista.core.transfer.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;

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

public class CashTransfer extends Transfer {

	public CashTransfer(Book book, TransferPurpose purpose, LocalDate settlementDate, Trade<?> trade,
			Currency currency) {
		super(book, null, purpose, settlementDate, trade);
		this.currency = currency;
	}

	public CashTransfer(Book book, Product product, TransferPurpose purpose, LocalDate settlementDate,
			Currency currency) {
		super(book, product, purpose, settlementDate, null);
		this.currency = currency;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7123636684384971261L;

	@Id
	private Currency currency;

	public Currency getCurrency() {
		return TradistaModelUtil.clone(currency);
	}

	public BigDecimal getAmount() {
		return quantityOrAmount;
	}

	public void setAmount(BigDecimal amount) {
		this.quantityOrAmount = amount;
	}

	@Override
	public Type getType() {
		return Transfer.Type.CASH;
	}

	@Override
	public CashTransfer clone() {
		CashTransfer cashTransfer = (CashTransfer) super.clone();
		cashTransfer.currency = TradistaModelUtil.clone(currency);
		return cashTransfer;
	}

}