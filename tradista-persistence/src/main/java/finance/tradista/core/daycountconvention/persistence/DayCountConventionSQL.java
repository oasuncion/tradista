package finance.tradista.core.daycountconvention.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.daycountconvention.model.DayCountConvention;

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

public class DayCountConventionSQL {

	public static DayCountConvention getDayCountConventionById(long id) {

		DayCountConvention dayCountConvention = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetDayCountConventionById = con
						.prepareStatement("SELECT * FROM DAY_COUNT_CONVENTION WHERE DAY_COUNT_CONVENTION.ID = ? ")) {
			stmtGetDayCountConventionById.setLong(1, id);
			try (ResultSet results = stmtGetDayCountConventionById.executeQuery()) {
				while (results.next()) {
					dayCountConvention = new DayCountConvention(results.getString("name"));
					dayCountConvention.setId(results.getLong("id"));
				}
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return dayCountConvention;
	}

	public static Set<DayCountConvention> getAllDayCountConventions() {

		Set<DayCountConvention> dayCountConventions = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetAllDayCountConventions = con
						.prepareStatement("SELECT * FROM DAY_COUNT_CONVENTION");
				ResultSet results = stmtGetAllDayCountConventions.executeQuery()) {
			while (results.next()) {
				if (dayCountConventions == null) {
					dayCountConventions = new TreeSet<DayCountConvention>();
				}
				DayCountConvention dayCountConvention = new DayCountConvention(results.getString("name"));
				dayCountConvention.setId(results.getLong("id"));
				dayCountConventions.add(dayCountConvention);
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return dayCountConventions;
	}

}