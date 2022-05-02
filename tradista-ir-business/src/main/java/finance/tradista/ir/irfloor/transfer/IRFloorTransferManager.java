package finance.tradista.ir.irfloor.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.DateUtil;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.FixingError;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.ir.ircapfloorcollar.model.IRCapFloorCollarTrade;
import finance.tradista.ir.ircapfloorcollar.transfer.IRCapFloorCollarTransferManager;

/*
 * Copyright 2018 Olivier Asuncion
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

public class IRFloorTransferManager extends IRCapFloorCollarTransferManager {

	protected ConfigurationBusinessDelegate configurationBusinessDelegate;

	public IRFloorTransferManager() {
		configurationBusinessDelegate = new ConfigurationBusinessDelegate();
	}

	@Override
	public void fixCashTransfer(CashTransfer transfer, long quoteSetId) throws TradistaBusinessException {
		IRCapFloorCollarTrade trade = (IRCapFloorCollarTrade) transfer.getTrade();
		BigDecimal notional = trade.getIrForwardTrade().getAmount();
		String quoteName = Index.INDEX + "." + trade.getIrForwardTrade().getReferenceRateIndex() + "."
				+ trade.getIrForwardTrade().getReferenceRateIndexTenor();
		BigDecimal ir = PricerUtil.getValueAsOfDateFromQuote(quoteName, quoteSetId, QuoteType.INTEREST_RATE,
				QuoteValue.CLOSE, transfer.getFixingDateTime().toLocalDate());
		BigDecimal difference;
		BigDecimal amount;
		BigDecimal fractionedNotional;
		if (ir == null) {
			FixingError fixingError = new FixingError();
			fixingError.setCashTransfer(transfer);
			fixingError.setErrorDate(LocalDateTime.now());
			String errorMsg = String.format(
					"Transfer %n cannot be fixed. Impossible to get the %s index closing value as of %tD in QuoteSet %s.",
					transfer.getId(), quoteName, transfer.getFixingDateTime(), quoteSetId);
			fixingError.setMessage(errorMsg);
			fixingError.setStatus(finance.tradista.core.error.model.Error.Status.UNSOLVED);
			List<FixingError> errors = new ArrayList<FixingError>(1);
			errors.add(fixingError);
			fixingErrorBusinessDelegate.saveFixingErrors(errors);
			throw new TradistaBusinessException(errorMsg);
		}
		if (!(ir.compareTo(trade.getFloorStrike()) < 0)) {
			// No transfer
			transferBusinessDelegate.deleteTransfer(transfer.getId());
			return;
			// TODO add a warn somewhere ?
		}
		fractionedNotional = notional.multiply(PricerUtil.daysToYear(trade.getIrForwardTrade().getDayCountConvention(),
				transfer.getFixingDateTime().toLocalDate(),
				DateUtil.addTenor(transfer.getFixingDateTime().toLocalDate(),
						trade.getIrForwardTrade().getReferenceRateIndexTenor())));
		difference = trade.getFloorStrike().subtract(ir);
		amount = fractionedNotional.multiply(difference.divide(BigDecimal.valueOf(100),
				configurationBusinessDelegate.getScale(), configurationBusinessDelegate.getRoundingMode()));
		Transfer.Direction direction;
		if (trade.isBuy()) {
			direction = Transfer.Direction.RECEIVE;
		} else {
			direction = Transfer.Direction.PAY;
		}
		transfer.setDirection(direction);
		transfer.setAmount(amount);
		transferBusinessDelegate.saveTransfer(transfer);
	}

}