package finance.tradista.core.book.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.TradistaAuthorizationFilteringInterceptor;
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

public class BookFilteringInterceptor extends TradistaAuthorizationFilteringInterceptor {

	private BookBusinessDelegate bookBusinessDelegate;

	public BookFilteringInterceptor() {
		super();
		bookBusinessDelegate = new BookBusinessDelegate();
	}

	@AroundInvoke
	public Object filter(InvocationContext ic) throws Exception {
		return proceed(ic);
	}

	protected void preFilter(InvocationContext ic) throws TradistaBusinessException {
		Object[] parameters = ic.getParameters();
		if (parameters.length > 0 && parameters[0] instanceof Book) {
			Book book = (Book) parameters[0];
			StringBuilder errMsg = new StringBuilder();
			User user = getCurrentUser();
			if (book.getId() != 0) {
				Book b = bookBusinessDelegate.getBookById(book.getId());
				if (b == null) {
					errMsg.append(String.format("The book %s was not found.%n", book.getName()));
				} else if (b.getProcessingOrg() == null) {
					errMsg.append(String.format("The book %s is a global one and you are not allowed to update it.%n",
							book.getName()));
				}
			}
			if (book.getProcessingOrg() != null && !book.getProcessingOrg().equals(user.getProcessingOrg())) {
				errMsg.append(String.format("The processing org %s was not found.", book.getProcessingOrg()));
			}
			if (errMsg.length() > 0) {
				throw new TradistaBusinessException(errMsg.toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Object postFilter(Object value) throws TradistaBusinessException {
		if (value != null) {
			User user = getCurrentUser();
			if (value instanceof Set) {
				Set<Book> books = (Set<Book>) value;
				if (!books.isEmpty()) {
					value = books.stream().filter(b -> b.getProcessingOrg().equals(user.getProcessingOrg()))
							.collect(Collectors.toSet());
				}
			}
			if (value instanceof Book) {
				Book book = (Book) value;
				if (!book.getProcessingOrg().equals(user.getProcessingOrg())) {
					value = null;
				}
			}
		}
		return value;
	}

}