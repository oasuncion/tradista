package finance.tradista.ai.reasoning.prm.probability.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

public class NormalProbabilityDistributionSQL {

	public static long saveNormalProbabilityDistribution(ContinuousProbabilityDistribution probabilityDistribution) {
		long normalProbabilityDistributionId = 0;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSaveProbabilityDistribution = (probabilityDistribution.getId() == 0)
						? con.prepareStatement("INSERT INTO PROBABILITY_DISTRIBUTION() VALUES () ",
								Statement.RETURN_GENERATED_KEYS)
						: null;
				PreparedStatement stmtSaveNormalProbabilityDistribution = (probabilityDistribution.getId() == 0)
						? con.prepareStatement(
								"INSERT INTO NORMAL_PROBABILITY_DISTRIBUTION (MEAN,VARIANCE) VALUES (?,?)")
						: con.prepareStatement(
								"UPDATE NORMAL_PROBABILITY_DISTRIBUTION SET MEAN = ?, VARIANCE = ? WHERE PROBABILITY_DISTRIBUTION = ?")) {
			if (probabilityDistribution.getId() == 0) {
				stmtSaveProbabilityDistribution.executeUpdate();
				try (ResultSet generatedKeys = stmtSaveProbabilityDistribution.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						normalProbabilityDistributionId = generatedKeys.getLong(1);
						probabilityDistribution.setId(normalProbabilityDistributionId);
					} else {
						throw new SQLException(
								"Creating normal probability distribution failed, no generated key obtained.");
					}
				}
			} else {
				normalProbabilityDistributionId = probabilityDistribution.getId();
				stmtSaveNormalProbabilityDistribution.setLong(3, probabilityDistribution.getId());
			}
			stmtSaveNormalProbabilityDistribution.setBigDecimal(1,
					new BigDecimal(probabilityDistribution.getRealDistributon().getNumericalMean()));
			stmtSaveNormalProbabilityDistribution.setBigDecimal(2,
					new BigDecimal(probabilityDistribution.getRealDistributon().getNumericalVariance()));
			stmtSaveNormalProbabilityDistribution.executeUpdate();

		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return normalProbabilityDistributionId;
	}

	public static boolean deleteNormalProbabilityDistribution(long probabilityDistributionId) {
		boolean bDeleted = false;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtDeleteNormalProbabilityDistribution = con
						.prepareStatement("DELETE FROM NORMAL_PROBABILITY_DISTRIBUTION WHERE ID = ?) ");
				PreparedStatement stmtDeleteProbabilityDistribution = con
						.prepareStatement("DELETE FROM PROBABILITY_DISTRIBUTION WHERE ID = ?) ")) {
			stmtDeleteNormalProbabilityDistribution.setLong(1, probabilityDistributionId);
			stmtDeleteNormalProbabilityDistribution.executeUpdate();

			stmtDeleteProbabilityDistribution.setLong(1, probabilityDistributionId);
			stmtDeleteProbabilityDistribution.executeUpdate();

			bDeleted = true;
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}

		return bDeleted;
	}

	public static ProbabilityDistribution getNormalProbabilityDistributionById(long id) {
		NormalProbabilityDistribution normalProbabilityDistribution = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetNormalProbabilityDistributionById = con.prepareStatement(
						"SELECT * FROM NORMAL_PROBABILITY_DISTRIBUTION WHERE PROBABILITY_DISTRIBUTION_ID = ?")) {
			stmtGetNormalProbabilityDistributionById.setLong(1, id);
			try (ResultSet results = stmtGetNormalProbabilityDistributionById.executeQuery()) {
				while (results.next()) {
					double sd = Math.sqrt(results.getBigDecimal("variance").doubleValue());
					double mean = results.getBigDecimal("mean").doubleValue();
					normalProbabilityDistribution = new NormalProbabilityDistribution((short) mean, (short) sd);
					normalProbabilityDistribution.setId(results.getLong("probability_distribution_id"));
				}
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return normalProbabilityDistribution;
	}

}