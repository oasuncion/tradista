package finance.tradista.ir.irswapoption.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.RatePoint;
import finance.tradista.core.marketdata.model.SurfacePoint;
import finance.tradista.ir.irswapoption.model.SwaptionVolatilitySurface;

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
public interface SwaptionVolatilitySurfaceService {

	Set<SwaptionVolatilitySurface> getAllSwaptionVolatilitySurfaces();

	SwaptionVolatilitySurface getSwaptionVolatilitySurfaceByName(String name);

	List<RatePoint> getInterestRateCurvePointsByCurveIdAndDate(long id, Integer year, Integer month);

	boolean saveSwaptionVolatilitySurfacePoints(long id, List<SurfacePoint<Float, Float, Float>> ratePoints,
			Float optionLifeTime, Float swapOptionLifetime);

	long saveSwaptionVolatilitySurface(SwaptionVolatilitySurface surface) throws TradistaBusinessException;

	boolean deleteSwaptionVolatilitySurface(long surfaceId) throws TradistaBusinessException;

	List<RatePoint> getInterestRateCurvePointsByCurveNameAndDates(String curveName, Date min, Date max);

	List<RatePoint> getInterestRateCurvePointsByCurveAndDates(InterestRateCurve curve, Date min, Date max);

	BigDecimal getVolatility(String volativitycurveName, int maturity, int tenor);

	List<SurfacePoint<Number, Number, Number>> getSwaptionVolatilitySurfacePointsBySurfaceIdOptionAndSwapLifetimes(
			long currentSwaptionVolatilitySurfaceId, Float optionLifetime, Float swapLifetime);

	List<SurfacePoint<Integer, Integer, BigDecimal>> generate(String value, String value2, String value3,
			LocalDate quoteDate, QuoteSet quoteSet, List<Long> quoteIds);

	Set<String> getAllInstances();

	Set<String> getAllInterpolators();

	Set<String> getAllGenerationAlgorithms();

	List<SurfacePoint<Integer, Integer, BigDecimal>> getSwaptionVolatilitySurfacePointsBySurfaceId(
			long volatilitySurfaceId);

	SwaptionVolatilitySurface getSwaptionVolatilitySurfaceById(long id);

}