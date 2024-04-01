package finance.tradista.core.user.ui.manager;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.util.ClientUtil;
import finance.tradista.core.legalentity.model.LegalEntity;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

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
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

@Named
@SessionScoped
public class UserManager implements Serializable {

	private static final long serialVersionUID = -3434595366265551240L;

	public String getCurrentUserProcessingOrg() {
		LegalEntity userPo = ClientUtil.getCurrentUser().getProcessingOrg();
		if (userPo != null) {
			return userPo.getShortName();
		}
		return StringUtils.EMPTY;
	}

}