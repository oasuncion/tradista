package finance.tradista.core.pricing.ui.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.service.CurrencyBusinessDelegate;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.pricing.pricer.Pricer;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.service.PricerBusinessDelegate;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/*
 * Copyright 2023 Olivier Asuncion
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

@Named
@ViewScoped
public class PricingController implements Serializable {

	private static final long serialVersionUID = 1681707647567789611L;

	private Pricer pricer;

	private PricingParameter pricingParameter;

	private Set<PricingParameter> allPricingParameters;

	private PricerMeasure pricerMeasure;

	private List<PricerMeasure> allPricerMeasures;

	private QuoteSet quoteSet;

	private LocalDate pricingDate;

	private Set<QuoteSet> allQuoteSets;

	private PricerBusinessDelegate pricerBusinessDelegate;

	private QuoteBusinessDelegate quoteBusinessDelegate;

	private CurrencyBusinessDelegate currencyBusinessDelegate;

	private String pricingMethod;

	private List<String> allPricingMethods;

	private Currency pricingCurrency;

	private Set<Currency> allCurrencies;

	private BigDecimal pricerResult;

	@PostConstruct
	public void init() {
		pricerBusinessDelegate = new PricerBusinessDelegate();
		quoteBusinessDelegate = new QuoteBusinessDelegate();
		currencyBusinessDelegate = new CurrencyBusinessDelegate();
		allPricingParameters = pricerBusinessDelegate.getAllPricingParameters();
		allQuoteSets = quoteBusinessDelegate.getAllQuoteSets();
		allCurrencies = currencyBusinessDelegate.getAllCurrencies();
		pricingDate = LocalDate.now();
		if (allPricingParameters != null && !allPricingParameters.isEmpty()) {
			pricingParameter = allPricingParameters.stream().findFirst().get();
		}
		updatePricer();
		updatePricerMeasures();
		if (allPricerMeasures != null && !allPricerMeasures.isEmpty()) {
			pricerMeasure = allPricerMeasures.stream().findFirst().get();
		}
		updatePricingMethods();
		if (allPricingMethods != null && !allPricingMethods.isEmpty()) {
			pricingMethod = allPricingMethods.stream().findFirst().get();
		}
	}

	public Pricer getPricer() {
		return pricer;
	}

	public void setPricer(Pricer pricer) {
		pricer = this.pricer;
	}

	public PricingParameter getPricingParameter() {
		return pricingParameter;
	}

	public void setPricingParameter(PricingParameter pricingParameter) {
		this.pricingParameter = pricingParameter;
	}

	public Set<PricingParameter> getAllPricingParameters() {
		return allPricingParameters;
	}

	public void setAllPricingParameters(Set<PricingParameter> allPricingParameters) {
		this.allPricingParameters = allPricingParameters;
	}

	public PricerMeasure getPricerMeasure() {
		return pricerMeasure;
	}

	public void setPricerMeasure(PricerMeasure pricerMeasure) {
		this.pricerMeasure = pricerMeasure;
	}

	public List<PricerMeasure> getAllPricerMeasures() {
		return allPricerMeasures;
	}

	public void setAllPricerMeasures(List<PricerMeasure> allPricerMeasures) {
		this.allPricerMeasures = allPricerMeasures;
	}

	public String getPricingMethod() {
		return pricingMethod;
	}

	public void setPricingMethod(String pricingMethod) {
		this.pricingMethod = pricingMethod;
	}

	public List<String> getAllPricingMethods() {
		return allPricingMethods;
	}

	public void setAllPricingMethods(List<String> allPricingMethods) {
		this.allPricingMethods = allPricingMethods;
	}

	public QuoteSet getQuoteSet() {
		return quoteSet;
	}

	public void setQuoteSet(QuoteSet quoteSet) {
		this.quoteSet = quoteSet;
	}

	public LocalDate getPricingDate() {
		return pricingDate;
	}

	public void setPricingDate(LocalDate pricingDate) {
		this.pricingDate = pricingDate;
	}

	public Set<QuoteSet> getAllQuoteSets() {
		return allQuoteSets;
	}

	public void setAllQuoteSets(Set<QuoteSet> allQuoteSets) {
		this.allQuoteSets = allQuoteSets;
	}

	public Currency getPricingCurrency() {
		return pricingCurrency;
	}

	public void setPricingCurrency(Currency pricingCurrency) {
		this.pricingCurrency = pricingCurrency;
	}

	public Set<Currency> getAllCurrencies() {
		return allCurrencies;
	}

	public void setAllCurrencies(Set<Currency> allCurrencies) {
		this.allCurrencies = allCurrencies;
	}

	public BigDecimal getPricerResult() {
		return pricerResult;
	}

	public void setPricerResult(BigDecimal pricerResult) {
		this.pricerResult = pricerResult;
	}

	public void price(GCRepoTrade gcRepoTrade) {
		try {
			pricerResult = pricerBusinessDelegate.calculate(gcRepoTrade, pricingParameter, pricingCurrency, pricingDate,
					pricerMeasure, pricingMethod);
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public void updatePricerMeasures() {
		if (pricer == null) {
			allPricerMeasures = null;
		} else {
			allPricerMeasures = pricer.getPricerMeasures();
		}
	}

	public void updatePricingMethods() {
		if (pricerMeasure == null) {
			allPricingMethods = null;
		} else {
			allPricingMethods = pricerBusinessDelegate.getAllPricingMethods(pricerMeasure);
		}
	}

	public void updatePricer() {
		if (pricingParameter == null) {
			pricer = null;
		} else {
			try {
				pricer = pricerBusinessDelegate.getPricer(GCRepoTrade.GC_REPO, pricingParameter);
			} catch (TradistaBusinessException tbe) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
			}
		}
	}

}