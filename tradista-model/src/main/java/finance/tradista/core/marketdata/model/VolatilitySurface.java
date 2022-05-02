package finance.tradista.core.marketdata.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.legalentity.model.LegalEntity;

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

public abstract class VolatilitySurface<X extends Number, Y extends Number, Z extends Number> extends TradistaObject
		implements MarketData, Generable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8759394476672552715L;

	protected List<SurfacePoint<X, Y, Z>> points;

	private String name;

	private LegalEntity processingOrg;

	private String algorithm;

	private String instance;

	private String interpolator;

	private LocalDate quoteDate;

	private List<Quote> quotes;

	private QuoteSet quoteSet;

	public VolatilitySurface(String name) {
		setName(name);
		points = new ArrayList<SurfacePoint<X, Y, Z>>();
	}

	public VolatilitySurface() {
		points = new ArrayList<SurfacePoint<X, Y, Z>>();
	}

	public QuoteSet getQuoteSet() {
		return quoteSet;
	}

	public void setQuoteSet(QuoteSet quoteSet) {
		this.quoteSet = quoteSet;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LegalEntity getProcessingOrg() {
		return processingOrg;
	}

	public void setProcessingOrg(LegalEntity processingOrg) {
		this.processingOrg = processingOrg;
	}

	@Override
	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public boolean isGenerated() {
		return algorithm != null;
	}

	@Override
	public String getInstance() {
		return instance;
	}

	@Override
	public void setInstance(String instance) {
		this.instance = instance;
	}

	@Override
	public String getInterpolator() {
		return interpolator;
	}

	@Override
	public void setInterpolator(String interpolator) {
		this.interpolator = interpolator;
	}

	@Override
	public LocalDate getQuoteDate() {
		return quoteDate;
	}

	@Override
	public void setQuoteDate(LocalDate quoteDate) {
		this.quoteDate = quoteDate;
	}

	public List<SurfacePoint<X, Y, Z>> getPoints() {
		return points;
	}

	public void setPoints(List<SurfacePoint<X, Y, Z>> points) {
		this.points = points;
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
		VolatilitySurface<?, ?, ?> other = (VolatilitySurface<?, ?, ?>) obj;
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

	@Override
	public String toString() {
		return name;
	}

}