package finance.tradista.core.pricing.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.marketdata.model.FXCurve;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.pricing.pricer.Pricer;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.Pricing;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.pricer.PricingParameterModule;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.trade.model.Trade;

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

public class PricerBusinessDelegate {

	private PricerService pricerService;

	private Map<String, PricingParameterModuleValidator> validators;

	public PricerBusinessDelegate() {
		pricerService = TradistaServiceLocator.getInstance().getPricerService();
		validators = new HashMap<String, PricingParameterModuleValidator>();
		PricingParameterModuleValidator validator = null;
		try {
			validator = TradistaUtil.getInstance(PricingParameterModuleValidator.class,
					"finance.tradista.security.equityoption.validator.PricingParameterDividendYieldCurveModuleValidator");
			validators.put("finance.tradista.security.equityoption.model.PricingParameterDividendYieldCurveModule",
					validator);
		} catch (TradistaTechnicalException tte) {
			// TODO Add log info
		}
		try {
			validator = TradistaUtil.getInstance(PricingParameterModuleValidator.class,
					"finance.tradista.fx.common.validator.PricingParameterUnrealizedPnlCalculationModuleValidator");
			validators.put("finance.tradista.fx.common.model.PricingParameterUnrealizedPnlCalculationModule",
					validator);
		} catch (TradistaTechnicalException tte) {
			// TODO Add log info
		}
		try {
			validator = TradistaUtil.getInstance(PricingParameterModuleValidator.class,
					"finance.tradista.fx.fxoption.validator.PricingParameterVolatilitySurfaceModuleValidator");
			validators.put("finance.tradista.fx.fxoption.model.PricingParameterVolatilitySurfaceModule", validator);
		} catch (TradistaTechnicalException tte) {
			// TODO Add log info
		}
		try {
			validator = TradistaUtil.getInstance(PricingParameterModuleValidator.class,
					"finance.tradista.ir.irswapoption.validator.PricingParameterVolatilitySurfaceModuleValidator");
			validators.put("finance.tradista.ir.irswapoption.model.PricingParameterVolatilitySurfaceModule", validator);
		} catch (TradistaTechnicalException tte) {
			// TODO Add log info
		}
		try {
			validator = TradistaUtil.getInstance(PricingParameterModuleValidator.class,
					"finance.tradista.security.equityoption.validator.PricingParameterVolatilitySurfaceModuleValidator");
			validators.put("finance.tradista.security.equityoption.model.PricingParameterVolatilitySurfaceModule",
					validator);
		} catch (TradistaTechnicalException tte) {
			// TODO Add log info
		}
	}

	public PricingParameter getPricingParameterById(long id) {
		return SecurityUtil.run(() -> pricerService.getPricingParameterById(id));
	}

	public PricingParameter getPricingParameterByNameAndPoId(String name, long poId) throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		if (StringUtils.isEmpty(name)) {
			errMsg.append(String.format("The name is mandatory."));
		} 
		if (poId < 0) {
			errMsg.append(String.format("The po id (%s) cannot be negative.", poId));
		} 
		if (!errMsg.isEmpty()) {
			throw new TradistaBusinessException("The name is mandatory.");
		}
		return SecurityUtil.run(() -> pricerService.getPricingParameterByNameAndPoId(name, poId));
	}

	public Set<PricingParameter> getAllPricingParameters() {
		return SecurityUtil.run(() -> pricerService.getAllPricingParameters());
	}

	public long savePricingParameter(PricingParameter param) throws TradistaBusinessException {
		if (param == null) {
			throw new TradistaBusinessException("The Pricing Parameter Set cannot be null.");
		}
		StringBuilder errMsg = new StringBuilder();
		if (StringUtils.isEmpty(param.getName())) {
			errMsg.append(String.format("Please select a Pricing Parameters Set Name.%n"));
		} else {
			if (param.getName().length() > 20) {
				errMsg.append(String.format("The Pricing Parameters Set Name cannot exceed 20 characters.%n"));
			}
		}
		if (param.getQuoteSet() == null) {
			errMsg.append(String.format("Please select a QuoteSet.%n"));
		} else {
			if (param.getProcessingOrg() != null && param.getQuoteSet().getProcessingOrg() != null
					&& !param.getQuoteSet().getProcessingOrg().equals(param.getProcessingOrg())) {
				errMsg.append(
						String.format("the Pricing Parameters Set's PO and the QuoteSet's PO should be the same.%n"));
			}
			if (param.getProcessingOrg() == null && param.getQuoteSet().getProcessingOrg() != null) {
				errMsg.append(String
						.format("If the Pricing Parameters Set is a global one, the QuoteSet must also be global.%n"));
			}
			if (param.getProcessingOrg() != null && param.getQuoteSet().getProcessingOrg() == null) {
				errMsg.append(String
						.format("If the QuoteSet is a global one, the Pricing Parameters Set must also be global.%n"));
			}
		}
		if (param.getDiscountCurves() != null && !param.getDiscountCurves().isEmpty()) {
			for (InterestRateCurve curve : param.getDiscountCurves().values()) {
				if (param.getProcessingOrg() != null && curve.getProcessingOrg() != null
						&& !curve.getProcessingOrg().equals(param.getProcessingOrg())) {
					errMsg.append(String.format(
							"the Pricing Parameters Set's PO and the Discount curve %s's PO should be the same.%n",
							curve));
				}
				if (param.getProcessingOrg() == null && curve.getProcessingOrg() != null) {
					errMsg.append(String.format(
							"If the Pricing Parameters Set is a global one, the the Discount curve %s must also be global.%n",
							curve));
				}
				if (param.getProcessingOrg() != null && curve.getProcessingOrg() == null) {
					errMsg.append(String.format(
							"If the the Discount curve %s is a global one, the Pricing Parameters Set must also be global.%n",
							curve));
				}
			}
		}
		if (param.getIndexCurves() != null && !param.getIndexCurves().isEmpty()) {
			for (InterestRateCurve curve : param.getIndexCurves().values()) {
				if (param.getProcessingOrg() != null && curve.getProcessingOrg() != null
						&& !curve.getProcessingOrg().equals(param.getProcessingOrg())) {
					errMsg.append(String.format(
							"the Pricing Parameters Set's PO and the Index curve %s's PO should be the same.%n",
							curve));
				}
				if (param.getProcessingOrg() == null && curve.getProcessingOrg() != null) {
					errMsg.append(String.format(
							"If the Pricing Parameters Set is a global one, the Index curve %s must also be global.%n",
							curve));
				}
				if (param.getProcessingOrg() != null && curve.getProcessingOrg() == null) {
					errMsg.append(String.format(
							"If the Index curve %s is a global one, the Pricing Parameters Set must also be global.%n",
							curve));
				}
			}
		}
		if (param.getFxCurves() != null && !param.getFxCurves().isEmpty()) {
			for (FXCurve curve : param.getFxCurves().values()) {
				if (param.getProcessingOrg() != null && curve.getProcessingOrg() != null
						&& !curve.getProcessingOrg().equals(param.getProcessingOrg())) {
					errMsg.append(String.format(
							"the Pricing Parameters Set's PO and the FX curve %s's PO should be the same.%n", curve));
				}
				if (param.getProcessingOrg() == null && curve.getProcessingOrg() != null) {
					errMsg.append(String.format(
							"If the Pricing Parameters Set is a global one, the FX curve %s must also be global.%n",
							curve));
				}
				if (param.getProcessingOrg() != null && curve.getProcessingOrg() == null) {
					errMsg.append(String.format(
							"If the FX curve %s is a global one, the Pricing Parameters Set must also be global.%n",
							curve));
				}
			}
		}

		if (param.getModules() != null && param.getModules().isEmpty()) {
			for (PricingParameterModule module : param.getModules()) {
				PricingParameterModuleValidator validator = getValidator(module);
				try {
					validator.validateModule(module, param);
				} catch (TradistaBusinessException abe) {
					errMsg.append(abe.getMessage());
				}
			}
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		return SecurityUtil.runEx(() -> pricerService.savePricingParameter(param));
	}

	public PricingParameterModuleValidator getValidator(PricingParameterModule module) {
		return validators.get(module.getClass().getName());
	}

	public boolean deletePricingParameter(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The id must be positive.");
		}
		return SecurityUtil.runEx(() -> pricerService.deletePricingParameter(id));
	}

	public Pricer getPricer(String product, PricingParameter pricingParameter) throws TradistaBusinessException {
		return SecurityUtil.runEx(() -> pricerService.getPricer(product, pricingParameter));
	}

	public List<String> getAllPricingMethods(PricerMeasure pm) {
		return SecurityUtil.run(() -> pricerService.getAllPricingMethods(pm));
	}

	public BigDecimal calculate(Trade<? extends Product> trade, Pricer pricer, PricingParameter pp, Currency currency,
			LocalDate date, String measure) throws TradistaBusinessException {

		StringBuilder sBuilder = new StringBuilder();
		if (trade == null) {
			sBuilder.append("The trade is mandatory.\n");
		}
		if (pricer == null) {
			sBuilder.append("The pricer is mandatory.\n");
		}
		if (pp == null) {
			sBuilder.append("The pricing parameters set is mandatory.\n");
		}
		if (currency == null) {
			sBuilder.append("The currency is mandatory.\n");
		}
		if (date == null) {
			sBuilder.append("The date is mandatory.\n");
		}
		if (StringUtils.isEmpty(measure)) {
			sBuilder.append("The measure name is mandatory.\n");
		}

		if (sBuilder.length() > 0) {
			throw new TradistaBusinessException(sBuilder.toString());
		}

		PricerMeasure pm = null;
		for (PricerMeasure currentPm : pricer.getPricerMeasures()) {
			if (currentPm.toString().equals(measure)) {
				pm = currentPm;
				break;
			}
		}
		try {
			return (BigDecimal) getMeasureMethod(pm.getClass(), pp, measure, trade).invoke(pm, pp, trade, currency,
					date);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			throw new TradistaBusinessException(e.getCause().getMessage());
		}
		return null;
	}

	public BigDecimal calculate(Trade<? extends Product> trade, PricingParameter pp, Currency currency, LocalDate date,
			PricerMeasure measure, String methodName) throws TradistaBusinessException {

		StringBuilder sBuilder = new StringBuilder();
		if (trade == null) {
			sBuilder.append("The trade is mandatory.\n");
		}
		if (pp == null) {
			sBuilder.append("The pricing parameters set is mandatory.\n");
		}
		if (currency == null) {
			sBuilder.append("The currency is mandatory.\n");
		}
		if (date == null) {
			sBuilder.append("The date is mandatory.\n");
		}
		if (measure == null) {
			sBuilder.append("The measure is mandatory.\n");
		}
		if (StringUtils.isEmpty(methodName)) {
			sBuilder.append("The method name is mandatory.\n");
		}

		if (sBuilder.length() > 0) {
			throw new TradistaBusinessException(sBuilder.toString());
		}

		try {
			return (BigDecimal) measure.getClass()
					.getMethod(methodName, PricingParameter.class, trade.getClass(), Currency.class, LocalDate.class)
					.invoke(measure, pp, trade, currency, date);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			throw new TradistaBusinessException(e.getCause().getMessage());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BigDecimal calculate(Product product, Book book, Pricer pricer, PricingParameter pp, Currency currency,
			LocalDate date, String measure) throws TradistaBusinessException {

		StringBuilder sBuilder = new StringBuilder();
		if (product == null) {
			sBuilder.append("The trade is mandatory.\n");
		}
		if (pricer == null) {
			sBuilder.append("The pricer is mandatory.\n");
		}
		if (pp == null) {
			sBuilder.append("The pricing parameters set is mandatory.\n");
		}
		if (currency == null) {
			sBuilder.append("The currency is mandatory.\n");
		}
		if (date == null) {
			sBuilder.append("The date is mandatory.\n");
		}
		if (StringUtils.isEmpty(measure)) {
			sBuilder.append("The measure name is mandatory.\n");
		}

		if (sBuilder.length() > 0) {
			throw new TradistaBusinessException(sBuilder.toString());
		}

		PricerMeasure pm = null;
		for (PricerMeasure currentPm : pricer.getProductPricerMeasures()) {
			if (currentPm.toString().equals(measure)) {
				pm = currentPm;
				break;
			}
		}
		try {
			return (BigDecimal) getProductMeasureMethod(pm.getClass(), pp, book, measure, product).invoke(pm, pp,
					product, book, currency, date);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			throw new TradistaBusinessException(e.getCause().getMessage());
		}
		return null;
	}

	private Method getMeasureMethod(Class<? extends PricerMeasure> pmClass, PricingParameter pp, String measure,
			Trade<? extends Product> trade) {

		String bookName = trade.getBook() != null ? trade.getBook().getName() : "";
		// First, we look in the PP if the method of the measure is specified
		// for this given book
		String value = pp.getParams().get(bookName + "." + trade.getProductType() + "." + measure + "Method");
		// Then, if it doesn't exist, we look in the PP if the method of the
		// measure is specified for all the books
		if (value == null) {
			value = pp.getParams().get(trade.getProductType() + "." + measure + "Method");
		}
		if (value != null) {
			try {
				return pmClass.getMethod(value, PricingParameter.class, trade.getClass(), Currency.class,
						LocalDate.class);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// If the NPV method is not specified, we use the default one
		for (Method m : pmClass.getMethods()) {
			if (m.isAnnotationPresent(Pricing.class)) {
				Pricing pr = m.getAnnotation(Pricing.class);
				switch (measure) {
				case "PNL": {
					if (pr.defaultPNL()) {
						return m;
					}
				}
				case "REALIZED_PNL": {
					if (pr.defaultREALIZED_PNL()) {
						return m;
					}
				}
				case "UNREALIZED_PNL": {
					if (pr.defaultUNREALIZED_PNL()) {
						return m;
					}
				}
				}
			}
		}
		return null;
	}

	private Method getProductMeasureMethod(Class<? extends PricerMeasure> pmClass, PricingParameter pp, Book book,
			String measure, Product product) {

		String bookName = book != null ? book.getName() : "";
		// First, we look in the PP if the method of the measure is specified
		// for this given book
		String value = pp.getParams().get(bookName + "." + product.getProductType() + "." + measure + "Method");
		// Then, if it doesn't exist, we look in the PP if the method of the
		// measure is specified for all the books
		if (value == null) {
			value = pp.getParams().get(product.getProductType() + "." + measure + "Method");
		}
		if (value != null) {
			try {
				return pmClass.getMethod(value, PricingParameter.class, Product.class, Book.class, Currency.class,
						LocalDate.class);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// If the method is not specified, we use the default one
		for (Method m : pmClass.getMethods()) {
			if (m.isAnnotationPresent(Pricing.class)) {
				Pricing pr = m.getAnnotation(Pricing.class);
				switch (measure) {
				case "PNL": {
					if (pr.defaultPNL()) {
						return m;
					}
				}
				case "REALIZED_PNL": {
					if (pr.defaultREALIZED_PNL()) {
						return m;
					}
				}
				case "UNREALIZED_PNL": {
					if (pr.defaultUNREALIZED_PNL()) {
						return m;
					}
				}
				}
			}
		}
		return null;
	}

	public Set<String> getPricingParametersSetByQuoteSetId(long quoteSetId) throws TradistaBusinessException {
		if (quoteSetId <= 0) {
			throw new TradistaBusinessException("The Quote Set id must be positive.");
		}
		return SecurityUtil.run(() -> pricerService.getPricingParametersSetByQuoteSetId(quoteSetId));
	}

	public List<CashFlow> generateCashFlows(long tradeId, PricingParameter pp, LocalDate valueDate)
			throws TradistaBusinessException {

		StringBuffer errMsg = new StringBuffer();

		if (tradeId <= 0) {
			errMsg.append(String.format("Trade id must be positive but it is %s.", tradeId));
		}
		if (pp == null) {
			errMsg.append(String.format("The Pricing Parameters Set is mandatory.%n"));
		}

		if (valueDate == null) {
			errMsg.append(String.format("The value Date is mandatory."));
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		return SecurityUtil.runEx(() -> pricerService.generateCashFlows(tradeId, pp, valueDate));
	}

	public List<CashFlow> generateCashFlows(PricingParameter pp, LocalDate valueDate, long positionDefinitionId)
			throws TradistaBusinessException {

		StringBuffer errMsg = new StringBuffer();

		if (positionDefinitionId <= 0) {
			errMsg.append(String.format("The Position Definition id must be positive.%n"));
		}

		if (pp == null) {
			errMsg.append(String.format("The Pricing Parameters Set is mandatory.%n"));
		}

		if (valueDate == null) {
			errMsg.append(String.format("The value Date is mandatory."));
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

		return SecurityUtil.runEx(() -> pricerService.generateCashFlows(pp, valueDate, positionDefinitionId));
	}

	public List<CashFlow> generateAllCashFlows(PricingParameter pp, LocalDate valueDate)
			throws TradistaBusinessException {
		StringBuffer errMsg = new StringBuffer();

		if (pp == null) {
			errMsg.append(String.format("The Pricing Parameters Set is mandatory.%n"));
		}

		if (valueDate == null) {
			errMsg.append(String.format("The value Date is mandatory."));
		}

		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}

		return SecurityUtil.runEx(() -> pricerService.generateAllCashFlows(pp, valueDate));
	}

}