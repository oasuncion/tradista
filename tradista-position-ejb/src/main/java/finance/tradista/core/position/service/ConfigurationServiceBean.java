package finance.tradista.core.position.service;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

@Startup
@Singleton
public class ConfigurationServiceBean implements LocalConfigurationService {

	private static ApplicationContext applicationContext;

	public static final String CONFIG_FILE_NAME = "tradista-position-context.xml";

	@PostConstruct
	public void init() {
		applicationContext = new ClassPathXmlApplicationContext("/META-INF/" + CONFIG_FILE_NAME);
	}

	@Override
	public int getFrequency() {
		return ((PositionProperties) applicationContext.getBean("positionProperties")).getFrequency();
	}

}