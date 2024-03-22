package finance.tradista.web.demo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.util.ColorUtil;
import finance.tradista.core.inventory.model.ProductInventory;
import finance.tradista.core.productinventory.service.ProductInventoryBusinessDelegate;

/*
 * Copyright 2022 Olivier Asuncion
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

@Named
@ViewScoped
public class InventoryView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 279320826504459625L;

	private LineChartModel lineModel;

	private BookBusinessDelegate bookBusinessDelegate;

	@PostConstruct
	public void init() {
		bookBusinessDelegate = new BookBusinessDelegate();
		lineModel = new LineChartModel();
		loadInventory();
	}

	public LineChartModel getLineModel() {
		return lineModel;
	}

	public void setLineModel(LineChartModel lineModel) {
		this.lineModel = lineModel;
	}

	public void loadInventory() {
		ChartData data = new ChartData();
		Set<ProductInventory> inventory = null;

		List<String> bgColors = new ArrayList<>();
		bgColors.addAll(ColorUtil.getBlueColorsList());

		List<String> labels = new ArrayList<>();

		Set<LocalDate> daysOfTheWeek = new TreeSet<>();

		for (int i = 0; i < 7; i++) {
			LocalDate date = LocalDate.now().plus(i, ChronoUnit.DAYS);
			daysOfTheWeek.add(date);
			labels.add(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
		}
		Book book = null;

		try {
			book = bookBusinessDelegate.getBookByName("Demo Book");
			inventory = new ProductInventoryBusinessDelegate().getProductInventories(LocalDate.now(),
					LocalDate.now().plusDays(6), "Equity", 0, book.getId(), false);
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}

		if (inventory != null && !inventory.isEmpty()) {
			Map<String, Set<ProductInventory>> invMap = new HashMap<>();

			for (ProductInventory inv : inventory) {
				if (!invMap.containsKey(inv.getProduct().toString())) {
					Set<ProductInventory> invSet = new HashSet<>();
					invSet.add(inv);
					invMap.put(inv.getProduct().toString(), invSet);
				} else {
					Set<ProductInventory> invSet = invMap.get(inv.getProduct().toString());
					invSet.add(inv);
					invMap.put(inv.getProduct().toString(), invSet);
				}
			}

			if (invMap != null && !invMap.isEmpty()) {
				int i = 0;
				for (Map.Entry<String, Set<ProductInventory>> entry : invMap.entrySet()) {
					LineChartDataSet dataSet = new LineChartDataSet();
					dataSet.setLabel(entry.getKey());
					dataSet.setFill(false);
					dataSet.setTension(0.1);
					dataSet.setBorderColor(ColorUtil.getBlueColorsList().get(i));
					List<Object> values = new ArrayList<>();
					for (LocalDate d : daysOfTheWeek) {
						for (ProductInventory inv : entry.getValue()) {
							if ((inv.getTo() == null)) {
								if (!inv.getFrom().isAfter(d)) {
									values.add(inv.getQuantity());
								}
							} else {
								if (d.isEqual(inv.getFrom()) || d.isEqual(inv.getTo())
										|| (d.isAfter(inv.getFrom()) && (d.isBefore(inv.getTo()))))
									values.add(inv.getQuantity());
							}
						}
					}
					dataSet.setData(values);
					data.addChartDataSet(dataSet);
					i++;
				}
			}
		}

		data.setLabels(labels);
		lineModel.setData(data);
	}

}