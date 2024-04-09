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
import finance.tradista.core.pricing.service.PricerService;
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
import finance.tradista.security.bond.service.BondService;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.equity.service.EquityService;
import finance.tradista.security.gcrepo.messaging.GCRepoTradeEvent;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
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

	@PostConstruct
	private void initialize() {
		context = factory.createContext();
		transferBusinessDelegate = new TransferBusinessDelegate();
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
	public Map<Security, Map<Book, BigDecimal>> getAllocatedCollateral(GCRepoTrade trade)
			throws TradistaBusinessException {
		Map<Security, Map<Book, BigDecimal>> securities = null;
		List<Transfer> givenCollateral = null;

		try {
			givenCollateral = transferBusinessDelegate.getTransfers(Type.PRODUCT, Transfer.Status.KNOWN, Direction.PAY,
					TransferPurpose.COLLATERAL_SETTLEMENT, trade.getId(), 0, 0, 0, null, null, null, null, null, null);
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
					Direction.RECEIVE, TransferPurpose.RETURNED_COLLATERAL, trade.getId(), 0, 0, 0, null, null, null,
					null, null, null);
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

}