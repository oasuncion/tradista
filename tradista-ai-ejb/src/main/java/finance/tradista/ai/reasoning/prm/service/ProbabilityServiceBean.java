package finance.tradista.ai.reasoning.prm.service;

import finance.tradista.ai.reasoning.prm.probability.model.ProbabilityLaw;
import finance.tradista.ai.reasoning.prm.probability.persistence.ProbabilityLawSQL;
import finance.tradista.core.common.exception.TradistaBusinessException;
import jakarta.ejb.Stateless;

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
public class ProbabilityServiceBean implements ProbabilityService {

	@Override
	public long saveProbabilityLaw(ProbabilityLaw probabilityLaw) throws TradistaBusinessException {
		if (probabilityLaw.getId() == 0) {
			checkFunctionName(probabilityLaw);
			return ProbabilityLawSQL.saveProbabilityLaw(probabilityLaw);
		} else {
			ProbabilityLaw oldProbabilityLaw = ProbabilityLawSQL.getProbabilityLawById(probabilityLaw.getId());
			if (!probabilityLaw.getFunction().getFunction().getName().equals(oldProbabilityLaw.getFunction().getFunction().getName())) {
				checkFunctionName(probabilityLaw);
			}
			return ProbabilityLawSQL.saveProbabilityLaw(probabilityLaw);
		}
	}
	
	private void checkFunctionName(ProbabilityLaw probabilityLaw) throws TradistaBusinessException {
		if (ProbabilityLawSQL.getProbabilityLawByFunctionName(probabilityLaw.getFunction().getFunction().getName()) != null) {
			throw new TradistaBusinessException(String.format(
					"The probability law with function '%s' already exists in the model.",
					probabilityLaw.getFunction().getFunction().getName()));
		} 
	}

}