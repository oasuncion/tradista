package finance.tradista.web.demo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.service.TradeBusinessDelegate;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.model.EquityTrade;

/*
 * Copyright 2022 Olivier Asuncion
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
public class TradesListView implements Serializable {

	private static final long serialVersionUID = -4314139362201924213L;
	private List<EquityTrade> trades;
	private List<EquityTrade> filteredTrades;
	private PositionDefinition posDef;

	@PostConstruct
	public void init() {
		posDef = new PositionDefinition(StringUtils.EMPTY, LoginView.getCurrentUser().getProcessingOrg());
		posDef.setProductType(Equity.EQUITY);
		try {
			posDef.setBook(new BookBusinessDelegate().getBookByName("Demo Book"));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
		loadTrades();
	}

	public List<EquityTrade> getTrades() {
		return trades;
	}

	public List<EquityTrade> getFilteredTrades() {
		return filteredTrades;
	}

	public void setFilteredTrades(List<EquityTrade> filteredTrades) {
		this.filteredTrades = filteredTrades;
	}

	public void loadTrades() {
		Set<Trade<?>> tradesSet = new TradeBusinessDelegate().getTrades(posDef);
		if (tradesSet != null) {
			trades = tradesSet.stream().map(trade -> (EquityTrade) trade).collect(Collectors.toList());
		}
	}

}