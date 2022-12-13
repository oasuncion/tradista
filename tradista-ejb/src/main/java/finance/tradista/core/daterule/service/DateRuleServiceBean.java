package finance.tradista.core.daterule.service;

import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.daterule.model.DateRule;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;

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
public class DateRuleServiceBean implements DateRuleService {

	@Override
	public Set<DateRule> getAllDateRules() {
		return DateRuleSQL.getAllDateRules();
	}

	@Override
	public DateRule getDateRuleById(long id) {
		return DateRuleSQL.getDateRuleById(id);
	}

	@Override
	public DateRule getDateRuleByName(String name) {
		return DateRuleSQL.getDateRuleByName(name);
	}

	@Override
	public long saveDateRule(DateRule dateRule) throws TradistaBusinessException {
		if (dateRule.getId() == 0) {
			checkNameExistence(dateRule);
		} else {
			DateRule oldDateRule = DateRuleSQL.getDateRuleById(dateRule.getId());
			if (!oldDateRule.getName().equals(dateRule.getName())) {
				checkNameExistence(dateRule);
			}
		}
		return DateRuleSQL.saveDateRule(dateRule);
	}

	private void checkNameExistence(DateRule dateRule) throws TradistaBusinessException {
		if (getDateRuleByName(dateRule.getName()) != null) {
			throw new TradistaBusinessException(
					String.format("A date rule with the name '%s' already exists in the system.", dateRule.getName()));
		}
	}

}