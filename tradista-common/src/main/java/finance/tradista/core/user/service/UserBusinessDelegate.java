package finance.tradista.core.user.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.legalentity.model.LegalEntity;
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

public class UserBusinessDelegate {

	private UserService userService;

	public UserBusinessDelegate() {
		userService = TradistaServiceLocator.getInstance().getUserService();
	}

	public long saveUser(User user) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (StringUtils.isBlank(user.getFirstName())) {
			errMsg.append(String.format("The first name cannot be empty.%n"));
		} else {
			if (user.getFirstName().length() > 20) {
				errMsg.append("The first name cannot exceed 20 characters.");
			}
		}
		if (StringUtils.isBlank(user.getSurname())) {
			errMsg.append(String.format("The first name cannot be empty.%n"));
		} else {
			if (user.getSurname().length() > 20) {
				errMsg.append("The surname cannot exceed 20 characters.");
			}
		}
		if (StringUtils.isBlank(user.getLogin())) {
			errMsg.append(String.format("The login cannot be empty.%n"));
		} else {
			if (user.getLogin().length() > 20) {
				errMsg.append("The login cannot exceed 20 characters.");
			}
		}
		if (StringUtils.isBlank(user.getPassword())) {
			errMsg.append(String.format("The password cannot be empty.%n"));
		} else {
			if (user.getPassword().length() > 20) {
				errMsg.append("The password cannot exceed 20 characters.");
			}
		}
		if (user.getProcessingOrg() == null) {
			errMsg.append(String.format("The processing org cannot be null.%n"));
		} else if (!user.getProcessingOrg().getRole().equals(LegalEntity.Role.PROCESSING_ORG)) {
			errMsg.append(String.format("The user legal entity must be a %s.%n", LegalEntity.Role.PROCESSING_ORG));
		}

		return SecurityUtil.runEx(() -> userService.saveUser(user));
	}

	public Set<User> getAllUsers() {
		return SecurityUtil.run(() -> userService.getAllUsers());
	}

	public Set<User> getUsersBySurname(String surname) throws TradistaBusinessException {
		if (StringUtils.isBlank(surname)) {
			throw new TradistaBusinessException("The surname cannot be empty.");
		}
		return SecurityUtil.run(() -> userService.getUsersBySurname(surname));
	}

	public User getUserById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException(String.format("The id (%s) must be positive.", id));
		}
		return SecurityUtil.run(() -> userService.getUserById(id));
	}

	public User login(String login, String password) throws TradistaBusinessException {
		User user;
		StringBuilder errMsg = new StringBuilder();
		if (StringUtils.isBlank(login)) {
			errMsg.append(String.format("The login cannot be empty.%n"));
		}
		if (StringUtils.isBlank(password)) {
			errMsg.append(String.format("The password cannot be empty."));
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		SecurityUtil.setCredential(login, password);
		user = SecurityUtil.run(() -> userService.getUserByLogin(login));
		ClientUtil.setCurrentUser(user);
		return user;
	}

	public User getUserByLogin(String login) {
		return SecurityUtil.run(() -> userService.getUserByLogin(login));
	}

}