package finance.tradista.core.marketdata.requestpreparator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.TradistaUtil;

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

public class RequestPreparatorFactory {

	private static RequestPreparator bloombergRequestPreparator;

	private RequestPreparatorFactory() {
	}

	public static RequestPreparator createRequestPreparator(String provider) throws TradistaBusinessException {
		if (StringUtils.isEmpty(provider)) {
			throw new TradistaBusinessException("The provider is mandatory.");
		}
		switch (provider) {
		case "BloombergServerAPI":
		case "BloombergBPipe": {
			if (bloombergRequestPreparator == null) {
				List<Class<RequestPreparator>> klasses = TradistaUtil.getAllClassesByType(RequestPreparator.class,
						"finance.tradista.core.marketdata.bloombergapi.requestpreparator");
				bloombergRequestPreparator = TradistaUtil.getInstance(klasses.get(0));
			}
			return bloombergRequestPreparator;
		}
		}
		return null;
	}

}
