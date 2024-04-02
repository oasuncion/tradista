package finance.tradista.security.gcrepo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookService;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.pricing.service.PricerService;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.processingorgdefaults.model.ProcessingOrgDefaults;
import finance.tradista.core.processingorgdefaults.service.ProcessingOrgDefaultsService;
import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.core.transfer.model.ProductTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.Transfer.Direction;
import finance.tradista.core.transfer.model.Transfer.Type;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.core.workflow.model.mapping.StatusMapper;
import finance.tradista.core.workflow.service.WorkflowService;
import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.service.WorkflowManager;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.service.BondService;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.equity.service.EquityService;
import finance.tradista.security.gcrepo.messaging.GCRepoTradeEvent;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.model.ProcessingOrgDefaultsCollateralManagementModule;
import finance.tradista.security.gcrepo.persistence.GCRepoTradeSQL;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTradeMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;

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

@SecurityDomain(value = "other")
@PermitAll
@Stateless
public class GCRepoTradeServiceBean implements GCRepoTradeService {

	private ConnectionFactory factory;

	private JMSContext context;

	private Destination destination;

	private static final String TRADE_DOES_NOT_EXIST = "The trade %d doesn't exist.";

	@EJB
	private WorkflowService workflowService;

	@EJB
	private PricerService pricerService;

	@EJB
	private BondService bondService;

	@EJB
	private EquityService equityService;

	@EJB
	private BookService bookService;

	@EJB
	private ProcessingOrgDefaultsService poDefaultsService;

	private TransferBusinessDelegate transferBusinessDelegate;

	private QuoteBusinessDelegate quoteBusinessDelegate;

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
		transferBusinessDelegate = new TransferBusinessDelegate();
		quoteBusinessDelegate = new QuoteBusinessDelegate();
	}

	@Interceptors({ GCRepoProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
	@Override
	public long saveGCRepoTrade(GCRepoTrade trade, String action) throws TradistaBusinessException {
		GCRepoTradeEvent event = new GCRepoTradeEvent();
		long result;
		if (trade.getId() != 0) {
			GCRepoTrade oldTrade = GCRepoTradeSQL.getTradeById(trade.getId());
			event.setOldTrade(oldTrade);
		}

		// Checking business consistency of collateral to add
		StringBuilder errMsg = new StringBuilder();
		if (trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty()) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : trade.getCollateralToAdd().entrySet()) {
				// 1. Security must exist
				Security secInDb = bondService.getBondById(entry.getKey().getId());
				if (secInDb == null) {
					secInDb = equityService.getEquityById(entry.getKey().getId());
				}
				if (secInDb == null) {
					errMsg.append(String.format(
							"The security %s cannot be found in the system, it cannot be added as collateral.%n",
							entry.getKey()));
					continue;
				}
				// 2. Security must be part of the GC Basket
				if (!trade.getGcBasket().getSecurities().contains(entry.getKey())) {
					errMsg.append(String.format(
							"The security %s cannot be found in the GC Basket %s, it cannot be added as collateral.%n",
							entry.getKey(), trade.getGcBasket().getName()));
					continue;
				}
				// 3. Books should exist
				Map<Book, BigDecimal> bookMap = entry.getValue();
				for (Map.Entry<Book, BigDecimal> bookEntry : bookMap.entrySet()) {
					Book bookInDb = bookService.getBookById(bookEntry.getKey().getId());
					if (bookInDb == null) {
						errMsg.append(String.format(
								"The origin book %s cannot be found in the system, it cannot be used as collateral source.%n",
								bookEntry.getKey().getName()));
					}
				}
			}

		}

		// Checking business consistency of collateral to remove
		if (trade.getCollateralToRemove() != null && !trade.getCollateralToRemove().isEmpty()) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : trade.getCollateralToRemove().entrySet()) {
				// 1. Security must exist
				Security secInDb = bondService.getBondById(entry.getKey().getId());
				if (secInDb == null) {
					secInDb = equityService.getEquityById(entry.getKey().getId());
				}
				if (secInDb == null) {
					errMsg.append(String.format(
							"The security %s cannot be found in the system, it cannot be removed from collateral.%n",
							entry.getKey()));
					continue;
				}
				// 2. Books should exist
				Map<Book, BigDecimal> bookMap = entry.getValue();
				for (Map.Entry<Book, BigDecimal> bookEntry : bookMap.entrySet()) {
					Book bookInDb = bookService.getBookById(bookEntry.getKey().getId());
					if (bookInDb == null) {
						errMsg.append(String.format(
								"The  book %s cannot be found in the system, it cannot be used as collateral source.%n",
								bookEntry.getKey().getName()));
					}
				}
			}
		}

		if (!errMsg.isEmpty()) {
			throw new TradistaBusinessException(errMsg.toString());
		}

		if (!StringUtils.isEmpty(action)) {
			try {
				Workflow workflow = WorkflowManager.getWorkflowByName(trade.getWorkflow());
				finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade mappedTrade = GCRepoTradeMapper.map(trade,
						workflow);
				mappedTrade = WorkflowManager.applyAction(mappedTrade, action);
				trade.setStatus(StatusMapper.map(mappedTrade.getStatus()));
			} catch (TradistaFlowBusinessException tfbe) {
				throw new TradistaBusinessException(tfbe);
			}
		}

		event.setTrade(trade);
		event.setAppliedAction(action);
		result = GCRepoTradeSQL.saveGCRepoTrade(trade);
		context.createProducer().send(destination, event);

		return result;
	}

	@PreDestroy
	private void clean() {
		context.close();
	}

	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	@Override
	public GCRepoTrade getGCRepoTradeById(long id) {
		return GCRepoTradeSQL.getTradeById(id);
	}

	@Override
	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	public Map<Security, Map<Book, BigDecimal>> getAllocatedCollateral(long tradeId) throws TradistaBusinessException {

		GCRepoTrade trade = getGCRepoTradeById(tradeId);

		if (trade == null) {
			throw new TradistaBusinessException(String.format(TRADE_DOES_NOT_EXIST, tradeId));
		}

		Map<Security, Map<Book, BigDecimal>> securities = null;
		List<Transfer> givenCollateral = null;

		try {
			givenCollateral = transferBusinessDelegate.getTransfers(Type.PRODUCT, Transfer.Status.KNOWN, Direction.PAY,
					TransferPurpose.COLLATERAL_SETTLEMENT, tradeId, 0, 0, 0, null, null, null, null, null, null);
		} catch (TradistaBusinessException tbe) {
			// Not expected here.
		}

		if (givenCollateral != null && !givenCollateral.isEmpty()) {
			givenCollateral = givenCollateral.stream()
					.filter(t -> t.getSettlementDate() == null || t.getSettlementDate().isBefore(LocalDate.now())
							|| t.getSettlementDate().isEqual(LocalDate.now()))
					.toList();
			securities = new HashMap<>(givenCollateral.size());
			for (Transfer t : givenCollateral) {
				if (securities.containsKey(t.getProduct())) {
					Map<Book, BigDecimal> bookMap = securities.get(t.getProduct());
					BigDecimal newQty = bookMap.get(trade.getBook()).add(((ProductTransfer) t).getQuantity());
					bookMap.put(trade.getBook(), newQty);
					securities.put((Security) t.getProduct(), bookMap);
				} else {
					Map<Book, BigDecimal> bookMap = new HashMap<>();
					BigDecimal newQty = ((ProductTransfer) t).getQuantity();
					bookMap.put(trade.getBook(), newQty);
					securities.put((Security) t.getProduct(), bookMap);
				}
			}
		}
		List<Transfer> returnedCollateral = null;
		try {
			returnedCollateral = transferBusinessDelegate.getTransfers(Type.PRODUCT, Transfer.Status.KNOWN,
					Direction.RECEIVE, TransferPurpose.RETURNED_COLLATERAL, tradeId, 0, 0, 0, null, null, null, null,
					null, null);
		} catch (TradistaBusinessException tbe) {
			// Not expected here.
		}
		if (returnedCollateral != null && !returnedCollateral.isEmpty()) {
			returnedCollateral = returnedCollateral.stream()
					.filter(t -> t.getSettlementDate() == null || t.getSettlementDate().isBefore(LocalDate.now())
							|| t.getSettlementDate().isEqual(LocalDate.now()))
					.toList();
			if (!returnedCollateral.isEmpty()) {
				if (securities == null) {
					securities = new HashMap<>(returnedCollateral.size());
				}
				for (Transfer t : returnedCollateral) {
					if (securities.containsKey(t.getProduct())) {
						Map<Book, BigDecimal> bookMap = securities.get(t.getProduct());
						BigDecimal newQty = bookMap.get(trade.getBook()).subtract(((ProductTransfer) t).getQuantity());
						bookMap.put(trade.getBook(), newQty);
						securities.put((Security) t.getProduct(), bookMap);
					}
				}
			}
		}

		return securities;
	}

	@Override
	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	public BigDecimal getCollateralMarketToMarket(long tradeId) throws TradistaBusinessException {
		// 1. Get the current collateral

		Map<Security, Map<Book, BigDecimal>> securities = getAllocatedCollateral(tradeId);
		GCRepoTrade trade = getGCRepoTradeById(tradeId);

		if (trade == null) {
			throw new TradistaBusinessException(String.format(TRADE_DOES_NOT_EXIST, tradeId));
		}
		// 2. Get the MTM of the current collateral
		return getCollateralMarketToMarket(securities, trade.getBook().getProcessingOrg());

	}

	@Override
	public BigDecimal getCollateralMarketToMarket(Map<Security, Map<Book, BigDecimal>> securities, LegalEntity po)
			throws TradistaBusinessException {

		BigDecimal mtm = BigDecimal.ZERO;

		ProcessingOrgDefaults poDefaults = poDefaultsService.getProcessingOrgDefaultsByPoId(po.getId());
		QuoteSet qs = ((ProcessingOrgDefaultsCollateralManagementModule) poDefaults
				.getModuleByName(ProcessingOrgDefaultsCollateralManagementModule.COLLATERAL_MANAGEMENT)).getQuoteSet();

		if (qs == null) {
			throw new TradistaBusinessException(
					String.format("The Collateral Quote Set for Processing Org Defaults of PO %s has not been found.",
							po.getShortName()));
		}

		// Calculate the total MTM value of the collateral

		if (securities != null) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : securities.entrySet()) {
				String quoteName = entry.getKey().getProductType() + "." + entry.getKey().getIsin() + "."
						+ entry.getKey().getExchange();
				QuoteValue qv = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(qs.getId(),
						quoteName, entry.getKey().getProductType().equals(Bond.BOND) ? QuoteType.BOND_PRICE
								: QuoteType.EQUITY_PRICE,
						LocalDate.now());
				if (qv == null) {
					throw new TradistaBusinessException(
							String.format("The security price %s could not be found on quote set %s as of %tD",
									quoteName, qs, LocalDate.now()));
				}
				BigDecimal price = qv.getClose() != null ? qv.getClose() : qv.getLast();
				if (price == null) {
					throw new TradistaBusinessException(String.format(
							"The closing or last price of the product %s could not be found on quote set %s as of %tD",
							entry.getKey(), qs, LocalDate.now()));
				}
				for (BigDecimal qty : entry.getValue().values()) {
					mtm = mtm.add(price.multiply(qty));
				}
			}
		}

		return mtm;

	}

	@Override
	@Interceptors(TradeAuthorizationFilteringInterceptor.class)
	public BigDecimal getExposure(long tradeId) throws TradistaBusinessException {
		BigDecimal exposure;
		BigDecimal rate;

		GCRepoTrade trade = getGCRepoTradeById(tradeId);

		if (trade == null) {
			throw new TradistaBusinessException(String.format(TRADE_DOES_NOT_EXIST, tradeId));
		}

		// Calculate the required exposure

		if (trade.isFixedRepoRate()) {
			rate = trade.getRepoRate();
		} else {
			ProcessingOrgDefaults poDefaults = poDefaultsService
					.getProcessingOrgDefaultsByPoId(trade.getBook().getProcessingOrg().getId());
			QuoteSet qs = ((ProcessingOrgDefaultsCollateralManagementModule) poDefaults
					.getModuleByName(ProcessingOrgDefaultsCollateralManagementModule.COLLATERAL_MANAGEMENT))
					.getQuoteSet();

			if (qs == null) {
				throw new TradistaBusinessException(String.format(
						"The Collateral Quote Set for Processing Org Defaults of PO %s has not been found.",
						trade.getBook().getProcessingOrg().getShortName()));
			}
			String quoteName = Index.INDEX + "." + trade.getIndex() + "." + trade.getIndexTenor();
			QuoteValue qv = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(qs.getId(), quoteName,
					QuoteType.INTEREST_RATE, LocalDate.now());
			if (qv == null) {
				throw new TradistaBusinessException(String.format(
						"The index %s could not be found on quote set %s as of %tD", quoteName, qs, LocalDate.now()));
			}
			// the index is expected to be defined as quote closing value.
			rate = qv.getClose();
			if (rate == null) {
				throw new TradistaBusinessException(
						String.format("The index %s (closing value) could not be found on quote set %s as of %tD",
								quoteName, qs, LocalDate.now()));
			}
		}

		exposure = trade.getAmount()
				.multiply(rate.multiply(PricerUtil.daysToYear(LocalDate.now(), trade.getEndDate())));

		// Apply the margin rate (by convention, margin rate is noted as follows: 105
		// for 5%)
		BigDecimal marginRate = trade.getMarginRate().divide(BigDecimal.valueOf(100));
		exposure = exposure.multiply(marginRate);

		return exposure;
	}

}