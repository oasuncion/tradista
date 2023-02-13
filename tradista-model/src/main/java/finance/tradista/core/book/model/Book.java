package finance.tradista.core.book.model;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.legalentity.model.LegalEntity;

/*
 * Copyright 2015 Olivier Asuncion
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

public class Book extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8145550319443892466L;

	@Id
	private String name;

	@Id
	private LegalEntity processingOrg;

	private String description;

	public Book(String name, LegalEntity processingOrg) {
		super();
		this.name = name;
		this.processingOrg = processingOrg;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LegalEntity getProcessingOrg() {
		return TradistaModelUtil.clone(processingOrg);
	}

	@Override
	public Book clone() {
		Book book = (Book) super.clone();
		book.processingOrg = TradistaModelUtil.clone(processingOrg);
		return book;
	}

	public String toString() {
		return name;
	}

}