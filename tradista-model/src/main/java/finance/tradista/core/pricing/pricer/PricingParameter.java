package finance.tradista.core.pricing.pricer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.model.CurrencyPair;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.marketdata.model.FXCurve;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.QuoteSet;

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

public class PricingParameter extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5219479844754106015L;

	public PricingParameter() {
		super();
		params = new HashMap<String, String>();
		indexCurves = new HashMap<Index, InterestRateCurve>();
		discountCurves = new HashMap<Currency, InterestRateCurve>();
		fxCurves = new HashMap<CurrencyPair, FXCurve>();
		customPricers = new HashMap<String, String>();
		modules = new ArrayList<PricingParameterModule>();
	}

	public PricingParameter(String name, QuoteSet quoteSet, LegalEntity po) {
		super();
		params = new HashMap<String, String>();
		indexCurves = new HashMap<Index, InterestRateCurve>();
		discountCurves = new HashMap<Currency, InterestRateCurve>();
		fxCurves = new HashMap<CurrencyPair, FXCurve>();
		customPricers = new HashMap<String, String>();
		modules = new ArrayList<PricingParameterModule>();
		this.name = name;
		this.quoteSet = quoteSet;
		this.processingOrg = po;
	}

	private String name;

	private QuoteSet quoteSet;

	private LegalEntity processingOrg;

	private List<PricingParameterModule> modules;

	private Map<Currency, InterestRateCurve> discountCurves;

	private Map<Index, InterestRateCurve> indexCurves;

	private Map<CurrencyPair, FXCurve> fxCurves;

	private Map<String, String> params;

	private Map<String, String> customPricers;

	public QuoteSet getQuoteSet() {
		return quoteSet;
	}

	public void setQuoteSet(QuoteSet quoteSet) {
		this.quoteSet = quoteSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public LegalEntity getProcessingOrg() {
		return processingOrg;
	}

	public void setProcessingOrg(LegalEntity processingOrg) {
		this.processingOrg = processingOrg;
	}

	public Map<Currency, InterestRateCurve> getDiscountCurves() {
		return discountCurves;
	}

	public void setDiscountCurves(Map<Currency, InterestRateCurve> discountCurves) {
		this.discountCurves = discountCurves;
	}

	public Map<Index, InterestRateCurve> getIndexCurves() {
		return indexCurves;
	}

	public void setIndexCurves(Map<Index, InterestRateCurve> indexCurves) {
		this.indexCurves = indexCurves;
	}

	public Map<CurrencyPair, FXCurve> getFxCurves() {
		return fxCurves;
	}

	public void setFxCurves(Map<CurrencyPair, FXCurve> fxCurves) {
		this.fxCurves = fxCurves;
	}

	public Map<String, String> getCustomPricers() {
		return customPricers;
	}

	public void setCustomPricers(Map<String, String> customPricers) {
		this.customPricers = customPricers;
	}

	public String toString() {
		return name;
	}

	public List<PricingParameterModule> getModules() {
		return modules;
	}

	public void setModules(List<PricingParameterModule> modules) {
		this.modules = modules;
	}

	public InterestRateCurve getDiscountCurve(Currency currency) {
		if (currency == null) {
			return null;
		}
		return discountCurves.get(currency);
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
		PricingParameter other = (PricingParameter) obj;
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

}