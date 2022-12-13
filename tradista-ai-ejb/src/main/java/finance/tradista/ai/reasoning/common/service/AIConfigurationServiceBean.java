package finance.tradista.ai.reasoning.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.ai.reasoning.common.util.TradistaAIProperties;
import finance.tradista.core.common.exception.TradistaBusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

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

@SecurityDomain(value = "other")
@PermitAll
@Startup
@Singleton
public class AIConfigurationServiceBean implements LocalConfigurationService {

	@PostConstruct
	public void init() {
		Properties properties = new Properties();
		InputStream in = TradistaAIProperties.class.getResourceAsStream("/META-INF/solver.properties");
		try {
			properties.load(in);
			in.close();
		} catch (IOException ioe) {
			// should not happen here.
		}
		try {
			TradistaAIProperties.load(properties);
		} catch (TradistaBusinessException abe) {
			// should not happen here.
		}
	}

	@Override
	public String getSolverPath() {
		return TradistaAIProperties.getSolverPath();
	}
}
