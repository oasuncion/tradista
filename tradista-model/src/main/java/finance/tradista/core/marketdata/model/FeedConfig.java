package finance.tradista.core.marketdata.model;

import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.legalentity.model.LegalEntity;

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

/**
 * Structure defined to map provider data to Tradista data. Defined in this order :
 * Provider Data to Tradista Data
 * 
 * @author olivier_asuncion
 *
 */
public class FeedConfig extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5519797441712646656L;

	private FeedType feedType;

	private String name;

	private Map<String, Quote> mapping;

	private Map<String, Map<String, String>> fieldsMapping;

	private LegalEntity processingOrg;

	public Map<String, Map<String, String>> getFieldsMapping() {
		return fieldsMapping;
	}

	public void setFieldsMapping(Map<String, Map<String, String>> fieldsMapping) {
		this.fieldsMapping = fieldsMapping;
	}

	public FeedConfig() {
		super();
		mapping = new HashMap<String, Quote>();
		fieldsMapping = new HashMap<String, Map<String, String>>();
	}

	public FeedConfig(String name, FeedType feedType, LegalEntity po) {
		super();
		this.name = name;
		this.feedType = feedType;
		processingOrg = po;
		mapping = new HashMap<String, Quote>();
		fieldsMapping = new HashMap<String, Map<String, String>>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FeedType getFeedType() {
		return feedType;
	}

	public void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}

	public Map<String, Quote> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, Quote> mapping) {
		this.mapping = mapping;
	}

	public LegalEntity getProcessingOrg() {
		return processingOrg;
	}

	public void setProcessingOrg(LegalEntity processingOrg) {
		this.processingOrg = processingOrg;
	}

	public Quote putAddress(String feedValue, Quote quote) {
		return mapping.put(feedValue, quote);
	}

	public String putField(String providerData, String providerField, String tradistaField) {
		Map<String, String> fldMapping = fieldsMapping.get(providerData);
		if (fldMapping == null) {
			fldMapping = new HashMap<String, String>();
		}
		return fldMapping.put(providerField, tradistaField);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((processingOrg == null) ? 0 : processingOrg.hashCode());
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
		FeedConfig other = (FeedConfig) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (processingOrg == null) {
			if (other.processingOrg != null)
				return false;
		} else if (!processingOrg.equals(other.processingOrg))
			return false;
		return true;
	}

	public String toString() {
		return name;
	}

}