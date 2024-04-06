package finance.tradista.core.user.model;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.legalentity.model.LegalEntity;

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

public class User extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7677381398422965047L;

	@Id
	private String firstName;

	@Id
	private String surname;

	@Id
	private LegalEntity processingOrg;

	public User(String firstName, String surname, LegalEntity processingOrg) {
		this.firstName = firstName;
		this.surname = surname;
		this.processingOrg = processingOrg;
	}

	private String login;

	private String password;

	public String getFirstName() {
		return firstName;
	}

	public String getSurname() {
		return surname;
	}

	public LegalEntity getProcessingOrg() {
		return TradistaModelUtil.clone(processingOrg);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return processingOrg == null;
	}

	@Override
	public User clone() {
		User user = (User) super.clone();
		user.processingOrg = TradistaModelUtil.clone(processingOrg);
		return user;
	}

}