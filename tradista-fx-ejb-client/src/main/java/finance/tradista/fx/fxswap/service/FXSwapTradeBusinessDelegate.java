package finance.tradista.fx.fxswap.service;

import java.time.LocalDate;

import finance.tradista.core.calendar.model.Calendar;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.exchange.model.Exchange;
import finance.tradista.fx.fx.service.AbstractFXTradeBusinessDelegate;
import finance.tradista.fx.fx.service.FXTradeBusinessDelegate;
import finance.tradista.fx.fxswap.model.FXSwapTrade;
import finance.tradista.fx.fxswap.validator.FXSwapTradeValidator;

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

public class FXSwapTradeBusinessDelegate extends AbstractFXTradeBusinessDelegate<FXSwapTrade> {

	private FXSwapTradeService fxSwapTradeService;

	private FXSwapTradeValidator validator;

	public FXSwapTradeBusinessDelegate() {
		validator = new FXSwapTradeValidator();
		fxSwapTradeService = TradistaServiceLocator.getInstance().getFXSwapTradeService();
	}

	public long saveFXSwapTrade(FXSwapTrade trade) throws TradistaBusinessException {

		validator.validateTrade(trade);

		return SecurityUtil.runEx(() -> fxSwapTradeService.saveFXSwapTrade(trade));

	}

	@Override
	public boolean isBusinessDay(FXSwapTrade fxSwapTrade, LocalDate date) throws TradistaBusinessException {
		boolean isBusinessDay = super.isBusinessDay(fxSwapTrade, date);
		Currency currencyOne = fxSwapTrade.getCurrencyOne();
		Calendar currencyOneCalendar;
		if (currencyOne == null) {
			throw new TradistaBusinessException("The FX Swap trade currency one cannot be null");
		}
		currencyOneCalendar = currencyOne.getCalendar();
		if (currencyOneCalendar == null) {
			// TODO Add a warning log
		}
		if (currencyOneCalendar != null) {
			return isBusinessDay && currencyOneCalendar.isBusinessDay(date);
		} else {
			return isBusinessDay;
		}
	}

	public Exchange getFXExchange() {
		return new FXTradeBusinessDelegate().getFXExchange();
	}

	public FXSwapTrade getFXSwapTradeById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The trade id must be positive.");
		}
		return SecurityUtil.run(() -> fxSwapTradeService.getFXSwapTradeById(id));
	}

}