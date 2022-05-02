package finance.tradista.core.parsing.csv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.parsing.parser.TradistaObjectParser;
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

public class CSVParser extends TradistaParser {

	@SuppressWarnings("unchecked")
	@Override
	public List<TradistaObject> parseFile(File file, String objectName) {
		TradistaObjectParser<TradistaObject, CSVRecord> objectParser;
		List<TradistaObject> objects = new ArrayList<TradistaObject>();
		org.apache.commons.csv.CSVParser parser = null;
		try {
			CSVFormat csvFormat = CSVFormat.RFC4180;
			if (config != null) {
				String fieldSeparator = config.get("fieldSeparator");
				if (fieldSeparator != null && !fieldSeparator.isEmpty()) {
					csvFormat.withRecordSeparator(fieldSeparator.substring(0, 1));
				}
			}
			parser = org.apache.commons.csv.CSVParser.parse(file, StandardCharsets.ISO_8859_1, csvFormat);
			objectParser = TradistaUtil.getInstance(TradistaObjectParser.class,
					"finance.tradista.core.parsing.csv.CSV" + objectName + "Parser");
		} catch (IOException ioe) {
			throw new TradistaTechnicalException(ioe);
		}
		for (CSVRecord csvRecord : parser) {
			objects.add(objectParser.parse(csvRecord));
		}

		return objects;
	}

}