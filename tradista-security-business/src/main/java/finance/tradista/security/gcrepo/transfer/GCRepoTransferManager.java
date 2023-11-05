package finance.tradista.security.gcrepo.transfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import finance.tradista.core.daycountconvention.model.DayCountConvention;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.FixingError;
import finance.tradista.core.transfer.model.ProductTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.Transfer.Status;
import finance.tradista.core.transfer.model.TransferManager;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.core.transfer.service.FixingErrorBusinessDelegate;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.gcrepo.messaging.GCRepoTradeEvent;
import finance.tradista.security.gcrepo.model.GCRepoTrade;

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

public class GCRepoTransferManager implements TransferManager<GCRepoTradeEvent> {

	protected TransferBusinessDelegate transferBusinessDelegate;

	protected FixingErrorBusinessDelegate fixingErrorBusinessDelegate;

	protected ConfigurationBusinessDelegate configurationBusinessDelegate;

	protected QuoteBusinessDelegate quoteBusinessDelegate;

	public GCRepoTransferManager() {
		transferBusinessDelegate = new TransferBusinessDelegate();
		fixingErrorBusinessDelegate = new FixingErrorBusinessDelegate();
		configurationBusinessDelegate = new ConfigurationBusinessDelegate();
		quoteBusinessDelegate = new QuoteBusinessDelegate();
	}

	@Override
	public void createTransfers(GCRepoTradeEvent event) throws TradistaBusinessException {
		GCRepoTrade trade = event.getTrade();
		List<Transfer> transfersToBeSaved = new ArrayList<Transfer>();

		if (event.getOldTrade() != null) {
			GCRepoTrade oldTrade = event.getOldTrade();

			// Checking if cash transfer of opening leg should be updated
			if (!oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getAmount().compareTo(trade.getAmount()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> existingCashTransfers = transferBusinessDelegate
						.getTransfersByTradeIdAndPurpose(oldTrade.getId(), TransferPurpose.CASH_SETTLEMENT, false);
				CashTransfer existingCashTransfer = (CashTransfer) existingCashTransfers.stream().findFirst().get();
				transfersToBeSaved.addAll(createOrUpdateCashPaymentOpeningLeg(existingCashTransfer, trade));
			}

			// Checking if cash transfer of closing leg should be updated
			if (!oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getAmount().compareTo(trade.getAmount()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.getEndDate() == null && trade.getEndDate() != null)
					|| (oldTrade.getEndDate() != null && trade.getEndDate() == null)
					|| (oldTrade.getEndDate() != null && trade.getEndDate() != null
							&& !oldTrade.getEndDate().isEqual(trade.getEndDate()))
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> existingCashTransfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(
						oldTrade.getId(), TransferPurpose.RETURNED_CASH_PLUS_INTEREST, false);
				CashTransfer existingCashTransfer = ((CashTransfer) existingCashTransfers.stream().findFirst().get());
				transfersToBeSaved.addAll(createOrUpdateCashPaymentClosingLeg(existingCashTransfer, trade));
			}

			// Checking if collateral payments should be updated
			if ((oldTrade.getEndDate() == null && trade.getEndDate() != null)
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (!oldTrade.getGcBasket().getSecurities().equals(trade.getGcBasket().getSecurities()))
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> existingCollateralTransfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(
						oldTrade.getId(), TransferPurpose.COLLATERAL_SETTLEMENT, false);
				transfersToBeSaved.addAll(createOrUpdateCollateralPayment(existingCollateralTransfers, trade));
			}

			// Checking if returned Collateral payments should be updated
			if ((oldTrade.getEndDate() == null && trade.getEndDate() != null)
					|| (oldTrade.getEndDate() != null && trade.getEndDate() == null)
					|| (oldTrade.getEndDate() != null && trade.getEndDate() != null
							&& !oldTrade.getEndDate().isEqual(trade.getEndDate()))
					|| (!oldTrade.getGcBasket().getSecurities().equals(trade.getGcBasket().getSecurities()))
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> existingReturnedCollateralTransfers = transferBusinessDelegate
						.getTransfersByTradeIdAndPurpose(oldTrade.getId(), TransferPurpose.RETURNED_COLLATERAL, false);
				transfersToBeSaved
						.addAll(createOrUpdateReturnedCollateralPayment(existingReturnedCollateralTransfers, trade));
			}

		} else {
			transfersToBeSaved.addAll(createOrUpdateCashPaymentOpeningLeg(null, trade));
			transfersToBeSaved.addAll(createOrUpdateCashPaymentClosingLeg(null, trade));
			transfersToBeSaved.addAll(createOrUpdateCollateralPayment(null, trade));
			transfersToBeSaved.addAll(createOrUpdateReturnedCollateralPayment(null, trade));

		}

		if (!transfersToBeSaved.isEmpty()) {
			transferBusinessDelegate.saveTransfers(transfersToBeSaved);
		}
	}

	private List<CashTransfer> createOrUpdateCashPaymentOpeningLeg(CashTransfer existingCashTransfer, GCRepoTrade trade)
			throws TradistaBusinessException {

		List<CashTransfer> cashPayments = new ArrayList<CashTransfer>();

		if (existingCashTransfer != null) {
			existingCashTransfer.setStatus(Status.CANCELED);
			cashPayments.add(existingCashTransfer);
		}

		// New cash settlement (opening leg)
		CashTransfer newCashPayment = new CashTransfer(trade.getBook(), TransferPurpose.CASH_SETTLEMENT,
				trade.getSettlementDate(), trade, trade.getCurrency());
		newCashPayment.setAmount(trade.getAmount());
		newCashPayment.setCreationDateTime(LocalDateTime.now());
		newCashPayment.setDirection(trade.isBuy() ? Transfer.Direction.RECEIVE : Transfer.Direction.PAY);
		newCashPayment.setStatus(Transfer.Status.KNOWN);
		newCashPayment.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		cashPayments.add(newCashPayment);

		return cashPayments;
	}

	private List<CashTransfer> createOrUpdateCashPaymentClosingLeg(CashTransfer existingCashTransfer, GCRepoTrade trade)
			throws TradistaBusinessException {

		List<CashTransfer> cashPayments = new ArrayList<CashTransfer>();

		if (existingCashTransfer != null) {
			existingCashTransfer.setStatus(Status.CANCELED);
			cashPayments.add(existingCashTransfer);
		}

		// Returned cash settlement (closing leg)
		CashTransfer newCashPayment = new CashTransfer(trade.getBook(), TransferPurpose.RETURNED_CASH_PLUS_INTEREST,
				trade.getSettlementDate(), trade, trade.getCurrency());
		if (trade.isFixedRepoRate()) {
			BigDecimal repoRate = trade.getRepoRate().divide(new BigDecimal(100),
					configurationBusinessDelegate.getScale(), configurationBusinessDelegate.getRoundingMode());
			BigDecimal interestAmount = trade.getAmount().multiply(repoRate).multiply(PricerUtil
					.daysToYear(new DayCountConvention("ACT/360"), trade.getSettlementDate(), trade.getEndDate()));
			newCashPayment.setAmount(trade.getAmount().add(interestAmount));
			newCashPayment.setStatus(Transfer.Status.KNOWN);
			newCashPayment.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		} else {
			newCashPayment.setStatus(Transfer.Status.UNKNOWN);
		}
		newCashPayment.setCreationDateTime(LocalDateTime.now());
		newCashPayment.setDirection(trade.isBuy() ? Transfer.Direction.PAY : Transfer.Direction.RECEIVE);

		cashPayments.add(newCashPayment);

		return cashPayments;
	}

	private List<ProductTransfer> createOrUpdateCollateralPayment(List<Transfer> existingCollateralTransfers,
			GCRepoTrade trade) throws TradistaBusinessException {

		List<ProductTransfer> collateralPaymentsToBeSaved = new ArrayList<ProductTransfer>();
		List<ProductTransfer> newCollateralPayments = new ArrayList<ProductTransfer>();

		List<Transfer> existingPotentialCollateralTransfers = null;
		if (existingCollateralTransfers != null) {
			existingPotentialCollateralTransfers = existingCollateralTransfers.stream()
					.filter(t -> t.getStatus().equals(Status.POTENTIAL)).toList();
		}

		for (Security sec : trade.getGcBasket().getSecurities()) {
			ProductTransfer newCollateralPayment = new ProductTransfer(trade.getBook(), sec,
					TransferPurpose.COLLATERAL_SETTLEMENT, trade.getSettlementDate(), trade);
			newCollateralPayment.setCreationDateTime(LocalDateTime.now());
			newCollateralPayment.setDirection(trade.isBuy() ? Transfer.Direction.PAY : Transfer.Direction.RECEIVE);
			newCollateralPayment.setStatus(Transfer.Status.POTENTIAL);
			if (existingPotentialCollateralTransfers == null
					|| !existingPotentialCollateralTransfers.contains(newCollateralPayment)) {
				collateralPaymentsToBeSaved.add(newCollateralPayment);
			}
			newCollateralPayments.add(newCollateralPayment);
		}

		// we cancel the existing collateral transfers that are not relevant anymore
		if (existingCollateralTransfers != null) {
			for (Transfer transfer : existingCollateralTransfers) {
				if (!newCollateralPayments.contains(transfer) || !transfer.getStatus().equals(Status.POTENTIAL)) {
					transfer.setStatus(Status.CANCELED);
					collateralPaymentsToBeSaved.add((ProductTransfer) transfer);
				}
			}
		}

		return collateralPaymentsToBeSaved;
	}

	private List<ProductTransfer> createOrUpdateReturnedCollateralPayment(List<Transfer> existingCollateralTransfers,
			GCRepoTrade trade) throws TradistaBusinessException {

		List<ProductTransfer> collateralPaymentsToBeSaved = new ArrayList<ProductTransfer>();
		List<ProductTransfer> newCollateralPayments = new ArrayList<ProductTransfer>();

		List<Transfer> existingPotentialCollateralTransfers = null;
		if (existingCollateralTransfers != null) {
			existingPotentialCollateralTransfers = existingCollateralTransfers.stream()
					.filter(t -> t.getStatus().equals(Status.POTENTIAL)).toList();
		}

		if (trade.getEndDate() != null) {
			for (Security sec : trade.getGcBasket().getSecurities()) {
				ProductTransfer newCollateralPayment = new ProductTransfer(trade.getBook(), sec,
						TransferPurpose.RETURNED_COLLATERAL, trade.getEndDate(), trade);
				newCollateralPayment.setCreationDateTime(LocalDateTime.now());
				newCollateralPayment.setDirection(trade.isBuy() ? Transfer.Direction.RECEIVE : Transfer.Direction.PAY);
				newCollateralPayment.setStatus(Transfer.Status.POTENTIAL);
				if (existingPotentialCollateralTransfers == null
						|| !existingPotentialCollateralTransfers.contains(newCollateralPayment)) {
					collateralPaymentsToBeSaved.add(newCollateralPayment);
				}
				newCollateralPayments.add(newCollateralPayment);
			}
		}

		// we cancel the existing collateral transfers that are not relevant anymore
		if (existingCollateralTransfers != null) {
			for (Transfer transfer : existingCollateralTransfers) {
				if (!newCollateralPayments.contains(transfer) || !transfer.getStatus().equals(Status.POTENTIAL)) {
					transfer.setStatus(Status.CANCELED);
					collateralPaymentsToBeSaved.add((ProductTransfer) transfer);
				}
			}
		}

		return collateralPaymentsToBeSaved;
	}

	@Override
	public void fixCashTransfer(CashTransfer transfer, long quoteSetId) throws TradistaBusinessException {
		GCRepoTrade trade = (GCRepoTrade) transfer.getTrade();
		BigDecimal amount;
		String quoteName = Index.INDEX + "." + trade.getIndex() + "." + trade.getIndexTenor();
		Set<QuoteValue> quoteValues = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDates(quoteSetId,
				quoteName, QuoteType.INTEREST_RATE, trade.getSettlementDate(), trade.getEndDate());

		if (quoteValues == null || quoteValues.isEmpty()) {
			String errorMsg = String.format(
					"Transfer %n cannot be fixed. Impossible to get the %s index closing value between %tD and %tD in QuoteSet %s.",
					transfer.getId(), quoteName, trade.getSettlementDate(), trade.getEndDate(), quoteSetId);
			createFixingError(transfer, quoteSetId, quoteName, errorMsg);
			throw new TradistaBusinessException(errorMsg);
		}

		Map<LocalDate, QuoteValue> quoteValuesMap = quoteValues.stream()
				.collect(Collectors.toMap(QuoteValue::getDate, Function.identity()));

		List<LocalDate> dates = trade.getSettlementDate().datesUntil(trade.getEndDate()).collect(Collectors.toList());

		BigDecimal repoRate = BigDecimal.ZERO;

		StringBuilder errorMsg = new StringBuilder();
		for (LocalDate date : dates) {
			if (!quoteValuesMap.containsKey(date) || quoteValuesMap.get(date).getClose() == null) {
				errorMsg.append(String.format("%tD ", date));
			} else {
				repoRate.add(quoteValuesMap.get(date).getClose());
			}
		}
		if (errorMsg.length() > 0) {
			errorMsg = new StringBuilder(String.format(
					"Transfer %n cannot be fixed. Impossible to get the %s index closing value in QuoteSet %s for dates : ",
					transfer.getId(), quoteName, quoteSetId)).append(errorMsg);
			createFixingError(transfer, quoteSetId, quoteName, errorMsg.toString());
			throw new TradistaBusinessException(errorMsg.toString());
		}

		repoRate = repoRate.divide(new BigDecimal(dates.size()));
		repoRate = repoRate.add(trade.getIndexOffset());
		repoRate = repoRate.divide(new BigDecimal(100), configurationBusinessDelegate.getScale(),
				configurationBusinessDelegate.getRoundingMode());

		amount = trade.getAmount().multiply(repoRate);
		if (amount.signum() == 0) {
			// No transfer
			transferBusinessDelegate.deleteTransfer(transfer.getId());
			return;
			// TODO add a warn somewhere ?
		}
		Transfer.Direction direction;

		if (trade.isBuy()) {
			if (amount.signum() > 0) {
				direction = Transfer.Direction.PAY;
			} else {
				direction = Transfer.Direction.RECEIVE;
				amount = amount.negate();
			}
		} else {
			if (amount.signum() > 0) {
				direction = Transfer.Direction.RECEIVE;
			} else {
				direction = Transfer.Direction.PAY;
				amount = amount.negate();
			}
		}

		transfer.setDirection(direction);
		transfer.setAmount(amount);
		transferBusinessDelegate.saveTransfer(transfer);
	}

	private void createFixingError(CashTransfer transfer, long quoteSetId, String quoteName, String errorMsg)
			throws TradistaBusinessException {
		FixingError fixingError = new FixingError();
		fixingError.setCashTransfer(transfer);
		fixingError.setErrorDate(LocalDateTime.now());
		fixingError.setMessage(errorMsg);
		fixingError.setStatus(finance.tradista.core.error.model.Error.Status.UNSOLVED);
		List<FixingError> errors = new ArrayList<FixingError>(1);
		errors.add(fixingError);
		fixingErrorBusinessDelegate.saveFixingErrors(errors);
		throw new TradistaBusinessException(errorMsg);
	}

}