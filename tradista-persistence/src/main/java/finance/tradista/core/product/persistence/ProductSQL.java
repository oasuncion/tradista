package finance.tradista.core.product.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.common.util.TradistaUtil;
import finance.tradista.core.exchange.persistence.ExchangeSQL;
import finance.tradista.core.product.model.Product;
import finance.tradista.core.product.service.ProductBusinessDelegate;

/*
 * Copyright 2016 Olivier Asuncion
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

public class ProductSQL {

	public static Product getProductById(long id) {
		Product product = null;

		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetProductById = con.prepareStatement("SELECT * FROM PRODUCT WHERE ID = ? ")) {
			stmtGetProductById.setLong(1, id);
			try (ResultSet results = stmtGetProductById.executeQuery()) {
				while (results.next()) {
					if (product == null) {
						product = getProduct(id);
						if (product == null) {
							// The product was not found
							return null;
						}
					}
					product.setId(results.getLong("id"));
					product.setCreationDate(results.getDate("creation_date").toLocalDate());
					product.setExchange(ExchangeSQL.getExchangeById(results.getLong("exchange_id")));
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return product;
	}

	private static Product getProduct(long id) {
		ProductBusinessDelegate productBusinessDelegate = new ProductBusinessDelegate();
		Set<String> products = productBusinessDelegate.getAvailableProductTypes();
		try {
			for (String prod : products) {
				try {
					Product product = TradistaUtil.callMethod(
							"finance.tradista." + productBusinessDelegate.getProductFamily(prod) + "."
									+ prod.toLowerCase() + ".persistence." + prod + "SQL",
							Product.class, "get" + prod + "ById", id);
					if (product != null) {
						return product;
					}
				} catch (TradistaTechnicalException tte) {
					// There is no product for this product type (ex: FX)
					continue;
				}
			}
		} catch (TradistaBusinessException tbe) {
			tbe.printStackTrace();
			throw new TradistaTechnicalException(tbe);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static Set<? extends Product> getAllProductsByType(String productType) {
		try {
			String productNameInMethod;
			Set<? extends Product> products = null;
			if (productType.equals("Equity")) {
				productNameInMethod = productType.substring(0, productType.length() - 1) + "ies";
			} else {
				productNameInMethod = productType + "s";
			}
			try {
				products = TradistaUtil.callMethod(
						"finance.tradista." + new ProductBusinessDelegate().getProductFamily(productType) + "."
								+ productType.toLowerCase() + ".persistence." + productType + "SQL",
						Set.class, "getAll" + productNameInMethod);
			} catch (TradistaTechnicalException tte) {
				// There is no product for this product type (ex: FX)
				return null;
			}

			return products;

		} catch (TradistaBusinessException tbe) {
			tbe.printStackTrace();
			throw new TradistaTechnicalException(tbe);
		}
	}

	public static String getProductTableByType(String productType) {
		switch (productType) {
		case "Bond":
			return "BOND";
		case "Equity":
			return "EQUITY";
		case "EquityOption":
			return "EQUITY_OPTION";
		case "Future":
			return "FUTURE";
		default:
			return null;
		}
	}

}