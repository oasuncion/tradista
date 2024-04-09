package finance.tradista.core.cashflow.ui.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.service.GCRepoPricerBusinessDelegate;
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
public class CashflowsController implements Serializable {

	private static final long serialVersionUID = 3525922991038977184L;

	private static final String CF_MSG = "cfMsg";

	private List<CashFlow> cashflows;

	private InterestRateCurve discountCurve;

	private GCRepoPricerBusinessDelegate gcRepoPricerBusinessDelegate;

	@PostConstruct
	public void init() {
		cashflows = Collections.synchronizedList(new ArrayList<CashFlow>());
		gcRepoPricerBusinessDelegate = new GCRepoPricerBusinessDelegate();
	}

	public void setCashflows(List<CashFlow> cashflows) {
		this.cashflows = cashflows;
	}

	public List<CashFlow> getCashflows() {
		return cashflows;
	}

	public void generate(GCRepoTrade trade, PricingParameter pp, LocalDate pricingDate) {
		try {
			cashflows = gcRepoPricerBusinessDelegate.generateCashFlows(trade, pp, pricingDate);
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(CF_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public InterestRateCurve getDiscountCurve() {
		return discountCurve;
	}

	public void setDiscountCurve(InterestRateCurve discountCurve) {
		this.discountCurve = discountCurve;
	}

	public void updateDiscountCurve(PricingParameter pp, Currency currency) {
		if (pp != null && currency != null) {
			discountCurve = pp.getDiscountCurve(currency);
			if (discountCurve == null) {
				String errMsg = String.format(
						"Pricing Parameters Set '%s' doesn't contain a discount curve for currency %s.", pp, currency);
				FacesContext.getCurrentInstance().addMessage(CF_MSG,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", errMsg));
			}
		} else {
			discountCurve = null;
		}
	}

}