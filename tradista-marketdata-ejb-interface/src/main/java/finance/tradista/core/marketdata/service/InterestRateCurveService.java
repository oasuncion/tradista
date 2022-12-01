package finance.tradista.core.marketdata.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Set;

import jakarta.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.RatePoint;
import finance.tradista.core.marketdata.model.ZeroCouponCurve;

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

@Remote
public interface InterestRateCurveService {

	Set<InterestRateCurve> getAllInterestRateCurves();

	Set<ZeroCouponCurve> getAllZeroCouponCurves();

	Set<String> getAllInterestRateCurveTypes();

	Set<String> getAllGenerationAlgorithms();

	Set<String> getAllInterpolators();

	InterestRateCurve getInterestRateCurveByName(String name);

	InterestRateCurve getInterestRateCurveById(long id);

	List<RatePoint> getInterestRateCurvePointsByCurveIdAndDate(long id, Year year, Month month)
			throws TradistaBusinessException;

	boolean saveInterestRateCurvePoints(long id, List<RatePoint> ratePoints, Year year, Month month)
			throws TradistaBusinessException;

	long saveInterestRateCurve(InterestRateCurve curve) throws TradistaBusinessException;

	boolean deleteInterestRateCurve(long curveId) throws TradistaBusinessException;

	List<RatePoint> getInterestRateCurvePointsByCurveIdAndDates(long curveId, LocalDate min, LocalDate max);

	List<RatePoint> getInterestRateCurvePointsByCurveId(long curveId);

	List<RatePoint> getInterestRateCurvePointsByCurveAndDates(InterestRateCurve curve, LocalDate min, LocalDate max);

	List<RatePoint> generate(String algorithm, String interpolator, String instance, LocalDate quoteDate,
			QuoteSet quoteSet, List<Long> quoteIds);

}