package finance.tradista.ai.reasoning.prm.probability.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import finance.tradista.ai.reasoning.prm.probability.model.ComplexProbabilityDistribution;
import finance.tradista.ai.reasoning.prm.probability.model.ContinuousProbabilityDistribution;
import finance.tradista.ai.reasoning.prm.probability.model.NormalProbabilityDistribution;
import finance.tradista.ai.reasoning.prm.probability.model.ProbabilityDistribution;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;

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

public class ProbabilityDistributionSQL {

	public static long saveProbabilityDistribution(ProbabilityDistribution probabilityDistribution) {

		long probabilityDistributionId;
		if (probabilityDistribution instanceof ComplexProbabilityDistribution) {
			probabilityDistributionId = ComplexProbabilityDistributionSQL
					.saveComplexProbabilityDistribution((ComplexProbabilityDistribution) probabilityDistribution);
		} else {
			// TODO So far, only continuous probability distribution are managed
			// Question: do we keep the 'instanceof' mechanism ?
			probabilityDistributionId = NormalProbabilityDistributionSQL
					.saveNormalProbabilityDistribution((ContinuousProbabilityDistribution) probabilityDistribution);
		}
		return probabilityDistributionId;
	}

	public static boolean deleteProbabilityDistribution(ProbabilityDistribution probabilityDistribution) {

		boolean bDeleted = false;
		if (probabilityDistribution instanceof ComplexProbabilityDistribution) {
			bDeleted = ComplexProbabilityDistributionSQL
					.deleteComplexProbabilityDistribution((ComplexProbabilityDistribution) probabilityDistribution);
		} else {
			// TODO So far, only continuous probability distribution are managed
			// Question: do we keep the 'instanceof' mechanism ?
			bDeleted = NormalProbabilityDistributionSQL
					.deleteNormalProbabilityDistribution(probabilityDistribution.getId());
		}
		return bDeleted;
	}

	public static ProbabilityDistribution getProbabilityDistributionById(long id) {
		ProbabilityDistribution probabilityDistribution = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetProbabilityDistributionById = con.prepareStatement(
						"SELECT * FROM PROBABILITY_DISTRIBUTION WHERE ID = ? LEFT OUTER JOIN COMPLEX_PROBABILITY_DISTRIBUTION ON COMPLEX_PROBABILITY_DISTRIBUTION_ID = ID LEFT OUTER JOIN CONTINUOUS_PROBABILITY_DISTRIBUTION ON CONTINUOUS_PROBABILITY_DISTRIBUTION_ID = ID")) {
			stmtGetProbabilityDistributionById.setLong(1, id);
			try (ResultSet results = stmtGetProbabilityDistributionById.executeQuery()) {

				while (results.next()) {
					if (results.getLong("complex_probability_distribution_id") != 0) {
						probabilityDistribution = new ComplexProbabilityDistribution();
						probabilityDistribution = ComplexProbabilityDistributionSQL
								.getComplexProbabilityDistributionById(
										results.getLong("complex_probability_distribution_id"));
					} else {
						probabilityDistribution = new NormalProbabilityDistribution();
						probabilityDistribution = NormalProbabilityDistributionSQL
								.getNormalProbabilityDistributionById(results.getLong("id"));
					}

				}
			}

		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return probabilityDistribution;
	}

}