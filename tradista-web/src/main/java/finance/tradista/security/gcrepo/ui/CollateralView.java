package finance.tradista.security.gcrepo.ui;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.inventory.model.ProductInventory;
import finance.tradista.core.productinventory.service.ProductInventoryBusinessDelegate;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.service.GCRepoTradeBusinessDelegate;
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
public class CollateralView implements Serializable {

    private static final long serialVersionUID = 1L;

    private GCRepoTradeBusinessDelegate gcRepoTradeBusinessDelegate;

    private ProductInventoryBusinessDelegate productInventoryBusinessDelegate;

    private String context;

    private Set<Collateral> collateralValues;

    private Set<Collateral> availableCollateralValues;

    private Set<Collateral> addedCollateralValues;

    @PostConstruct
    public void init() {
	gcRepoTradeBusinessDelegate = new GCRepoTradeBusinessDelegate();
	productInventoryBusinessDelegate = new ProductInventoryBusinessDelegate();
    }

    public class Collateral implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal quantity;

	private String security;

	private String book;

	public BigDecimal getQuantity() {
	    return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
	    this.quantity = quantity;
	}

	public String getSecurity() {
	    return security;
	}

	public void setSecurity(String security) {
	    this.security = security;
	}

	public String getBook() {
	    return book;
	}

	public void setBook(String book) {
	    this.book = book;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getEnclosingInstance().hashCode();
	    result = prime * result + Objects.hash(security);
	    result = prime * result + Objects.hash(book);
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    Collateral other = (Collateral) obj;
	    if (!getEnclosingInstance().equals(other.getEnclosingInstance())) {
		return false;
	    }
	    return Objects.equals(security, other.security) && Objects.equals(book, other.book);
	}

	private CollateralView getEnclosingInstance() {
	    return CollateralView.this;
	}

    }

    public Set<Collateral> getCollateralValues() {
	return collateralValues;
    }

    public void setCollateralValues(Set<Collateral> collateralValues) {
	this.collateralValues = collateralValues;
    }

    public Set<Collateral> getAddedCollateralValues() {
	return addedCollateralValues;
    }

    public void setAddedCollateralValues(Set<Collateral> addedCollateralValues) {
	this.addedCollateralValues = addedCollateralValues;
    }

    public Set<Collateral> getAvailableCollateralValues() {
	return availableCollateralValues;
    }

    public void setAvailableCollateralValues(Set<Collateral> availableCollateralValues) {
	this.availableCollateralValues = availableCollateralValues;
    }

    public void addColateral(String security, String book) {

    }

    public void refresh(long tradeId) {
	if (collateralValues == null) {
	    collateralValues = new HashSet<>();
	}
	if (availableCollateralValues == null) {
	    availableCollateralValues = new HashSet<>();
	}
	collateralValues.clear();
	availableCollateralValues.clear();
	Map<Security, BigDecimal> sec = null;
	Set<ProductInventory> inventory = null;
	try {
	    GCRepoTrade trade = gcRepoTradeBusinessDelegate.getGCRepoTradeById(tradeId);
	    // TODO Think about a configurable mechanism for context determination
	    if (trade.getStatus().getName().equals("UNDER_ALLOCATED")) {
		context = "ALLOCATION";
	    }
	    sec = gcRepoTradeBusinessDelegate.getAllocatedCollateral(tradeId);

	    if (sec != null) {
		for (Map.Entry<Security, BigDecimal> entry : sec.entrySet()) {
		    Collateral col = new Collateral();
		    col.setQuantity(entry.getValue());
		    col.setSecurity(entry.getKey().toString());
		    collateralValues.add(col);
		}
	    }

	    inventory = productInventoryBusinessDelegate.getProductInventories(null, null, Bond.BOND, tradeId, tradeId,
		    true);

	    if (inventory != null) {
		for (ProductInventory inv : inventory) {
		    Collateral col = new Collateral();
		    col.setQuantity(inv.getQuantity());
		    col.setSecurity(((Bond) inv.getProduct()).getIsin());
		    col.setBook(inv.getBook().getName());
		    availableCollateralValues.add(col);
		}
	    }

	    inventory = productInventoryBusinessDelegate.getProductInventories(null, null, Equity.EQUITY, tradeId,
		    tradeId, true);

	    if (inventory != null) {
		for (ProductInventory inv : inventory) {
		    Collateral col = new Collateral();
		    col.setQuantity(inv.getQuantity());
		    col.setSecurity(((Bond) inv.getProduct()).getIsin());
		    col.setBook(inv.getBook().getName());
		    availableCollateralValues.add(col);
		}
	    }
	} catch (TradistaBusinessException tbe) {
	    FacesContext.getCurrentInstance().addMessage(null,
		    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
	}
    }

    public String getContext() {
	return context;
    }

    public void setContext(String context) {
	this.context = context;
    }

}