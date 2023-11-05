package finance.tradista.core.product.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.position.model.PositionDefinition;
import finance.tradista.core.product.model.Product;

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

public class ProductBusinessDelegate {

	private ProductService productService;

	private static Set<String> canBeOTC;

	private static Set<String> canBeListed;

	private static Set<String> allProductTypes;

	static {
		canBeOTC = new HashSet<String>();
		canBeOTC.add("FX");
		canBeOTC.add("FXSwap");
		canBeOTC.add("FXOption");
		canBeOTC.add("FXNDF");
		canBeOTC.add("IRSwap");
		canBeOTC.add("IRCapFloorCollar");
		canBeOTC.add("IRSwapOption");
		canBeOTC.add("LoanDeposit");
		canBeOTC.add("CcySwap");
		canBeOTC.add("FRA");
		canBeOTC.add("EquityOption");
		canBeOTC.add("GCRepo");

		canBeListed = new HashSet<String>();
		canBeListed.add("EquityOption");
		canBeListed.add("Bond");
		canBeListed.add("Equity");
		canBeListed.add("Future");

		allProductTypes = new HashSet<String>();
		allProductTypes.addAll(canBeOTC);
		allProductTypes.addAll(canBeListed);
	}

	public ProductBusinessDelegate() {
		productService = TradistaServiceLocator.getInstance().getProductService();
	}

	public Set<String> getAvailableProductTypes() {
		return SecurityUtil.run(() -> productService.getAvailableProductTypes());
	}

	public Set<String> getAllProductTypes() {
		return allProductTypes;
	}

	public Set<? extends Product> getAllProductsByType(String productType) throws TradistaBusinessException {
		if (StringUtils.isEmpty(productType)) {
			Set<Product> products = null;
			for (String type : getAvailableListableProductTypes()) {
				Set<? extends Product> prods = SecurityUtil.runEx(() -> productService.getAllProductsByType(type));
				if (prods != null && !prods.isEmpty()) {
					if (products == null) {
						products = new HashSet<Product>();
					}
					products.addAll(prods);
				}
			}
			return products;
		}
		return SecurityUtil.runEx(() -> productService.getAllProductsByType(productType));
	}

	public String getProductFamily(String productType) throws TradistaBusinessException {
		switch (productType) {
		case "LoanDeposit":
			return "mm";
		case "Loan":
			return "mm";
		case "Deposit":
			return "mm";
		case "Bond":
			return "security";
		case "FX":
			return "fx";
		case "FXSpot":
			return "fx";
		case "FXForward":
			return "fx";
		case "FXSwap":
			return "fx";
		case "FXOption":
			return "fx";
		case "FXNDF":
			return "fx";
		case "IRSwap":
			return "ir";
		case "IRSwapOption":
			return "ir";
		case "CcySwap":
			return "ir";
		case "IRCapFloorCollar":
			return "ir";
		case "IRCap":
			return "ir";
		case "IRFloor":
			return "ir";
		case "IRCollar":
			return "ir";
		case "FRA":
			return "ir";
		case "Future":
			return "ir";
		case "Equity":
			return "security";
		case "EquityOption":
			return "security";
		case "GCRepo":
			return "security";
		}
		throw new TradistaBusinessException(String.format("This product type %s is not identified.", productType));
	}

	public Product getProductById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("The product id must be positive.");
		}
		return SecurityUtil.run(() -> productService.getProductById(id));
	}

	public boolean canBeOTC(String productType) throws TradistaBusinessException {
		if (StringUtils.isEmpty(productType)) {
			throw new TradistaBusinessException("The product type is mandatory.");
		}
		return canBeOTC.contains(productType);
	}

	public boolean canBeListed(String productType) throws TradistaBusinessException {
		if (StringUtils.isEmpty(productType)) {
			throw new TradistaBusinessException("The product type is mandatory.");
		}
		return canBeListed.contains(productType);
	}

	public Set<? extends Product> getProducts(PositionDefinition posDef) throws TradistaBusinessException {
		if (posDef == null) {
			throw new TradistaBusinessException("The position definition is mandatory.");
		}
		if (posDef.getProduct() != null) {
			Set<Product> product = new HashSet<Product>();
			product.add(posDef.getProduct());
			return product;
		} else {
			return getAllProductsByType(posDef.getProductType());
		}
	}

	public Set<String> getAvailableListableProductTypes() {
		Set<String> productTypes = getAvailableProductTypes();
		Set<String> listableProductTypes = null;
		if (productTypes != null) {
			for (String p : productTypes) {
				try {
					if (canBeListed(p)) {
						if (listableProductTypes == null) {
							listableProductTypes = new HashSet<String>();
						}
						listableProductTypes.add(p);
					}
				} catch (TradistaBusinessException tbe) {
					// cannot happen here because p won't be empty.
				}
			}
		}
		return listableProductTypes;
	}

	public Set<String> getAvailableFXProductTypes() {
		Set<String> productTypes = getAvailableProductTypes();
		Set<String> fxProductTypes = productTypes.stream().filter(p -> {
			try {
				return getProductFamily(p).equals("fx");
			} catch (TradistaBusinessException abe) {
			}
			return false;
		}).collect(Collectors.toSet());

		return fxProductTypes;
	}

	public Set<Product> getAllProducts() {
		return SecurityUtil.run(() -> productService.getAllProducts());
	}

}