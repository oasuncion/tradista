package finance.tradista.core.trade.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import finance.tradista.core.book.persistence.BookSQL;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.currency.persistence.CurrencySQL;
import finance.tradista.core.legalentity.persistence.LegalEntitySQL;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.product.service.ProductBusinessDelegate;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.workflow.persistence.StatusSQL;

/*
 * Copyright 2015 Olivier Asuncion
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

public class TradeSQL {

    public static List<Trade<? extends Product>> getTradesByCreationDate(LocalDate creationDate) {
	List<Trade<? extends Product>> trades = null;

	try (Connection con = TradistaDB.getConnection();
		PreparedStatement stmtGetTradesByCreationDate = con
			.prepareStatement("SELECT * FROM TRADE WHERE " + "CREATION_DATE = ? ")) {
	    stmtGetTradesByCreationDate.setDate(1, java.sql.Date.valueOf(creationDate));
	    try (ResultSet results = stmtGetTradesByCreationDate.executeQuery()) {
		while (results.next()) {
		    Trade<? extends Product> trade = getTrade(results.getLong("id"), false);
		    if (trade == null) {
			// the trade is an underlying: we don't add it.
			continue;
		    }
		    trade.setId(results.getLong("id"));
		    java.sql.Date tradeDate = results.getDate("trade_date");
		    if (tradeDate != null) {
			trade.setTradeDate(tradeDate.toLocalDate());
		    }
		    java.sql.Date settlementDate = results.getDate("settlement_date");
		    if (settlementDate != null) {
			trade.setSettlementDate(settlementDate.toLocalDate());
		    }
		    trade.setAmount(results.getBigDecimal("amount"));
		    trade.setCurrency(CurrencySQL.getCurrencyById(results.getLong("currency_id")));
		    trade.setCreationDate(results.getDate("creation_Date").toLocalDate());
		    trade.setBook(BookSQL.getBookById(results.getLong("book_id")));
		    long statusId = results.getLong("status_id");
		    if (statusId != 0) {
			trade.setStatus(StatusSQL.getStatusById(statusId));
		    }
		    if (trades == null) {
			trades = new ArrayList<Trade<? extends Product>>();
		    }
		    trades.add(trade);
		}
	    }
	} catch (SQLException sqle) {
	    sqle.printStackTrace();
	    throw new TradistaTechnicalException(sqle);
	}
	return trades;
    }

    public static Trade<? extends Product> getTradeById(long id, boolean includeUnderlying) {
	Trade<? extends Product> trade = null;

	try (Connection con = TradistaDB.getConnection();
		PreparedStatement stmtGetTradeById = con.prepareStatement("SELECT * FROM TRADE WHERE ID = ? ")) {
	    stmtGetTradeById.clearParameters();
	    stmtGetTradeById.setLong(1, id);
	    try (ResultSet results = stmtGetTradeById.executeQuery()) {
		while (results.next()) {
		    if (trade == null) {
			trade = getTrade(id, includeUnderlying);
			if (trade == null) {
			    // The trade was not found (incorrect id or underlying
			    // type).
			    return null;
			}
		    }
		    trade.setId(results.getLong("id"));
		    java.sql.Date tradeDate = results.getDate("trade_date");
		    if (tradeDate != null) {
			trade.setTradeDate(tradeDate.toLocalDate());
		    }
		    java.sql.Date settlementDate = results.getDate("settlement_date");
		    if (settlementDate != null) {
			trade.setSettlementDate(settlementDate.toLocalDate());
		    }
		    long statusId = results.getLong("status_id");
		    if (statusId != 0) {
			trade.setStatus(StatusSQL.getStatusById(statusId));
		    }
		    trade.setCurrency(CurrencySQL.getCurrencyById(results.getLong("currency_id")));
		    trade.setAmount(results.getBigDecimal("amount"));
		    trade.setCreationDate(results.getDate("creation_Date").toLocalDate());
		    trade.setBook(BookSQL.getBookById(results.getLong("book_id")));
		    trade.setCounterparty(LegalEntitySQL.getLegalEntityById(results.getLong("counterparty_id")));
		}
	    }
	} catch (SQLException sqle) {
	    sqle.printStackTrace();
	    throw new TradistaTechnicalException(sqle);
	}
	return trade;
    }

    public static void setTradeCommonFields(Trade<? extends Product> trade, ResultSet rs)
	    throws TradistaBusinessException {

	if (rs == null) {
	    throw new TradistaTechnicalException("the ResultSet cannot be null.");
	}

	if (trade == null) {
	    throw new TradistaBusinessException("The trade cannot be null.");
	}

	try {
	    // Commmon fields
	    trade.setId(rs.getLong("id"));
	    trade.setBuySell(rs.getBoolean("buy_sell"));
	    trade.setCreationDate(rs.getDate("creation_Date").toLocalDate());
	    java.sql.Date tradeDate = rs.getDate("trade_date");
	    if (tradeDate != null) {
		trade.setTradeDate(tradeDate.toLocalDate());
	    }
	    java.sql.Date settlementDate = rs.getDate("settlement_date");
	    if (settlementDate != null) {
		trade.setSettlementDate(settlementDate.toLocalDate());
	    }
	    trade.setAmount(rs.getBigDecimal("amount"));
	    trade.setCounterparty(LegalEntitySQL.getLegalEntityById(rs.getLong("counterparty_id")));
	    trade.setCurrency(CurrencySQL.getCurrencyById(rs.getLong("currency_id")));
	    trade.setBook(BookSQL.getBookById(rs.getLong("book_id")));
	    long statusId = rs.getLong("status_id");
	    if (statusId != 0) {
		trade.setStatus(StatusSQL.getStatusById(statusId));
	    }

	} catch (SQLException sqle) {
	    sqle.printStackTrace();
	    throw new TradistaTechnicalException(sqle);
	}
    }

    private static Trade<? extends Product> getTrade(long id, boolean includeUnderlying) {
	ProductBusinessDelegate productBusinessDelegate = new ProductBusinessDelegate();
	Set<String> products = productBusinessDelegate.getAllProductTypes();
	try {
	    for (String product : products) {
		Class<?> serviceClass = getTradeSQLClass(productBusinessDelegate, product);
		Trade<? extends Product> trade;
		Class<Trade<? extends Product>> klass = null;
		switch (product) {
		case "IRSwap":
		case "Equity":
		case "FX": {
		    trade = TradistaUtil.callMethod(serviceClass.getCanonicalName(), klass, "getTradeById", id,
			    includeUnderlying);
		    break;
		}
		default: {
		    trade = TradistaUtil.callMethod(serviceClass.getCanonicalName(), klass, "getTradeById", id);
		}
		}

		if (trade != null) {
		    return trade;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new TradistaTechnicalException(e);
	}

	return null;

    }

    private static Class<?> getTradeSQLClass(ProductBusinessDelegate productBusinessDelegate, String product)
	    throws ClassNotFoundException, TradistaBusinessException {
	return TradistaUtil.getClass("finance.tradista." + productBusinessDelegate.getProductFamily(product) + "."
		+ product.toLowerCase() + ".persistence." + product + "TradeSQL");
    }

    public static List<Trade<? extends Product>> getTradesByDates(LocalDate minCreationDate, LocalDate maxCreationDate,
	    LocalDate minTradeDate, LocalDate maxTradeDate) {
	List<Trade<? extends Product>> trades = new ArrayList<Trade<? extends Product>>();

	try (Connection con = TradistaDB.getConnection(); Statement stmt = con.createStatement()) {
	    String query = "SELECT * FROM TRADE ";
	    if (minCreationDate != null || maxCreationDate != null || minTradeDate != null || maxTradeDate != null) {
		if (minCreationDate != null) {
		    query += " WHERE CREATION_DATE >= '"
			    + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(minCreationDate) + "'";
		}
		if (maxCreationDate != null) {
		    if (query.contains("WHERE")) {
			query += " AND ";
		    } else {
			query += "WHERE";
		    }
		    query += " CREATION_DATE <= '" + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(maxCreationDate)
			    + "'";
		}
		if (minTradeDate != null) {
		    if (query.contains("WHERE")) {
			query += " AND ";
		    } else {
			query += "WHERE";
		    }
		    query += " TRADE_DATE >= '" + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(minTradeDate) + "'";
		}
		if (maxTradeDate != null) {
		    if (query.contains("WHERE")) {
			query += " AND ";
		    } else {
			query += "WHERE";
		    }
		    query += " TRADE_DATE <= '" + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(maxTradeDate) + "'";
		}
	    }
	    try (ResultSet results = stmt.executeQuery(query)) {
		while (results.next()) {
		    Trade<? extends Product> trade = getTrade(results.getLong("id"), false);
		    if (trade == null) {
			// the trade is an underlying: we don't add it.
			continue;
		    }
		    trade.setId(results.getLong("id"));
		    java.sql.Date tradeDate = results.getDate("trade_date");
		    if (tradeDate != null) {
			trade.setTradeDate(tradeDate.toLocalDate());
		    }
		    java.sql.Date settlementDate = results.getDate("settlement_date");
		    if (settlementDate != null) {
			trade.setSettlementDate(settlementDate.toLocalDate());
		    }
		    trade.setAmount(results.getBigDecimal("amount"));
		    trade.setCreationDate(results.getDate("creation_Date").toLocalDate());
		    trade.setCurrency(CurrencySQL.getCurrencyById(results.getLong("currency_id")));
		    trade.setBook(BookSQL.getBookById(results.getLong("book_id")));
		    long statusId = results.getLong("status_id");
		    if (statusId != 0) {
			trade.setStatus(StatusSQL.getStatusById(statusId));
		    }
		    trades.add(trade);
		}
	    }
	} catch (SQLException sqle) {
	    sqle.printStackTrace();
	    throw new TradistaTechnicalException(sqle);
	}
	return trades;
    }

    public static Set<Trade<? extends Product>> getTrades(PositionDefinition posDef) {
	Set<Trade<? extends Product>> trades = null;

	try (Connection con = TradistaDB.getConnection();
		PreparedStatement stmtGetTradesByPosition = con.prepareStatement(buildSQLTradeQuery(posDef))) {
	    try (ResultSet results = stmtGetTradesByPosition.executeQuery()) {
		while (results.next()) {
		    if (trades == null) {
			trades = new HashSet<Trade<? extends Product>>();
		    }
		    Trade<? extends Product> trade = null;
		    Class<Trade<? extends Product>> klass = null;

		    if (posDef.getProductType() != null) {
			Class<?> serviceClass;
			try {
			    serviceClass = getTradeSQLClass(new ProductBusinessDelegate(), posDef.getProductType());
			    trade = TradistaUtil.callMethod(serviceClass.getCanonicalName(), klass, "getTrade",
				    results);
			} catch (ClassNotFoundException | TradistaBusinessException | SecurityException
				| IllegalArgumentException e) {
			} catch (Exception e) {
			    e.printStackTrace();
			    throw new TradistaTechnicalException(e);
			}
		    } else {
			ProductBusinessDelegate productBusinessDelegate = new ProductBusinessDelegate();
			Set<String> productTypes = productBusinessDelegate.getAvailableProductTypes();
			try {
			    for (String productType : productTypes) {
				Class<?> serviceClass = getTradeSQLClass(productBusinessDelegate, productType);
				trade = TradistaUtil.callMethod(serviceClass.getCanonicalName(), klass, "getTrade",
					results);
				if (trade != null) {
				    break;
				}
			    }
			} catch (Exception e) {
			    e.printStackTrace();
			    throw new TradistaTechnicalException(e);
			}
		    }

		    if (trade != null) {
			trades.add(trade);
		    }
		}
	    }
	} catch (SQLException sqle) {
	    sqle.printStackTrace();
	    throw new TradistaTechnicalException(sqle);
	}
	return trades;
    }

    private static String buildSQLTradeQuery(PositionDefinition posDef) {
	String table = "TRADE";
	String specificTable = null;
	String join;
	if (posDef.getProductType() == null) {
	    join = " LEFT OUTER JOIN VANILLA_OPTION_TRADE ON VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN FXSPOT_TRADE UNDERLYING_FXSPOT ON VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID = UNDERLYING_FXSPOT.FXSPOT_TRADE_ID";
	    join += " LEFT OUTER JOIN IRSWAP_TRADE UNDERLYING_IRSWAP ON VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID = UNDERLYING_IRSWAP.IRSWAP_TRADE_ID";
	    join += " LEFT OUTER JOIN IRSWAP_OPTION_TRADE ON VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID = IRSWAP_OPTION_TRADE.IRSWAP_OPTION_TRADE_ID";
	    join += " LEFT OUTER JOIN EQUITY_TRADE UNDERLYING_EQUITY ON VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID = UNDERLYING_EQUITY.EQUITY_TRADE_ID";
	    // TODO Check if outer join on und_trade is needed in the case of options, I
	    // think so.
	    join += " LEFT OUTER JOIN TRADE UND_EQUITY_TRADE ON UNDERLYING_EQUITY.EQUITY_TRADE_ID=UND_EQUITY_TRADE.ID";
	    join += " LEFT OUTER JOIN TRADE UND_FXSPOT_TRADE ON UNDERLYING_FXSPOT.FXSPOT_TRADE_ID=UND_FXSPOT_TRADE.ID";
	    join += " LEFT OUTER JOIN TRADE UND_IRSWAP_TRADE ON UNDERLYING_IRSWAP.IRSWAP_TRADE_ID=UND_IRSWAP_TRADE.ID";
	    join += " LEFT OUTER JOIN FXNDF_TRADE ON FXNDF_TRADE.FXNDF_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN IRSWAP_TRADE ON IRSWAP_TRADE.IRSWAP_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN CCYSWAP_TRADE ON CCYSWAP_TRADE.CCYSWAP_TRADE_ID = IRSWAP_TRADE.IRSWAP_TRADE_ID";
	    join += " LEFT OUTER JOIN LOAN_DEPOSIT_TRADE ON LOAN_DEPOSIT_TRADE.LOAN_DEPOSIT_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN IRFORWARD_TRADE ON IRFORWARD_TRADE.IRFORWARD_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN FRA_TRADE ON FRA_TRADE.FRA_TRADE_ID = IRFORWARD_TRADE.IRFORWARD_TRADE_ID";
	    join += " LEFT OUTER JOIN FUTURE_TRADE ON FUTURE_TRADE.FUTURE_TRADE_ID = IRFORWARD_TRADE.IRFORWARD_TRADE_ID";
	    join += " LEFT OUTER JOIN FXSWAP_TRADE ON FXSWAP_TRADE.FXSWAP_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN BOND_TRADE ON BOND_TRADE.BOND_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN EQUITY_TRADE ON EQUITY_TRADE.EQUITY_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN IRCAP_FLOOR_COLLAR_TRADE ON IRCAP_FLOOR_COLLAR_TRADE.IRCAP_FLOOR_COLLAR_TRADE_ID = TRADE.ID";
	    join += " LEFT OUTER JOIN IRFORWARD_TRADE FWD_TRADE ON FWD_TRADE.IRFORWARD_TRADE_ID = IRCAP_FLOOR_COLLAR_TRADE.IRFORWARD_TRADE_ID";
	    join += " LEFT OUTER JOIN TRADE UND_IRFORWARD_TRADE ON UND_IRFORWARD_TRADE.ID = FWD_TRADE.IRFORWARD_TRADE_ID";
	    join += " LEFT OUTER JOIN FXSPOT_TRADE ON FXSPOT_TRADE.FXSPOT_TRADE_ID = TRADE.ID WHERE";
	} else {
	    join = buildJoin(posDef.getProductType());
	    specificTable = getTableNameByProductType(posDef.getProductType());
	    table += ", " + specificTable;
	}
	String filters = "";
	if (!join.endsWith("WHERE")) {
	    filters = " AND ";
	}
	filters += " TRADE.BOOK_ID = " + posDef.getBook().getId();
	if (posDef.getCounterparty() != null) {
	    filters += " AND TRADE.COUNTERPARTY_ID=" + posDef.getCounterparty().getId();
	}
	if (posDef.getProduct() != null) {
	    filters += " AND TRADE.PRODUCT_ID=" + posDef.getProduct().getId();
	}
	if (posDef.getProductType() == null || posDef.getProductType().equals("EquityOption")) {

	}

	// Control added to be sure to not retrieve options underlying
	filters += " AND TRADE.TRADE_DATE IS NOT NULL";

	return "SELECT " + buildAliases(posDef.getProductType()) + " FROM " + table + join + filters;
    }

    private static String buildAliases(String productType) {
	StringBuilder aliases = new StringBuilder();
	aliases.append("TRADE.ID ID,");
	aliases.append("TRADE.BUY_SELL BUY_SELL,");
	aliases.append("TRADE.CREATION_DATE CREATION_DATE,");
	aliases.append("TRADE.TRADE_DATE TRADE_DATE,");
	aliases.append("TRADE.SETTLEMENT_DATE SETTLEMENT_DATE,");
	aliases.append("TRADE.PRODUCT_ID PRODUCT_ID,");
	aliases.append("TRADE.AMOUNT AMOUNT,");
	aliases.append("TRADE.COUNTERPARTY_ID COUNTERPARTY_ID,");
	aliases.append("TRADE.CURRENCY_ID CURRENCY_ID,");
	aliases.append("TRADE.BOOK_ID BOOK_ID,");

	if (productType == null) {
	    aliases.append("CCYSWAP_TRADE.CCYSWAP_TRADE_ID CCYSWAP_TRADE_ID,");
	    aliases.append("CCYSWAP_TRADE.CURRENCY_TWO_ID CURRENCY_TWO_ID,");
	    aliases.append("CCYSWAP_TRADE.NOTIONAL_AMOUNT_TWO NOTIONAL_AMOUNT_TWO,");

	    aliases.append("VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID VANILLA_OPTION_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.STYLE STYLE,");
	    aliases.append("VANILLA_OPTION_TRADE.TYPE TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.STRIKE STRIKE,");
	    aliases.append("VANILLA_OPTION_TRADE.MATURITY_DATE OPTION_MATURITY_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.EXERCISE_DATE EXERCISE_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID UNDERLYING_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_TYPE SETTLEMENT_TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_DATE_OFFSET SETTLEMENT_DATE_OFFSET,");
	    aliases.append("VANILLA_OPTION_TRADE.QUANTITY OPTION_QUANTITY,");

	    aliases.append("FXNDF_TRADE.FXNDF_TRADE_ID FXNDF_TRADE_ID,");
	    aliases.append("FXNDF_TRADE.NON_DELIVERABLE_CURRENCY_ID NON_DELIVERABLE_CURRENCY_ID,");
	    aliases.append("FXNDF_TRADE.NDF_RATE NDF_RATE,");

	    aliases.append("IRSWAP_TRADE.IRSWAP_TRADE_ID IRSWAP_TRADE_ID,");
	    aliases.append("IRSWAP_TRADE.MATURITY_DATE IRSWAP_MATURITY_DATE,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_FREQUENCY IRSWAP_PAYMENT_FREQUENCY,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_FREQUENCY IRSWAP_RECEPTION_FREQUENCY,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_SPREAD PAYMENT_SPREAD,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_SPREAD RECEPTION_SPREAD,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_FIXED_INTEREST_RATE PAYMENT_FIXED_INTEREST_RATE,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_REFERENCE_RATE_INDEX_ID PAYMENT_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_REFERENCE_RATE_INDEX_ID RECEPTION_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_REFERENCE_RATE_INDEX_TENOR PAYMENT_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_REFERENCE_RATE_INDEX_TENOR RECEPTION_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_DAY_COUNT_CONVENTION_ID PAYMENT_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_DAY_COUNT_CONVENTION_ID RECEPTION_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_INTEREST_PAYMENT PAYMENT_INTEREST_PAYMENT,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_INTEREST_PAYMENT RECEPTION_INTEREST_PAYMENT,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_INTEREST_FIXING PAYMENT_INTEREST_FIXING,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_INTEREST_FIXING RECEPTION_INTEREST_FIXING,");
	    aliases.append("IRSWAP_TRADE.MATURITY_TENOR IRSWAP_MATURITY_TENOR,");

	    aliases.append("LOAN_DEPOSIT_TRADE.LOAN_DEPOSIT_TRADE_ID LOAN_DEPOSIT_TRADE_ID,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FIXED_RATE LOAN_DEPOSIT_FIXED_RATE,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FLOATING_RATE_INDEX_ID FLOATING_RATE_INDEX_ID,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FLOATING_RATE_INDEX_TENOR FLOATING_RATE_INDEX_TENOR,");
	    aliases.append("LOAN_DEPOSIT_TRADE.DAY_COUNT_CONVENTION_ID LOAN_DEPOSIT_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("LOAN_DEPOSIT_TRADE.PAYMENT_FREQUENCY PAYMENT_FREQUENCY,");
	    aliases.append("LOAN_DEPOSIT_TRADE.END_DATE END_DATE,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FIXING_PERIOD FIXING_PERIOD,");
	    aliases.append("LOAN_DEPOSIT_TRADE.SPREAD SPREAD,");
	    aliases.append("LOAN_DEPOSIT_TRADE.DIRECTION DIRECTION,");
	    aliases.append("LOAN_DEPOSIT_TRADE.INTEREST_PAYMENT LOAN_DEPOSIT_INTEREST_PAYMENT,");
	    aliases.append("LOAN_DEPOSIT_TRADE.INTEREST_FIXING LOAN_DEPOSIT_INTEREST_FIXING,");

	    aliases.append("FRA_TRADE.FRA_TRADE_ID FRA_TRADE_ID,");
	    aliases.append("FRA_TRADE.FIXED_RATE FRA_FIXED_RATE,");

	    aliases.append("FUTURE_TRADE.FUTURE_TRADE_ID FUTURE_TRADE_ID,");
	    aliases.append("FUTURE_TRADE.QUANTITY FUTURE_QUANTITY,");

	    aliases.append("FXSWAP_TRADE.FXSWAP_TRADE_ID FXSWAP_TRADE_ID,");
	    aliases.append("FXSWAP_TRADE.CURRENCY_ONE_ID FXSWAP_CURRENCY_ONE_ID,");
	    aliases.append("FXSWAP_TRADE.SETTLEMENT_DATE_FORWARD SETTLEMENT_DATE_FORWARD,");
	    aliases.append("FXSWAP_TRADE.AMOUNT_ONE_FORWARD AMOUNT_ONE_FORWARD,");
	    aliases.append("FXSWAP_TRADE.AMOUNT_ONE_SPOT AMOUNT_ONE_SPOT,");
	    aliases.append("FXSWAP_TRADE.AMOUNT_TWO_FORWARD AMOUNT_TWO_FORWARD,");

	    aliases.append("FXSPOT_TRADE.FXSPOT_TRADE_ID FXSPOT_TRADE_ID,");
	    aliases.append("FXSPOT_TRADE.CURRENCY_ONE_ID FXSPOT_CURRENCY_ONE_ID,");
	    aliases.append("FXSPOT_TRADE.AMOUNT_ONE AMOUNT_ONE,");

	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.IRCAP_FLOOR_COLLAR_TRADE_ID IRCAP_FLOOR_COLLAR_TRADE_ID,");
	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.CAP_STRIKE CAP_STRIKE,");
	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.FLOOR_STRIKE FLOOR_STRIKE,");
	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.IRFORWARD_TRADE_ID IRFORWARD_TRADE_ID,");

	    aliases.append("FWD_TRADE.IRFORWARD_TRADE_ID FWD_TRADE_ID,");
	    aliases.append("FWD_TRADE.MATURITY_DATE FWD_MATURITY_DATE,");
	    aliases.append("FWD_TRADE.FREQUENCY FWD_FREQUENCY,");
	    aliases.append("FWD_TRADE.REFERENCE_RATE_INDEX_ID FWD_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("FWD_TRADE.REFERENCE_RATE_INDEX_TENOR FWD_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("FWD_TRADE.DAY_COUNT_CONVENTION_ID FWD_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("FWD_TRADE.INTEREST_PAYMENT FWD_INTEREST_PAYMENT,");
	    aliases.append("FWD_TRADE.INTEREST_FIXING FWD_INTEREST_FIXING,");

	    aliases.append("IRFORWARD_TRADE.IRFORWARD_TRADE_ID IRFORWARD_TRADE_ID,");
	    aliases.append("IRFORWARD_TRADE.MATURITY_DATE IRFORWARD_MATURITY_DATE,");
	    aliases.append("IRFORWARD_TRADE.FREQUENCY IRFORWARD_FREQUENCY,");
	    aliases.append("IRFORWARD_TRADE.REFERENCE_RATE_INDEX_ID IRFORWARD_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRFORWARD_TRADE.REFERENCE_RATE_INDEX_TENOR IRFORWARD_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRFORWARD_TRADE.DAY_COUNT_CONVENTION_ID IRFORWARD_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRFORWARD_TRADE.INTEREST_PAYMENT IRFORWARD_INTEREST_PAYMENT,");
	    aliases.append("IRFORWARD_TRADE.INTEREST_FIXING IRFORWARD_INTEREST_FIXING,");

	    aliases.append("UNDERLYING_EQUITY.EQUITY_TRADE_ID UNDERLYING_EQUITY_TRADE_ID,");
	    aliases.append("UNDERLYING_EQUITY.QUANTITY UNDERLYING_EQUITY_QUANTITY,");

	    aliases.append("UNDERLYING_IRSWAP.IRSWAP_TRADE_ID UNDERLYING_IRSWAP_TRADE_ID,");
	    aliases.append("UNDERLYING_IRSWAP.MATURITY_DATE UNDERLYING_IRSWAP_MATURITY_DATE,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_FREQUENCY UNDERLYING_IRSWAP_PAYMENT_FREQUENCY,");
	    aliases.append("UNDERLYING_IRSWAP.RECEPTION_FREQUENCY UNDERLYING_IRSWAP_RECEPTION_FREQUENCY,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_SPREAD UNDERLYING_IRSWAP_PAYMENT_SPREAD,");
	    aliases.append("UNDERLYING_IRSWAP.RECEPTION_SPREAD UNDERLYING_IRSWAP_RECEPTION_SPREAD,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_FIXED_INTEREST_RATE UNDERLYING_IRSWAP_PAYMENT_FIXED_INTEREST_RATE,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_REFERENCE_RATE_INDEX_ID UNDERLYING_IRSWAP_PAYMENT_REFERENCE_RATE_INDEX_ID,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_REFERENCE_RATE_INDEX_ID UNDERLYING_IRSWAP_RECEPTION_REFERENCE_RATE_INDEX_ID,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_REFERENCE_RATE_INDEX_TENOR UNDERLYING_IRSWAP_PAYMENT_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_REFERENCE_RATE_INDEX_TENOR UNDERLYING_IRSWAP_RECEPTION_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_DAY_COUNT_CONVENTION_ID UNDERLYING_IRSWAP_PAYMENT_DAY_COUNT_CONVENTION_ID,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_DAY_COUNT_CONVENTION_ID UNDERLYING_IRSWAP_RECEPTION_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_INTEREST_PAYMENT UNDERLYING_IRSWAP_PAYMENT_INTEREST_PAYMENT,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_INTEREST_PAYMENT UNDERLYING_IRSWAP_RECEPTION_INTEREST_PAYMENT,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_INTEREST_FIXING UNDERLYING_IRSWAP_PAYMENT_INTEREST_FIXING,");
	    aliases.append("UNDERLYING_IRSWAP.RECEPTION_INTEREST_FIXING UNDERLYING_IRSWAP_RECEPTION_INTEREST_FIXING,");

	    aliases.append("IRSWAP_OPTION_TRADE.IRSWAP_OPTION_TRADE_ID IRSWAP_OPTION_TRADE_ID,");
	    aliases.append("IRSWAP_OPTION_TRADE.CASH_SETTLEMENT_AMOUNT CASH_SETTLEMENT_AMOUNT,");
	    aliases.append(
		    "IRSWAP_OPTION_TRADE.ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_ID ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_ID,");
	    aliases.append(
		    "IRSWAP_OPTION_TRADE.ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_TENOR ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_TENOR,");

	    aliases.append("UNDERLYING_FXSPOT.FXSPOT_TRADE_ID UNDERLYING_FXSPOT_TRADE_ID,");
	    aliases.append("UNDERLYING_FXSPOT.CURRENCY_ONE_ID UNDERLYING_FXSPOT_CURRENCY_ONE_ID,");
	    aliases.append("UNDERLYING_FXSPOT.AMOUNT_ONE UNDERLYING_FXSPOT_AMOUNT_ONE,");

	    aliases.append("UND_EQUITY_TRADE.ID UND_EQUITY_ID,");
	    aliases.append("UND_EQUITY_TRADE.BUY_SELL UND_EQUITY_BUY_SELL,");
	    aliases.append("UND_EQUITY_TRADE.CREATION_DATE UND_EQUITY_CREATION_DATE,");
	    aliases.append("UND_EQUITY_TRADE.TRADE_DATE UND_EQUITY_TRADE_DATE,");
	    aliases.append("UND_EQUITY_TRADE.SETTLEMENT_DATE UND_EQUITY_SETTLEMENT_DATE,");
	    aliases.append("UND_EQUITY_TRADE.PRODUCT_ID UND_EQUITY_PRODUCT_ID,");
	    aliases.append("UND_EQUITY_TRADE.AMOUNT UND_EQUITY_AMOUNT,");
	    aliases.append("UND_EQUITY_TRADE.COUNTERPARTY_ID UND_EQUITY_COUNTERPARTY_ID,");
	    aliases.append("UND_EQUITY_TRADE.CURRENCY_ID UND_EQUITY_CURRENCY_ID,");
	    aliases.append("UND_EQUITY_TRADE.BOOK_ID UND_EQUITY_BOOK_ID,");

	    aliases.append("UND_FXSPOT_TRADE.ID UND_FXSPOT_ID,");
	    aliases.append("UND_FXSPOT_TRADE.BUY_SELL UND_FXSPOT_BUY_SELL,");
	    aliases.append("UND_FXSPOT_TRADE.CREATION_DATE UND_FXSPOT_CREATION_DATE,");
	    aliases.append("UND_FXSPOT_TRADE.TRADE_DATE UND_FXSPOT_TRADE_DATE,");
	    aliases.append("UND_FXSPOT_TRADE.SETTLEMENT_DATE UND_FXSPOT_SETTLEMENT_DATE,");
	    aliases.append("UND_FXSPOT_TRADE.PRODUCT_ID UND_FXSPOT_PRODUCT_ID,");
	    aliases.append("UND_FXSPOT_TRADE.AMOUNT UND_FXSPOT_AMOUNT,");
	    aliases.append("UND_FXSPOT_TRADE.COUNTERPARTY_ID UND_FXSPOT_COUNTERPARTY_ID,");
	    aliases.append("UND_FXSPOT_TRADE.CURRENCY_ID UND_FXSPOT_CURRENCY_ID,");
	    aliases.append("UND_FXSPOT_TRADE.BOOK_ID UND_FXSPOT_BOOK_ID,");

	    aliases.append("UND_IRSWAP_TRADE.ID UND_IRSWAP_ID,");
	    aliases.append("UND_IRSWAP_TRADE.BUY_SELL UND_IRSWAP_BUY_SELL,");
	    aliases.append("UND_IRSWAP_TRADE.CREATION_DATE UND_IRSWAP_CREATION_DATE,");
	    aliases.append("UND_IRSWAP_TRADE.TRADE_DATE UND_IRSWAP_TRADE_DATE,");
	    aliases.append("UND_IRSWAP_TRADE.SETTLEMENT_DATE UND_IRSWAP_SETTLEMENT_DATE,");
	    aliases.append("UND_IRSWAP_TRADE.PRODUCT_ID UND_IRSWAP_PRODUCT_ID,");
	    aliases.append("UND_IRSWAP_TRADE.AMOUNT UND_IRSWAP_AMOUNT,");
	    aliases.append("UND_IRSWAP_TRADE.COUNTERPARTY_ID UND_IRSWAP_COUNTERPARTY_ID,");
	    aliases.append("UND_IRSWAP_TRADE.CURRENCY_ID UND_IRSWAP_CURRENCY_ID,");
	    aliases.append("UND_IRSWAP_TRADE.BOOK_ID UND_IRSWAP_BOOK_ID,");

	    aliases.append("UND_IRFORWARD_TRADE.ID UND_IRFORWARD_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.BUY_SELL UND_IRFORWARD_BUY_SELL,");
	    aliases.append("UND_IRFORWARD_TRADE.CREATION_DATE UND_IRFORWARD_CREATION_DATE,");
	    aliases.append("UND_IRFORWARD_TRADE.TRADE_DATE UND_IRFORWARD_TRADE_DATE,");
	    aliases.append("UND_IRFORWARD_TRADE.SETTLEMENT_DATE UND_IRFORWARD_SETTLEMENT_DATE,");
	    aliases.append("UND_IRFORWARD_TRADE.PRODUCT_ID UND_IRFORWARD_PRODUCT_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.AMOUNT UND_IRFORWARD_AMOUNT,");
	    aliases.append("UND_IRFORWARD_TRADE.COUNTERPARTY_ID UND_IRFORWARD_COUNTERPARTY_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.CURRENCY_ID UND_IRFORWARD_CURRENCY_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.BOOK_ID UND_IRFORWARD_BOOK_ID, ");

	    aliases.append("BOND_TRADE.BOND_TRADE_ID BOND_TRADE_ID,");
	    aliases.append("BOND_TRADE.QUANTITY BOND_QUANTITY,");

	    aliases.append("EQUITY_TRADE.EQUITY_TRADE_ID EQUITY_TRADE_ID,");
	    aliases.append("EQUITY_TRADE.QUANTITY EQUITY_QUANTITY");

	    return aliases.toString();
	}

	switch (productType) {
	case "CcySwap": {
	    aliases.append("IRSWAP_TRADE.IRSWAP_TRADE_ID IRSWAP_TRADE_ID,");
	    aliases.append("IRSWAP_TRADE.MATURITY_DATE IRSWAP_MATURITY_DATE,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_FREQUENCY IRSWAP_PAYMENT_FREQUENCY,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_FREQUENCY IRSWAP_RECEPTION_FREQUENCY,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_SPREAD PAYMENT_SPREAD,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_SPREAD RECEPTION_SPREAD,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_FIXED_INTEREST_RATE PAYMENT_FIXED_INTEREST_RATE,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_REFERENCE_RATE_INDEX_ID PAYMENT_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_REFERENCE_RATE_INDEX_TENOR PAYMENT_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_REFERENCE_RATE_INDEX_ID RECEPTION_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_REFERENCE_RATE_INDEX_TENOR RECEPTION_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_DAY_COUNT_CONVENTION_ID PAYMENT_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_DAY_COUNT_CONVENTION_ID RECEPTION_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_INTEREST_PAYMENT PAYMENT_INTEREST_PAYMENT,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_INTEREST_PAYMENT RECEPTION_INTEREST_PAYMENT,");
	    aliases.append("IRSWAP_TRADE.MATURITY_TENOR IRSWAP_MATURITY_TENOR,");

	    aliases.append("CCYSWAP_TRADE.CCYSWAP_TRADE_ID CCYSWAP_TRADE_ID,");
	    aliases.append("CCYSWAP_TRADE.CURRENCY_TWO_ID CURRENCY_TWO_ID,");
	    aliases.append("CCYSWAP_TRADE.NOTIONAL_AMOUNT_TWO NOTIONAL_AMOUNT_TWO");
	    break;
	}
	case "EquityOption": {
	    aliases.append("VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID VANILLA_OPTION_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.STYLE STYLE,");
	    aliases.append("VANILLA_OPTION_TRADE.TYPE TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.STRIKE STRIKE,");
	    aliases.append("VANILLA_OPTION_TRADE.MATURITY_DATE OPTION_MATURITY_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.EXERCISE_DATE EXERCISE_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID UNDERLYING_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_TYPE SETTLEMENT_TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_DATE_OFFSET SETTLEMENT_DATE_OFFSET,");
	    aliases.append("VANILLA_OPTION_TRADE.QUANTITY OPTION_QUANTITY,");

	    aliases.append("UNDERLYING_EQUITY.EQUITY_TRADE_ID UNDERLYING_EQUITY_TRADE_ID,");
	    aliases.append("UNDERLYING_EQUITY.QUANTITY UNDERLYING_EQUITY_QUANTITY,");

	    aliases.append("UND_EQUITY_TRADE.ID UND_EQUITY_ID,");
	    aliases.append("UND_EQUITY_TRADE.BUY_SELL UND_EQUITY_BUY_SELL,");
	    aliases.append("UND_EQUITY_TRADE.CREATION_DATE UND_EQUITY_CREATION_DATE,");
	    aliases.append("UND_EQUITY_TRADE.TRADE_DATE UND_EQUITY_TRADE_DATE,");
	    aliases.append("UND_EQUITY_TRADE.SETTLEMENT_DATE UND_EQUITY_SETTLEMENT_DATE,");
	    aliases.append("UND_EQUITY_TRADE.PRODUCT_ID UND_EQUITY_PRODUCT_ID,");
	    aliases.append("UND_EQUITY_TRADE.AMOUNT UND_EQUITY_AMOUNT,");
	    aliases.append("UND_EQUITY_TRADE.COUNTERPARTY_ID UND_EQUITY_COUNTERPARTY_ID,");
	    aliases.append("UND_EQUITY_TRADE.CURRENCY_ID UND_EQUITY_CURRENCY_ID,");
	    aliases.append("UND_EQUITY_TRADE.BOOK_ID UND_EQUITY_BOOK_ID ");
	    break;
	}
	case "FXNDF": {
	    aliases.append("FXNDF_TRADE.FXNDF_TRADE_ID FXNDF_TRADE_ID,");
	    aliases.append("FXNDF_TRADE.NON_DELIVERABLE_CURRENCY_ID NON_DELIVERABLE_CURRENCY_ID,");
	    aliases.append("FXNDF_TRADE.NDF_RATE NDF_RATE");
	    break;
	}
	case "IRSwap": {
	    aliases.append("IRSWAP_TRADE.IRSWAP_TRADE_ID IRSWAP_TRADE_ID,");
	    aliases.append("IRSWAP_TRADE.MATURITY_DATE IRSWAP_MATURITY_DATE,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_FREQUENCY IRSWAP_PAYMENT_FREQUENCY,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_FREQUENCY IRSWAP_RECEPTION_FREQUENCY,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_SPREAD PAYMENT_SPREAD,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_SPREAD RECEPTION_SPREAD,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_FIXED_INTEREST_RATE PAYMENT_FIXED_INTEREST_RATE,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_REFERENCE_RATE_INDEX_ID PAYMENT_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_REFERENCE_RATE_INDEX_ID RECEPTION_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_REFERENCE_RATE_INDEX_TENOR PAYMENT_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_REFERENCE_RATE_INDEX_TENOR RECEPTION_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_DAY_COUNT_CONVENTION_ID PAYMENT_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_DAY_COUNT_CONVENTION_ID RECEPTION_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_INTEREST_PAYMENT PAYMENT_INTEREST_PAYMENT,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_INTEREST_PAYMENT RECEPTION_INTEREST_PAYMENT,");
	    aliases.append("IRSWAP_TRADE.PAYMENT_INTEREST_FIXING PAYMENT_INTEREST_FIXING,");
	    aliases.append("IRSWAP_TRADE.RECEPTION_INTEREST_FIXING RECEPTION_INTEREST_FIXING,");
	    aliases.append("IRSWAP_TRADE.MATURITY_TENOR IRSWAP_MATURITY_TENOR");
	    break;
	}
	case "LoanDeposit": {
	    aliases.append("LOAN_DEPOSIT_TRADE.LOAN_DEPOSIT_TRADE_ID LOAN_DEPOSIT_TRADE_ID,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FIXED_RATE LOAN_DEPOSIT_FIXED_RATE,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FLOATING_RATE_INDEX_ID FLOATING_RATE_INDEX_ID,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FLOATING_RATE_INDEX_TENOR FLOATING_RATE_INDEX_TENOR,");
	    aliases.append("LOAN_DEPOSIT_TRADE.DAY_COUNT_CONVENTION_ID LOAN_DEPOSIT_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("LOAN_DEPOSIT_TRADE.PAYMENT_FREQUENCY PAYMENT_FREQUENCY,");
	    aliases.append("LOAN_DEPOSIT_TRADE.END_DATE END_DATE,");
	    aliases.append("LOAN_DEPOSIT_TRADE.FIXING_PERIOD FIXING_PERIOD,");
	    aliases.append("LOAN_DEPOSIT_TRADE.SPREAD SPREAD,");
	    aliases.append("LOAN_DEPOSIT_TRADE.DIRECTION DIRECTION,");
	    aliases.append("LOAN_DEPOSIT_TRADE.INTEREST_PAYMENT INTEREST_PAYMENT,");
	    aliases.append("LOAN_DEPOSIT_TRADE.INTEREST_FIXING INTEREST_FIXING");
	    break;
	}
	case "FRA": {
	    aliases.append("IRFORWARD_TRADE.IRFORWARD_TRADE_ID IRFORWARD_TRADE_ID,");
	    aliases.append("IRFORWARD_TRADE.MATURITY_DATE IRFORWARD_MATURITY_DATE,");
	    aliases.append("IRFORWARD_TRADE.FREQUENCY IRFORWARD_FREQUENCY,");
	    aliases.append("IRFORWARD_TRADE.REFERENCE_RATE_INDEX_ID IRFORWARD_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRFORWARD_TRADE.REFERENCE_RATE_INDEX_TENOR IRFORWARD_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRFORWARD_TRADE.DAY_COUNT_CONVENTION_ID IRFORWARD_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRFORWARD_TRADE.INTEREST_PAYMENT IRFORWARD_INTEREST_PAYMENT,");
	    aliases.append("IRFORWARD_TRADE.INTEREST_FIXING IRFORWARD_INTEREST_FIXING,");

	    aliases.append("FRA_TRADE.FRA_TRADE_ID FRA_TRADE_ID,");
	    aliases.append("FRA_TRADE.FIXED_RATE FRA_FIXED_RATE");
	    break;
	}
	case "Future": {
	    aliases.append("IRFORWARD_TRADE.IRFORWARD_TRADE_ID IRFORWARD_TRADE_ID,");
	    aliases.append("IRFORWARD_TRADE.MATURITY_DATE IRFORWARD_MATURITY_DATE,");
	    aliases.append("IRFORWARD_TRADE.FREQUENCY IRFORWARD_FREQUENCY,");
	    aliases.append("IRFORWARD_TRADE.REFERENCE_RATE_INDEX_ID IRFORWARD_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("IRFORWARD_TRADE.REFERENCE_RATE_INDEX_TENOR IRFORWARD_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("IRFORWARD_TRADE.DAY_COUNT_CONVENTION_ID IRFORWARD_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("IRFORWARD_TRADE.INTEREST_PAYMENT IRFORWARD_INTEREST_PAYMENT,");
	    aliases.append("IRFORWARD_TRADE.INTEREST_FIXING IRFORWARD_INTEREST_FIXING,");

	    aliases.append("FUTURE_TRADE.FUTURE_TRADE_ID FUTURE_TRADE_ID,");
	    aliases.append("FUTURE_TRADE.QUANTITY FUTURE_QUANTITY");
	    break;
	}
	case "Bond": {
	    aliases.append("BOND_TRADE.BOND_TRADE_ID BOND_TRADE_ID,");
	    aliases.append("BOND_TRADE.QUANTITY BOND_QUANTITY");
	    break;
	}
	case "Equity": {
	    aliases.append("EQUITY_TRADE.EQUITY_TRADE_ID EQUITY_TRADE_ID,");
	    aliases.append("EQUITY_TRADE.QUANTITY EQUITY_QUANTITY");
	    break;
	}
	case "FXOption": {
	    aliases.append("VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID VANILLA_OPTION_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.STYLE STYLE,");
	    aliases.append("VANILLA_OPTION_TRADE.TYPE TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.STRIKE STRIKE,");
	    aliases.append("VANILLA_OPTION_TRADE.MATURITY_DATE OPTION_MATURITY_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.EXERCISE_DATE EXERCISE_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID UNDERLYING_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_TYPE SETTLEMENT_TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_DATE_OFFSET SETTLEMENT_DATE_OFFSET,");

	    aliases.append("UNDERLYING_FXSPOT.FXSPOT_TRADE_ID UNDERLYING_FXSPOT_TRADE_ID,");
	    aliases.append("UNDERLYING_FXSPOT.CURRENCY_ONE_ID UNDERLYING_FXSPOT_CURRENCY_ONE_ID,");
	    aliases.append("UNDERLYING_FXSPOT.AMOUNT_ONE UNDERLYING_FXSPOT_AMOUNT_ONE, ");

	    aliases.append("UND_FXSPOT_TRADE.ID UND_FXSPOT_ID,");
	    aliases.append("UND_FXSPOT_TRADE.BUY_SELL UND_FXSPOT_BUY_SELL,");
	    aliases.append("UND_FXSPOT_TRADE.CREATION_DATE UND_FXSPOT_CREATION_DATE,");
	    aliases.append("UND_FXSPOT_TRADE.TRADE_DATE UND_FXSPOT_TRADE_DATE,");
	    aliases.append("UND_FXSPOT_TRADE.SETTLEMENT_DATE UND_FXSPOT_SETTLEMENT_DATE,");
	    aliases.append("UND_FXSPOT_TRADE.PRODUCT_ID UND_FXSPOT_PRODUCT_ID,");
	    aliases.append("UND_FXSPOT_TRADE.AMOUNT UND_FXSPOT_AMOUNT,");
	    aliases.append("UND_FXSPOT_TRADE.COUNTERPARTY_ID UND_FXSPOT_COUNTERPARTY_ID,");
	    aliases.append("UND_FXSPOT_TRADE.CURRENCY_ID UND_FXSPOT_CURRENCY_ID,");
	    aliases.append("UND_FXSPOT_TRADE.BOOK_ID UND_FXSPOT_BOOK_ID");
	    break;
	}
	case "FXSwap": {
	    aliases.append("FXSWAP_TRADE.FXSWAP_TRADE_ID FXSWAP_TRADE_ID,");
	    aliases.append("FXSWAP_TRADE.CURRENCY_ONE_ID FXSWAP_CURRENCY_ONE_ID,");
	    aliases.append("FXSWAP_TRADE.SETTLEMENT_DATE_FORWARD SETTLEMENT_DATE_FORWARD,");
	    aliases.append("FXSWAP_TRADE.AMOUNT_ONE_FORWARD AMOUNT_ONE_FORWARD,");
	    aliases.append("FXSWAP_TRADE.AMOUNT_ONE_SPOT AMOUNT_ONE_SPOT,");
	    aliases.append("FXSWAP_TRADE.AMOUNT_TWO_FORWARD AMOUNT_TWO_FORWARD ");
	    break;
	}
	case "FX": {
	    aliases.append("FXSPOT_TRADE.FXSPOT_TRADE_ID FXSPOT_TRADE_ID,");
	    aliases.append("FXSPOT_TRADE.CURRENCY_ONE_ID FXSPOT_CURRENCY_ONE_ID,");
	    aliases.append("FXSPOT_TRADE.AMOUNT_ONE AMOUNT_ONE ");
	    break;
	}
	case "IRCapFloorCollar": {
	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.IRCAP_FLOOR_COLLAR_TRADE_ID IRCAP_FLOOR_COLLAR_TRADE_ID,");
	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.CAP_STRIKE CAP_STRIKE,");
	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.FLOOR_STRIKE FLOOR_STRIKE,");
	    aliases.append("IRCAP_FLOOR_COLLAR_TRADE.IRFORWARD_TRADE_ID IRFORWARD_TRADE_ID,");

	    aliases.append("FWD_TRADE.IRFORWARD_TRADE_ID FWD_TRADE_ID,");
	    aliases.append("FWD_TRADE.MATURITY_DATE FWD_MATURITY_DATE,");
	    aliases.append("FWD_TRADE.FREQUENCY FWD_FREQUENCY,");
	    aliases.append("FWD_TRADE.REFERENCE_RATE_INDEX_ID FWD_REFERENCE_RATE_INDEX_ID,");
	    aliases.append("FWD_TRADE.REFERENCE_RATE_INDEX_TENOR FWD_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append("FWD_TRADE.DAY_COUNT_CONVENTION_ID FWD_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("FWD_TRADE.INTEREST_PAYMENT FWD_INTEREST_PAYMENT,");
	    aliases.append("FWD_TRADE.INTEREST_FIXING FWD_INTEREST_FIXING,");

	    aliases.append("UND_IRFORWARD_TRADE.ID UND_IRFORWARD_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.BUY_SELL UND_IRFORWARD_BUY_SELL,");
	    aliases.append("UND_IRFORWARD_TRADE.CREATION_DATE UND_IRFORWARD_CREATION_DATE,");
	    aliases.append("UND_IRFORWARD_TRADE.TRADE_DATE UND_IRFORWARD_TRADE_DATE,");
	    aliases.append("UND_IRFORWARD_TRADE.SETTLEMENT_DATE UND_IRFORWARD_SETTLEMENT_DATE,");
	    aliases.append("UND_IRFORWARD_TRADE.PRODUCT_ID UND_IRFORWARD_PRODUCT_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.AMOUNT UND_IRFORWARD_AMOUNT,");
	    aliases.append("UND_IRFORWARD_TRADE.COUNTERPARTY_ID UND_IRFORWARD_COUNTERPARTY_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.CURRENCY_ID UND_IRFORWARD_CURRENCY_ID,");
	    aliases.append("UND_IRFORWARD_TRADE.BOOK_ID UND_IRFORWARD_BOOK_ID ");
	    break;
	}
	case "IRSwapOption": {
	    aliases.append("VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID VANILLA_OPTION_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.STYLE STYLE,");
	    aliases.append("VANILLA_OPTION_TRADE.TYPE TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.STRIKE STRIKE,");
	    aliases.append("VANILLA_OPTION_TRADE.MATURITY_DATE OPTION_MATURITY_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.EXERCISE_DATE EXERCISE_DATE,");
	    aliases.append("VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID UNDERLYING_TRADE_ID,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_TYPE SETTLEMENT_TYPE,");
	    aliases.append("VANILLA_OPTION_TRADE.SETTLEMENT_DATE_OFFSET SETTLEMENT_DATE_OFFSET,");

	    aliases.append("UNDERLYING_IRSWAP.IRSWAP_TRADE_ID UNDERLYING_IRSWAP_TRADE_ID,");
	    aliases.append("UNDERLYING_IRSWAP.MATURITY_DATE UNDERLYING_IRSWAP_MATURITY_DATE,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_FREQUENCY UNDERLYING_IRSWAP_PAYMENT_FREQUENCY,");
	    aliases.append("UNDERLYING_IRSWAP.RECEPTION_FREQUENCY UNDERLYING_IRSWAP_RECEPTION_FREQUENCY,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_SPREAD UNDERLYING_IRSWAP_PAYMENT_SPREAD,");
	    aliases.append("UNDERLYING_IRSWAP.RECEPTION_SPREAD UNDERLYING_IRSWAP_RECEPTION_SPREAD,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_FIXED_INTEREST_RATE UNDERLYING_IRSWAP_PAYMENT_FIXED_INTEREST_RATE,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_REFERENCE_RATE_INDEX_ID UNDERLYING_IRSWAP_PAYMENT_REFERENCE_RATE_INDEX_ID,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_REFERENCE_RATE_INDEX_ID UNDERLYING_IRSWAP_RECEPTION_REFERENCE_RATE_INDEX_ID,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_REFERENCE_RATE_INDEX_TENOR UNDERLYING_IRSWAP_PAYMENT_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_REFERENCE_RATE_INDEX_TENOR UNDERLYING_IRSWAP_RECEPTION_REFERENCE_RATE_INDEX_TENOR,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.PAYMENT_DAY_COUNT_CONVENTION_ID UNDERLYING_IRSWAP_PAYMENT_DAY_COUNT_CONVENTION_ID,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_DAY_COUNT_CONVENTION_ID UNDERLYING_IRSWAP_RECEPTION_DAY_COUNT_CONVENTION_ID,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_INTEREST_PAYMENT UNDERLYING_IRSWAP_PAYMENT_INTEREST_PAYMENT,");
	    aliases.append(
		    "UNDERLYING_IRSWAP.RECEPTION_INTEREST_PAYMENT UNDERLYING_IRSWAP_RECEPTION_INTEREST_PAYMENT,");
	    aliases.append("UNDERLYING_IRSWAP.PAYMENT_INTEREST_FIXING UNDERLYING_IRSWAP_PAYMENT_INTEREST_FIXING,");
	    aliases.append("UNDERLYING_IRSWAP.RECEPTION_INTEREST_FIXING UNDERLYING_IRSWAP_RECEPTION_INTEREST_FIXING,");

	    aliases.append("UND_IRSWAP_TRADE.ID UND_IRSWAP_ID,");
	    aliases.append("UND_IRSWAP_TRADE.BUY_SELL UND_IRSWAP_BUY_SELL,");
	    aliases.append("UND_IRSWAP_TRADE.CREATION_DATE UND_IRSWAP_CREATION_DATE,");
	    aliases.append("UND_IRSWAP_TRADE.TRADE_DATE UND_IRSWAP_TRADE_DATE,");
	    aliases.append("UND_IRSWAP_TRADE.SETTLEMENT_DATE UND_IRSWAP_SETTLEMENT_DATE,");
	    aliases.append("UND_IRSWAP_TRADE.PRODUCT_ID UND_IRSWAP_PRODUCT_ID,");
	    aliases.append("UND_IRSWAP_TRADE.AMOUNT UND_IRSWAP_AMOUNT,");
	    aliases.append("UND_IRSWAP_TRADE.COUNTERPARTY_ID UND_IRSWAP_COUNTERPARTY_ID,");
	    aliases.append("UND_IRSWAP_TRADE.CURRENCY_ID UND_IRSWAP_CURRENCY_ID,");
	    aliases.append("UND_IRSWAP_TRADE.BOOK_ID UND_IRSWAP_BOOK_ID,");

	    aliases.append("IRSWAP_OPTION_TRADE.IRSWAP_OPTION_TRADE_ID IRSWAP_OPTION_TRADE_ID,");
	    aliases.append("IRSWAP_OPTION_TRADE.CASH_SETTLEMENT_AMOUNT CASH_SETTLEMENT_AMOUNT,");
	    aliases.append(
		    "IRSWAP_OPTION_TRADE.ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_ID ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_ID,");
	    aliases.append(
		    "IRSWAP_OPTION_TRADE.ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_TENOR ALTERNATIVE_CASH_SETTLEMENT_REFERENCE_RATE_INDEX_TENOR");
	    break;
	}
	}
	return aliases.toString();
    }

    private static String buildJoin(String productType) {
	String where = " WHERE ";
	switch (productType) {
	case "CcySwap": {
	    where += "TRADE.ID = IRSWAP_TRADE.IRSWAP_TRADE_ID AND IRSWAP_TRADE.IRSWAP_TRADE_ID=CCYSWAP_TRADE.CCYSWAP_TRADE_ID";
	    break;
	}
	case "EquityOption": {
	    where += "TRADE.ID = VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID AND VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID = UNDERLYING_EQUITY.EQUITY_TRADE_ID AND UNDERLYING_EQUITY.EQUITY_TRADE_ID=UND_EQUITY_TRADE.ID";
	    break;
	}
	case "FXNDF": {
	    where += "TRADE.ID = FXNDF_TRADE.FXNDF_TRADE_ID";
	    break;
	}
	case "IRSwap": {
	    where += "TRADE.ID = IRSWAP_TRADE.IRSWAP_TRADE_ID";
	    break;
	}
	case "LoanDeposit": {
	    where += "TRADE.ID = LOAN_DEPOSIT_TRADE.LOAN_DEPOSIT_TRADE_ID";
	    break;
	}
	case "FRA": {
	    where += "TRADE.ID = IRFORWARD_TRADE.IRFORWARD_TRADE_ID AND IRFORWARD_TRADE.IRFORWARD_TRADE_ID = FRA_TRADE.FRA_TRADE_ID";
	    break;
	}
	case "Future": {
	    where += "TRADE.ID = IRFORWARD_TRADE.IRFORWARD_TRADE_ID AND IRFORWARD_TRADE.IRFORWARD_TRADE_ID = FUTURE_TRADE.FUTURE_TRADE_ID";
	    break;
	}
	case "FXOption": {
	    where += "TRADE.ID = VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID AND VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID = UNDERLYING_FXSPOT.FXSPOT_TRADE_ID AND UNDERLYING_FXSPOT.FXSPOT_TRADE_ID=UND_FXSPOT_TRADE.ID";
	    break;
	}
	case "FXSwap": {
	    where += "TRADE.ID = FXSWAP_TRADE.FXSWAP_TRADE_ID";
	    break;
	}
	case "Bond": {
	    where += "TRADE.ID = BOND_TRADE.BOND_TRADE_ID";
	    break;
	}
	case "Equity": {
	    where += "TRADE.ID = EQUITY_TRADE.EQUITY_TRADE_ID";
	    break;
	}
	case "FX": {
	    where += "TRADE.ID = FXSPOT_TRADE.FXSPOT_TRADE_ID";
	    break;
	}
	case "IRCapFloorCollar": {
	    where += "TRADE.ID = IRCAP_FLOOR_COLLAR_TRADE.IRCAP_FLOOR_COLLAR_TRADE_ID AND IRCAP_FLOOR_COLLAR_TRADE.IRFORWARD_TRADE_ID = FWD_TRADE.IRFORWARD_TRADE_ID AND FWD_TRADE.IRFORWARD_TRADE_ID = UND_IRFORWARD_TRADE.ID ";
	    break;
	}
	case "IRSwapOption": {
	    where += "TRADE.ID = VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID AND VANILLA_OPTION_TRADE.UNDERLYING_TRADE_ID = UNDERLYING_IRSWAP.IRSWAP_TRADE_ID AND UNDERLYING_IRSWAP.IRSWAP_TRADE_ID = UND_IRSWAP_TRADE.ID AND VANILLA_OPTION_TRADE.VANILLA_OPTION_TRADE_ID = IRSWAP_OPTION_TRADE.IRSWAP_OPTION_TRADE_ID";
	    break;
	}
	}
	return where;
    }

    private static String getTableNameByProductType(String productType) {
	if (productType == null) {
	    return null;
	}
	switch (productType) {
	case "CcySwap":
	    return "CCYSWAP_TRADE, IRSWAP_TRADE";
	case "EquityOption":
	    return "VANILLA_OPTION_TRADE, EQUITY_TRADE UNDERLYING_EQUITY, TRADE UND_EQUITY_TRADE";
	case "FXNDF":
	    return "FXNDF_TRADE";
	case "IRSwap":
	    return "IRSWAP_TRADE";
	case "LoanDeposit":
	    return "LOAN_DEPOSIT_TRADE";
	case "FRA":
	    return "FRA_TRADE, IRFORWARD_TRADE";
	case "Future":
	    return "FUTURE_TRADE, IRFORWARD_TRADE";
	case "Bond":
	    return "BOND_TRADE";
	case "Equity":
	    return "EQUITY_TRADE";
	case "FXOption":
	    return "VANILLA_OPTION_TRADE, FXSPOT_TRADE UNDERLYING_FXSPOT, TRADE UND_FXSPOT_TRADE";
	case "FXSwap":
	    return "FXSWAP_TRADE";
	case "FX":
	    return "FXSPOT_TRADE";
	case "IRCapFloorCollar":
	    return "IRCAP_FLOOR_COLLAR_TRADE, IRFORWARD_TRADE FWD_TRADE, TRADE UND_IRFORWARD_TRADE";
	case "IRSwapOption":
	    return "VANILLA_OPTION_TRADE, IRSWAP_TRADE UNDERLYING_IRSWAP, TRADE UND_IRSWAP_TRADE, IRSWAP_OPTION_TRADE";
	}

	return null;
    }

    public static List<Trade<? extends Product>> getAllTrades() {
	return getTradesByDates(null, null, null, null);
    }

}