package finance.tradista.ai.reasoning.common.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.ai.reasoning.common.model.Formula;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;

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

public class FormulaBusinessDelegate {

	private FormulaService formulaService;

	public FormulaBusinessDelegate() {
		formulaService = TradistaServiceLocator.getInstance().getFormulaService();
	}

	public List<Formula> getAllFormulas() {
		return SecurityUtil.run(() -> formulaService.getAllFormulas());
	}

	public boolean saveFormulas(Formula... formulas) throws TradistaBusinessException {
		if (formulas != null && formulas.length > 0) {
			return SecurityUtil.runEx(() -> formulaService.saveFormulas(formulas));
		}
		return false;

	}

	public List<Formula> validateFormulas(Formula... formulas) throws TradistaBusinessException {
		if (formulas != null && formulas.length > 0) {
			return SecurityUtil.runEx(() -> formulaService.validateFormulas(formulas));
		}
		return null;

	}

	public boolean query(String functionName, String... parameters) throws TradistaBusinessException {
		if (StringUtils.isEmpty(functionName)) {
			throw new TradistaBusinessException("The function name is mandatory.");
		}
		return SecurityUtil.runEx(() -> formulaService.query(functionName, parameters));
	}

}