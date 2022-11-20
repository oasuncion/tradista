package finance.tradista.core.marketdata.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Set;

import jakarta.ejb.Remote;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.Curve;
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
public interface CurveService {
	
	public Curve<LocalDate, BigDecimal> getCurveById(long curveId);

	public List<RatePoint> getCurvePointsByCurveAndDates(Curve<LocalDate, BigDecimal> curve, LocalDate min,
			LocalDate max);

	public Set<Curve<? extends LocalDate, ? extends BigDecimal>> getAllCurves();

	public List<RatePoint> getCurvePointsByCurveAndDate(Curve<? extends LocalDate, ? extends BigDecimal> curve,
			Year year, Month month) throws TradistaBusinessException;

	public boolean saveCurvePoints(Curve<? extends LocalDate, ? extends BigDecimal> curve, List<RatePoint> ratePoints,
			Year year, Month month) throws TradistaBusinessException;

	public Set<String> getAllCurveTypes();

	public long saveCurve(Curve<LocalDate, BigDecimal> curve) throws TradistaBusinessException;

	public boolean deleteCurve(Curve<? extends LocalDate, ? extends BigDecimal> curve) throws TradistaBusinessException;
}
