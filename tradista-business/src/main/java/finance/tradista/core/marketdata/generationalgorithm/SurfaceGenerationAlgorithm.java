package finance.tradista.core.marketdata.generationalgorithm;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.marketdata.interpolator.MultivariateInterpolator;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.SurfacePoint;

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

public interface SurfaceGenerationAlgorithm  {
	
	List<SurfacePoint<Number,Number,Number>> generate(String surfaceType, String instance, List<Long> quoteIds, LocalDate quoteDate, QuoteSet quoteSet, MultivariateInterpolator interpolator);
	
	List<SurfacePoint<Integer, BigDecimal, BigDecimal>> generate(String surfaceType, String instance, List<String> quoteNames, LocalDate quoteDate, QuoteSet quoteSet, MultivariateInterpolator interpolator, List<BigDecimal> deltas);

}
