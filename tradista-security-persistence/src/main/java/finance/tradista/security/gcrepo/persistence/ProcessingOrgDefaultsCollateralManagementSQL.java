package finance.tradista.security.gcrepo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.persistence.QuoteSetSQL;
import finance.tradista.security.gcrepo.model.AllocationConfiguration;
import finance.tradista.security.gcrepo.model.ProcessingOrgDefaultsCollateralManagementModule;

/*
 * Copyright 2024 Olivier Asuncion
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

public class ProcessingOrgDefaultsCollateralManagementSQL {

	public static void saveProcessingOrgDefaultsModule(Connection con,
			ProcessingOrgDefaultsCollateralManagementModule module, long poId) {
		try (PreparedStatement stmtSaveProcessingOrgDefaultsCollateralManagement = con.prepareStatement(
				"INSERT INTO PROCESSING_ORG_DEFAULTS_COLLATERAL_MANAGEMENT(PROCESSING_ORG_ID, QUOTE_SET_ID, ALLOCATION_CONFIGURATION_ID) VALUES(?, ?, ?)");
				PreparedStatement stmtDeleteProcessingOrgDefaultsCollateralManagement = con.prepareStatement(
						"DELETE FROM PROCESSING_ORG_DEFAULTS_COLLATERAL_MANAGEMENT WHERE PROCESSING_ORG_ID = ?")) {
			QuoteSet qs = module.getQuoteSet();
			AllocationConfiguration allocConfig = module.getAllocationConfiguration();
			stmtDeleteProcessingOrgDefaultsCollateralManagement.setLong(1, poId);
			stmtDeleteProcessingOrgDefaultsCollateralManagement.executeUpdate();
			stmtSaveProcessingOrgDefaultsCollateralManagement.setLong(1, poId);
			if (qs != null) {
				stmtSaveProcessingOrgDefaultsCollateralManagement.setLong(2, qs.getId());
			} else {
				stmtSaveProcessingOrgDefaultsCollateralManagement.setNull(2, Types.BIGINT);
			}
			if (allocConfig != null) {
				stmtSaveProcessingOrgDefaultsCollateralManagement.setLong(3, allocConfig.getId());
			} else {
				stmtSaveProcessingOrgDefaultsCollateralManagement.setNull(3, Types.BIGINT);
			}
			stmtSaveProcessingOrgDefaultsCollateralManagement.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
	}

	public static ProcessingOrgDefaultsCollateralManagementModule getProcessingOrgDefaultsModuleByPoId(Connection con,
			long poId) {

		// ProcessingOrgDefaults modules are always returned, even if not persisted yet.
		ProcessingOrgDefaultsCollateralManagementModule module = new ProcessingOrgDefaultsCollateralManagementModule();

		try (PreparedStatement stmtGetProcessingOrgDefaultsCollateralManagementByProcessingOrgDefaultsId = con
				.prepareStatement(
						"SELECT * FROM PROCESSING_ORG_DEFAULTS_COLLATERAL_MANAGEMENT WHERE PROCESSING_ORG_ID = ?")) {
			stmtGetProcessingOrgDefaultsCollateralManagementByProcessingOrgDefaultsId.setLong(1, poId);
			try (ResultSet results = stmtGetProcessingOrgDefaultsCollateralManagementByProcessingOrgDefaultsId
					.executeQuery()) {
				while (results.next()) {
					QuoteSet qs = QuoteSetSQL.getQuoteSetById(results.getLong("quote_set_id"));
					module.setQuoteSet(qs);
					AllocationConfiguration allocConfig = AllocationConfigurationSQL
							.getAllocationConfigurationById(results.getLong("allocation_configuration_id"));
					module.setAllocationConfiguration(allocConfig);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}

		return module;
	}

}