package finance.tradista.fx.common.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.persistence.BookSQL;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.fx.common.model.PricingParameterUnrealizedPnlCalculationModule;
import finance.tradista.fx.common.model.PricingParameterUnrealizedPnlCalculationModule.BookProductTypePair;
import finance.tradista.fx.common.model.PricingParameterUnrealizedPnlCalculationModule.UnrealizedPnlCalculation;

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

public class PricingParameterUnrealizedPnlCalculationSQL {

	public static void savePricingParameterModule(Connection con, PricingParameterUnrealizedPnlCalculationModule module,
			long pricingParamId) {
		try (PreparedStatement stmtSavePricingParameterUnrealizedPnlCalculations = con.prepareStatement(
				"INSERT INTO PRICING_PARAMETER_UNREALIZED_PNL_CALCULATION(PRICING_PARAMETER_ID, BOOK_ID, PRODUCT_TYPE, PNL_CALCULATION) VALUES(?, ?, ?, ?)")) {

			if (pricingParamId != 0) {
				// Then, we delete the data for this pricingParam
				try (PreparedStatement stmtDeletePricingParameterUnrealizedPnlCalculations = con.prepareStatement(
						"DELETE FROM PRICING_PARAMETER_UNREALIZED_PNL_CALCULATION WHERE PRICING_PARAMETER_ID = ?")) {
					stmtDeletePricingParameterUnrealizedPnlCalculations.setLong(1, pricingParamId);
					stmtDeletePricingParameterUnrealizedPnlCalculations.executeUpdate();
				}
			}

			for (Map.Entry<BookProductTypePair, UnrealizedPnlCalculation> entry : module.getUnrealizedPnlCalculations()
					.entrySet()) {
				stmtSavePricingParameterUnrealizedPnlCalculations.clearParameters();
				stmtSavePricingParameterUnrealizedPnlCalculations.setLong(1, pricingParamId);
				Book book = entry.getKey().getBook();
				if (book != null) {
					stmtSavePricingParameterUnrealizedPnlCalculations.setLong(2, book.getId());
				} else {
					stmtSavePricingParameterUnrealizedPnlCalculations.setNull(2, Types.BIGINT);
				}
				stmtSavePricingParameterUnrealizedPnlCalculations.setString(3, entry.getKey().getProductType());
				stmtSavePricingParameterUnrealizedPnlCalculations.setString(4, entry.getValue().name());
				stmtSavePricingParameterUnrealizedPnlCalculations.addBatch();
			}
			stmtSavePricingParameterUnrealizedPnlCalculations.executeBatch();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
	}

	public static PricingParameterUnrealizedPnlCalculationModule getPricingParameterModuleByPricingParameterId(
			Connection con, long id) {
		PricingParameterUnrealizedPnlCalculationModule module = null;
		Map<finance.tradista.fx.common.model.PricingParameterUnrealizedPnlCalculationModule.BookProductTypePair, UnrealizedPnlCalculation> curves = new HashMap<BookProductTypePair, UnrealizedPnlCalculation>();

		try (PreparedStatement stmtGetPricingParameterDividendYieldCurvesByPricingParameterId = con.prepareStatement(
				"SELECT * FROM PRICING_PARAMETER_UNREALIZED_PNL_CALCULATION WHERE PRICING_PARAMETER_ID = ?")) {
			stmtGetPricingParameterDividendYieldCurvesByPricingParameterId.setLong(1, id);
			try (ResultSet results = stmtGetPricingParameterDividendYieldCurvesByPricingParameterId.executeQuery()) {
				while (results.next()) {
					if (module == null) {
						module = new PricingParameterUnrealizedPnlCalculationModule();
					}
					curves.put(
							new BookProductTypePair(BookSQL.getBookById(results.getLong("book_id")),
									results.getString("product_type")),
							UnrealizedPnlCalculation.valueOf(results.getString("pnl_calculation")));
					module.setUnrealizedPnlCalculations(curves);
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
				"DELETE FROM PRICING_PARAMETER_UNREALIZED_PNL_CALCULATION WHERE PRICING_PARAMETER_ID = ?")) {
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