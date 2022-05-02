package finance.tradista.core.common.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.parsing.parser.TradistaParser;

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

public class ParserUtil {

	public static List<? extends TradistaObject> parse(File file, String objectName, Map<String, String> config)
			throws TradistaTechnicalException {
		String extension;
		if (file == null) {
			throw new TradistaTechnicalException("The file cannot be null.");
		}
		if (!file.exists()) {
			throw new TradistaTechnicalException("The file doesn't exist.");
		}
		if (objectName == null || objectName.isEmpty()) {
			throw new TradistaTechnicalException("The object name cannot be null or empty.");
		}
		extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		TradistaParser parser = TradistaUtil.getInstance(TradistaParser.class,
				"finance.tradista.core.parser." + extension + "." + extension.toUpperCase() + "Parser");
		parser.setConfig(config);

		return parser.parseFile(file, objectName);
	}
}