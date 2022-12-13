package finance.tradista.core.marketdata.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.marketdata.model.VolatilitySurface;

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

public class SurfaceBusinessDelegate {

	private SurfaceService surfaceService;

	public SurfaceBusinessDelegate() {
		surfaceService = TradistaServiceLocator.getInstance().getSurfaceService();
	}

	public boolean surfaceExists(VolatilitySurface<?, ?, ?> surface, String type) throws TradistaBusinessException {
		if (surface == null) {
			throw new TradistaBusinessException("The surface is mandatory.");
		}
		if (StringUtils.isBlank(type)) {
			throw new TradistaBusinessException("The type is mandatory.");
		}
		return SecurityUtil.run(() -> surfaceService.surfaceExists(surface, type));
	}

	public List<VolatilitySurface<?, ?, ?>> getSurfaces(String surfaceType) {
		return SecurityUtil.run(() -> surfaceService.getSurfaces(surfaceType));
	}

	public VolatilitySurface<?, ?, ?> getSurfaceById(long surfaceId) throws TradistaBusinessException {
		if (surfaceId <= 0) {
			throw new TradistaBusinessException("The surface id must be positive.");
		}
		return SecurityUtil.runEx(() -> surfaceService.getSurfaceById(surfaceId));
	}

}