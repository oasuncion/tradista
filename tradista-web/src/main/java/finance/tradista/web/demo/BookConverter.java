package finance.tradista.web.demo;

import java.io.Serializable;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;

/*
 * Copyright 2022 Olivier Asuncion
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

@Named
@ViewScoped
public class BookConverter implements Serializable, Converter<Book> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3802860683043711768L;
	private BookBusinessDelegate bookBusinessDelegate;

	public BookConverter() {
		bookBusinessDelegate = new BookBusinessDelegate();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Book book) {
		return book.toString();
	}

	@Override
	public Book getAsObject(FacesContext context, UIComponent component, String value) {
		Book book = null;
		try {
			book = bookBusinessDelegate.getBookByName(value);
		} catch (TradistaBusinessException tbe) {
			throw new ConverterException(String.format("Could not convert book %s", value), tbe);
		}
		return book;
	}

}