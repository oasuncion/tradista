package finance.tradista.security.equityoption.pricer;

import java.math.BigDecimal;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.security.equityoption.model.EquityOptionTrade;

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

public final class PricerEquityOptionUtil {

	public static BigDecimal getProfitAfterExercice(EquityOptionTrade equityOptionTrade)
			throws TradistaBusinessException {

		if (equityOptionTrade == null) {
			throw new TradistaBusinessException("The equity Option trade is mandatory.");
		}

		if (equityOptionTrade.getExerciseDate() == null) {
			// Log warn
			return BigDecimal.valueOf(0);
		}

		if (equityOptionTrade.isCall()) {
			if (equityOptionTrade.getEquityOption() != null) {
				return equityOptionTrade.getStrike().multiply(equityOptionTrade.getEquityOption().getQuantity())
						.negate();
			}
			return equityOptionTrade.getStrike().negate();
		}

		if (equityOptionTrade.isPut()) {
			if (equityOptionTrade.getEquityOption() != null) {
				return equityOptionTrade.getStrike().multiply(equityOptionTrade.getEquityOption().getQuantity());
			}
			return equityOptionTrade.getStrike();
		}

		return null;
	}

}