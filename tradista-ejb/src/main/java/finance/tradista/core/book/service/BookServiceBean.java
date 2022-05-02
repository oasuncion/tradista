package finance.tradista.core.book.service;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.persistence.BookSQL;
import finance.tradista.core.book.service.BookFilteringInterceptor;
import finance.tradista.core.book.service.BookService;
import finance.tradista.core.common.exception.TradistaBusinessException;

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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class BookServiceBean implements BookService {

	@Interceptors(BookFilteringInterceptor.class)
	@Override
	public Set<Book> getAllBooks() {
		return BookSQL.getAllBooks();
	}

	@Override
	public long saveBook(Book book) throws TradistaBusinessException {
		if (book.getId() == 0) {
			checkBookExistence(book);
		} else {
			Book oldBook = BookSQL.getBookById(book.getId());
			if (!oldBook.getName().equals(book.getName())
					|| !oldBook.getProcessingOrg().equals(book.getProcessingOrg())) {
				checkBookExistence(book);
			}
		}
		return BookSQL.saveBook(book);
	}

	private void checkBookExistence(Book book) throws TradistaBusinessException {
		if (BookSQL.bookExists(book.getName(), book.getProcessingOrg().getId())) {
			throw new TradistaBusinessException(
					String.format("A book with the name '%s' and the PO '%s' already exists in the system.",
							book.getName(), book.getProcessingOrg().getShortName()));
		}
	}

	@Interceptors(BookFilteringInterceptor.class)
	@Override
	public Book getBookByName(String name) {
		return BookSQL.getBookByName(name);
	}

	@Interceptors(BookFilteringInterceptor.class)
	@Override
	public Book getBookById(long id) {
		return BookSQL.getBookById(id);
	}

}