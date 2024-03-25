package finance.tradista.core.transfer.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
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

public abstract class Transfer extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3471863038215096341L;

	public enum Status {
		UNKNOWN, KNOWN, POTENTIAL, CANCELED;

		@Override
		public String toString() {
			switch (this) {
			case UNKNOWN:
				return "Unknown";
			case KNOWN:
				return "Known";
			case CANCELED:
				return "Canceled";
			case POTENTIAL:
				return "Potential";
			}
			return super.toString();
		}

		/**
		 * Gets a Status from a display name. Display names are used in GUIs. A display
		 * name of a Status is the result of its toString() method.
		 * 
		 * @param type
		 * @return
		 */
		public static Status getStatus(String displayName) {
			switch (displayName) {
			case "Unknown":
				return UNKNOWN;
			case "Known":
				return KNOWN;
			case "Canceled":
				return CANCELED;
			case "Potential":
				return POTENTIAL;
			}
			return null;
		}
	}

	public enum Type {
		CASH, PRODUCT;

		@Override
		public String toString() {
			switch (this) {
			case CASH:
				return "Cash";
			case PRODUCT:
				return "Product";
			}
			return super.toString();
		}

		/**
		 * Gets a Type from a display name. Display names are used in GUIs. A display
		 * name of a Type is the result of its toString() method.
		 * 
		 * @param type
		 * @return
		 */
		public static Type getType(String displayName) {
			switch (displayName) {
			case "Cash":
				return CASH;
			case "Product":
				return PRODUCT;
			}
			return null;
		}
	}

	public enum Direction {
		PAY, RECEIVE;

		@Override
		public String toString() {
			switch (this) {
			case PAY:
				return "Pay";
			case RECEIVE:
				return "Receive";
			}
			return super.toString();
		}

		/**
		 * Gets a Direction from a display name. Display names are used in GUIs. A
		 * display name of a Direction is the result of its toString() method.
		 * 
		 * @param type
		 * @return
		 */
		public static Direction getDirection(String displayName) {
			switch (displayName) {
			case "Pay":
				return PAY;
			case "Receive":
				return RECEIVE;
			}
			return null;
		}
	};

	private Status status;

	@Id
	private TransferPurpose purpose;

	private Direction direction;

	protected BigDecimal quantityOrAmount;

	@Id
	private Trade<?> trade;

	private LocalDateTime creationDateTime;

	private LocalDateTime fixingDateTime;

	@Id
	private LocalDate settlementDate;

	@Id
	private Product product;

	@Id
	private Book book;

	public Transfer(Book book, Product product, TransferPurpose purpose, LocalDate settlementDate, Trade<?> trade) {
		this.book = book;
		this.product = product;
		this.purpose = purpose;
		this.settlementDate = settlementDate;
		this.trade = trade;
		if (product == null) {
			if (trade != null) {
				this.product = trade.getProduct();
			}
		}
	}

	public Product getProduct() {
		return TradistaModelUtil.clone(product);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public abstract Type getType();

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Trade<?> getTrade() {
		return TradistaModelUtil.clone(trade);
	}

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(LocalDateTime creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public LocalDateTime getFixingDateTime() {
		return fixingDateTime;
	}

	public void setFixingDateTime(LocalDateTime fixingDateTime) {
		this.fixingDateTime = fixingDateTime;
	}

	public LocalDate getSettlementDate() {
		return settlementDate;
	}

	public TransferPurpose getPurpose() {
		return purpose;
	}

	public Book getBook() {
		return TradistaModelUtil.clone(book);
	}

	@Override
	public Transfer clone() {
		Transfer transfer = (Transfer) super.clone();
		transfer.trade = TradistaModelUtil.clone(trade);
		transfer.product = TradistaModelUtil.clone(product);
		transfer.book = TradistaModelUtil.clone(book);
		return transfer;
	}

}