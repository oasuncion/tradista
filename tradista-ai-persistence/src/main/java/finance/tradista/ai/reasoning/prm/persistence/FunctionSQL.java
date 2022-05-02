package finance.tradista.ai.reasoning.prm.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import finance.tradista.ai.reasoning.prm.model.Function;
import finance.tradista.ai.reasoning.prm.model.Parameter;
import finance.tradista.ai.reasoning.prm.model.Type;
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

public class FunctionSQL {

	public static long saveFunction(Function function) {
		long functionId = 0;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSaveFunction = (function.getId() == 0)
						? con.prepareStatement("INSERT INTO FUNCTION(NAME, RETURN_TYPE) VALUES (?, ?) ",
								Statement.RETURN_GENERATED_KEYS)
						: con.prepareStatement("UPDATE FUNCTION SET NAME=?, RETURN_TYPE = ? WHERE ID = ? ");
				PreparedStatement stmtDeleteParameter = (function.getId() != 0)
						? con.prepareStatement("DELETE FROM PARAMETER WHERE FUNCTION_ID = ?")
						: null;
				PreparedStatement stmtSaveParameter = (function.getParameters() != null
						&& !function.getParameters().isEmpty())
								? con.prepareStatement(
										"INSERT INTO PARAMETER(POSITION, TYPE, FUNCTION_ID) VALUES (?, ?, ?)")
								: null) {
			if (function.getId() != 0) {
				stmtSaveFunction.setLong(3, function.getId());
			}
			stmtSaveFunction.setString(1, function.getName());
			stmtSaveFunction.setString(2, function.getReturnType().getName());
			stmtSaveFunction.executeUpdate();

			if (function.getId() == 0) {
				try (ResultSet generatedKeys = stmtSaveFunction.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						functionId = generatedKeys.getLong(1);
						function.setId(functionId);
					} else {
						throw new SQLException("Creating function failed, no generated key obtained.");
					}
				}
			} else {
				functionId = function.getId();
			}

			if (function.getId() != 0) {
				stmtDeleteParameter.setLong(1, function.getId());
				stmtDeleteParameter.executeUpdate();
			}

			if (function.getParameters() != null && !function.getParameters().isEmpty()) {
				int position = 1;
				for (Parameter param : function.getParameters()) {
					stmtSaveParameter.setLong(1, position);
					stmtSaveParameter.setString(2, param.getType().getName());
					stmtSaveParameter.setLong(3, functionId);
					stmtSaveParameter.addBatch();
					position++;
				}
				stmtSaveParameter.executeBatch();
			}

		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return functionId;
	}

	public static Function getFunctionById(long id) {
		Function function = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetFunctionById = con.prepareStatement("SELECT * FROM FUNCTION WHERE ID = ?");
				PreparedStatement stmtGetParametersByFunctionId = con
						.prepareStatement("SELECT * FROM PARAMETER WHERE FUNCTION_ID = ?  ORDER BY POSITION")) {
			stmtGetFunctionById.setLong(1, id);
			try (ResultSet results = stmtGetFunctionById.executeQuery()) {
				while (results.next()) {
					function = new Function();
					function.setId(results.getLong("id"));
					function.setName(results.getString("name"));
					function.setReturnType(new Type(results.getString("return_type")));
				}
			}

			if (function == null) {
				return null;
			}

			stmtGetParametersByFunctionId.setLong(1, id);
			try (ResultSet results = stmtGetParametersByFunctionId.executeQuery()) {
				while (results.next()) {
					Parameter param = new Parameter();
					param.setId(results.getLong("id"));
					param.setType(new Type(results.getString("type")));
					function.addParameter(param);
				}
			}

		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return function;
	}

	public static Function getFunctionByName(String name) {
		Function function = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetFunctionByName = con.prepareStatement("SELECT * FROM FUNCTION WHERE NAME = ?");
				PreparedStatement stmtGetParametersByFunctionId = con
						.prepareStatement("SELECT * FROM PARAMETER WHERE FUNCTION_ID = ?  ORDER BY POSITION")) {
			stmtGetFunctionByName.setString(1, name);
			try (ResultSet results = stmtGetFunctionByName.executeQuery()) {
				while (results.next()) {
					function = new Function();
					function.setId(results.getLong("id"));
					function.setName(results.getString("name"));
					function.setReturnType(new Type(results.getString("return_type")));
				}
			}

			if (function == null) {
				return null;
			}

			stmtGetParametersByFunctionId.setLong(1, function.getId());
			try (ResultSet results = stmtGetParametersByFunctionId.executeQuery()) {
				while (results.next()) {
					Parameter param = new Parameter();
					param.setId(results.getLong("id"));
					param.setType(new Type(results.getString("type")));
					function.addParameter(param);
				}
			}

		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return function;
	}

}