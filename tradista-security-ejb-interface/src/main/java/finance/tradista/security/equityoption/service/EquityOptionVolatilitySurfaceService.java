package finance.tradista.security.equityoption.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import jakarta.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.SurfacePoint;
import finance.tradista.security.equityoption.model.EquityOptionVolatilitySurface;

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
public interface EquityOptionVolatilitySurfaceService {

	Set<EquityOptionVolatilitySurface> getAllEquityOptionVolatilitySurfaces();

	EquityOptionVolatilitySurface getEquityOptionVolatilitySurfaceByName(String name);

	boolean saveEquityOptionVolatilitySurfacePoints(long id,
			List<SurfacePoint<Long, BigDecimal, BigDecimal>> ratePoints, Long optionExpiry, BigDecimal delta);

	long saveEquityOptionVolatilitySurface(EquityOptionVolatilitySurface surface) throws TradistaBusinessException;

	boolean deleteEquityOptionVolatilitySurface(long surfaceId) throws TradistaBusinessException;

	BigDecimal getVolatility(String volativitycurveName, int maturity, double tenor);

	List<SurfacePoint<Number, Number, Number>> getEquityOptionVolatilitySurfacePointsBySurfaceIdOptionExpiryAndStrike(
			long surfaceId, Long optionExpiry, BigDecimal strike);

	List<SurfacePoint<Integer, BigDecimal, BigDecimal>> generate(String value, String value2, String value3,
			LocalDate quoteDate, QuoteSet quoteSet, List<String> quoteNames, List<BigDecimal> strikes);

	Set<String> getAllInstances();

	Set<String> getAllInterpolators();

	Set<String> getAllGenerationAlgorithms();

	List<SurfacePoint<Long, BigDecimal, BigDecimal>> getEquityOptionVolatilitySurfacePointsBySurfaceId(
			long volatilitySurfaceId);

	BigDecimal getVolatility(String volatilitySurfaceName, int time);

	EquityOptionVolatilitySurface getEquityOptionVolatilitySurfaceById(long id);

}