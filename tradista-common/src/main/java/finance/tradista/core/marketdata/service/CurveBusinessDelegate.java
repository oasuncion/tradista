package finance.tradista.core.marketdata.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.marketdata.model.Curve;
import finance.tradista.core.marketdata.model.RatePoint;

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

public class CurveBusinessDelegate {

	private CurveService curveService;

	public CurveBusinessDelegate() {
		curveService = TradistaServiceLocator.getInstance().getCurveService();
	}

	public Curve<LocalDate, BigDecimal> getCurveById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The curve id must be positive.");
		}
		return SecurityUtil.run(() -> curveService.getCurveById(id));
	}

	public Set<Curve<? extends LocalDate, ? extends BigDecimal>> getAllCurves() {
		return SecurityUtil.run(() -> curveService.getAllCurves());
	}

	public List<RatePoint> getCurvePointsByCurveAndDates(Curve<LocalDate, BigDecimal> curve, LocalDate min,
			LocalDate max) throws TradistaBusinessException {
		if (curve == null) {
			throw new TradistaBusinessException("The curve cannot be null.");
		}
		return SecurityUtil.run(() -> curveService.getCurvePointsByCurveAndDates(curve, min, max));
	}

	public List<RatePoint> getCurvePointsByCurveAndDate(Curve<? extends LocalDate, ? extends BigDecimal> curve,
			Year year, Month month) throws TradistaBusinessException {
		if (curve == null) {
			throw new TradistaBusinessException("The curve cannot be null.");
		}
		return SecurityUtil.runEx(() -> curveService.getCurvePointsByCurveAndDate(curve, year, month));
	}

	public boolean saveCurvePoints(Curve<? extends LocalDate, ? extends BigDecimal> curve, List<RatePoint> ratePoints,
			Year year, Month month) throws TradistaBusinessException {
		if (curve == null) {
			throw new TradistaBusinessException("The curve cannot be null.");
		}
		return SecurityUtil.runEx(() -> curveService.saveCurvePoints(curve, ratePoints, year, month));
	}

	public Set<String> getAllCurveTypes() {
		return SecurityUtil.run(() -> curveService.getAllCurveTypes());
	}

	public long saveCurve(Curve<LocalDate, BigDecimal> curve) throws TradistaBusinessException {
		if (curve == null) {
			throw new TradistaBusinessException("The curve cannot be null.");
		}
		return SecurityUtil.runEx(() -> curveService.saveCurve(curve));
	}

	public boolean deleteCurve(Curve<? extends LocalDate, ? extends BigDecimal> curve) throws TradistaBusinessException {
		if (curve == null) {
			throw new TradistaBusinessException("The curve cannot be null.");
		} else if (curve.getId() <= 0) {
			throw new TradistaBusinessException("The curve id must be positive.");
		}
		return SecurityUtil.runEx(() -> curveService.deleteCurve(curve));
	}

}