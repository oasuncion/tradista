package finance.tradista.core.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.product.model.Product;

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

public class ProductInventory extends TradistaObject implements Comparable<ProductInventory> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -386706153298729738L;

	private Product product;

	private BigDecimal quantity;

	private BigDecimal averagePrice;

	private LocalDate from;

	private LocalDate to;

	private Book book;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public LocalDate getFrom() {
		return from;
	}

	public void setFrom(LocalDate from) {
		this.from = from;
	}

	public LocalDate getTo() {
		return to;
	}

	public void setTo(LocalDate to) {
		this.to = to;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((book == null) ? 0 : book.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductInventory other = (ProductInventory) obj;
		if (book == null) {
			if (other.book != null)
				return false;
		} else if (!book.equals(other.book))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		return true;
	}

	@Override
	public int compareTo(ProductInventory pi) {
		if (pi == null) {
			return 1;
		}
		int eq = getBook().toString().compareTo(pi.getBook().toString());
		if (eq == 0) {
			eq = getProduct().getProductType().toString().compareTo(pi.getProduct().getProductType().toString());
			if (eq == 0) {
				eq = (int) (getProduct().getId() - pi.getProduct().getId());
				if (eq == 0) {
					return getFrom().compareTo(pi.getFrom());
				} else {
					return eq;
				}
			}
		}
		return eq;
	}

	@Override
	public String toString() {
		return "Inventory [product=" + product + ", quantity=" + quantity + ", averagePrice=" + averagePrice + ", from="
				+ from + ", to=" + to + ", book=" + book + "]";
	}

}