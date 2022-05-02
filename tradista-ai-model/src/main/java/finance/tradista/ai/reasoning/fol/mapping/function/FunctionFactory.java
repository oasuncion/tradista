package finance.tradista.ai.reasoning.fol.mapping.function;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.ai.reasoning.common.model.Function;
import finance.tradista.ai.reasoning.common.model.NPVFXGTEFunction;
import finance.tradista.core.common.exception.TradistaBusinessException;

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

public final class FunctionFactory {

	public static Function<?> createFunction(String functionName) throws TradistaBusinessException {
		if (StringUtils.isBlank(functionName)) {
			throw new TradistaBusinessException("Error: trying to create a function from a null or empty name.");
		}
		if (functionName.startsWith(NPVFXGTEFunction.NPV_FX_GTE)) {
			String stringThreshold = functionName.split("_")[functionName.split("_").length - 1].replace("dot", ".");
			BigDecimal threshold = new BigDecimal(stringThreshold);
			return new NPVFXGTEFunction(threshold);
		}
		return null;
	}

}
