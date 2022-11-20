package finance.tradista.core.currency.service;

import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.persistence.CurrencySQL;
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

@Stateless
public class CurrencyServiceBean implements CurrencyService {

	@Override
	public Set<Currency> getAllCurrencies() {
		return CurrencySQL.getAllCurrencies();
	}

	@Override
	public Currency getCurrencyByIsoCode(String isoCode) {
		return CurrencySQL.getCurrencyByIsoCode(isoCode);
	}

	@Override
	public Currency getCurrencyById(long id) {
		return CurrencySQL.getCurrencyById(id);
	}

	@Override
	public long saveCurrency(Currency currency) throws TradistaBusinessException {
		if (currency.getId() == 0) {
			checkIsoCodeExistence(currency);
			checkNameExistence(currency);
		} else {
			Currency oldCurrency = CurrencySQL.getCurrencyById(currency.getId());
			if (!oldCurrency.getIsoCode().equals(currency.getIsoCode())) {
				checkIsoCodeExistence(currency);
			}
			if (!oldCurrency.getName().equals(currency.getName())) {
				checkNameExistence(currency);
			}
		}
		return CurrencySQL.saveCurrency(currency);
	}

	private void checkIsoCodeExistence(Currency currency) throws TradistaBusinessException {
		if (currencyExists(currency.getIsoCode())) {
			throw new TradistaBusinessException(String.format("A currency with ISO code '%s' already exists in the system.",
					currency.getIsoCode()));
		}
	}

	private void checkNameExistence(Currency currency) throws TradistaBusinessException {
		if (getCurrencyByName(currency.getName()) != null) {
			throw new TradistaBusinessException(
					String.format("A currency with name '%s' already exists in the system.", currency.getName()));
		}
	}

	@Override
	public Currency getCurrencyByName(String name) {
		return CurrencySQL.getCurrencyByName(name);
	}

	@Override
	public boolean currencyExists(String isoCode) {
		return CurrencySQL.currencyExists(isoCode);
	}

	@Override
	public Set<Currency> getDeliverableCurrencies() {
		return CurrencySQL.getDeliverableCurrencies();
	}

	@Override
	public Set<Currency> getNonDeliverableCurrencies() {
		return CurrencySQL.getNonDeliverableCurrencies();
	}

}