package finance.tradista.core.common.servicelocator;

import static finance.tradista.core.common.util.TradistaConstants.CORE_PACKAGE;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import finance.tradista.ai.agent.service.AssetManagerAgentService;
import finance.tradista.ai.agent.service.MandateService;
import finance.tradista.ai.reasoning.common.service.FormulaService;
import finance.tradista.ai.reasoning.fol.service.FolFormulaService;
import finance.tradista.core.batch.service.BatchService;
import finance.tradista.core.book.service.BookService;
import finance.tradista.core.calendar.service.CalendarService;
import finance.tradista.core.cashinventory.service.CashInventoryService;
import finance.tradista.core.common.service.InformationService;
import finance.tradista.core.configuration.service.ConfigurationService;
import finance.tradista.core.currency.service.CurrencyService;
import finance.tradista.core.dailypnl.service.DailyPnlService;
import finance.tradista.core.daterule.service.DateRuleService;
import finance.tradista.core.daycountconvention.service.DayCountConventionService;
import finance.tradista.core.error.service.ErrorService;
import finance.tradista.core.exchange.service.ExchangeService;
import finance.tradista.core.index.service.IndexService;
import finance.tradista.core.legalentity.service.LegalEntityService;
import finance.tradista.core.marketdata.service.CurveService;
import finance.tradista.core.marketdata.service.FXCurveService;
import finance.tradista.core.marketdata.service.FeedService;
import finance.tradista.core.marketdata.service.InterestRateCurveService;
import finance.tradista.core.marketdata.service.MarketDataConfigurationService;
import finance.tradista.core.marketdata.service.MarketDataInformationService;
import finance.tradista.core.marketdata.service.MarketDataService;
import finance.tradista.core.marketdata.service.QuoteService;
import finance.tradista.core.marketdata.service.SurfaceService;
import finance.tradista.core.position.service.PositionCalculationErrorService;
import finance.tradista.core.position.service.PositionDefinitionService;
import finance.tradista.core.position.service.PositionService;
import finance.tradista.core.pricing.service.PricerService;
import finance.tradista.core.product.service.ProductService;
import finance.tradista.core.productinventory.service.ProductInventoryService;
import finance.tradista.core.trade.service.TradeService;
import finance.tradista.core.transfer.service.FixingErrorService;
import finance.tradista.core.transfer.service.TransferService;
import finance.tradista.core.user.service.UserService;
import finance.tradista.fx.common.service.FXInformationService;
import finance.tradista.fx.fx.service.FXPricerService;
import finance.tradista.fx.fx.service.FXTradeService;
import finance.tradista.fx.fxndf.service.FXNDFPricerService;
import finance.tradista.fx.fxndf.service.FXNDFTradeService;
import finance.tradista.fx.fxoption.service.FXOptionPricerService;
import finance.tradista.fx.fxoption.service.FXOptionTradeService;
import finance.tradista.fx.fxoption.service.FXVolatilitySurfaceService;
import finance.tradista.fx.fxswap.service.FXSwapPricerService;
import finance.tradista.fx.fxswap.service.FXSwapTradeService;
import finance.tradista.ir.ccyswap.service.CcySwapPricerService;
import finance.tradista.ir.ccyswap.service.CcySwapTradeService;
import finance.tradista.ir.common.service.IRInformationService;
import finance.tradista.ir.fra.service.FRAPricerService;
import finance.tradista.ir.fra.service.FRATradeService;
import finance.tradista.ir.future.service.FutureContractSpecificationService;
import finance.tradista.ir.future.service.FuturePricerService;
import finance.tradista.ir.future.service.FutureService;
import finance.tradista.ir.future.service.FutureTradeService;
import finance.tradista.ir.ircapfloorcollar.service.IRCapFloorCollarPricerService;
import finance.tradista.ir.ircapfloorcollar.service.IRCapFloorCollarTradeService;
import finance.tradista.ir.irswap.service.IRSwapPricerService;
import finance.tradista.ir.irswap.service.IRSwapTradeService;
import finance.tradista.ir.irswapoption.service.IRSwapOptionPricerService;
import finance.tradista.ir.irswapoption.service.IRSwapOptionTradeService;
import finance.tradista.ir.irswapoption.service.SwaptionVolatilitySurfaceService;
import finance.tradista.mm.common.service.MMInformationService;
import finance.tradista.mm.loandeposit.service.LoanDepositPricerService;
import finance.tradista.mm.loandeposit.service.LoanDepositTradeService;
import finance.tradista.security.bond.service.BondPricerService;
import finance.tradista.security.bond.service.BondService;
import finance.tradista.security.bond.service.BondTradeService;
import finance.tradista.security.common.service.SecurityInformationService;
import finance.tradista.security.equity.service.EquityPricerService;
import finance.tradista.security.equity.service.EquityService;
import finance.tradista.security.equity.service.EquityTradeService;
import finance.tradista.security.equityoption.service.EquityOptionContractSpecificationService;
import finance.tradista.security.equityoption.service.EquityOptionPricerService;
import finance.tradista.security.equityoption.service.EquityOptionService;
import finance.tradista.security.equityoption.service.EquityOptionTradeService;
import finance.tradista.security.equityoption.service.EquityOptionVolatilitySurfaceService;

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

public class TradistaServiceLocator {

	private static TradistaServiceLocator instance = new TradistaServiceLocator();

	private static BondService bondService;

	private static BondTradeService bondTradeService;

	private static MarketDataService marketDataService;

	private static LegalEntityService legalEntityService;

	private static PricerService pricerService;

	private static QuoteService quoteService;

	private static CurrencyService currencyService;

	private static BookService bookService;

	private static UserService userService;

	private static CalendarService calendarService;

	private static DateRuleService dateRuleService;

	private static ExchangeService exchangeService;

	private static InterestRateCurveService interestRateCurveService;

	private static FXCurveService fxCurveService;

	private static CurveService curveService;

	private static DayCountConventionService dayCountConventionService;

	private static IndexService indexService;

	private static CcySwapTradeService ccySwapTradeService;

	private static EquityService equityService;

	private static EquityTradeService equityTradeService;

	private static EquityOptionTradeService equityOptionTradeService;

	private static EquityOptionService equityOptionService;

	private static EquityOptionContractSpecificationService equityOptionSpecificationService;

	private static EquityOptionVolatilitySurfaceService equityOptionVolatilitySurfaceService;

	private static FRATradeService fraTradeService;

	private static FutureService futureService;

	private static FutureContractSpecificationService futureContractSpecificationService;

	private static FutureTradeService futureTradeService;

	private static FXNDFTradeService fxNDFTradeService;

	private static FXOptionTradeService fxOptionTradeService;

	private static FXTradeService fxTradeService;

	private static FXSwapTradeService fxSwapTradeService;

	private static IRCapFloorCollarTradeService irCapFloorCollarTradeService;

	private static IRSwapOptionTradeService irSwapOptionTradeService;

	private static IRSwapTradeService irSwapTradeService;

	private static FeedService feedService;

	private static FXVolatilitySurfaceService fxVolatilitySurfaceService;

	private static SwaptionVolatilitySurfaceService swaptionVolatilitySurfaceService;

	private static LoanDepositTradeService loanDepositService;

	private static TradeService tradeService;

	private static ProductService productService;

	private static MarketDataConfigurationService marketDataConfigurationService;

	private static BatchService batchService;

	private static FXPricerService fxPricerService;

	private static FXNDFPricerService fxNdfPricerService;

	private static FXOptionPricerService fxOptionPricerService;

	private static FXSwapPricerService fxSwapPricerService;

	private static CcySwapPricerService ccySwapPricerService;

	private static FRAPricerService fraPricerService;

	private static FuturePricerService futurePricerService;

	private static IRSwapOptionPricerService irSwapOptionPricerService;

	private static IRSwapPricerService irSwapPricerService;

	private static IRCapFloorCollarPricerService irCapFloorCollarPricerService;

	private static LoanDepositPricerService loanDepositPricerService;

	private static BondPricerService bondPricerService;

	private static EquityPricerService equityPricerService;

	private static EquityOptionPricerService equityOptionPricerService;

	private static SurfaceService surfaceService;

	private static PositionDefinitionService positionDefinitionService;

	private static PositionCalculationErrorService positionCalculationErrorService;

	private static FixingErrorService fixingErrorService;

	private static ErrorService errorService;

	private static PositionService positionService;

	private static DailyPnlService dailyPnlService;

	private static ProductInventoryService productInventoryService;

	private static CashInventoryService cashInventoryService;

	private static InformationService informationService;

	private static MarketDataInformationService marketDataInformationService;

	private static FXInformationService fxInformationService;

	private static IRInformationService irInformationService;

	private static MMInformationService mmInformationService;

	private static SecurityInformationService securityInformationService;

	private static ConfigurationService configurationService;

	private static FolFormulaService folFormulaService;

	private static FormulaService formulaService;

	private static MandateService mandateService;

	private static AssetManagerAgentService assetManagerAgentService;

	private static TransferService transferService;

	private static final String APP = "app";

	private static final String CORE_EJB = "core-ejb";

	private static final String SECURITY_EJB = "security-ejb";

	private static final String IR_EJB = "ir-ejb";

	private static final String FX_EJB = "fx-ejb";

	private static final String MM_EJB = "mm-ejb";

	private static final String SECURITY_COMMON_SERVICE_PACKAGE = "finance.tradista.security.common.service";

	private static final String BOND_SERVICE_PACKAGE = "finance.tradista.security.bond.service";

	private static final String EQUITY_SERVICE_PACKAGE = "finance.tradista.security.equity.service";

	private static final String EQUITY_OPTION_SERVICE_PACKAGE = "finance.tradista.security.equityoption.service";

	private static final String IR_COMMON_SERVICE_PACKAGE = "finance.tradista.ir.common.service";

	private static final String CCY_SWAP_SERVICE_PACKAGE = "finance.tradista.ir.ccyswap.service";

	private static final String FRA_SERVICE_PACKAGE = "finance.tradista.ir.fra.service";

	private static final String FUTURE_SERVICE_PACKAGE = "finance.tradista.ir.future.service";

	private static final String IR_CAP_FLOOR_COLLAR_SERVICE_PACKAGE = "finance.tradista.ir.ircapfloorcollar.service";

	private static final String IR_SWAP_OPTION_SERVICE_PACKAGE = "finance.tradista.ir.irswapoption.service";

	private static final String IR_SWAP_SERVICE_PACKAGE = "finance.tradista.ir.irswap.service";

	private static final String FX_SERVICE_PACKAGE = "finance.tradista.fx.fx.service";

	private static final String FX_COMMON_SERVICE_PACKAGE = "finance.tradista.fx.common.service";

	private static final String FX_NDF_SERVICE_PACKAGE = "finance.tradista.fx.fxndf.service";

	private static final String FX_OPTION_SERVICE_PACKAGE = "finance.tradista.fx.fxoption.service";

	private static final String FX_SWAP_SERVICE_PACKAGE = "finance.tradista.fx.fxswap.service";

	private static final String LOAN_DEPOSIT_SERVICE_PACKAGE = "finance.tradista.mm.loandeposit.service";

	private static final String MM_COMMON_SERVICE_PACKAGE = "finance.tradista.mm.common.service";

	private static final String MARKET_DATA_APP = "marketdata-app";

	private static final String MARKET_DATA_EJB = "marketdata-ejb";

	private static final String MARKET_DATA_SERVICE_PACKAGE = "finance.tradista.core.marketdata.service";

	private static final String POSITION_APP = "position-app";

	private static final String POSITION_EJB = "position-ejb";

	private static final String POSITION_SERVICE_PACKAGE = "finance.tradista.core.position.service";

	private static final String TRANSFER_SERVICE_PACKAGE = "finance.tradista.core.transfer.service";

	private static final String CONFIGURATION_SERVICE_PACKAGE = "finance.tradista.core.configuration.service";

	private static final String DAILY_PNL_SERVICE_PACKAGE = "finance.tradista.core.dailypnl.service";

	private static final String PRODUCT_INVENTORY_SERVICE_PACKAGE = "finance.tradista.core.productinventory.service";

	private static final String CASH_INVENTORY_SERVICE_PACKAGE = "finance.tradista.core.cashinventory.service";

	private static final String COMMON_SERVICE_PACKAGE = "finance.tradista.core.common.service";

	private static final String AI_APP = "ai-app";

	private static final String AI_EJB = "ai-ejb";

	private static final String FOL_SERVICE_PACKAGE = "finance.tradista.ai.reasoning.fol.service";

	private static final String AI_COMMON_SERVICE_PACKAGE = "finance.tradista.ai.reasoning.common.service";

	private static final String AGENT_SERVICE_PACKAGE = "finance.tradista.ai.agent.service";

	private static final String TRANSFER_APP = "transfer-app";

	private static final String TRANSFER_EJB = "transfer-ejb";

	private static final String EJB_PREFIX = "ejb:";

	private static final String JAVA_GLOBAL_PREFIX = "java:global";

	private static final String USER_SERVICE_PACKAGE = CORE_PACKAGE + ".user.service";

	private Context context;

	private TradistaServiceLocator() {
		try {
			context = new InitialContext();
		} catch (NamingException ne) {
			ne.printStackTrace();
		}
	}

	public static TradistaServiceLocator getInstance() {
		return instance;
	}

	public MarketDataService getMarketDataService() {

		if (marketDataService != null) {
			return marketDataService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/MarketDataServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".MarketDataService";
		try {
			marketDataService = (MarketDataService) context.lookup(ejbString);
			return marketDataService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public QuoteService getQuoteService() {

		if (quoteService != null) {
			return quoteService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/QuoteServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".QuoteService";
		try {
			quoteService = (QuoteService) context.lookup(ejbString);
			return quoteService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ConfigurationService getConfigurationService() {

		if (configurationService != null) {
			return configurationService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/ConfigurationServiceBean!" + CONFIGURATION_SERVICE_PACKAGE
				+ ".ConfigurationService";
		try {
			configurationService = (ConfigurationService) context.lookup(ejbString);
			return configurationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FeedService getFeedService() {

		if (feedService != null) {
			return feedService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/FeedServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".FeedService";
		try {
			feedService = (FeedService) context.lookup(ejbString);
			return feedService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public LegalEntityService getLegalEntityService() {

		if (legalEntityService != null) {
			return legalEntityService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/LegalEntityServiceBean!" + CORE_PACKAGE
				+ ".legalentity.service.LegalEntityService";
		try {
			legalEntityService = (LegalEntityService) context.lookup(ejbString);
			return legalEntityService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public DayCountConventionService getDayCountConventionService() {

		if (dayCountConventionService != null) {
			return dayCountConventionService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/DayCountConventionServiceBean!" + CORE_PACKAGE
				+ ".daycountconvention.service.DayCountConventionService";
		try {
			dayCountConventionService = (DayCountConventionService) context.lookup(ejbString);
			return dayCountConventionService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IndexService getIndexService() {

		if (indexService != null) {
			return indexService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/IndexServiceBean!" + CORE_PACKAGE
				+ ".index.service.IndexService";
		try {
			indexService = (IndexService) context.lookup(ejbString);
			return indexService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public TradeService getTradeService() {

		if (tradeService != null) {
			return tradeService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/TradeServiceBean!" + CORE_PACKAGE
				+ ".trade.service.TradeService";
		try {
			tradeService = (TradeService) context.lookup(ejbString);
			return tradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ProductService getProductService() {

		if (productService != null) {
			return productService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/ProductServiceBean!" + CORE_PACKAGE
				+ ".product.service.ProductService";
		try {
			productService = (ProductService) context.lookup(ejbString);
			return productService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PositionDefinitionService getPositionDefinitionService() {

		if (positionDefinitionService != null) {
			return positionDefinitionService;
		}

		String ejbString = "ejb:" + POSITION_APP + "/" + POSITION_EJB + "/PositionDefinitionServiceBean!"
				+ POSITION_SERVICE_PACKAGE + ".PositionDefinitionService";
		try {
			positionDefinitionService = (PositionDefinitionService) context.lookup(ejbString);
			return positionDefinitionService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PositionCalculationErrorService getPositionCalculationErrorService() {

		if (positionCalculationErrorService != null) {
			return positionCalculationErrorService;
		}

		String ejbString = "ejb:" + POSITION_APP + "/" + POSITION_EJB + "/PositionCalculationErrorServiceBean!"
				+ POSITION_SERVICE_PACKAGE + ".PositionCalculationErrorService";
		try {
			positionCalculationErrorService = (PositionCalculationErrorService) context.lookup(ejbString);
			return positionCalculationErrorService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FixingErrorService getFixingErrorService() {

		if (fixingErrorService != null) {
			return fixingErrorService;
		}

		String ejbString = "ejb:" + TRANSFER_APP + "/" + TRANSFER_EJB + "/FixingErrorServiceBean!"
				+ TRANSFER_SERVICE_PACKAGE + ".FixingErrorService";
		try {
			fixingErrorService = (FixingErrorService) context.lookup(ejbString);
			return fixingErrorService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ErrorService getErrorService() {

		if (errorService != null) {
			return errorService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/ErrorServiceBean!" + CORE_PACKAGE
				+ ".error.service.ErrorService";
		try {
			errorService = (ErrorService) context.lookup(ejbString);
			return errorService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PositionService getPositionService() {

		if (positionService != null) {
			return positionService;
		}

		String ejbString = "ejb:" + POSITION_APP + "/" + POSITION_EJB + "/PositionServiceBean!"
				+ POSITION_SERVICE_PACKAGE + ".PositionService";
		try {
			positionService = (PositionService) context.lookup(ejbString);
			return positionService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FolFormulaService getFolFormulaService() {

		if (folFormulaService != null) {
			return folFormulaService;
		}

		String ejbString = "ejb:" + AI_APP + "/" + AI_EJB + "/FolFormulaServiceBean!" + FOL_SERVICE_PACKAGE
				+ ".FolFormulaService";
		try {
			folFormulaService = (FolFormulaService) context.lookup(ejbString);
			return folFormulaService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FormulaService getFormulaService() {

		if (formulaService != null) {
			return formulaService;
		}

		String ejbString = "ejb:" + AI_APP + "/" + AI_EJB + "/FormulaServiceBean!" + AI_COMMON_SERVICE_PACKAGE
				+ ".FormulaService";
		try {
			formulaService = (FormulaService) context.lookup(ejbString);
			return formulaService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public MandateService getMandateService() {

		if (mandateService != null) {
			return mandateService;
		}

		String ejbString = "ejb:" + AI_APP + "/" + AI_EJB + "/MandateServiceBean!" + AGENT_SERVICE_PACKAGE
				+ ".MandateService";
		try {
			mandateService = (MandateService) context.lookup(ejbString);
			return mandateService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public AssetManagerAgentService getAssetManagerAgentService() {

		if (assetManagerAgentService != null) {
			return assetManagerAgentService;
		}

		String ejbString = "ejb:" + AI_APP + "/" + AI_EJB + "/AssetManagerAgentServiceBean!" + AGENT_SERVICE_PACKAGE
				+ ".AssetManagerAgentService";
		try {
			assetManagerAgentService = (AssetManagerAgentService) context.lookup(ejbString);
			return assetManagerAgentService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public DailyPnlService getDailyPnlService() {

		if (dailyPnlService != null) {
			return dailyPnlService;
		}

		String ejbString = "ejb:" + POSITION_APP + "/" + POSITION_EJB + "/DailyPnlServiceBean!"
				+ DAILY_PNL_SERVICE_PACKAGE + ".DailyPnlService";
		try {
			dailyPnlService = (DailyPnlService) context.lookup(ejbString);
			return dailyPnlService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ProductInventoryService getProductInventoryService() {

		if (productInventoryService != null) {
			return productInventoryService;
		}

		String ejbString = "ejb:" + POSITION_APP + "/" + POSITION_EJB + "/ProductInventoryServiceBean!"
				+ PRODUCT_INVENTORY_SERVICE_PACKAGE + ".ProductInventoryService";
		try {
			productInventoryService = (ProductInventoryService) context.lookup(ejbString);
			return productInventoryService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CurrencyService getCurrencyService() {

		if (currencyService != null) {
			return currencyService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/CurrencyServiceBean!" + CORE_PACKAGE
				+ ".currency.service.CurrencyService";
		try {
			currencyService = (CurrencyService) context.lookup(ejbString);
			return currencyService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BookService getBookService() {

		if (bookService != null) {
			return bookService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/BookServiceBean!" + CORE_PACKAGE
				+ ".book.service.BookService";
		try {
			bookService = (BookService) context.lookup(ejbString);
			return bookService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public UserService getUserService() {
		return (UserService) getService(userService, APP, CORE_EJB, USER_SERVICE_PACKAGE, "UserService");
	}

	public CalendarService getCalendarService() {

		if (calendarService != null) {
			return calendarService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/CalendarServiceBean!" + CORE_PACKAGE
				+ ".calendar.service.CalendarService";
		try {
			calendarService = (CalendarService) context.lookup(ejbString);
			return calendarService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public DateRuleService getDateRuleService() {

		if (dateRuleService != null) {
			return dateRuleService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/DateRuleServiceBean!" + CORE_PACKAGE
				+ ".daterule.service.DateRuleService";
		try {
			dateRuleService = (DateRuleService) context.lookup(ejbString);
			return dateRuleService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ExchangeService getExchangeService() {

		if (exchangeService != null) {
			return exchangeService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/ExchangeServiceBean!" + CORE_PACKAGE
				+ ".exchange.service.ExchangeService";
		try {
			exchangeService = (ExchangeService) context.lookup(ejbString);
			return exchangeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PricerService getPricerService() {

		if (pricerService != null) {
			return pricerService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/PricerServiceBean!" + CORE_PACKAGE
				+ ".pricing.service.PricerService";
		try {
			pricerService = (PricerService) context.lookup(ejbString);
			return pricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public InterestRateCurveService getInterestRateCurveService() {

		if (interestRateCurveService != null) {
			return interestRateCurveService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/InterestRateCurveServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".InterestRateCurveService";
		try {
			interestRateCurveService = (InterestRateCurveService) context.lookup(ejbString);
			return interestRateCurveService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXCurveService getFXCurveService() {

		if (fxCurveService != null) {
			return fxCurveService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/FXCurveServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".FXCurveService";
		try {
			fxCurveService = (FXCurveService) context.lookup(ejbString);
			return fxCurveService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CurveService getCurveService() {

		if (curveService != null) {
			return curveService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/CurveServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".CurveService";
		try {
			curveService = (CurveService) context.lookup(ejbString);
			return curveService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BondService getBondService() {

		if (bondService != null) {
			return bondService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/BondServiceBean!" + BOND_SERVICE_PACKAGE
				+ ".BondService";
		try {
			bondService = (BondService) context.lookup(ejbString);
			return bondService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityService getEquityService() {

		if (equityService != null) {
			return equityService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityServiceBean!" + EQUITY_SERVICE_PACKAGE
				+ ".EquityService";
		try {
			equityService = (EquityService) context.lookup(ejbString);
			return equityService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityTradeService getEquityTradeService() {

		if (equityTradeService != null) {
			return equityTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityTradeServiceBean!" + EQUITY_SERVICE_PACKAGE
				+ ".EquityTradeService";
		try {
			equityTradeService = (EquityTradeService) context.lookup(ejbString);
			return equityTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityPricerService getEquityPricerService() {

		if (equityPricerService != null) {
			return equityPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityPricerServiceBean!" + EQUITY_SERVICE_PACKAGE
				+ ".EquityPricerService";
		try {
			equityPricerService = (EquityPricerService) context.lookup(ejbString);
			return equityPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityOptionTradeService getEquityOptionTradeService() {

		if (equityOptionTradeService != null) {
			return equityOptionTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityOptionTradeServiceBean!"
				+ EQUITY_OPTION_SERVICE_PACKAGE + ".EquityOptionTradeService";
		try {
			equityOptionTradeService = (EquityOptionTradeService) context.lookup(ejbString);
			return equityOptionTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityOptionContractSpecificationService getEquityOptionSpecificationService() {

		if (equityOptionSpecificationService != null) {
			return equityOptionSpecificationService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityOptionContractSpecificationServiceBean!"
				+ EQUITY_OPTION_SERVICE_PACKAGE + ".EquityOptionContractSpecificationService";
		try {
			equityOptionSpecificationService = (EquityOptionContractSpecificationService) context.lookup(ejbString);
			return equityOptionSpecificationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityOptionService getEquityOptionService() {

		if (equityOptionService != null) {
			return equityOptionService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityOptionServiceBean!"
				+ EQUITY_OPTION_SERVICE_PACKAGE + ".EquityOptionService";
		try {
			equityOptionService = (EquityOptionService) context.lookup(ejbString);
			return equityOptionService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityOptionPricerService getEquityOptionPricerService() {

		if (equityOptionPricerService != null) {
			return equityOptionPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityOptionPricerServiceBean!"
				+ EQUITY_OPTION_SERVICE_PACKAGE + ".EquityOptionPricerService";
		try {
			equityOptionPricerService = (EquityOptionPricerService) context.lookup(ejbString);
			return equityOptionPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquityOptionVolatilitySurfaceService getEquityOptionVolatilitySurfaceService() {

		if (equityOptionVolatilitySurfaceService != null) {
			return equityOptionVolatilitySurfaceService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/EquityOptionVolatilitySurfaceServiceBean!"
				+ EQUITY_OPTION_SERVICE_PACKAGE + ".EquityOptionVolatilitySurfaceService";
		try {
			equityOptionVolatilitySurfaceService = (EquityOptionVolatilitySurfaceService) context.lookup(ejbString);
			return equityOptionVolatilitySurfaceService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BondTradeService getBondTradeService() {

		if (bondTradeService != null) {
			return bondTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/BondTradeServiceBean!" + BOND_SERVICE_PACKAGE
				+ ".BondTradeService";
		try {
			bondTradeService = (BondTradeService) context.lookup(ejbString);
			return bondTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BondPricerService getBondPricerService() {

		if (bondPricerService != null) {
			return bondPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/BondPricerServiceBean!" + BOND_SERVICE_PACKAGE
				+ ".BondPricerService";
		try {
			bondPricerService = (BondPricerService) context.lookup(ejbString);
			return bondPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CcySwapTradeService getCcySwapTradeService() {

		if (ccySwapTradeService != null) {
			return ccySwapTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/CcySwapTradeServiceBean!" + CCY_SWAP_SERVICE_PACKAGE
				+ ".CcySwapTradeService";
		try {
			ccySwapTradeService = (CcySwapTradeService) context.lookup(ejbString);
			return ccySwapTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CcySwapPricerService getCcySwapPricerService() {

		if (ccySwapPricerService != null) {
			return ccySwapPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/CcySwapPricerServiceBean!" + CCY_SWAP_SERVICE_PACKAGE
				+ ".CcySwapPricerService";
		try {
			ccySwapPricerService = (CcySwapPricerService) context.lookup(ejbString);
			return ccySwapPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FRATradeService getFRATradeService() {

		if (fraTradeService != null) {
			return fraTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/FRATradeServiceBean!" + FRA_SERVICE_PACKAGE
				+ ".FRATradeService";
		try {
			fraTradeService = (FRATradeService) context.lookup(ejbString);
			return fraTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FRAPricerService getFRAPricerService() {

		if (fraPricerService != null) {
			return fraPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/FRAPricerServiceBean!" + FRA_SERVICE_PACKAGE
				+ ".FRAPricerService";
		try {
			fraPricerService = (FRAPricerService) context.lookup(ejbString);
			return fraPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IRCapFloorCollarTradeService getIRCapFloorCollarTradeService() {

		if (irCapFloorCollarTradeService != null) {
			return irCapFloorCollarTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/IRCapFloorCollarTradeServiceBean!"
				+ IR_CAP_FLOOR_COLLAR_SERVICE_PACKAGE + ".IRCapFloorCollarTradeService";
		try {
			irCapFloorCollarTradeService = (IRCapFloorCollarTradeService) context.lookup(ejbString);
			return irCapFloorCollarTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IRCapFloorCollarPricerService getIRCapFloorCollarPricerService() {

		if (irCapFloorCollarPricerService != null) {
			return irCapFloorCollarPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/IRCapFloorCollarPricerServiceBean!"
				+ IR_CAP_FLOOR_COLLAR_SERVICE_PACKAGE + ".IRCapFloorCollarPricerService";
		try {
			irCapFloorCollarPricerService = (IRCapFloorCollarPricerService) context.lookup(ejbString);
			return irCapFloorCollarPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IRSwapOptionTradeService getIRSwapOptionTradeService() {

		if (irSwapOptionTradeService != null) {
			return irSwapOptionTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/IRSwapOptionTradeServiceBean!"
				+ IR_SWAP_OPTION_SERVICE_PACKAGE + ".IRSwapOptionTradeService";
		try {
			irSwapOptionTradeService = (IRSwapOptionTradeService) context.lookup(ejbString);
			return irSwapOptionTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IRSwapOptionPricerService getIRSwapOptionPricerService() {

		if (irSwapOptionPricerService != null) {
			return irSwapOptionPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/IRSwapOptionPricerServiceBean!"
				+ IR_SWAP_OPTION_SERVICE_PACKAGE + ".IRSwapOptionPricerService";
		try {
			irSwapOptionPricerService = (IRSwapOptionPricerService) context.lookup(ejbString);
			return irSwapOptionPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IRSwapTradeService getIRSwapTradeService() {

		if (irSwapTradeService != null) {
			return irSwapTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/IRSwapTradeServiceBean!" + IR_SWAP_SERVICE_PACKAGE
				+ ".IRSwapTradeService";
		try {
			irSwapTradeService = (IRSwapTradeService) context.lookup(ejbString);
			return irSwapTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IRSwapPricerService getIRSwapPricerService() {

		if (irSwapPricerService != null) {
			return irSwapPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/IRSwapPricerServiceBean!" + IR_SWAP_SERVICE_PACKAGE
				+ ".IRSwapPricerService";
		try {
			irSwapPricerService = (IRSwapPricerService) context.lookup(ejbString);
			return irSwapPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SwaptionVolatilitySurfaceService getSwaptionVolatilitySurfaceService() {

		if (swaptionVolatilitySurfaceService != null) {
			return swaptionVolatilitySurfaceService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/SwaptionVolatilitySurfaceServiceBean!"
				+ IR_SWAP_OPTION_SERVICE_PACKAGE + ".SwaptionVolatilitySurfaceService";
		try {
			swaptionVolatilitySurfaceService = (SwaptionVolatilitySurfaceService) context.lookup(ejbString);
			return swaptionVolatilitySurfaceService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FutureService getFutureService() {

		if (futureService != null) {
			return futureService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/FutureServiceBean!" + FUTURE_SERVICE_PACKAGE
				+ ".FutureService";
		try {
			futureService = (FutureService) context.lookup(ejbString);
			return futureService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FutureContractSpecificationService getFutureContractSpecificationService() {

		if (futureContractSpecificationService != null) {
			return futureContractSpecificationService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/FutureContractSpecificationServiceBean!"
				+ FUTURE_SERVICE_PACKAGE + ".FutureContractSpecificationService";
		try {
			futureContractSpecificationService = (FutureContractSpecificationService) context.lookup(ejbString);
			return futureContractSpecificationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FutureTradeService getFutureTradeService() {

		if (futureTradeService != null) {
			return futureTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/FutureTradeServiceBean!" + FUTURE_SERVICE_PACKAGE
				+ ".FutureTradeService";
		try {
			futureTradeService = (FutureTradeService) context.lookup(ejbString);
			return futureTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FuturePricerService getFuturePricerService() {

		if (futurePricerService != null) {
			return futurePricerService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/FuturePricerServiceBean!" + FUTURE_SERVICE_PACKAGE
				+ ".FuturePricerService";
		try {
			futurePricerService = (FuturePricerService) context.lookup(ejbString);
			return futurePricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXNDFTradeService getFXNDFTradeService() {

		if (fxNDFTradeService != null) {
			return fxNDFTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXNDFTradeServiceBean!" + FX_NDF_SERVICE_PACKAGE
				+ ".FXNDFTradeService";
		try {
			fxNDFTradeService = (FXNDFTradeService) context.lookup(ejbString);
			return fxNDFTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXPricerService getFXPricerService() {

		if (fxPricerService != null) {
			return fxPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXPricerServiceBean!" + FX_SERVICE_PACKAGE
				+ ".FXPricerService";
		try {
			fxPricerService = (FXPricerService) context.lookup(ejbString);
			return fxPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXOptionPricerService getFXOptionPricerService() {

		if (fxOptionPricerService != null) {
			return fxOptionPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXOptionPricerServiceBean!" + FX_OPTION_SERVICE_PACKAGE
				+ ".FXOptionPricerService";
		try {
			fxOptionPricerService = (FXOptionPricerService) context.lookup(ejbString);
			return fxOptionPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXNDFPricerService getFXNDFPricerService() {

		if (fxNdfPricerService != null) {
			return fxNdfPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXNDFPricerServiceBean!" + FX_NDF_SERVICE_PACKAGE
				+ ".FXNDFPricerService";
		try {
			fxNdfPricerService = (FXNDFPricerService) context.lookup(ejbString);
			return fxNdfPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXSwapPricerService getFXSwapPricerService() {

		if (fxSwapPricerService != null) {
			return fxSwapPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXSwapPricerServiceBean!" + FX_SWAP_SERVICE_PACKAGE
				+ ".FXSwapPricerService";
		try {
			fxSwapPricerService = (FXSwapPricerService) context.lookup(ejbString);
			return fxSwapPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXOptionTradeService getFXOptionTradeService() {

		if (fxOptionTradeService != null) {
			return fxOptionTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXOptionTradeServiceBean!" + FX_OPTION_SERVICE_PACKAGE
				+ ".FXOptionTradeService";
		try {
			fxOptionTradeService = (FXOptionTradeService) context.lookup(ejbString);
			return fxOptionTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXVolatilitySurfaceService getFXVolatilitySurfaceService() {

		if (fxVolatilitySurfaceService != null) {
			return fxVolatilitySurfaceService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXVolatilitySurfaceServiceBean!" + FX_OPTION_SERVICE_PACKAGE
				+ ".FXVolatilitySurfaceService";
		try {
			fxVolatilitySurfaceService = (FXVolatilitySurfaceService) context.lookup(ejbString);
			return fxVolatilitySurfaceService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXSwapTradeService getFXSwapTradeService() {

		if (fxSwapTradeService != null) {
			return fxSwapTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXSwapTradeServiceBean!" + FX_SWAP_SERVICE_PACKAGE
				+ ".FXSwapTradeService";
		try {
			fxSwapTradeService = (FXSwapTradeService) context.lookup(ejbString);
			return fxSwapTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXTradeService getFXTradeService() {

		if (fxTradeService != null) {
			return fxTradeService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXTradeServiceBean!" + FX_SERVICE_PACKAGE
				+ ".FXTradeService";
		try {
			fxTradeService = (FXTradeService) context.lookup(ejbString);
			return fxTradeService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public LoanDepositTradeService getLoanDepositService() {

		if (loanDepositService != null) {
			return loanDepositService;
		}

		String ejbString = "ejb:" + APP + "/" + MM_EJB + "/LoanDepositTradeServiceBean!" + LOAN_DEPOSIT_SERVICE_PACKAGE
				+ ".LoanDepositTradeService";
		try {
			loanDepositService = (LoanDepositTradeService) context.lookup(ejbString);
			return loanDepositService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public LoanDepositPricerService getLoanDepositPricerService() {

		if (loanDepositPricerService != null) {
			return loanDepositPricerService;
		}

		String ejbString = "ejb:" + APP + "/" + MM_EJB + "/LoanDepositPricerServiceBean!" + LOAN_DEPOSIT_SERVICE_PACKAGE
				+ ".LoanDepositPricerService";
		try {
			loanDepositPricerService = (LoanDepositPricerService) context.lookup(ejbString);
			return loanDepositPricerService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public MarketDataConfigurationService getMarketDataConfigurationService() {
		if (marketDataConfigurationService != null) {
			return marketDataConfigurationService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/MarketDataConfigurationServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".MarketDataConfigurationService";
		try {
			marketDataConfigurationService = (MarketDataConfigurationService) context.lookup(ejbString);
			return marketDataConfigurationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BatchService getBatchService() {
		if (batchService != null) {
			return batchService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/BatchServiceBean!" + CORE_PACKAGE
				+ ".batch.service.BatchService";
		try {
			batchService = (BatchService) context.lookup(ejbString);
			return batchService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SurfaceService getSurfaceService() {
		if (surfaceService != null) {
			return surfaceService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/SurfaceServiceBean!" + MARKET_DATA_SERVICE_PACKAGE
				+ ".SurfaceService";
		try {
			surfaceService = (SurfaceService) context.lookup(ejbString);
			return surfaceService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public InformationService getInformationService() {
		if (informationService != null) {
			return informationService;
		}

		String ejbString = "ejb:" + APP + "/" + CORE_EJB + "/InformationServiceBean!" + COMMON_SERVICE_PACKAGE
				+ ".InformationService";
		try {
			informationService = (InformationService) context.lookup(ejbString);
			return informationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public MarketDataInformationService getMarketDataInformationService() {
		if (marketDataInformationService != null) {
			return marketDataInformationService;
		}

		String ejbString = "ejb:" + MARKET_DATA_APP + "/" + MARKET_DATA_EJB + "/MarketDataInformationServiceBean!"
				+ MARKET_DATA_SERVICE_PACKAGE + ".MarketDataInformationService";
		try {
			marketDataInformationService = (MarketDataInformationService) context.lookup(ejbString);
			return marketDataInformationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public FXInformationService getFXInformationService() {
		if (fxInformationService != null) {
			return fxInformationService;
		}

		String ejbString = "ejb:" + APP + "/" + FX_EJB + "/FXInformationServiceBean!" + FX_COMMON_SERVICE_PACKAGE
				+ ".FXInformationService";
		try {
			fxInformationService = (FXInformationService) context.lookup(ejbString);
			return fxInformationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public MMInformationService getMMInformationService() {
		if (mmInformationService != null) {
			return mmInformationService;
		}

		String ejbString = "ejb:" + APP + "/" + MM_EJB + "/MMInformationServiceBean!" + MM_COMMON_SERVICE_PACKAGE
				+ ".MMInformationService";
		try {
			mmInformationService = (MMInformationService) context.lookup(ejbString);
			return mmInformationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IRInformationService getIRInformationService() {
		if (irInformationService != null) {
			return irInformationService;
		}

		String ejbString = "ejb:" + APP + "/" + IR_EJB + "/IRInformationServiceBean!" + IR_COMMON_SERVICE_PACKAGE
				+ ".IRInformationService";
		try {
			irInformationService = (IRInformationService) context.lookup(ejbString);
			return irInformationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SecurityInformationService getSecurityInformationService() {
		if (securityInformationService != null) {
			return securityInformationService;
		}

		String ejbString = "ejb:" + APP + "/" + SECURITY_EJB + "/SecurityInformationServiceBean!"
				+ SECURITY_COMMON_SERVICE_PACKAGE + ".SecurityInformationService";
		try {
			securityInformationService = (SecurityInformationService) context.lookup(ejbString);
			return securityInformationService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CashInventoryService getCashInventoryService() {

		if (cashInventoryService != null) {
			return cashInventoryService;
		}

		String ejbString = "ejb:" + POSITION_APP + "/" + POSITION_EJB + "/CashInventoryServiceBean!"
				+ CASH_INVENTORY_SERVICE_PACKAGE + ".CashInventoryService";
		try {
			cashInventoryService = (CashInventoryService) context.lookup(ejbString);
			return cashInventoryService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public TransferService getTransferService() {

		if (transferService != null) {
			return transferService;
		}

		String ejbString = "ejb:" + TRANSFER_APP + "/" + TRANSFER_EJB + "/TransferServiceBean!"
				+ TRANSFER_SERVICE_PACKAGE + ".TransferService";
		try {
			transferService = (TransferService) context.lookup(ejbString);
			return transferService;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Object getService(Object service, String application, String module, String packageName,
			String serviceName) {

		if (service != null) {
			return service;
		}

		String ejbString = JAVA_GLOBAL_PREFIX + "/" + application + "/" + module + "/" + serviceName + "Bean!"
				+ packageName + "." + serviceName;
//		try {
//			service = context.lookup(ejbString);
//			return service;
//		} catch (NamingException ne) {
//			// TODO Have a log instead
//			ne.printStackTrace();
//		}

		ejbString = EJB_PREFIX + application + "/" + module + "/" + serviceName + "Bean!" + packageName + "."
				+ serviceName;
		try {
			service = context.lookup(ejbString);
			return service;
		} catch (NamingException ne) {
			// TODO Have a log instead
			ne.printStackTrace();
		}
		return null;
	}

}