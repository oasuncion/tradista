package finance.tradista.core.processingorgdefaults.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.legalentity.persistence.LegalEntitySQL;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaults;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaultsModule;

/*
 * Copyright 2024 Olivier Asuncion
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

public class ProcessingOrgDefaultsSQL {

	private static Map<String, Class<?>> daoClasses = new HashMap<>();

	static {
		Class<?> daoClass = null;
		try {
			daoClass = TradistaUtil.getClass(
					"finance.tradista.security.gcrepo.persistence.ProcessingOrgDefaultsCollateralManagementSQL");
			daoClasses.put("finance.tradista.security.gcrepo.model.ProcessingOrgDefaultsCollateralManagementModule",
					daoClass);
		} catch (TradistaTechnicalException tte) {
			// TODO Add log info
		}
	}

	public static ProcessingOrgDefaults getProcessingOrgDefaultsByPoId(long poId) {
		// ProcessingOrgDefaults are always returned, even if it has not been saved yet.
		ProcessingOrgDefaults poDefaults = new ProcessingOrgDefaults(LegalEntitySQL.getLegalEntityById(poId));
		try (Connection con = TradistaDB.getConnection();) {
			for (Class<?> daoClass : daoClasses.values()) {
				try {
					Method method = daoClass.getMethod("getProcessingOrgDefaultsModuleByPoId", Connection.class,
							long.class);
					ProcessingOrgDefaultsModule module = (ProcessingOrgDefaultsModule) method.invoke(daoClass, con,
							poId);
					poDefaults.addModule(module);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new TradistaTechnicalException(e);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return poDefaults;
	}

	public static long saveProcessingOrgDefaults(ProcessingOrgDefaults poDefaults) {
		long pricingParamId = 0;
		try (Connection con = TradistaDB.getConnection()) {
			List<ProcessingOrgDefaultsModule> modules = poDefaults.getModules();
			if (modules != null && !modules.isEmpty()) {
				for (ProcessingOrgDefaultsModule module : poDefaults.getModules()) {
					// Save the module
					ProcessingOrgDefaultsSQL.saveProcessingOrgDefaultsModule(module, con,
							poDefaults.getProcessingOrg().getId());
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return pricingParamId;
	}

	private static void saveProcessingOrgDefaultsModule(ProcessingOrgDefaultsModule module, Connection con, long poId) {
		// Get the right DAO
		Class<?> daoClass = daoClasses.get(module.getClass().getName());
		try {
			Method method = daoClass.getMethod("saveProcessingOrgDefaultsModule", Connection.class, module.getClass(),
					long.class);
			method.invoke(daoClass, con, module, poId);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			throw new TradistaTechnicalException(e);
		}
	}

}