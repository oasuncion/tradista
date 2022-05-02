package finance.tradista.security.bond.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.configuration.service.ConfigurationBusinessDelegate;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.transfer.model.CashTransfer;
import finance.tradista.core.transfer.model.FixingError;
import finance.tradista.core.transfer.model.ProductTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.TransferManager;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.core.transfer.service.FixingErrorBusinessDelegate;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.security.bond.messaging.BondTradeEvent;
import finance.tradista.security.bond.model.BondTrade;

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

public class BondTransferManager implements TransferManager<BondTradeEvent> {

	protected TransferBusinessDelegate transferBusinessDelegate;

	protected FixingErrorBusinessDelegate fixingErrorBusinessDelegate;

	protected ConfigurationBusinessDelegate configurationBusinessDelegate;

	public BondTransferManager() {
		transferBusinessDelegate = new TransferBusinessDelegate();
		fixingErrorBusinessDelegate = new FixingErrorBusinessDelegate();
		configurationBusinessDelegate = new ConfigurationBusinessDelegate();
	}

	@Override
	public void createTransfers(BondTradeEvent event) throws TradistaBusinessException {
		BondTrade trade = event.getTrade();
		List<Transfer> transfersToBeSaved = new ArrayList<Transfer>();
		// Get the cash transfers currently planned to be received for this bond
		List<CashTransfer> existingTransfers = transferBusinessDelegate
				.getCashTransfersByProductIdAndStartDate(trade.getProduct().getId(), trade.getSettlementDate());

		List<CashTransfer> cashTransfers = null;
		if (event.getOldTrade() != null) {
			BondTrade oldTrade = event.getOldTrade();

			List<CashTransfer> oldTradeExistingTransfers = null;
			if (!oldTrade.getProduct().equals(trade.getProduct()) && oldTrade.isBuy()) {
				oldTradeExistingTransfers = transferBusinessDelegate.getCashTransfersByProductIdAndStartDate(
						oldTrade.getProduct().getId(), oldTrade.getSettlementDate());
			}

			if (!oldTrade.getProduct().equals(trade.getProduct()) || !oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getProduct().getPrincipal().compareTo(trade.getProduct().getPrincipal()) != 0
					|| oldTrade.getQuantity().compareTo(trade.getQuantity()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| !oldTrade.getProduct().getMaturityDate().isEqual(trade.getProduct().getMaturityDate())
					|| !oldTrade.getProduct().getDatedDate().isEqual(trade.getProduct().getDatedDate())
					|| oldTrade.getProduct().getCouponType().equals(trade.getProduct().getCouponType())
					|| (oldTrade.getProduct().getCoupon() != null && trade.getProduct().getCoupon() != null
							&& (oldTrade.getProduct().getCoupon().compareTo(trade.getProduct().getCoupon()) != 0))
					|| !oldTrade.getProduct().getCouponFrequency().equals(trade.getProduct().getCouponFrequency())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				transfersToBeSaved.addAll(createOrUpdateCoupons(existingTransfers, oldTradeExistingTransfers,
						cashTransfers, trade, oldTrade, trade.isBuy()));

			}

			if (!oldTrade.getProduct().equals(trade.getProduct()) || !oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getQuantity().compareTo(trade.getQuantity()) != 0
					|| oldTrade.getAmount().compareTo(trade.getAmount()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.BOND_PAYMENT, false);
				// if the transfers list is null or empty, it is not normal, but
				// the process
				// should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					for (Transfer transfer : transfers) {
						transfer.setStatus(Transfer.Status.CANCELED);
						transfersToBeSaved.add(transfer);
					}
				}
				transfersToBeSaved.add(createNewBondPayment(cashTransfers, trade));

			}

			if (!oldTrade.getProduct().equals(trade.getProduct()) || !oldTrade.getCurrency().equals(trade.getCurrency())
					|| oldTrade.getProduct().getPrincipal().compareTo(trade.getProduct().getPrincipal()) != 0
					|| oldTrade.getQuantity().compareTo(trade.getQuantity()) != 0
					|| oldTrade.getAmount().compareTo(trade.getAmount()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| !oldTrade.getProduct().getMaturityDate().isEqual(trade.getProduct().getMaturityDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				transfersToBeSaved.addAll(createOrUpdateNotionalRepayment(existingTransfers, oldTradeExistingTransfers,
						cashTransfers, trade, oldTrade, trade.isBuy()));

			}

			if (!oldTrade.getProduct().equals(trade.getProduct())
					|| oldTrade.getQuantity().compareTo(trade.getQuantity()) != 0
					|| !oldTrade.getSettlementDate().isEqual(trade.getSettlementDate())
					|| (oldTrade.isBuy() != trade.isBuy()) || (!oldTrade.getBook().equals(trade.getBook()))) {
				List<Transfer> transfers = transferBusinessDelegate.getTransfersByTradeIdAndPurpose(oldTrade.getId(),
						TransferPurpose.BOND_SETTLEMENT, false);
				// if the transfers list is null or empty, it is not normal, but
				// the process
				// should
				// continue.
				if (transfers == null || transfers.isEmpty()) {
					// TODO logs + Errors viewable in the error report ?
				} else {
					for (Transfer transfer : transfers) {
						transfer.setStatus(Transfer.Status.CANCELED);
						transfersToBeSaved.add(transfer);
					}
				}
				transfersToBeSaved.add(createNewBondSettlement(trade));
			}

		} else {
			transfersToBeSaved.add(createNewBondSettlement(trade));
			transfersToBeSaved.add(createNewBondPayment(cashTransfers, trade));

			transfersToBeSaved
					.addAll(createOrUpdateCoupons(existingTransfers, cashTransfers, null, trade, null, trade.isBuy()));
			transfersToBeSaved.addAll(createOrUpdateNotionalRepayment(existingTransfers, cashTransfers, null, trade,
					null, trade.isBuy()));

		}

		if (!transfersToBeSaved.isEmpty()) {
			transferBusinessDelegate.saveTransfers(transfersToBeSaved);
		}
	}

	private ProductTransfer createNewBondSettlement(BondTrade trade) throws TradistaBusinessException {
		ProductTransfer productTransfer = new ProductTransfer();
		productTransfer.setCreationDateTime(LocalDateTime.now());
		if (trade.isBuy()) {
			productTransfer.setDirection(Transfer.Direction.RECEIVE);
		} else {
			productTransfer.setDirection(Transfer.Direction.PAY);
		}
		productTransfer.setSettlementDate(trade.getSettlementDate());
		productTransfer.setFixingDateTime(trade.getCreationDate().atStartOfDay());
		productTransfer.setProduct(trade.getProduct());
		productTransfer.setPurpose(TransferPurpose.BOND_SETTLEMENT);
		productTransfer.setQuantity(trade.getQuantity());
		productTransfer.setStatus(Transfer.Status.KNOWN);
		productTransfer.setTrade(trade);
		productTransfer.setBook(trade.getBook());

		return productTransfer;
	}

	private List<CashTransfer> createOrUpdateCoupons(List<CashTransfer> existingTransfers,
			List<CashTransfer> oldTradeExistingTransfers, List<CashTransfer> cashTransfers, BondTrade trade,
			BondTrade oldTrade, boolean addition) throws TradistaBusinessException {

		if (cashTransfers == null) {
			cashTransfers = BondTransferUtil.generateCashSettlements(trade);
		}

		List<CashTransfer> coupons = new ArrayList<CashTransfer>();

		// oldTradeExistingTransfers is not null, trade product has been changed
		// and we
		// must cancel the old trade's generated coupons
		if (oldTradeExistingTransfers != null) {
			for (CashTransfer oldTradeExistingTransfer : oldTradeExistingTransfers) {
				if (oldTradeExistingTransfer.getPurpose().equals(TransferPurpose.COUPON)) {
					BigDecimal oldTradeAmount = oldTrade.getQuantity()
							.multiply(oldTrade.getProduct().getPrincipal().multiply(oldTrade.getProduct().getCoupon()
									.divide(BigDecimal.valueOf(100), configurationBusinessDelegate.getRoundingMode())));

					oldTradeExistingTransfer.setAmount(oldTradeExistingTransfer.getAmount().subtract(oldTradeAmount));

					if (oldTradeExistingTransfer.getAmount().signum() <= 0) {
						transferBusinessDelegate.deleteTransfer(oldTradeExistingTransfer.getId());
					} else {
						coupons.add(oldTradeExistingTransfer);
					}
					break;
				}
			}
		}

		for (CashTransfer transfer : cashTransfers) {
			if (transfer.getPurpose().equals(TransferPurpose.COUPON)) {
				boolean exists = false;
				if (existingTransfers != null) {
					for (CashTransfer existingTransfer : existingTransfers) {
						if (transfer.getPurpose().equals(TransferPurpose.COUPON)
								&& existingTransfer.getSettlementDate().isEqual(transfer.getSettlementDate())
								&& existingTransfer.getStatus().equals(transfer.getStatus())) {
							if (existingTransfer.getStatus().equals(Transfer.Status.KNOWN)) {
								exists = true;
								BigDecimal oldTradeAmount = BigDecimal.ZERO;
								if (oldTrade != null && oldTrade.isBuy()) {
									oldTradeAmount = oldTradeAmount.add(oldTrade.getQuantity()
											.multiply(oldTrade.getProduct().getPrincipal()
													.multiply(oldTrade.getProduct().getCoupon().divide(
															BigDecimal.valueOf(100),
															configurationBusinessDelegate.getRoundingMode()))));
								}
								if (addition) {
									existingTransfer.setAmount(existingTransfer.getAmount().subtract(oldTradeAmount)
											.add(transfer.getAmount()));
								} else {
									existingTransfer
											.setAmount(existingTransfer.getAmount().subtract(transfer.getAmount()));
								}
								if (existingTransfer.getAmount().signum() <= 0) {
									transferBusinessDelegate.deleteTransfer(existingTransfer.getId());
								} else {
									coupons.add(existingTransfer);
								}
								break;
							}
							if (existingTransfer.getStatus().equals(Transfer.Status.UNKNOWN)) {
								exists = true;
							}
						}
					}
				}
				if (!exists && trade.isBuy()) {
					coupons.add(transfer);
				}
			}
		}

		return coupons;
	}

	private List<CashTransfer> createOrUpdateNotionalRepayment(List<CashTransfer> existingTransfers,
			List<CashTransfer> oldTradeExistingTransfers, List<CashTransfer> cashTransfers, BondTrade trade,
			BondTrade oldTrade, boolean addition) throws TradistaBusinessException {

		if (cashTransfers == null) {
			cashTransfers = BondTransferUtil.generateCashSettlements(trade);
		}

		List<CashTransfer> notionalRepayments = new ArrayList<CashTransfer>();

		// oldTradeExistingTransfers is not null, trade product has been changed
		// and we
		// must cancel the old trade's generated coupons
		if (oldTradeExistingTransfers != null) {
			for (CashTransfer oldTradeExistingTransfer : oldTradeExistingTransfers) {
				if (oldTradeExistingTransfer.getPurpose().equals(TransferPurpose.NOTIONAL_REPAYMENT)) {
					BigDecimal oldTradeAmount = oldTrade.getProduct().getPrincipal().multiply(oldTrade.getQuantity());

					oldTradeExistingTransfer.setAmount(oldTradeExistingTransfer.getAmount().subtract(oldTradeAmount));

					if (oldTradeExistingTransfer.getAmount().signum() <= 0) {
						transferBusinessDelegate.deleteTransfer(oldTradeExistingTransfer.getId());
					} else {
						notionalRepayments.add(oldTradeExistingTransfer);
					}
					break;
				}
			}
		}

		for (CashTransfer transfer : cashTransfers) {
			if (transfer.getPurpose().equals(TransferPurpose.NOTIONAL_REPAYMENT)) {
				boolean exists = false;
				if (existingTransfers != null) {
					for (CashTransfer existingTransfer : existingTransfers) {
						if (transfer.getPurpose().equals(TransferPurpose.NOTIONAL_REPAYMENT)
								&& existingTransfer.getSettlementDate().isEqual(transfer.getSettlementDate())
								&& existingTransfer.getStatus().equals(transfer.getStatus())
								&& existingTransfer.getStatus().equals(Transfer.Status.KNOWN)) {
							exists = true;
							BigDecimal oldTradeAmount = BigDecimal.ZERO;
							if (oldTrade != null && oldTrade.isBuy()) {
								oldTradeAmount = oldTradeAmount
										.add(oldTrade.getQuantity().multiply(oldTrade.getProduct().getPrincipal()));
							}
							if (addition) {
								existingTransfer.setAmount(existingTransfer.getAmount().subtract(oldTradeAmount)
										.add(transfer.getAmount()));
							} else {
								existingTransfer.setAmount(existingTransfer.getAmount().subtract(transfer.getAmount()));
							}
							if (existingTransfer.getAmount().signum() <= 0) {
								transferBusinessDelegate.deleteTransfer(existingTransfer.getId());
							} else {
								notionalRepayments.add(existingTransfer);
							}
							break;
						}
					}
				}
				if (!exists && trade.isBuy()) {
					notionalRepayments.add(transfer);
				}
			}
		}

		return notionalRepayments;
	}

	private CashTransfer createNewBondPayment(List<CashTransfer> cashTransfers, BondTrade trade)
			throws TradistaBusinessException {

		if (cashTransfers == null) {
			cashTransfers = BondTransferUtil.generateCashSettlements(trade);
		}

		for (Transfer transfer : cashTransfers) {
			if (transfer.getPurpose().equals(TransferPurpose.BOND_PAYMENT)) {
				return (CashTransfer) transfer;
			}
		}

		return null;
	}

	@Override
	public void fixCashTransfer(CashTransfer transfer, long quoteSetId) throws TradistaBusinessException {
		BondTrade trade = (BondTrade) transfer.getTrade();
		BigDecimal notional = trade.getAmount();
		BigDecimal fractionedNotional;
		BigDecimal amount;
		String quoteName = Index.INDEX + "." + trade.getProduct().getReferenceRateIndex() + "."
				+ trade.getProduct().getCouponFrequency();
		BigDecimal ir = PricerUtil.getValueAsOfDateFromQuote(quoteName, quoteSetId, QuoteType.INTEREST_RATE,
				QuoteValue.CLOSE, transfer.getFixingDateTime().toLocalDate());
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
		fractionedNotional = notional.multiply(PricerUtil.daysToYear(trade.getProduct().getCouponFrequency()));
		amount = fractionedNotional.multiply(ir.divide(BigDecimal.valueOf(100),
				configurationBusinessDelegate.getScale(), configurationBusinessDelegate.getRoundingMode()));
		if (amount.signum() == 0) {
			// No transfer
			transferBusinessDelegate.deleteTransfer(transfer.getId());
			return;
			// TODO add a warn somewhere ?
		}
		Transfer.Direction direction;

		if (trade.isBuy()) {
			if (amount.signum() > 0) {
				direction = Transfer.Direction.RECEIVE;
			} else {
				direction = Transfer.Direction.PAY;
				amount = amount.negate();
			}
		} else {
			if (amount.signum() > 0) {
				direction = Transfer.Direction.PAY;
			} else {
				direction = Transfer.Direction.RECEIVE;
				amount = amount.negate();
			}
		}

		transfer.setDirection(direction);
		transfer.setAmount(amount);
		transferBusinessDelegate.saveTransfer(transfer);
	}

}