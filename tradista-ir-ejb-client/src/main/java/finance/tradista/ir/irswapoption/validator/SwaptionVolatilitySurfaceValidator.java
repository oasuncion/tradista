package finance.tradista.ir.irswapoption.validator;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.ir.irswapoption.model.SwaptionVolatilitySurface;

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

public class SwaptionVolatilitySurfaceValidator {

	public void validateSurface(SwaptionVolatilitySurface surface)
			throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (surface == null) {
			throw new TradistaBusinessException("The surface cannot be null.%n");
		}
		if (surface.getName() == null) {
			errMsg.append(String.format("The surface name is mandatory.%n"));
		}
		if (surface.isGenerated()) {
			if (surface.getAlgorithm() == null) {
				errMsg.append(String
						.format("The algorithm is mandatory when the surface is generated.%n"));
			}
			if (surface.getInterpolator() == null) {
				errMsg.append(String
						.format("The interpolator is mandatory when the surface is generated.%n"));
			}
			if (surface.getInstance() == null) {
				errMsg.append(String
						.format("The instance is mandatory when the surface is generated.%n"));
			}
			if (surface.getQuoteDate() == null) {
				errMsg.append(String
						.format("The quote date is mandatory when the surface is generated.%n"));
			}
			if (surface.getQuoteSet() == null) {
				errMsg.append(String
						.format("The quote set is mandatory when the surface is generated.%n"));
			}
			if (surface.getQuotes() == null || surface.getQuotes().isEmpty()) {
				errMsg.append(String
						.format("At least one quote must be selected when the surface is generated.%n"));
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}
