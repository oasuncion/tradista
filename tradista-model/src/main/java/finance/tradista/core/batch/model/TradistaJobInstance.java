package finance.tradista.core.batch.model;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDetail;

import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.legalentity.model.LegalEntity.Role;

/*
 * Copyright 2018 Olivier Asuncion
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

public class TradistaJobInstance extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8887650019932154806L;

	private JobDetail jobDetail;

	private String jobType;

	private String name;

	private Map<String, Object> properties;

	public static final String PROCESSING_ORG_PROPERTY_KEY = "ProcessingOrg";

	public TradistaJobInstance(JobDetail jobDetail, String jobType) {
		super();
		this.jobDetail = jobDetail;
		this.jobType = jobType;
		this.name = jobDetail.getKey().getName();
		properties = new HashMap<String, Object>();
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public TradistaJobInstance(String name, String jobType) {
		super();
		this.jobType = jobType;
		this.name = name;
		properties = new HashMap<String, Object>();
	}

	public String getJobType() {
		return jobType;
	}

	public String getName() {
		return name;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	/**
	 * Uses new properties if it was set, otherwise uses stored properties
	 * 
	 * @param key key of the property
	 * @return value of the property
	 */
	public Object getJobPropertyValue(String key) {
		if (properties.isEmpty() && (jobDetail != null)) {
			return jobDetail.getJobDataMap().get(key);
		} else {
			// For now, properties are only String.
			return properties.get(key);
		}
	}

	/**
	 * Returns new properties if it was set, otherwise returns stored properties
	 * 
	 * @return new properties if it was set, otherwise stored properties
	 */
	public Map<String, Object> getProperties() {
		if (properties.isEmpty() && (jobDetail != null)) {
			return jobDetail.getJobDataMap();
		} else {
			return properties;
		}
	}

	public LegalEntity getProcessingOrg() {
		Object o = getJobPropertyValue(PROCESSING_ORG_PROPERTY_KEY);
		if (o != null) {
			return (LegalEntity) o;
		} else {
			return null;
		}
	}

	/**
	 * Flag to indicate if the object was already persisted or is a totally new one.
	 * 
	 * @return true if the object was already persisted, false if the object is
	 *         totally new.
	 */
	public boolean isSaved() {
		return (jobDetail != null);
	}

	public void setProcessingOrg(LegalEntity po) {
		if (po != null && po.getRole().equals(Role.PROCESSING_ORG)) {
			if (properties.isEmpty() && (jobDetail != null)) {
				jobDetail.getJobDataMap().put(PROCESSING_ORG_PROPERTY_KEY, po);
			} else {
				properties.put(PROCESSING_ORG_PROPERTY_KEY, po);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getProcessingOrg() == null) ? 0 : getProcessingOrg().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradistaJobInstance other = (TradistaJobInstance) obj;
		if (getProcessingOrg() == null) {
			if (other.getProcessingOrg() != null)
				return false;
		} else if (!getProcessingOrg().equals(other.getProcessingOrg()))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}