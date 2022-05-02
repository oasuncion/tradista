package finance.tradista.core.marketdata.bootstraphandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.Instrument;
import finance.tradista.core.tenor.model.Tenor;

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
public interface BootstrapHandler {
	
	String getInstrumentName();

	String getKeyFromQuoteName(String quoteName);

	Instrument getInstrumentByKey(String key, LocalDate quoteDate) throws TradistaBusinessException;

	boolean isZeroCoupon(Instrument instrument, LocalDate quoteDate);

	LocalDate getMaturityDate(Instrument instrument);

	String getKey(Instrument instrument, LocalDate localDate);

	List<CashFlow> getPendingCashFlows(Instrument instrument, LocalDate date, BigDecimal price, Map<LocalDate, List<BigDecimal>> zeroCouponRates, Map<LocalDate, BigDecimal> interpolatedValues) throws TradistaBusinessException;

	Tenor getFrequency(Instrument instrument);

	/**
	 * Calculates a Zero coupon rate for instruments identified as "Zero Coupon" instruments (ie helper.isZeroCoupon == true)
	 * @param instrument
	 * @param price
	 * @param date
	 * @return
	 */
	BigDecimal calcZeroCoupon(Instrument instrument, BigDecimal price,
			LocalDate date);

	/**
	 * Price used during the zero coupons determination by bootstrapping.
	 * It is used in this equalization :
	 * discounted cfs = price
	 * @param price
	 * @return
	 */
	BigDecimal getPrice(BigDecimal price);

}
