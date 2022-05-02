package finance.tradista.ir.irswapoption.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.index.persistence.IndexSQL;
import finance.tradista.ir.irswapoption.model.PricingParameterVolatilitySurfaceModule;
import finance.tradista.ir.irswapoption.model.SwaptionVolatilitySurface;

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

public class PricingParameterVolatilitySurfaceSQL {

	public static void savePricingParameterModule(Connection con, PricingParameterVolatilitySurfaceModule module,
			long pricingParamId) {
		try (PreparedStatement stmtSavePricingParameterSwaptionVolatilitySurfaces = con.prepareStatement(
				"INSERT INTO PRICING_PARAMETER_SWAPTION_VOLATILITY_SURFACE(PRICING_PARAMETER_ID, INDEX_ID, VOLATILITY_SURFACE_ID) VALUES(?, ?, ?)")) {

			if (pricingParamId != 0) {
				// Then, we delete the data for this pricingParam
				try (PreparedStatement stmtDeletePricingParameterEquityOptionVolatilitySurfaces = con.prepareStatement(
						"DELETE FROM PRICING_PARAMETER_SWAPTION_VOLATILITY_SURFACE WHERE PRICING_PARAMETER_ID = ?")) {
					stmtDeletePricingParameterEquityOptionVolatilitySurfaces.setLong(1, pricingParamId);
					stmtDeletePricingParameterEquityOptionVolatilitySurfaces.executeUpdate();
				}
			}
			for (Map.Entry<Index, SwaptionVolatilitySurface> entry : module.getVolatilitySurfaces().entrySet()) {
				stmtSavePricingParameterSwaptionVolatilitySurfaces.clearParameters();
				stmtSavePricingParameterSwaptionVolatilitySurfaces.setLong(1, pricingParamId);
				stmtSavePricingParameterSwaptionVolatilitySurfaces.setLong(2, entry.getKey().getId());
				stmtSavePricingParameterSwaptionVolatilitySurfaces.setLong(3, entry.getValue().getId());
				stmtSavePricingParameterSwaptionVolatilitySurfaces.addBatch();
			}
			stmtSavePricingParameterSwaptionVolatilitySurfaces.executeBatch();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
	}

	public static PricingParameterVolatilitySurfaceModule getPricingParameterModuleByPricingParameterId(Connection con,
			long id) {
		PricingParameterVolatilitySurfaceModule module = null;
		Map<Index, SwaptionVolatilitySurface> surfaces = new HashMap<Index, SwaptionVolatilitySurface>();

		try (PreparedStatement stmtGetPricingParameterSwaptionVolatilitySurfacesByPricingParameterId = con
				.prepareStatement(
						"SELECT * FROM PRICING_PARAMETER_SWAPTION_VOLATILITY_SURFACE WHERE PRICING_PARAMETER_ID = ?")) {
			stmtGetPricingParameterSwaptionVolatilitySurfacesByPricingParameterId.setLong(1, id);
			try (ResultSet results = stmtGetPricingParameterSwaptionVolatilitySurfacesByPricingParameterId
					.executeQuery()) {
				while (results.next()) {
					if (module == null) {
						module = new PricingParameterVolatilitySurfaceModule();
					}
					surfaces.put(IndexSQL.getIndexById(results.getLong("index_id")), SwaptionVolatilitySurfaceSQL
							.getSwaptionVolatilitySurfaceById(results.getLong("volatility_surface_id")));
					module.setVolatilitySurfaces(surfaces);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return module;
	}

	public static boolean deletePricingParameterModule(Connection con, long id) {
		boolean bSaved = false;

		try (PreparedStatement stmtDeletePricingParameterModule = con.prepareStatement(
				"DELETE FROM PRICING_PARAMETER_SWAPTION_VOLATILITY_SURFACE WHERE PRICING_PARAMETER_ID = ?")) {
			stmtDeletePricingParameterModule.setLong(1, id);
			stmtDeletePricingParameterModule.executeUpdate();
			bSaved = true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return bSaved;
	}

}