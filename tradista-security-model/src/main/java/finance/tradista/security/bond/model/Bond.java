package finance.tradista.security.bond.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.marketdata.model.Instrument;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.security.common.model.Security;

/*
 * Copyright 2014 Olivier Asuncion
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

public class Bond extends Security implements Instrument {

	private static final long serialVersionUID = -1544895032L;

	public static final String BOND = "Bond";
	
	public static enum CapFloorCollar {
		NONE, CAP, FLOOR, COLLAR;
		public String toString() {
			switch (this) {
			case NONE:
				return "None";
			case CAP:
				return "Cap";
			case FLOOR:
				return "Floor";
			case COLLAR:
				return "Collar";
			}
			return super.toString();
		}
	};

	public Bond() {

	}

	private BigDecimal coupon;

	private LocalDate maturityDate;

	private BigDecimal principal;

	private BigDecimal redemptionPrice;

	private Currency redemptionCurrency;

	private LocalDate datedDate;

	private String couponType;

	private Tenor couponFrequency;

	private Index referenceRateindex;

	private BigDecimal cap;

	private BigDecimal floor;

	private BigDecimal spread;

	private BigDecimal leverageFactor;

	private List<Coupon> coupons;

	public List<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	public BigDecimal getCoupon() {
		return coupon;
	}

	public LocalDate getLastCouponDate() {
		Coupon lastCoupon = null;
		for (Coupon coupon : coupons) {
			if (lastCoupon == null) {
				lastCoupon = coupon;
			} else {
				if (coupon.getDate().isAfter(lastCoupon.getDate())) {
					lastCoupon = coupon;
				}
			}
		}
		return lastCoupon.getDate();
	}

	public void setCoupon(BigDecimal coupon) {
		this.coupon = coupon;
	}

	public LocalDate getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(LocalDate maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public String getProductType() {
		return BOND;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public String toString() {
		return getIsin() + " - " + getExchange();
	}

	public LocalDate getDatedDate() {
		return datedDate;
	}

	public void setDatedDate(LocalDate datedDate) {
		this.datedDate = datedDate;
	}

	public String getCouponType() {
		return couponType;
	}

	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}

	public Tenor getCouponFrequency() {
		return couponFrequency;
	}

	public void setCouponFrequency(Tenor couponFrequency) {
		this.couponFrequency = couponFrequency;
	}

	public BigDecimal getRedemptionPrice() {
		return redemptionPrice;
	}

	public void setRedemptionPrice(BigDecimal redemptionPrice) {
		this.redemptionPrice = redemptionPrice;
	}

	public Currency getRedemptionCurrency() {
		return redemptionCurrency;
	}

	public long getRedemptionCurrencyId() {
		if (redemptionCurrency != null) {
			return redemptionCurrency.getId();
		} else {
			return 0;
		}
	}

	public void setRedemptionCurrency(Currency redemptionCurrency) {
		this.redemptionCurrency = redemptionCurrency;
	}

	public Index getReferenceRateIndex() {
		return referenceRateindex;
	}

	public void setReferenceRateIndex(Index index) {
		this.referenceRateindex = index;
	}

	public BigDecimal getCap() {
		return cap;
	}

	public void setCap(BigDecimal cap) {
		this.cap = cap;
	}

	public BigDecimal getFloor() {
		return floor;
	}

	public void setFloor(BigDecimal floor) {
		this.floor = floor;
	}

	public boolean isCap() {
		return (cap != null && floor == null);
	}

	public boolean isFloor() {
		return (cap == null && floor != null);
	}

	public boolean isCollar() {
		return (cap != null && floor != null);
	}

	public BigDecimal getSpread() {
		return spread;
	}

	public void setSpread(BigDecimal spread) {
		this.spread = spread;
	}

	public BigDecimal getLeverageFactor() {
		return leverageFactor;
	}

	public void setLeverageFactor(BigDecimal leverageFactor) {
		this.leverageFactor = leverageFactor;
	}

	@Override
	public String getInstrumentName() {
		return BOND;
	}

	@Override
	public boolean equals(Object o) {
		Bond b = null;
		if (o == null) {
			return false;
		}
		if (!(o instanceof Bond)) {
			return false;
		}
		b = (Bond) o;
		if (b == this) {
			return true;
		}
		return b.getIsin().equals(getIsin()) && b.getExchange().equals(getExchange());
	}

	@Override
	public int hashCode() {
		return (getIsin() + "-" + getExchange()).hashCode();
	}

}