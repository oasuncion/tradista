package finance.tradista.core.productinventory.service;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
import finance.tradista.core.inventory.model.ProductInventory;
import finance.tradista.core.user.model.User;

/*
 * Copyright 2019 Olivier Asuncion
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

public class ProductInventoryFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private BookBusinessDelegate bookBusinessDelegate;

	public ProductInventoryFilteringInterceptor() {
		super();
		bookBusinessDelegate = new BookBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		long bookId = (long) parameters[4];
		if (bookId != 0) {
			Book book = bookBusinessDelegate.getBookById(bookId);
			if (book == null) {
				throw new TradistaBusinessException(String.format("The Book %s was not found.", bookId));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Object postFilter(Object value) {
		if (value != null) {
			if (value instanceof Set) {
				Set<ProductInventory> productInventories = (Set<ProductInventory>) value;
				User user = getCurrentUser();
				value = productInventories.stream()
						.filter(pi -> pi.getBook().getProcessingOrg().equals(user.getProcessingOrg()))
						.collect(Collectors.toCollection(TreeSet::new));
			}
		}
		return value;
	}

}