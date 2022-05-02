package finance.tradista.core.common.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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

public class MathProperties {	
	
	private static short scale;
	
	private static RoundingMode roundingMode;
	
	private static DecimalFormat uiDecimalFormat;
	

	public static DecimalFormat getUIDecimalFormat() {
		return uiDecimalFormat;
	}

	public static void setUIDecimalFormat(DecimalFormat decimalFormat) {
		MathProperties.uiDecimalFormat = decimalFormat;
	}

	public static short getScale() {
		return scale;
	}

	public void setScale(short scl) {
		scale = scl;
	}

	public static RoundingMode getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(String rMode) {
		roundingMode = RoundingMode.valueOf(rMode);
	}
		
}