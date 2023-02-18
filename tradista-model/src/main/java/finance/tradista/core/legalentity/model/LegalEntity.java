package finance.tradista.core.legalentity.model;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaObject;

/*
 * Copyright 2014 Olivier Asuncion
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

public class LegalEntity extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2952827349654099841L;

	public static enum Role {
		PROCESSING_ORG, COUNTERPARTY;

		public String toString() {
			switch (this) {
			case PROCESSING_ORG:
				return "ProcessingOrg";
			case COUNTERPARTY:
				return "Counterparty";
			}
			return super.toString();
		}

		/**
		 * Gets a Role from a display name. Display names are used in GUIs. A display
		 * name of a Role is the result of its toString() method.
		 * 
		 * @param type
		 * @return
		 */
		public static Role getRole(String displayName) {
			switch (displayName) {
			case "ProcessingOrg":
				return PROCESSING_ORG;
			case "Counterparty":
				return COUNTERPARTY;
			}
			return null;
		}
	}

	@Id
	private String shortName;

	private String longName;

	private String description;

	private Role role;

	public LegalEntity(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String toString() {
		return shortName;
	}

}