package finance.tradista.core.marketdata.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.service.InformationBusinessDelegate;
import finance.tradista.core.marketdata.model.VolatilitySurface;
import finance.tradista.core.marketdata.persistence.SurfaceSQL;
import finance.tradista.core.marketdata.service.SurfaceService;
import finance.tradista.core.marketdata.service.VolatilitySurfaceFilteringInterceptor;
import finance.tradista.fx.fxoption.model.FXVolatilitySurface;
import finance.tradista.fx.fxoption.service.FXVolatilitySurfaceBusinessDelegate;
import finance.tradista.ir.irswapoption.model.SwaptionVolatilitySurface;
import finance.tradista.ir.irswapoption.service.SwaptionVolatilitySurfaceBusinessDelegate;
import finance.tradista.security.equityoption.model.EquityOptionVolatilitySurface;
import finance.tradista.security.equityoption.service.EquityOptionVolatilitySurfaceBusinessDelegate;

/*
 * Copyright 2019 Olivier Asuncion
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

@Stateless
public class SurfaceServiceBean implements SurfaceService {

	private InformationBusinessDelegate informationBusinessDelegate;

	@PostConstruct
	private void init() {
		informationBusinessDelegate = new InformationBusinessDelegate();
	}

	@Override
	public boolean surfaceExists(VolatilitySurface<?, ?, ?> surface, String type) {
		return SurfaceSQL.surfaceExists(surface, type);
	}

	@Override
	public List<VolatilitySurface<?, ?, ?>> getSurfaces(String surfaceType) {
		List<VolatilitySurface<?, ?, ?>> surfaces = null;
		if (surfaceType == null) {
			if (informationBusinessDelegate.hasFXModule()) {
				Set<FXVolatilitySurface> fxSurfaces = new FXVolatilitySurfaceBusinessDelegate()
						.getAllFXVolatilitySurfaces();
				if (fxSurfaces != null && !fxSurfaces.isEmpty()) {
					surfaces = new ArrayList<VolatilitySurface<?, ?, ?>>();
					surfaces.addAll(fxSurfaces);
				}
			}
			if (informationBusinessDelegate.hasSecurityModule()) {
				Set<EquityOptionVolatilitySurface> equityOptionSurfaces = new EquityOptionVolatilitySurfaceBusinessDelegate()
						.getAllEquityOptionVolatilitySurfaces();
				if (equityOptionSurfaces != null && !equityOptionSurfaces.isEmpty()) {
					if (surfaces == null) {
						surfaces = new ArrayList<VolatilitySurface<?, ?, ?>>();
					}
					surfaces.addAll(equityOptionSurfaces);
				}
			}
			if (informationBusinessDelegate.hasIRModule()) {
				Set<SwaptionVolatilitySurface> swaptionVolatilitySurfaces = new SwaptionVolatilitySurfaceBusinessDelegate()
						.getAllSwaptionVolatilitySurfaces();
				if (swaptionVolatilitySurfaces != null && !swaptionVolatilitySurfaces.isEmpty()) {
					if (surfaces == null) {
						surfaces = new ArrayList<VolatilitySurface<?, ?, ?>>();
					}
					surfaces.addAll(swaptionVolatilitySurfaces);
				}
			}
		} else if (surfaceType.equals("IR")) {
			if (informationBusinessDelegate.hasIRModule()) {
				Set<SwaptionVolatilitySurface> swaptionVolatilitySurfaces = new SwaptionVolatilitySurfaceBusinessDelegate()
						.getAllSwaptionVolatilitySurfaces();
				if (swaptionVolatilitySurfaces != null && !swaptionVolatilitySurfaces.isEmpty()) {
					surfaces = new ArrayList<VolatilitySurface<?, ?, ?>>();
					surfaces.addAll(swaptionVolatilitySurfaces);
				}
			}
		} else if (surfaceType.equals("FX")) {
			if (informationBusinessDelegate.hasFXModule()) {
				Set<FXVolatilitySurface> fxSurfaces = new FXVolatilitySurfaceBusinessDelegate()
						.getAllFXVolatilitySurfaces();
				if (fxSurfaces != null && !fxSurfaces.isEmpty()) {
					surfaces = new ArrayList<VolatilitySurface<?, ?, ?>>();
					surfaces.addAll(fxSurfaces);
				}
			}
		} else if (surfaceType.equals("EquityOption")) {
			if (informationBusinessDelegate.hasSecurityModule()) {
				Set<EquityOptionVolatilitySurface> equityOptionSurfaces = new EquityOptionVolatilitySurfaceBusinessDelegate()
						.getAllEquityOptionVolatilitySurfaces();
				if (equityOptionSurfaces != null && !equityOptionSurfaces.isEmpty()) {
					surfaces = new ArrayList<VolatilitySurface<?, ?, ?>>();
					surfaces.addAll(equityOptionSurfaces);
				}
			}
		}
		return surfaces;
	}

	@Interceptors(VolatilitySurfaceFilteringInterceptor.class)
	@Override
	public VolatilitySurface<?, ?, ?> getSurfaceById(long id) throws TradistaBusinessException {
		VolatilitySurface<?, ?, ?> surface = null;
		if (informationBusinessDelegate.hasFXModule()) {
			surface = new FXVolatilitySurfaceBusinessDelegate().getFXVolatilitySurfaceById(id);
		}
		if (surface == null) {
			if (informationBusinessDelegate.hasSecurityModule()) {
				surface = new EquityOptionVolatilitySurfaceBusinessDelegate().getEquityOptionVolatilitySurfaceById(id);
			}
		}
		if (surface == null) {
			if (informationBusinessDelegate.hasIRModule()) {
				surface = new SwaptionVolatilitySurfaceBusinessDelegate().getSwaptionVolatilitySurfaceById(id);
			}
		}

		return surface;
	}

}