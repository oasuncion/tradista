package finance.tradista.core.pricing.service;

import static finance.tradista.core.common.util.TradistaConstants.TRADISTA_PACKAGE;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.configuration.service.LocalConfigurationService;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.position.service.PositionDefinitionBusinessDelegate;
import finance.tradista.core.position.service.PositionDefinitionProductScopeFilteringInterceptor;
import finance.tradista.core.pricing.persistence.PricingParameterSQL;
import finance.tradista.core.pricing.pricer.Parameterizable;
import finance.tradista.core.pricing.pricer.Pricer;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.product.service.ProductBusinessDelegate;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.persistence.TradeSQL;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;

/*
 * Copyright 2019 Olivier Asuncion
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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class PricerServiceBean implements PricerService {

	@EJB
	private LocalConfigurationService configurationService;

	private ProductBusinessDelegate productBusinessDelegate;

	@PostConstruct
	private void init() {
		productBusinessDelegate = new ProductBusinessDelegate();
	}

	@Interceptors(PricingParameterFilteringInterceptor.class)
	@Override
	public PricingParameter getPricingParameterById(long id) {
		return PricingParameterSQL.getPricingParameterById(id);
	}

	@Interceptors(PricingParameterFilteringInterceptor.class)
	@Override
	public Set<PricingParameter> getAllPricingParameters() {
		return PricingParameterSQL.getAllPricingParameters();
	}

	@Interceptors(PricingParameterFilteringInterceptor.class)
	public long savePricingParameter(PricingParameter param) throws TradistaBusinessException {
		if (param.getId() == 0) {
			checkPricingParameterExistence(param);
		} else {
			PricingParameter oldParam = PricingParameterSQL.getPricingParameterById(param.getId());
			if (!oldParam.getName().equals(param.getName())) {
				checkPricingParameterExistence(param);
			}
		}
		return PricingParameterSQL.savePricingParameter(param);

	}

	private void checkPricingParameterExistence(PricingParameter param) throws TradistaBusinessException {
		if (PricingParameterSQL.getPricingParameterByNameAndPoId(param.getName(),
				param.getProcessingOrg() == null ? 0 : param.getProcessingOrg().getId()) != null) {
			String errMsg;
			if (param.getProcessingOrg() == null) {
				errMsg = "A global pricing parameters set named %s already exists in the system.";
			} else {
				errMsg = "A pricing parameters set named %s already exists in the system for the PO %s.";
			}
			throw new TradistaBusinessException(String.format(errMsg, param.getName(), param.getProcessingOrg()));
		}
	}

	@Interceptors(PricingParameterFilteringInterceptor.class)
	public boolean deletePricingParameter(long id) throws TradistaBusinessException {
		// First, check if there is a position definition depending on this
		// pricing parameters set.
		// If it is the case, we alert the user and do not try to remove the
		// pricing parameter.

		Set<String> positionDefinitions = new PositionDefinitionBusinessDelegate()
				.getPositionDefinitionsByPricingParametersSetId(id);
		if (positionDefinitions != null && !positionDefinitions.isEmpty()) {
			throw new TradistaBusinessException(String.format(
					"Impossible to delete the Pricing Parameters Set with id '%d' because it is used by the following Position definition(s): %s",
					id, Arrays.toString(positionDefinitions.toArray())));
		}
		return PricingParameterSQL.deletePricingParameter(id);

	}

	public Pricer getPricer(String product, PricingParameter pricingParameter) throws TradistaBusinessException {

		if (pricingParameter == null) {
			throw new IllegalArgumentException("Pricing parameter is null");
		}

		String value = pricingParameter.getParams().get(product + ".Pricer");

		// Retrieve Tradista default pricer
		if (value == null) {

			String productType = new ProductBusinessDelegate().getProductFamily(product);

			String className = "finance.tradista." + productType + "." + product.toLowerCase() + ".pricer.Pricer"
					+ product;

			return TradistaUtil.getInstance(Pricer.class, className);
		}

		else {
			// Retrieve Custom Pricer
			return getPricer(configurationService.getCustomPackage(), value);
		}
	}

	private Pricer getPricer(String pckg, String name) {

		List<Class<?>> classes = TradistaUtil.getAllClassesByTypeAndAnnotation(Pricer.class, Parameterizable.class,
				pckg);
		Parameterizable annotation;
		for (Class<?> klass : classes) {
			annotation = klass.getAnnotation(Parameterizable.class);
			if (name.equals(annotation.name())) {
				try {
					return (Pricer) TradistaUtil.getInstance(klass);
				} catch (TradistaTechnicalException tte) {
					// TODO Auto-generated catch block
					tte.printStackTrace();
				}
			}
		}

		return null;

	}

	public List<String> getAllPricingMethods(PricerMeasure pm) {
		final List<String> methods = new ArrayList<String>();
		Class<?> klass = pm.getClass();
		// iterate through the list of methods declared in the class represented
		// by klass variable, and add those annotated with the specified
		// annotation
		final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
		for (final Method method : allMethods) {
			if (method.isAnnotationPresent(Pricing.class)) {
				methods.add(method.getName());
			}
		}
		return methods;
	}

	@Override
	public Set<String> getPricingParametersSetByQuoteSetId(long quoteSetId) {
		return PricingParameterSQL.getPricingParametersSetByQuoteSetId(quoteSetId);
	}

	@SuppressWarnings("unchecked")
	@Interceptors(CashFlowPreFilteringInterceptor.class)
	@Override
	public List<CashFlow> generateCashFlows(long tradeId, PricingParameter pp, LocalDate valueDate)
			throws TradistaBusinessException {
		Trade<?> trade = TradeSQL.getTradeById(tradeId, true);
		try {
			String productFamily = productBusinessDelegate.getProductFamily(trade.getProductType());
			String fullClassName = TRADISTA_PACKAGE + "." + productFamily + "." + trade.getProductType().toLowerCase()
					+ ".service." + trade.getProductType() + "PricerBusinessDelegate";
			return (TradistaUtil.callMethod(fullClassName, List.class, "generateCashFlows", trade, pp, valueDate));
		} catch (TradistaBusinessException tbe) {
			String errMsg = "Trade id " + trade.getId() + ": " + tbe.getMessage();
			throw new TradistaBusinessException(errMsg);
		}
	}

	@Interceptors({ PositionDefinitionProductScopeFilteringInterceptor.class, CashFlowPreFilteringInterceptor.class })
	@SuppressWarnings("unchecked")
	@Override
	public List<CashFlow> generateCashFlows(PricingParameter pp, LocalDate valueDate, long positionDefinitionId)
			throws TradistaBusinessException {
		PositionDefinitionBusinessDelegate positionDefinitionBusinessDelegate = new PositionDefinitionBusinessDelegate();
		PositionDefinition posDef = positionDefinitionBusinessDelegate.getPositionDefinitionById(positionDefinitionId);
		if (posDef == null) {
			throw new TradistaBusinessException(String.format(
					"The position definition wit id %s could not be found in the system.", positionDefinitionId));
		}
		List<CashFlow> cfs = new ArrayList<CashFlow>();
		Set<Trade<?>> trades = TradeSQL.getTrades(posDef);
		if (trades != null && !trades.isEmpty()) {
			for (Trade<?> trade : trades) {
				try {
					String productFamily = productBusinessDelegate.getProductFamily(trade.getProductType());
					String fullClassName = TRADISTA_PACKAGE + "." + productFamily + "."
							+ trade.getProductType().toLowerCase() + ".service." + trade.getProductType()
							+ "PricerBusinessDelegate";
					cfs.addAll(TradistaUtil.callMethod(fullClassName, List.class, "generateCashFlows", trade, pp,
							valueDate));
				} catch (TradistaBusinessException tbe) {
					String errMsg = "Trade id " + trade.getId() + ": " + tbe.getMessage();
					throw new TradistaBusinessException(errMsg);
				}
			}
		}
		Collections.sort(cfs);
		return cfs;
	}

	@Interceptors(CashFlowPreFilteringInterceptor.class)
	@SuppressWarnings("unchecked")
	@Override
	public List<CashFlow> generateAllCashFlows(PricingParameter pp, LocalDate valueDate)
			throws TradistaBusinessException {
		List<Trade<?>> trades = TradeSQL.getAllTrades();
		List<CashFlow> cfs = new ArrayList<CashFlow>();
		if (trades != null && !trades.isEmpty()) {
			for (Trade<?> trade : trades) {
				try {
					String productFamily = productBusinessDelegate.getProductFamily(trade.getProductType());
					String fullClassName = TRADISTA_PACKAGE + "." + productFamily + "."
							+ trade.getProductType().toLowerCase() + ".service." + trade.getProductType()
							+ "PricerBusinessDelegate";
					cfs.addAll(TradistaUtil.callMethod(fullClassName, List.class, "generateCashFlows", trade, pp,
							valueDate));
				} catch (TradistaBusinessException tbe) {
					String errMsg = "Trade id " + trade.getId() + ": " + tbe.getMessage();
					throw new TradistaBusinessException(errMsg);
				}
			}
		}
		Collections.sort(cfs);
		return cfs;
	}

}