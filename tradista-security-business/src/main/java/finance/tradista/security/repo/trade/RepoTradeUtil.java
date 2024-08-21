package finance.tradista.security.repo.trade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.transfer.model.ProductTransfer;
import finance.tradista.core.transfer.model.Transfer;
import finance.tradista.core.transfer.model.Transfer.Direction;
import finance.tradista.core.transfer.model.Transfer.Type;
import finance.tradista.core.transfer.model.TransferPurpose;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.security.bond.service.BondBusinessDelegate;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.equity.service.EquityBusinessDelegate;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.repo.model.RepoTrade;

/********************************************************************************
 * Copyright (c) 2024 Olivier Asuncion
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

public final class RepoTradeUtil {

	private static TransferBusinessDelegate transferBusinessDelegate = new TransferBusinessDelegate();

	private static BondBusinessDelegate bondBusinessDelegate = new BondBusinessDelegate();

	private static EquityBusinessDelegate equityBusinessDelegate = new EquityBusinessDelegate();

	private static BookBusinessDelegate bookBusinessDelegate = new BookBusinessDelegate();

	private RepoTradeUtil() {
	}

	public static Map<Security, Map<Book, BigDecimal>> getAllocatedCollateral(RepoTrade trade)
			throws TradistaBusinessException {
		Map<Security, Map<Book, BigDecimal>> securities = null;
		List<Transfer> givenCollateral = null;

		try {
			givenCollateral = transferBusinessDelegate.getTransfers(Type.PRODUCT, Transfer.Status.KNOWN, Direction.PAY,
					TransferPurpose.COLLATERAL_SETTLEMENT, trade.getId(), 0, 0, 0, null, null, null, null, null, null);
		} catch (TradistaBusinessException tbe) {
			// Not expected here.
		}

		if (givenCollateral != null && !givenCollateral.isEmpty()) {
			givenCollateral = givenCollateral.stream()
					.filter(t -> t.getSettlementDate() == null || t.getSettlementDate().isBefore(LocalDate.now())
							|| t.getSettlementDate().isEqual(LocalDate.now()))
					.toList();
			securities = new HashMap<>(givenCollateral.size());
			for (Transfer t : givenCollateral) {
				if (securities.containsKey(t.getProduct())) {
					Map<Book, BigDecimal> bookMap = securities.get(t.getProduct());
					BigDecimal newQty = bookMap.get(trade.getBook()).add(((ProductTransfer) t).getQuantity());
					bookMap.put(trade.getBook(), newQty);
					securities.put((Security) t.getProduct(), bookMap);
				} else {
					Map<Book, BigDecimal> bookMap = new HashMap<>();
					BigDecimal newQty = ((ProductTransfer) t).getQuantity();
					bookMap.put(trade.getBook(), newQty);
					securities.put((Security) t.getProduct(), bookMap);
				}
			}
		}
		List<Transfer> returnedCollateral = null;
		try {
			returnedCollateral = transferBusinessDelegate.getTransfers(Type.PRODUCT, Transfer.Status.KNOWN,
					Direction.RECEIVE, TransferPurpose.RETURNED_COLLATERAL, trade.getId(), 0, 0, 0, null, null, null,
					null, null, null);
		} catch (TradistaBusinessException tbe) {
			// Not expected here.
		}
		if (returnedCollateral != null && !returnedCollateral.isEmpty()) {
			returnedCollateral = returnedCollateral.stream()
					.filter(t -> t.getSettlementDate() == null || t.getSettlementDate().isBefore(LocalDate.now())
							|| t.getSettlementDate().isEqual(LocalDate.now()))
					.toList();
			if (!returnedCollateral.isEmpty()) {
				if (securities == null) {
					securities = new HashMap<>(returnedCollateral.size());
				}
				for (Transfer t : returnedCollateral) {
					if (securities.containsKey(t.getProduct())) {
						Map<Book, BigDecimal> bookMap = securities.get(t.getProduct());
						BigDecimal newQty = bookMap.get(trade.getBook()).subtract(((ProductTransfer) t).getQuantity());
						bookMap.put(trade.getBook(), newQty);
						securities.put((Security) t.getProduct(), bookMap);
					}
				}
			}
		}

		return securities;
	}

	public void checkCollateralConsistency(RepoTrade trade) throws TradistaBusinessException {
		// Checking business consistency of collateral to add
		StringBuilder errMsg = new StringBuilder();
		if (trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty()) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : trade.getCollateralToAdd().entrySet()) {
				// 1. Security must exist
				Security secInDb = bondBusinessDelegate.getBondById(entry.getKey().getId());
				if (secInDb == null) {
					secInDb = equityBusinessDelegate.getEquityById(entry.getKey().getId());
				}
				if (secInDb == null) {
					errMsg.append(String.format(
							"The security %s cannot be found in the system, it cannot be added as collateral.%n",
							entry.getKey()));
					continue;
				}
				if (trade instanceof GCRepoTrade gcRepoTrade) {
					// 2. (GC Repos only) Security must be part of the GC Basket
					if (!gcRepoTrade.getGcBasket().getSecurities().contains(entry.getKey())) {
						errMsg.append(String.format(
								"The security %s cannot be found in the GC Basket %s, it cannot be added as collateral.%n",
								entry.getKey(), gcRepoTrade.getGcBasket().getName()));
						continue;
					}
				}
				// 3. Books should exist
				Map<Book, BigDecimal> bookMap = entry.getValue();
				for (Map.Entry<Book, BigDecimal> bookEntry : bookMap.entrySet()) {
					Book bookInDb = bookBusinessDelegate.getBookById(bookEntry.getKey().getId());
					if (bookInDb == null) {
						errMsg.append(String.format(
								"The origin book %s cannot be found in the system, it cannot be used as collateral source.%n",
								bookEntry.getKey().getName()));
					}
				}
			}

		}

		// Checking business consistency of collateral to remove
		if (trade.getCollateralToRemove() != null && !trade.getCollateralToRemove().isEmpty()) {
			for (Map.Entry<Security, Map<Book, BigDecimal>> entry : trade.getCollateralToRemove().entrySet()) {
				// 1. Security must exist
				Security secInDb = bondBusinessDelegate.getBondById(entry.getKey().getId());
				if (secInDb == null) {
					secInDb = equityBusinessDelegate.getEquityById(entry.getKey().getId());
				}
				if (secInDb == null) {
					errMsg.append(String.format(
							"The security %s cannot be found in the system, it cannot be removed from collateral.%n",
							entry.getKey()));
					continue;
				}
				// 2. Books should exist
				Map<Book, BigDecimal> bookMap = entry.getValue();
				for (Map.Entry<Book, BigDecimal> bookEntry : bookMap.entrySet()) {
					Book bookInDb = bookBusinessDelegate.getBookById(bookEntry.getKey().getId());
					if (bookInDb == null) {
						errMsg.append(String.format(
								"The book %s cannot be found in the system, it cannot be used as collateral source.%n",
								bookEntry.getKey().getName()));
					}
				}
			}
		}

		if (!errMsg.isEmpty()) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}