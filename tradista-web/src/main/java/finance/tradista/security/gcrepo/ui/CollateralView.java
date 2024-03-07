package finance.tradista.security.gcrepo.ui;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.inventory.model.ProductInventory;
import finance.tradista.core.productinventory.service.ProductInventoryBusinessDelegate;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.service.BondBusinessDelegate;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.equity.model.Equity;
import finance.tradista.security.equity.service.EquityBusinessDelegate;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.service.GCRepoTradeBusinessDelegate;
import finance.tradista.web.demo.ColorUtil;
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

	private BondBusinessDelegate bondBusinessDelegate;

	private EquityBusinessDelegate equityBusinessDelegate;

	private BookBusinessDelegate bookBusinessDelegate;

	private String context;

	private Set<Collateral> collateralValues;

	private Set<Collateral> availableCollateralValues;

	private Set<Collateral> addedCollateralValues;

	private DonutChartModel collateralMarketValueDonutModel;

	private String securityToAdd;

	private String fromBookToAdd;

	private BigDecimal maxQuantityToAdd;

	private BigDecimal quantityToAdd;

	private String exchangeToAdd;

	private GCRepoTrade trade;

	private static final String COL_MSG = "colMsg";

	@PostConstruct
	public void init() {
		gcRepoTradeBusinessDelegate = new GCRepoTradeBusinessDelegate();
		productInventoryBusinessDelegate = new ProductInventoryBusinessDelegate();
		collateralMarketValueDonutModel = new DonutChartModel();
		bondBusinessDelegate = new BondBusinessDelegate();
		equityBusinessDelegate = new EquityBusinessDelegate();
		bookBusinessDelegate = new BookBusinessDelegate();
	}

	public class Collateral implements Serializable {

		private static final long serialVersionUID = 1L;

		private BigDecimal quantity;

		private String security;

		private String book;

		private String exchange;

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

		public String getExchange() {
			return exchange;
		}

		public void setExchange(String exchange) {
			this.exchange = exchange;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(security);
			result = prime * result + Objects.hash(book);
			result = prime * result + Objects.hash(exchange);
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
			return Objects.equals(security, other.security) && Objects.equals(book, other.book)
					&& Objects.equals(exchange, other.exchange);
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

	public void setCollateralToAdd(String security, String exchange, String book, BigDecimal quantity) {
		securityToAdd = security;
		exchangeToAdd = exchange;
		fromBookToAdd = book;
		maxQuantityToAdd = quantity;
	}

	public void updateCollateralToAdd(BigDecimal quantity) {

		if (quantity.compareTo(maxQuantityToAdd) > 0) {
			FacesContext.getCurrentInstance().addMessage(COL_MSG, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					String.format("You cannot allocate more than %.2f", maxQuantityToAdd.doubleValue())));
			clearCollateralToAdd();
			return;
		}

		if (collateralValues != null) {
			boolean found = false;
			for (Collateral coll : collateralValues) {
				if (coll.security.equals(securityToAdd) && coll.exchange.equals(exchangeToAdd)) {
					coll.setQuantity(coll.quantity.add(quantity));
					found = true;
					break;
				}
			}
			if (!found) {
				Collateral collToAdd = new Collateral();
				collToAdd.setQuantity(quantity);
				collToAdd.setSecurity(securityToAdd);
				collToAdd.setExchange(exchangeToAdd);
				collateralValues.add(collToAdd);
			}
		} else {
			collateralValues = new HashSet<>();
			Collateral coll = new Collateral();
			coll.setQuantity(quantity);
			coll.setSecurity(securityToAdd);
			coll.setExchange(exchangeToAdd);
			collateralValues.add(coll);
		}

		if (availableCollateralValues != null) {
			Collateral toBeRemoved = null;
			for (Collateral coll : availableCollateralValues) {
				if (coll.security.equals(securityToAdd) && coll.exchange.equals(exchangeToAdd)) {
					coll.setQuantity(coll.quantity.subtract(quantity));
					if (coll.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
						toBeRemoved = coll;
					}
					break;
				}
			}
			if (toBeRemoved != null) {
				availableCollateralValues.remove(toBeRemoved);
			}
		}

		if (addedCollateralValues != null) {
			boolean found = false;
			for (Collateral coll : addedCollateralValues) {
				if (coll.security.equals(securityToAdd) && coll.exchange.equals(exchangeToAdd)
						&& coll.book.equals(fromBookToAdd)) {
					coll.setQuantity(coll.quantity.add(quantity));
					found = true;
					break;
				}
			}
			if (!found) {
				Collateral collToAdd = new Collateral();
				collToAdd.setQuantity(quantity);
				collToAdd.setBook(fromBookToAdd);
				collToAdd.setSecurity(securityToAdd);
				collToAdd.setExchange(exchangeToAdd);
				addedCollateralValues.add(collToAdd);
			}
		} else {
			addedCollateralValues = new HashSet<>();
			Collateral coll = new Collateral();
			coll.setQuantity(quantity);
			coll.setSecurity(securityToAdd);
			coll.setExchange(exchangeToAdd);
			coll.setBook(fromBookToAdd);
			addedCollateralValues.add(coll);
		}

		refreshDonutModel();
	}

	public void removeCollateral(String security, String exchange, String fromBook, BigDecimal quantity) {

		if (collateralValues != null) {
			Collateral toBeRemoved = null;
			for (Collateral coll : collateralValues) {
				if (coll.security.equals(security) && coll.exchange.equals(exchange)) {
					coll.setQuantity(coll.quantity.subtract(quantity));
					if (coll.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
						toBeRemoved = coll;
					}
					break;
				}
			}
			if (toBeRemoved != null) {
				collateralValues.remove(toBeRemoved);
			}
		}

		if (addedCollateralValues != null) {
			addedCollateralValues = addedCollateralValues.stream().filter(
					c -> !c.security.equals(security) || !c.book.equals(fromBook) || !c.exchange.equals(exchange))
					.collect(Collectors.toSet());
		}

		if (availableCollateralValues != null) {
			boolean found = false;
			for (Collateral coll : availableCollateralValues) {
				if (coll.security.equals(security) && coll.exchange.equals(exchange) && coll.book.equals(fromBook)) {
					coll.setQuantity(coll.quantity.add(quantity));
					found = true;
					break;
				}
			}
			if (!found) {
				Collateral removedColl = new Collateral();
				removedColl.setQuantity(quantity);
				removedColl.setBook(fromBook);
				removedColl.setSecurity(security);
				removedColl.setExchange(exchange);
				availableCollateralValues.add(removedColl);
			}
		} else {
			availableCollateralValues = new HashSet<>();
			Collateral coll = new Collateral();
			coll.setQuantity(quantity);
			coll.setBook(fromBook);
			coll.setSecurity(security);
			coll.setExchange(exchange);
			availableCollateralValues.add(coll);
		}

		refreshDonutModel();
	}

	public void clearCollateralToAdd() {
		securityToAdd = null;
		fromBookToAdd = null;
		maxQuantityToAdd = null;
		exchangeToAdd = null;

	}

	public void clear() {
		clearCollateralToAdd();
		context = null;
		trade = null;
		collateralValues = null;
		addedCollateralValues = null;
		availableCollateralValues = null;
		collateralMarketValueDonutModel.setData(null);
	}

	public void refresh(long tradeId) {

		try {
			GCRepoTrade trade = gcRepoTradeBusinessDelegate.getGCRepoTradeById(tradeId);
			if (trade != null) {
				if (collateralValues == null) {
					collateralValues = new HashSet<>();
				}
				if (availableCollateralValues == null) {
					availableCollateralValues = new HashSet<>();
				}
				collateralValues.clear();
				availableCollateralValues.clear();
				Map<Security, Map<Book, BigDecimal>> sec = null;
				Set<ProductInventory> inventory = null;
				// TODO Think about a configurable mechanism for context determination
				if (trade.getStatus().getName().equals("UNDER_ALLOCATED")) {
					context = "ALLOCATION";
				}
				sec = gcRepoTradeBusinessDelegate.getAllocatedCollateral(tradeId);

				if (sec != null) {
					for (Map.Entry<Security, Map<Book, BigDecimal>> entry : sec.entrySet()) {
						if (entry.getValue() != null) {
							for (BigDecimal qty : entry.getValue().values()) {
								Collateral col = new Collateral();
								col.setQuantity(qty);
								col.setSecurity(entry.getKey().toString());
								col.setExchange(entry.getKey().getExchange().getCode());
								collateralValues.add(col);
							}
						}
					}
				}

				inventory = productInventoryBusinessDelegate.getProductInventories(null, null, Bond.BOND, 0, 0, true);

				if (inventory != null) {
					for (ProductInventory inv : inventory) {
						if (trade.getGcBasket().getSecurities().contains(inv.getProduct())) {
							Collateral col = new Collateral();
							col.setQuantity(inv.getQuantity());
							col.setSecurity(((Bond) inv.getProduct()).getIsin());
							col.setExchange(((Bond) inv.getProduct()).getExchange().getCode());
							col.setBook(inv.getBook().getName());
							availableCollateralValues.add(col);
						}
					}
				}

				inventory = productInventoryBusinessDelegate.getProductInventories(null, null, Equity.EQUITY, 0, 0,
						true);

				if (inventory != null) {
					for (ProductInventory inv : inventory) {
						if (trade.getGcBasket().getSecurities().contains(inv.getProduct())) {
							Collateral col = new Collateral();
							col.setQuantity(inv.getQuantity());
							col.setSecurity(((Equity) inv.getProduct()).getIsin());
							col.setExchange(((Equity) inv.getProduct()).getExchange().getCode());
							col.setBook(inv.getBook().getName());
							availableCollateralValues.add(col);
						}
					}
				}

				this.trade = trade;

				refreshDonutModel();

			}

		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(COL_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public DonutChartModel getCollateralMarketValueDonutModel() {
		return collateralMarketValueDonutModel;
	}

	public void setCollateralMarketValueDonutModel(DonutChartModel collateralMarketValueDonutModel) {
		this.collateralMarketValueDonutModel = collateralMarketValueDonutModel;
	}

	public String getSecurityToAdd() {
		return securityToAdd;
	}

	public void setSecurityToAdd(String securityToAdd) {
		this.securityToAdd = securityToAdd;
	}

	public String getFromBookToAdd() {
		return fromBookToAdd;
	}

	public void setFromBookToAdd(String fromBookToAdd) {
		this.fromBookToAdd = fromBookToAdd;
	}

	public BigDecimal getMaxQuantityToAdd() {
		return maxQuantityToAdd;
	}

	public void setMaxQuantityToAdd(BigDecimal maxQuantityToAdd) {
		this.maxQuantityToAdd = maxQuantityToAdd;
	}

	public BigDecimal getQuantityToAdd() {
		return quantityToAdd;
	}

	public void setQuantityToAdd(BigDecimal quantityToAdd) {
		this.quantityToAdd = quantityToAdd;
	}

	public String getExchangeToAdd() {
		return exchangeToAdd;
	}

	public void setExchangeToAdd(String exchangeToAdd) {
		this.exchangeToAdd = exchangeToAdd;
	}

	public GCRepoTrade getTrade() {
		return trade;
	}

	public void setTrade(GCRepoTrade trade) {
		this.trade = trade;
	}

	public void refreshDonutModel() {
		ChartData data = new ChartData();
		DonutChartDataSet dataSet = new DonutChartDataSet();

		try {
			BigDecimal collateralMarketValue = gcRepoTradeBusinessDelegate.getCollateralMarketToMarket(trade.getId());
			BigDecimal exposure = gcRepoTradeBusinessDelegate.getExposure(trade.getId());

			// Add collateral added from the GUI
			Map<Security, Map<Book, BigDecimal>> addedSecurities = getAddedSecurities();
			if (addedSecurities != null && !addedSecurities.isEmpty()) {
				collateralMarketValue = collateralMarketValue.add(gcRepoTradeBusinessDelegate
						.getCollateralMarketToMarket(addedSecurities, trade.getBook().getProcessingOrg().getId()));
			}

			List<Number> values = new ArrayList<>();
			values.add(collateralMarketValue);
			values.add(exposure.subtract(collateralMarketValue));
			dataSet.setData(values);

			List<String> bgColors = new ArrayList<>();
			bgColors.add(ColorUtil.getTurquoise());
			bgColors.add(ColorUtil.getBloodRed());
			dataSet.setBackgroundColor(bgColors);

			data.addChartDataSet(dataSet);
			List<String> labels = new ArrayList<>();
			labels.add("Collateral Mark to Market");
			labels.add("Uncovered exposure");
			data.setLabels(labels);

			collateralMarketValueDonutModel.setData(data);
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(COL_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public Map<Security, Map<Book, BigDecimal>> getAddedSecurities() {
		if (addedCollateralValues == null || addedCollateralValues.isEmpty()) {
			return null;
		}
		// Add collateral added from the GUI
		Map<Security, Map<Book, BigDecimal>> securities = new HashMap<>();
		for (Collateral col : addedCollateralValues) {
			Security security = bondBusinessDelegate.getBondByIsinAndExchangeCode(col.security, col.exchange);
			if (security == null) {
				security = equityBusinessDelegate.getEquityByIsinAndExchangeCode(col.security, col.exchange);
			}
			Book book = null;
			try {
				book = bookBusinessDelegate.getBookByName(col.book);
			} catch (TradistaBusinessException tbe) {
				// Not expected here
			}
			Map<Book, BigDecimal> bookMap = null;
			if (securities.containsKey(security)) {
				bookMap = securities.get(security);
			} else {
				bookMap = new HashMap<>();
			}
			bookMap.put(book, col.quantity);
			securities.put(security, bookMap);

		}
		return securities;

	}

}