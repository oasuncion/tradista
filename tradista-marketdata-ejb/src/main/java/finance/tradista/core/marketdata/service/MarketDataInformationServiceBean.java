package finance.tradista.core.marketdata.service;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.ejb.Stateless;

import finance.tradista.core.marketdata.service.MarketDataConfigurationBusinessDelegate;
import finance.tradista.core.marketdata.service.MarketDataInformationService;

/*
 * Copyright 2017 Olivier Asuncion
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
public class MarketDataInformationServiceBean implements MarketDataInformationService {

	@Override
	public Map<String, String> getMarketDataModuleVersions() {
		Map<String, String> map = null;
		MarketDataConfigurationBusinessDelegate marketDataConfigurationBusinessDelegate = new MarketDataConfigurationBusinessDelegate();
		Set<String> modules = marketDataConfigurationBusinessDelegate.getModules();
		if (modules != null && !modules.isEmpty()) {
			map = new TreeMap<String, String>();
			for (String m : modules) {
				map.put(m,
						Package.getPackage("finance.tradista.core.marketdata." + m.toLowerCase()).getImplementationVersion());
			}
		}
		return map;
	}

}
