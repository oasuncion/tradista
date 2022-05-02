package finance.tradista.ir.irswapoption.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.marketdata.model.Generable;
import finance.tradista.core.marketdata.model.SurfacePoint;
import finance.tradista.core.marketdata.model.VolatilitySurface;

/*
 * Copyright 2014 Olivier Asuncion
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

public class SwaptionVolatilitySurface extends VolatilitySurface<Integer, Integer, BigDecimal> implements Generable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2717361966579455059L;

	public SwaptionVolatilitySurface(String name) {
		super(name);
	}

	public SwaptionVolatilitySurface() {
		super();
	}

	public BigDecimal getVolatilityByOptionExpiryAndSwapTenor(int optionExpiry, int swapTenor)
			throws TradistaBusinessException {
		if (points == null) {
			throw new TradistaBusinessException("The surface points list is null.");
		} else if (points.isEmpty()) {
			throw new TradistaBusinessException("The surface points list is empty.");
		}

		List<BigDecimal> volats = new ArrayList<BigDecimal>();

		for (SurfacePoint<Integer, Integer, BigDecimal> p : points) {
			if (p.getxAxis() == optionExpiry && p.getyAxis() == swapTenor) {
				volats.add(p.getzAxis());
			}
		}

		if (volats.isEmpty()) {
			throw new TradistaBusinessException(String.format(
					"No values found for this option expiry: % and this swap tenor: %s", optionExpiry, swapTenor));
		}

		BigDecimal avgVolat = BigDecimal.valueOf(0);

		for (BigDecimal v : volats) {
			avgVolat = avgVolat.add(v);
		}

		return avgVolat.divide(BigDecimal.valueOf(volats.size()), RoundingMode.HALF_EVEN);

	}

}