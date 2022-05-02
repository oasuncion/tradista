package finance.tradista.core.common.persistence.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import finance.tradista.core.common.exception.TradistaTechnicalException;

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

public class TradistaDB {

	private static DataSource ds;

	static {
		try {
			InitialContext context = new InitialContext();
			ds = (DataSource) context.lookup("java:/TradistaDataSource");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		Connection con = null;
		try {
			// Should use a connection pool as we are using
			// org.apache.derby.jdbc.ClientConnectionPoolDataSource
			con = ds.getConnection();
		} catch (SQLException sqle) {
			// TODO put error log
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}

		return con;
	}

}