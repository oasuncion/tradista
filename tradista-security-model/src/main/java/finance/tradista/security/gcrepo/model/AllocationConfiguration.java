package finance.tradista.security.gcrepo.model;

import java.util.Set;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.legalentity.model.LegalEntity;

/*
 * Copyright 2024 Olivier Asuncion
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
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OaF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

public class AllocationConfiguration extends TradistaObject implements Comparable<AllocationConfiguration> {

	private static final long serialVersionUID = -1407477512383084643L;

	@Id
	private String name;

	@Id
	private LegalEntity processingOrg;

	private Set<Book> books;

	public AllocationConfiguration(String name, LegalEntity po) {
		this.name = name;
		this.processingOrg = po;
	}

	public String getName() {
		return name;
	}

	public LegalEntity getProcessingOrg() {
		return processingOrg;
	}

	@SuppressWarnings("unchecked")
	public Set<Book> getBooks() {
		return (Set<Book>) TradistaModelUtil.deepCopy(books);
	}

	public void setBooks(Set<Book> books) {
		this.books = books;
	}

	@Override
	public int compareTo(AllocationConfiguration ac) {
		return name.compareTo(ac.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public AllocationConfiguration clone() {
		AllocationConfiguration allocConfig = (AllocationConfiguration) super.clone();
		allocConfig.setBooks((Set<Book>) TradistaModelUtil.deepCopy(books));
		allocConfig.processingOrg = TradistaModelUtil.clone(processingOrg);
		return allocConfig;
	}

	@Override
	public String toString() {
		return name;
	}
}