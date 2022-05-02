package finance.tradista.core.marketdata.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.marketdata.model.VolatilitySurface;

/*
 * Copyright 2015 Olivier Asuncion
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

public class SurfaceSQL {

	public static boolean surfaceExists(VolatilitySurface<?, ?, ?> surface, String type) {
		boolean exists = false;

		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetSurfaceByName = con.prepareStatement(
						"SELECT 1 FROM VOLATILITY_SURFACE WHERE NAME = ? AND PROCESSING_ORG_ID = ? AND TYPE = ?")) {
			stmtGetSurfaceByName.setString(1, surface.getName());
			stmtGetSurfaceByName.setLong(2, surface.getProcessingOrg().getId());
			stmtGetSurfaceByName.setString(3, type);
			try (ResultSet results = stmtGetSurfaceByName.executeQuery()) {
				while (results.next()) {
					exists = true;
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return exists;
	}

}