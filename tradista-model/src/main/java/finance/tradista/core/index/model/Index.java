package finance.tradista.core.index.model;

import finance.tradista.core.common.model.TradistaObject;

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

public class Index extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3890592152599924134L;

	public static final String INDEX = "Index";
	
	public Index() {
	}

	private String name;

	private String description;
	
	private boolean isPrefixed;
	
	public static enum Fixing {
		PREFIXED, POSTFIXED;
		public String toString() {
			switch (this) {
			case PREFIXED:
				return "Prefixed";
			case POSTFIXED:
				return "PostFixed";
			}
			return super.toString();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Index(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPrefixed() {
		return isPrefixed;
	}

	public void setPrefixed(boolean isPrefixed) {
		this.isPrefixed = isPrefixed;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof Index)) {
			return false;
		}
		
		if (name == null) {
			return ((Index)o).getName() == null;
		}

		return name.equals(((Index) o).getName());
	}

	public int hashCode() {
		return name.hashCode();
	}

	public String toString() {
		return name;
	}

}