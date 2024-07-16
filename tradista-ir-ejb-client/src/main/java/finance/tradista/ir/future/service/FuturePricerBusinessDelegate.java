package finance.tradista.ir.future.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.pricing.exception.PricerException;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.ir.future.model.Future;
import finance.tradista.ir.future.model.FutureTrade;
import finance.tradista.ir.future.validator.FutureTradeValidator;
import finance.tradista.ir.future.validator.FutureValidator;

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

public class FuturePricerBusinessDelegate implements Serializable {

	private static final long serialVersionUID = -7137084591554434226L;

	private FuturePricerService futurePricerService;

	private FutureTradeValidator tradeValidator;

	private FutureValidator futureValidator;

	public FuturePricerBusinessDelegate() {
		futurePricerService = TradistaServiceLocator.getInstance().getFuturePricerService();
		tradeValidator = new FutureTradeValidator();
		futureValidator = new FutureValidator();
	}

	public BigDecimal npvValuation(PricingParameter params, FutureTrade trade, Currency currency, LocalDate pricingDate)
			throws PricerException, TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> futurePricerService.npvValuation(params, trade, currency, pricingDate));
	}

	public BigDecimal pvValuation(PricingParameter params, FutureTrade trade, Currency currency, LocalDate pricingDate)
			throws PricerException, TradistaBusinessException {
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> futurePricerService.pvValuation(params, trade, currency, pricingDate));
	}

	public BigDecimal pnlDefault(PricingParameter params, Future future, Book book, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {
		futureValidator.validateProduct(future);
		return SecurityUtil.runEx(() -> futurePricerService.pnlDefault(params, future, book, currency, pricingDate));
	}

	public BigDecimal realizedPnlDefault(PricingParameter params, Future future, Book book, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {
		futureValidator.validateProduct(future);
		return SecurityUtil
				.runEx(() -> futurePricerService.realizedPnlDefault(params, future, book, currency, pricingDate));
	}

	public BigDecimal unrealizedPnlDefault(PricingParameter params, Future future, Book book, Currency currency,
			LocalDate pricingDate) throws PricerException, TradistaBusinessException {
		futureValidator.validateProduct(future);
		return SecurityUtil
				.runEx(() -> futurePricerService.unrealizedPnlDefault(params, future, book, currency, pricingDate));
	}

	public List<CashFlow> generateCashFlows(FutureTrade trade, PricingParameter pp, LocalDate pricingDate)
			throws TradistaBusinessException {
		StringBuffer errorMsg = new StringBuffer();
		if (trade == null) {
			errorMsg.append(String.format("The trade cannot be null.%n"));
		}
		if (pp == null) {
			errorMsg.append(String.format("The pricing parameters cannot be null.%n"));
		}
		if (pricingDate == null) {
			errorMsg.append(String.format("The pricing date cannot be null.%n"));
		}
		if (errorMsg.length() > 0) {
			throw new TradistaBusinessException(errorMsg.toString());
		}
		tradeValidator.validateTrade(trade);
		return SecurityUtil.runEx(() -> futurePricerService.generateCashFlows(pp, trade, pricingDate));
	}

}