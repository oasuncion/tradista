package finance.tradista.security.bond.service;

import java.time.LocalDate;
import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.persistence.BondSQL;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.service.EquityServiceBean;

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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class BondServiceBean implements BondService {

	@Override
	@Interceptors(BondProductScopeFilteringInterceptor.class)
	public long saveBond(Bond bond) throws TradistaBusinessException {
		if (bond.getId() == 0) {
			checkIsinExistence(bond);
			return BondSQL.saveBond(bond);
		} else {
			Bond oldBond = BondSQL.getBondById(bond.getId());
			if (!bond.getIsin().equals(oldBond.getIsin())) {
				checkIsinExistence(bond);
			}
			return BondSQL.saveBond(bond);
		}
	}

	private void checkIsinExistence(Bond bond) throws TradistaBusinessException {
		if (new BondServiceBean().getBondByIsinAndExchangeCode(bond.getIsin(), bond.getExchange().getCode()) != null) {
			throw new TradistaBusinessException(String.format("This bond '%s' already exists in the exchange %s.",
					bond.getIsin(), bond.getExchange().getCode()));
		} else {
			Set<Equity> equities = new EquityServiceBean().getEquitiesByIsin(bond.getIsin());
			if (equities != null && !equities.isEmpty()) {
				throw new TradistaBusinessException(
						String.format("There is already an equity with the same ISIN %s.", bond.getIsin()));
			}
		}
	}

	@Override
	public Set<Bond> getBondsByCreationDate(LocalDate date) {
		return BondSQL.getBondsByCreationDate(date);
	}

	@Override
	public Set<Bond> getAllBonds() {
		return BondSQL.getAllBonds();
	}

	@Override
	public Bond getBondById(long id) {
		return BondSQL.getBondById(id);
	}

	@Override
	public Set<Bond> getBondsByDates(LocalDate minCreationDate, LocalDate maxCreationDate, LocalDate minMaturityDate,
			LocalDate maxMaturityDate) {
		return BondSQL.getBondsByDates(minCreationDate, maxCreationDate, minMaturityDate, maxMaturityDate);
	}

	@Override
	public Set<Bond> getBondsByIsin(String isin) {
		return BondSQL.getBondsByIsin(isin);
	}

	@Override
	public Bond getBondByIsinAndExchangeCode(String isin, String exchangeCode) {
		return BondSQL.getBondByIsinAndExchangeCode(isin, exchangeCode);
	}

}
