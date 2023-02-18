package finance.tradista.security.bond.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaObject;

/*
 * Copyright 2014 Olivier Asuncion
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

/**
 * Class used to represented Bond coupons. Used only when calculating coupons
 * for one specific bond, that's why a Coupon object is identified by its date.
 * Prefer usage of Cashflow class, this Coupon class may be deprecated in the
 * future.
 * 
 * @author OA
 *
 */
public class Coupon extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5271486827367738893L;

	@Id
	private LocalDate date;

	private BigDecimal amount;

	public Coupon(LocalDate date) {
		super();
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}