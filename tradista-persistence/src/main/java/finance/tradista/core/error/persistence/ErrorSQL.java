package finance.tradista.core.error.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.error.model.Error.Status;

/*
 * Copyright 2016 Olivier Asuncion
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

public class ErrorSQL {

	public static void deleteErrors(String errorType, Status status, LocalDate errorDateFrom, LocalDate errorDateTo) {
		Set<String> errorTypes = TradistaUtil.getAllErrorTypes();
		Set<Long> ids = getErrorIds(errorType, status, errorDateFrom, errorDateTo);
		try {
			if (ids != null && !ids.isEmpty()) {
				for (String err : errorTypes) {
					Class<?> persistenceClass = null;
					List<Class<?>> klasses = TradistaUtil.getAllClassesByRegex("[^*]+.persistence." + err + "SQL",
							"finance.tradista.**");
					if (klasses != null && klasses.size() > 1) {
						persistenceClass = klasses.get(0);
					}
					TradistaUtil.callMethod(persistenceClass.getName(), Void.class, "deleteErrors", ids);
				}
			}
		} catch (TradistaBusinessException abe) {
			throw new TradistaTechnicalException(abe);
		}
		ErrorSQL.deleteErrors(ids);
	}

	public static void deleteErrors(Set<Long> ids) {
		if (ids != null && !ids.isEmpty()) {
			try (Connection con = TradistaDB.getConnection();
					PreparedStatement stmtDeleteErrors = con.prepareStatement("DELETE FROM ERROR WHERE ID = ?")) {
				for (long id : ids) {
					stmtDeleteErrors.setLong(1, id);
					stmtDeleteErrors.addBatch();
				}
				stmtDeleteErrors.executeBatch();
			} catch (SQLException sqle) {
				// TODO Manage logs
				sqle.printStackTrace();
				throw new TradistaTechnicalException(sqle);
			}
		}
	}

	public static Set<Long> getErrorIds(String errorType, Status status, LocalDate errorDateFrom,
			LocalDate errorDateTo) {
		Set<Long> ids = null;
		StringBuilder sqlQuery = new StringBuilder("SELECT ID FROM ERROR");
		if (!StringUtils.isEmpty(errorType)) {
			sqlQuery.append(" WHERE TYPE = '" + errorType + "'");
		}
		if (status != null) {
			if (sqlQuery.toString().contains("WHERE")) {
				sqlQuery.append(" AND ");
			} else {
				sqlQuery.append(" WHERE ");
			}
			sqlQuery.append(" STATUS = '" + status.name() + "'");
		}
		if (errorDateFrom != null) {
			if (sqlQuery.toString().contains("WHERE")) {
				sqlQuery.append(" AND ");
			} else {
				sqlQuery.append(" WHERE ");
			}
			sqlQuery.append(" ERROR_DATE >= '" + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(errorDateFrom) + "'");
		}
		if (errorDateTo != null) {
			if (sqlQuery.toString().contains("WHERE")) {
				sqlQuery.append(" AND ");
			} else {
				sqlQuery.append(" WHERE ");
			}
			sqlQuery.append(" ERROR_DATE <= '" + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(errorDateTo) + "'");
		}
		try (Connection con = TradistaDB.getConnection();
				Statement stmtGetErrorIds = con.createStatement();
				ResultSet results = stmtGetErrorIds.executeQuery(sqlQuery.toString())) {
			while (results.next()) {
				if (ids == null) {
					ids = new HashSet<Long>();
				}
				ids.add(results.getLong("id"));
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return ids;
	}

}