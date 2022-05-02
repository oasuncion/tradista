package finance.tradista.ai.agent.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import finance.tradista.ai.agent.model.Agent;
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

public class AgentSQL {

	public static long saveAgent(Agent agent) {
		long agentId = 0;

		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSaveAgent = (agent.getId() != 0)
						? con.prepareStatement(
								"UPDATE AGENT SET NAME = ?, ONLY_INFORMATIVE = ?, STARTED = ? WHERE ID = ? ")
						: con.prepareStatement("INSERT INTO AGENT(NAME, ONLY_INFORMATIVE, STARTED) VALUES (?, ?, ?) ",
								Statement.RETURN_GENERATED_KEYS)) {
			if (agent.getId() != 0) {
				stmtSaveAgent.setLong(4, agent.getId());
			}
			stmtSaveAgent.setString(1, agent.getName());
			stmtSaveAgent.setBoolean(2, agent.isOnlyInformative());
			stmtSaveAgent.setBoolean(3, agent.isStarted());
			stmtSaveAgent.executeUpdate();

			if (agent.getId() == 0) {
				try (ResultSet generatedKeys = stmtSaveAgent.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						agentId = generatedKeys.getLong(1);
						agent.setId(agentId);
					} else {
						throw new SQLException("Creating agent failed, no generated key obtained.");
					}
				}
			} else {
				agentId = agent.getId();
			}

		} catch (SQLException sqle) {
			throw new TradistaTechnicalException(sqle);
		}
		agent.setId(agentId);
		return agentId;
	}

	public static Agent getAgentById(long id) {
		Agent agent = null;

		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetAgentById = con.prepareStatement("SELECT * FROM AGENT WHERE ID = ?")) {
			stmtGetAgentById.setLong(1, id);
			try (ResultSet results = stmtGetAgentById.executeQuery()) {

				while (results.next()) {
					agent = new Agent();
					agent.setId(results.getLong("id"));
					agent.setName(results.getString("name"));
					agent.setOnlyInformative(results.getBoolean("only_informative"));
					agent.setStarted(results.getBoolean("started"));
				}
			}

		} catch (SQLException sqle) {
			throw new TradistaTechnicalException(sqle);
		}
		return agent;
	}

	public static Agent getAgentByName(String name) {
		Agent agent = null;

		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetAgentByName = con.prepareStatement("SELECT * FROM AGENT WHERE NAME = ?")) {
			stmtGetAgentByName.setString(1, name);
			try (ResultSet results = stmtGetAgentByName.executeQuery()) {

				while (results.next()) {
					agent = new Agent();
					agent.setId(results.getLong("id"));
					agent.setName(results.getString("name"));
					agent.setOnlyInformative(results.getBoolean("only_informative"));
					agent.setStarted(results.getBoolean("started"));
				}
			}

		} catch (SQLException sqle) {
			throw new TradistaTechnicalException(sqle);
		}
		return agent;
	}

	public static Set<Agent> getAllStartedAgents() {
		Set<Agent> agents = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetAllStartedAgents = con
						.prepareStatement("SELECT * FROM AGENT WHERE STARTED = TRUE");
				ResultSet results = stmtGetAllStartedAgents.executeQuery()) {
			while (results.next()) {
				if (agents == null) {
					agents = new HashSet<Agent>();
				}
				Agent agent = new Agent();
				agent.setId(results.getLong("id"));
				agent.setName(results.getString("name"));
				agent.setOnlyInformative(results.getBoolean("only_informative"));
				agent.setStarted(results.getBoolean("started"));
				agents.add(agent);
			}

		} catch (SQLException sqle) {
			throw new TradistaTechnicalException(sqle);
		}
		return agents;
	}

}