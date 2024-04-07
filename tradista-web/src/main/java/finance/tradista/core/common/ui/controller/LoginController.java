package finance.tradista.core.common.ui.controller;

import java.io.Serializable;

import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.user.service.UserBusinessDelegate;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/*
 * Copyright 2023 Olivier Asuncion
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
public class LoginController implements Serializable {

	private static final long serialVersionUID = -7912603586721092288L;

	private String login;

	private String password;

	private String originalUrl;

	@PostConstruct
	public void init() {
		originalUrl = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("jakarta.servlet.forward.servlet_path");
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

	public String login() {
		final String FACES_REDIRECT_EQUALS_TRUE = "?faces-redirect=true";
		String url = "/pages/gcrepotrade" + FACES_REDIRECT_EQUALS_TRUE;
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			request.login(getLogin(), getPassword());
			ClientUtil.setCurrentUser(
					new UserBusinessDelegate().getUserByLogin(externalContext.getUserPrincipal().getName()));
		} catch (ServletException se) {
			context.addMessage("errMsg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed.", null));
			return null;
		}
		if (originalUrl != null) {
			url = originalUrl + FACES_REDIRECT_EQUALS_TRUE;
		}

		return url;
	}
}