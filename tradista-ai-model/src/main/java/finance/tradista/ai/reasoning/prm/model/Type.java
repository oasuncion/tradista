package finance.tradista.ai.reasoning.prm.model;

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

public class Type {

	protected static final String DECIMAL_TYPE_NAME = "Decimal";

	protected static final String INTEGER_TYPE_NAME = "Integer";

	protected static final String STRING_TYPE_NAME = "String";

	public static final Type DECIMAL = new Type(DECIMAL_TYPE_NAME);

	public static final Type INTEGER = new Type(INTEGER_TYPE_NAME);

	public static final Type STRING = new Type(STRING_TYPE_NAME);

	private String name;

	public Type(String name) {
		this.name = name;
	}

	public boolean checkValue(String value, Type type) {
		try {
			switch (type.getName()) {
			case DECIMAL_TYPE_NAME: {
				Double.parseDouble(value);
				break;
			}
			case INTEGER_TYPE_NAME: {
				Integer.parseInt(value);
				break;
			}

			}
		} catch (NullPointerException | NumberFormatException e) {
			return false;
		}
		return true;

	}

	public String getName() {
		return name;
	}

}