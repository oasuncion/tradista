package finance.tradista.security.gcrepo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.ejb3.annotation.SecurityDomain;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.trade.service.TradeAuthorizationFilteringInterceptor;
import finance.tradista.core.transfer.model.ProductTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.core.transfer.model.Transfer.Direction;
import finance.tradista.core.transfer.model.Transfer.Type;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.core.workflow.model.Action;
import finance.tradista.core.workflow.model.mapping.ActionMapper;
import finance.tradista.core.workflow.model.mapping.StatusMapper;
import finance.tradista.core.workflow.service.WorkflowService;
import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.service.WorkflowManager;
import finance.tradista.security.common.model.Security;
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

    private TransferBusinessDelegate transferBusinessDelegate;

    @PostConstruct
    private void initialize() {
	context = factory.createContext();
	transferBusinessDelegate = new TransferBusinessDelegate();
    }

    @Interceptors({ GCRepoProductScopeFilteringInterceptor.class, TradeAuthorizationFilteringInterceptor.class })
    @Override
    public long saveGCRepoTrade(GCRepoTrade trade, Action action) throws TradistaBusinessException {
	GCRepoTradeEvent event = new GCRepoTradeEvent();
	GCRepoTrade resultTrade = null;
	if (trade.getId() != 0) {
	    GCRepoTrade oldTrade = GCRepoTradeSQL.getTradeById(trade.getId());
	    event.setOldTrade(oldTrade);
	}

	if (action != null) {
	    try {
		Workflow workflow = WorkflowManager.getWorkflowByName(trade.getWorkflow());
		finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade mappedTrade = GCRepoTradeMapper.map(trade,
			workflow);
		mappedTrade = WorkflowManager.applyAction(mappedTrade, ActionMapper.map(action, workflow));
		trade.setStatus(StatusMapper.map(mappedTrade.getStatus()));
	    } catch (TradistaFlowBusinessException tfbe) {
		throw new TradistaBusinessException(tfbe);
	    }
	}

	event.setTrade(trade);
	long result = GCRepoTradeSQL.saveGCRepoTrade(trade);

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
    public Map<Security, BigDecimal> getAllocatedCollateral(long tradeId) {
	Map<Security, BigDecimal> securities = null;
	List<Transfer> givenCollateral = null;

	try {
	    givenCollateral = transferBusinessDelegate.getTransfers(Type.PRODUCT, Transfer.Status.KNOWN, Direction.PAY,
		    TransferPurpose.COLLATERAL_SETTLEMENT, tradeId, 0, 0, 0, null, null, null, null, null, null);
	} catch (TradistaBusinessException tbe) {
	    // Not expected here.
	}

	if (givenCollateral != null && !givenCollateral.isEmpty()) {
	    givenCollateral = givenCollateral.stream()
		    .filter(t -> t.getSettlementDate() == null || t.getSettlementDate().isBefore(LocalDate.now()))
		    .toList();
	    securities = new HashMap<>(givenCollateral.size());
	    for (Transfer t : givenCollateral) {
		if (securities.containsKey(t.getProduct())) {
		    BigDecimal newQty = securities.get(t.getProduct()).add(((ProductTransfer) t).getQuantity());
		    securities.put((Security) t.getProduct(), newQty);
		} else {
		    securities.put((Security) t.getProduct(), ((ProductTransfer) t).getQuantity());
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
		    .filter(t -> t.getSettlementDate() == null || t.getSettlementDate().isBefore(LocalDate.now()))
		    .toList();
	    securities = new HashMap<>(returnedCollateral.size());
	    for (Transfer t : returnedCollateral) {
		if (securities.containsKey(t.getProduct())) {
		    BigDecimal newQty = securities.get(t.getProduct()).subtract(((ProductTransfer) t).getQuantity());
		    securities.put((Security) t.getProduct(), newQty);
		}
	    }
	}

	return securities;
    }

}