package finance.tradista.core.configuration.service;

import java.math.RoundingMode;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import finance.tradista.core.common.service.CustomProperties;
import finance.tradista.core.common.util.MathProperties;
import finance.tradista.core.configuration.model.UIConfiguration;
import finance.tradista.core.configuration.persistence.UIConfigurationSQL;
import finance.tradista.core.user.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.interceptor.Interceptors;

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
public class ConfigurationServiceBean implements LocalConfigurationService, ConfigurationService {

	private static ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		applicationContext = new ClassPathXmlApplicationContext("/META-INF/tradista-context.xml");
	}

	@Override
	public Scheduler getScheduler() {
		Scheduler scheduler;
		// Instanciating the scheduler
		scheduler = (Scheduler) applicationContext.getBean("TradistaScheduler");

		return scheduler;
	}

	@SuppressWarnings("static-access")
	@Override
	public short getScale() {
		return ((MathProperties) applicationContext.getBean("mathProperties")).getScale();
	}

	@SuppressWarnings("static-access")
	@Override
	public RoundingMode getRoundingMode() {
		return ((MathProperties) applicationContext.getBean("mathProperties")).getRoundingMode();
	}

	@SuppressWarnings("static-access")
	@Override
	public String getCustomPackage() {
		return ((CustomProperties) applicationContext.getBean("customProperties")).getCustomPackage();
	}

	@Interceptors(ConfigurationPreFilteringInterceptor.class)
	@Override
	public UIConfiguration getUIConfiguration(User user) {
		UIConfiguration uiConfiguration = UIConfigurationSQL.getUIConfiguration(user);
		// if the UI Configuration is not customized, we load a default one.
		if (uiConfiguration == null) {
			uiConfiguration = new UIConfiguration();
			uiConfiguration.setUser(user);
		}
		return uiConfiguration;
	}

	@Interceptors(ConfigurationPreFilteringInterceptor.class)
	@Override
	public void saveUIConfiguration(UIConfiguration configuration) {
		UIConfigurationSQL.saveUIConfiguration(configuration);
	}

}