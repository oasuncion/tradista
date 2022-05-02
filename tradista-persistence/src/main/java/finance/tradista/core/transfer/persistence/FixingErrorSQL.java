package finance.tradista.core.transfer.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.error.model.Error.Status;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.FixingError;

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

public class FixingErrorSQL {

	public static boolean saveFixingErrors(List<FixingError> errors) {
		boolean bSaved = false;

		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSaveErrors = con.prepareStatement(
						"INSERT INTO ERROR(TYPE, MESSAGE, STATUS, ERROR_DATE) VALUES(?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				PreparedStatement stmtSaveFixingErrors = con.prepareStatement("INSERT INTO FIXING_ERROR VALUES(?, ?)");
				PreparedStatement stmtUpdateErrors = con
						.prepareStatement("UPDATE ERROR SET TYPE=?, MESSAGE=?, STATUS=?, ERROR_DATE=? WHERE ID=?");
				PreparedStatement stmtUpdateFixingErrors = con
						.prepareStatement("UPDATE FIXING_ERROR SET TRANSFER_ID = ? WHERE ERROR_ID=?")) {
			for (FixingError error : errors) {
				if (error.getId() == 0) {
					stmtSaveErrors.setString(1, error.getType());
					stmtSaveErrors.setString(2, error.getMessage());
					stmtSaveErrors.setString(3, error.getStatus().name());
					stmtSaveErrors.setTimestamp(4, java.sql.Timestamp.valueOf(error.getErrorDate()));
					stmtSaveErrors.executeUpdate();
					try (ResultSet generatedKeys = stmtSaveErrors.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							error.setId(generatedKeys.getLong(1));
						} else {
							throw new SQLException("Creating error failed, no generated key obtained.");
						}
					}
					stmtSaveFixingErrors.setLong(1, error.getId());
					stmtSaveFixingErrors.setLong(2, error.getCashTransfer().getId());
					stmtSaveFixingErrors.addBatch();
				} else {
					stmtUpdateFixingErrors.setLong(1, error.getCashTransfer().getId());
					stmtUpdateFixingErrors.setLong(2, error.getId());
					stmtUpdateFixingErrors.addBatch();

					stmtUpdateErrors.setString(1, error.getType());
					stmtUpdateErrors.setString(2, error.getMessage());
					stmtUpdateErrors.setString(3, error.getStatus().name());
					stmtUpdateErrors.setTimestamp(4, java.sql.Timestamp.valueOf(error.getErrorDate()));
					stmtUpdateErrors.setLong(5, error.getId());
					stmtUpdateErrors.addBatch();
				}
			}
			stmtSaveFixingErrors.executeBatch();
			stmtUpdateErrors.executeBatch();
			stmtUpdateFixingErrors.executeBatch();
			bSaved = true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}

		return bSaved;
	}

	public static void solveFixingError(Set<Long> solved, LocalDate date) {
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSolveFixingErrors = con.prepareStatement(
						"UPDATE ERROR SET STATUS = ?, SOLVING_DATE = ? WHERE ID IN (SELECT ERROR_ID FROM FIXING_ERROR WHERE TRANSFER_ID = ?)")) {
			for (long id : solved) {
				stmtSolveFixingErrors.setString(1, finance.tradista.core.error.model.Error.Status.SOLVED.name());
				stmtSolveFixingErrors.setDate(2, java.sql.Date.valueOf(date));
				stmtSolveFixingErrors.setLong(3, id);
				stmtSolveFixingErrors.addBatch();
			}
			stmtSolveFixingErrors.executeBatch();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
	}

	public static void solveFixingError(long transferId, LocalDate date) {
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSolveFixingError = con.prepareStatement(
						"UPDATE ERROR SET STATUS = ?, SOLVING_DATE = ? WHERE ID IN (SELECT ERROR_ID FROM TRANSFER_ERROR WHERE TRANSFER_ID = ?)")) {
			stmtSolveFixingError.setString(1, finance.tradista.core.error.model.Error.Status.SOLVED.name());
			stmtSolveFixingError.setDate(2, java.sql.Date.valueOf(date));
			stmtSolveFixingError.setLong(3, transferId);
			stmtSolveFixingError.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
	}

	public static List<FixingError> getFixingErrors(long transferId, Status status, LocalDate errorDateFrom,
			LocalDate errorDateTo, LocalDate solvingDateFrom, LocalDate solvingDateTo) {
		List<FixingError> fixingErrors = null;

		try (Connection con = TradistaDB.getConnection(); Statement stmtGetFixingErrors = con.createStatement()) {
			String sqlQuery = "SELECT * FROM FIXING_ERROR, ERROR ";
			String dateSqlQuery = "";

			if (errorDateFrom != null && errorDateTo != null) {
				dateSqlQuery = " WHERE ERROR_DATE >=" + "'"
						+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(errorDateFrom) + "'" + " AND ERROR_DATE <= "
						+ "'" + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(errorDateTo) + "'";
			} else {
				if (errorDateFrom == null && errorDateTo != null) {
					dateSqlQuery = " WHERE ERROR_DATE <= " + "'"
							+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(errorDateTo) + "'";
				}
				if (errorDateFrom != null && errorDateTo == null) {
					dateSqlQuery = " WHERE ERROR_DATE >= " + "'"
							+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(errorDateFrom) + "'";
				}
				if (errorDateFrom == null && errorDateTo == null) {
					dateSqlQuery += "";
				}
			}

			if (solvingDateFrom != null && solvingDateTo != null) {
				if (dateSqlQuery.contains("WHERE")) {
					dateSqlQuery += " AND ";
				} else {
					dateSqlQuery += " WHERE ";
				}
				dateSqlQuery += " SOLVING_DATE >=" + "'"
						+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(solvingDateFrom) + "'"
						+ " AND SOLVING_DATE <= " + "'"
						+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(solvingDateTo) + "'";
			} else {
				if (solvingDateFrom == null && solvingDateTo != null) {
					if (dateSqlQuery.contains("WHERE")) {
						dateSqlQuery += " AND ";
					} else {
						dateSqlQuery += " WHERE ";
					}
					dateSqlQuery += " SOLVING_DATE <= " + "'"
							+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(solvingDateTo) + "'";
				}
				if (solvingDateFrom != null && solvingDateTo == null) {
					if (dateSqlQuery.contains("WHERE")) {
						dateSqlQuery += " AND ";
					} else {
						dateSqlQuery += " WHERE ";
					}
					dateSqlQuery += " SOLVING_DATE >= " + "'"
							+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(solvingDateFrom) + "'";
				}
				if (solvingDateFrom == null && solvingDateTo == null) {
					dateSqlQuery += "";
				}
			}

			sqlQuery += dateSqlQuery;

			String transferSqlQuery = "";

			if (transferId > 0) {
				if (sqlQuery.contains("WHERE")) {
					transferSqlQuery = " AND";
				} else {
					transferSqlQuery = " WHERE";
				}
				transferSqlQuery += " TRANSFER_ID IN (SELECT ID FROM TRANSFER WHERE ID = " + transferId + ")";
			}

			sqlQuery += transferSqlQuery;

			String statusSqlQuery = "";

			if (status != null) {
				if (sqlQuery.contains("WHERE")) {
					statusSqlQuery = " AND";
				} else {
					statusSqlQuery = " WHERE";
				}
				statusSqlQuery += " STATUS = '" + status.name() + "'";
			}

			sqlQuery += statusSqlQuery;
			String joinSqlQuery = "";

			if (sqlQuery.contains("WHERE")) {
				joinSqlQuery = " AND";
			} else {
				joinSqlQuery = " WHERE";
			}
			joinSqlQuery += " ID = ERROR_ID";

			sqlQuery += joinSqlQuery;

			try (ResultSet results = stmtGetFixingErrors.executeQuery(sqlQuery)) {
				while (results.next()) {
					if (fixingErrors == null) {
						fixingErrors = new ArrayList<FixingError>();
					}
					FixingError fixingError = new FixingError();
					fixingError.setId(results.getLong("id"));
					fixingError.setMessage(results.getString("message"));
					Timestamp solvingDate = results.getTimestamp("solving_date");
					if (solvingDate != null) {
						fixingError.setSolvingDate(solvingDate.toLocalDateTime());
					}
					fixingError.setErrorDate(results.getTimestamp("error_date").toLocalDateTime());
					fixingError.setStatus(Status.valueOf(results.getString("status")));
					fixingError.setCashTransfer(
							(CashTransfer) TransferSQL.getTransferById(results.getLong("transfer_id")));
					fixingErrors.add(fixingError);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}

		return fixingErrors;
	}

	public static void deleteErrors(Set<Long> ids) {
		if (ids != null && !ids.isEmpty()) {
			try (Connection con = TradistaDB.getConnection();
					PreparedStatement stmtDeleteErrors = con
							.prepareStatement("DELETE FROM FIXING_ERROR WHERE ERROR_ID = ?")) {
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

}