package finance.tradista.ai.agent.service;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.ai.agent.model.Mandate;
import finance.tradista.ai.agent.persistence.MandateSQL;
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
public class MandateServiceBean implements MandateService {

	@Override
	public long saveMandate(Mandate mandate) throws TradistaBusinessException {
		if (mandate.getId() == 0) {
			checkName(mandate);
			return MandateSQL.saveMandate(mandate);
		} else {
			Mandate oldMandate = MandateSQL.getMandateById(mandate.getId());
			if (!mandate.getName().equals(oldMandate.getName())) {
				checkName(mandate);
			}
			return MandateSQL.saveMandate(mandate);
		}
	}

	private void checkName(Mandate mandate) throws TradistaBusinessException {
		if (MandateSQL.getMandateByName(mandate.getName()) != null) {
			throw new TradistaBusinessException(
					String.format("The mandate with name '%s' already exists.", mandate.getName()));
		}
	}
	
	@Override
	public Mandate getMandateById(long id) {
		return MandateSQL.getMandateById(id);
	}

}