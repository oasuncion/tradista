package finance.tradista.security.bond.bootstraphandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import finance.tradista.core.marketdata.bootstraphandler.BootstrapHandler;
import finance.tradista.core.marketdata.model.Instrument;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.model.Coupon;
import finance.tradista.security.bond.pricer.PricerBondUtil;
import finance.tradista.security.bond.service.BondBusinessDelegate;

/********************************************************************************
 * Copyright (c) 2018 Olivier Asuncion
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

public class BondBootstrapHandler implements BootstrapHandler {

	private ConfigurationBusinessDelegate configurationBusinessDelegate;

	public BondBootstrapHandler() {
		configurationBusinessDelegate = new ConfigurationBusinessDelegate();
	}

	@Override
	public String getInstrumentName() {
		return Bond.BOND;
	}

	@Override
	public String getKeyFromQuoteName(String quoteName) {
		return quoteName.substring(Bond.BOND.length() + 1, quoteName.length());
	}

	@Override
	public Instrument getInstrumentByKey(String key, LocalDate quoteDate) {
		String[] keyArray = key.split(".");
		String isin = keyArray[0];
		String exchangeCode = keyArray[1];
		return new BondBusinessDelegate().getBondByIsinAndExchangeCode(isin, exchangeCode);
	}

	@Override
	public boolean isZeroCoupon(Instrument instrument, LocalDate quoteDate) {
		return ((((Bond) instrument).getCoupon().compareTo(BigDecimal.valueOf(0))) == 0);
	}

	@Override
	public LocalDate getMaturityDate(Instrument instrument) {
		return ((Bond) instrument).getMaturityDate();
	}

	@Override
	public String getKey(Instrument instrument, LocalDate quoteDate) {
		return ((Bond) instrument).getIsin() + "." + ((Bond) instrument).getExchange().getCode();
	}

	@Override
	public List<CashFlow> getPendingCashFlows(Instrument instrument, LocalDate date, BigDecimal price,
			Map<LocalDate, List<BigDecimal>> zeroCouponRates, Map<LocalDate, BigDecimal> interpolatedValues)
			throws TradistaBusinessException {
		List<CashFlow> cashFlows = new ArrayList<CashFlow>();
		Bond bond = (Bond) instrument;
		BigDecimal cfAmount = bond.getPrincipal().multiply(bond.getCoupon().divide(BigDecimal.valueOf(100)));
		List<Coupon> coupons;
		try {
			coupons = PricerBondUtil.getPendingCoupons((Bond) instrument, date, 0, false);
		} catch (PricerException pe) {
			throw new TradistaBusinessException(pe.getMessage());
		}
		for (Coupon coupon : coupons) {
			CashFlow cf = new CashFlow();
			if (coupon.getDate().isEqual(bond.getMaturityDate())) {
				cf.setAmount(bond.getPrincipal()
						.add(bond.getPrincipal().multiply(bond.getCoupon().divide(BigDecimal.valueOf(100)))));
			} else {
				cf.setAmount(cfAmount);
			}
			cf.setDate(coupon.getDate());
			cf.setCurrency(bond.getCurrency());
			cashFlows.add(cf);
		}

		return cashFlows;
	}

	@Override
	public Tenor getFrequency(Instrument instrument) {
		return ((Bond) instrument).getCouponFrequency();
	}

	@Override
	public BigDecimal calcZeroCoupon(Instrument instrument, BigDecimal price, LocalDate date) {
		// Calculate the "day to year" of the period from the date to the bond
		// maturity date
		BigDecimal daysToYear = PricerUtil.daysToYear(date, getMaturityDate(instrument));
		System.out.println(daysToYear.doubleValue());
		BigDecimal couponsNumberbyYear = BigDecimal.valueOf(1).divide(daysToYear,
				configurationBusinessDelegate.getRoundingMode());
		BigDecimal profit = ((Bond) instrument).getPrincipal().subtract(price);
		BigDecimal zc = couponsNumberbyYear.multiply(profit).divide(price,
				configurationBusinessDelegate.getRoundingMode());
		// Returns the continuously compounded zc rate
		return couponsNumberbyYear.multiply(BigDecimal.valueOf(Math.log(
				1 + (zc.divide(couponsNumberbyYear, configurationBusinessDelegate.getRoundingMode())).doubleValue())));

	}

	@Override
	public BigDecimal getPrice(BigDecimal price) {
		return price;
	}

}
