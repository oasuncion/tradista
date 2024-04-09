package finance.tradista.security.gcrepo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.security.common.model.Security;

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
/**
 * Class representing a trade on General Collateral Repo.
 * 
 *
 */
public class GCRepoTrade extends Trade<Product> {

	private static final long serialVersionUID = 8452035320272812574L;

	public static final String GC_REPO = "GCRepo";

	private GCBasket gcBasket;

	private BigDecimal repoRate;

	private BigDecimal marginRate;

	private Index index;

	private Tenor indexTenor;

	private BigDecimal indexOffset;

	private LocalDate endDate;

	private boolean rightOfSubstitution;

	private boolean rightOfReuse;

	private boolean crossCurrencyCollateral;

	private boolean terminableOnDemand;

	private short noticePeriod;

	private Map<Security, Map<Book, BigDecimal>> collateralToAdd;

	private Map<Security, Map<Book, BigDecimal>> collateralToRemove;

	private Map<LocalDate, BigDecimal> partialTerminations;

	@Override
	public String getWorkflow() {
		return GC_REPO;
	}

	@Override
	public String getProductType() {
		return GC_REPO;
	}

	public BigDecimal getRepoRate() {
		return repoRate;
	}

	public void setRepoRate(BigDecimal repoRate) {
		this.repoRate = repoRate;
	}

	/**
	 * By convention, expressed relatively to 100. Ex: 105 for a margin rate of 5%
	 * 
	 * @return
	 */
	public BigDecimal getMarginRate() {
		return marginRate;
	}

	public void setMarginRate(BigDecimal marginRate) {
		this.marginRate = marginRate;
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public Tenor getIndexTenor() {
		return indexTenor;
	}

	public void setIndexTenor(Tenor indexTenor) {
		this.indexTenor = indexTenor;
	}

	public BigDecimal getIndexOffset() {
		return indexOffset;
	}

	public void setIndexOffset(BigDecimal indexOffset) {
		this.indexOffset = indexOffset;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public boolean isRightOfSubstitution() {
		return rightOfSubstitution;
	}

	public void setRightOfSubstitution(boolean rightOfSubstitution) {
		this.rightOfSubstitution = rightOfSubstitution;
	}

	public boolean isRightOfReuse() {
		return rightOfReuse;
	}

	public void setRightOfReuse(boolean rightOfReuse) {
		this.rightOfReuse = rightOfReuse;
	}

	public boolean isCrossCurrencyCollateral() {
		return crossCurrencyCollateral;
	}

	public void setCrossCurrencyCollateral(boolean crossCurrencyCollateral) {
		this.crossCurrencyCollateral = crossCurrencyCollateral;
	}

	public boolean isTerminableOnDemand() {
		return terminableOnDemand;
	}

	public void setTerminableOnDemand(boolean terminableOnDemand) {
		this.terminableOnDemand = terminableOnDemand;
	}

	public short getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(short noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public boolean isFixedRepoRate() {
		return index == null;
	}

	public GCBasket getGcBasket() {
		return gcBasket;
	}

	public void setGcBasket(GCBasket gcBasket) {
		this.gcBasket = gcBasket;
	}

	@SuppressWarnings("unchecked")
	public Map<Security, Map<Book, BigDecimal>> getCollateralToAdd() {
		return (Map<Security, Map<Book, BigDecimal>>) TradistaModelUtil.deepCopy(collateralToAdd);
	}

	public void setCollateralToAdd(Map<Security, Map<Book, BigDecimal>> collateralToAdd) {
		this.collateralToAdd = collateralToAdd;
	}

	@SuppressWarnings("unchecked")
	public Map<Security, Map<Book, BigDecimal>> getCollateralToRemove() {
		return (Map<Security, Map<Book, BigDecimal>>) TradistaModelUtil.deepCopy(collateralToRemove);
	}

	public void setCollateralToRemove(Map<Security, Map<Book, BigDecimal>> collateralToRemove) {
		this.collateralToRemove = collateralToRemove;
	}

	@SuppressWarnings("unchecked")
	public Map<LocalDate, BigDecimal> getPartialTerminations() {
		return (Map<LocalDate, BigDecimal>) TradistaModelUtil.deepCopy(partialTerminations);
	}

	public void setPartialTerminations(Map<LocalDate, BigDecimal> partialTerminations) {
		this.partialTerminations = partialTerminations;
	}

	public void addParTialTermination(LocalDate date, BigDecimal reduction) {
		if (partialTerminations == null) {
			partialTerminations = new HashMap<>();
		}
		partialTerminations.putIfAbsent(date, BigDecimal.ZERO);
		partialTerminations.put(date, partialTerminations.get(date).add(reduction));
	}

}