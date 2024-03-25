package finance.tradista.core.transfer.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.book.model.Book;
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

public class ProductTransfer extends Transfer {

	public ProductTransfer(Book book, TransferPurpose purpose, LocalDate settlementDate, Trade<?> trade) {
		super(book, null, purpose, settlementDate, trade);
	}

	public ProductTransfer(Book book, Product product, TransferPurpose purpose, LocalDate settlementDate,
			Trade<?> trade) {
		super(book, product, purpose, settlementDate, trade);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3528142875953447004L;

	public BigDecimal getQuantity() {
		return quantityOrAmount;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantityOrAmount = quantity;
	}

	@Override
	public Type getType() {
		return Transfer.Type.PRODUCT;
	}

}