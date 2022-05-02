package finance.tradista.ai.reasoning.prm.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import finance.tradista.ai.reasoning.prm.model.Variable;
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

public class VariableSQL {

	public static long saveVariable(Variable variable) {
		long variableId = 0;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSaveValue = (variable.getId() == 0)
						? con.prepareStatement("INSERT INTO VALUE(TYPE, NAME) VALUES (?, ?) ",
								Statement.RETURN_GENERATED_KEYS)
						: con.prepareStatement("UPDATE VALUE SET TYPE = ?,  NAME = ? WHERE ID = ? ");
				PreparedStatement stmtSaveVariable = (variable.getId() == 0)
						? con.prepareStatement("INSERT INTO VARIABLE(VARIABLE_ID) VALUES (?) ")
						: null) {
			if (variable.getId() != 0) {
				stmtSaveValue.setLong(3, variable.getId());
			}
			stmtSaveValue.setString(1, variable.getType().getName());
			stmtSaveValue.setString(2, variable.getName());
			stmtSaveValue.executeUpdate();

			if (variable.getId() == 0) {
				try (ResultSet generatedKeys = stmtSaveValue.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						variableId = generatedKeys.getLong(1);
						variable.setId(variableId);
					} else {
						throw new SQLException("Creating variable failed, no generated key obtained.");
					}
				}
			} else {
				variableId = variable.getId();
			}

			if (variable.getId() == 0) {
				stmtSaveVariable.setLong(1, variable.getId());
				stmtSaveVariable.executeUpdate();
			}

		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return variableId;
	}

}