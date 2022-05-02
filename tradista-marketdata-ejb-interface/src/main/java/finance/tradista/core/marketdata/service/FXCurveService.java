package finance.tradista.core.marketdata.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.marketdata.model.FXCurve;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.RatePoint;

/*
 * Copyright 2016 Olivier Asuncion
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

@Remote
public interface FXCurveService {

	Set<FXCurve> getAllFXCurves();

	Set<String> getAllGenerationAlgorithms();

	Set<String> getAllInterpolators();

	FXCurve getFXCurveById(long id);

	List<RatePoint> getFXCurvePointsByCurveIdAndDate(long id, Year year, Month month) throws TradistaBusinessException;

	boolean saveFXCurvePoints(long id, List<RatePoint> ratePoints, Year year, Month month)
			throws TradistaBusinessException;

	long saveFXCurve(FXCurve curve) throws TradistaBusinessException;

	boolean deleteFXCurve(long curveId) throws TradistaBusinessException;

	List<RatePoint> getFXCurvePointsByCurveIdAndDates(long curveId, LocalDate min, LocalDate max);

	List<RatePoint> getFXCurvePointsByCurveId(long curveId);

	List<RatePoint> generate(String algorithm, String interpolator, String instance, LocalDate quoteDate,
			QuoteSet quoteSet, Currency primaryCurrency, Currency quoteCurrency,
			InterestRateCurve primaryCurrencyIRCurve, InterestRateCurve quoteCurrencyIRCurve)
			throws TradistaBusinessException;

}