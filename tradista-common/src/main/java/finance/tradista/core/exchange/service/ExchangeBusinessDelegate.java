package finance.tradista.core.exchange.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.exchange.model.Exchange;

/*
 * Copyright 2018 Olivier Asuncion
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

public class ExchangeBusinessDelegate {

	private ExchangeService exchangeService;

	public ExchangeBusinessDelegate() {
		exchangeService = TradistaServiceLocator.getInstance().getExchangeService();
	}

	public Set<Exchange> getAllExchanges() {
		return SecurityUtil.run(() -> exchangeService.getAllExchanges());
	}

	public Exchange getExchangeById(long id) {
		return SecurityUtil.run(() -> exchangeService.getExchangeById(id));
	}

	public Exchange getExchangeByCode(String code) {
		return SecurityUtil.run(() -> exchangeService.getExchangeByCode(code));
	}

	public long saveExchange(Exchange exchange) throws TradistaBusinessException {
		if (StringUtils.isBlank(exchange.getCode())) {
			throw new TradistaBusinessException("The code cannot be empty.");
		}
		return SecurityUtil.runEx(() -> exchangeService.saveExchange(exchange));
	}

}
