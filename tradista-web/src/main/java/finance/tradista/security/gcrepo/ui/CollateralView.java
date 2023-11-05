package finance.tradista.security.gcrepo.ui;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import finance.tradista.security.common.model.Security;
import finance.tradista.security.gcrepo.service.GCRepoTradeBusinessDelegate;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
	gcRepoTradeBusinessDelegate = new GCRepoTradeBusinessDelegate();
    }

    public class Collateral implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal quantity;

	private String security;

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

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getEnclosingInstance().hashCode();
	    result = prime * result + Objects.hash(security);
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
	    return Objects.equals(security, other.security);
	}

	private CollateralView getEnclosingInstance() {
	    return CollateralView.this;
	}

    }

    private Set<Collateral> collateralValues;

    public Set<Collateral> getCollateralValues() {
	return collateralValues;
    }

    public void setCollateralValues(Set<Collateral> collateralValues) {
	this.collateralValues = collateralValues;
    }

    public void refresh(long tradeId) {
	collateralValues.clear();
	Map<Security, BigDecimal> sec = gcRepoTradeBusinessDelegate.getAllocatedCollateral(tradeId);
    }

}