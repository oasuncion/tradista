package finance.tradista.web.demo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;

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
public class BookView implements Serializable {

	private static final long serialVersionUID = -7107650532883558795L;

	private DonutChartModel donutModel;

	private BookBusinessDelegate bookBusinessDelegate;

	@PostConstruct
	public void init() {
		bookBusinessDelegate = new BookBusinessDelegate();
		donutModel = new DonutChartModel();
		loadBook();
	}

	public DonutChartModel getDonutModel() {
		return donutModel;
	}

	public void setDonutModel(DonutChartModel donutModel) {
		this.donutModel = donutModel;
	}

	public void loadBook() {
		ChartData data = new ChartData();
		Map<String, Map<String, BigDecimal>> bookContent = null;

		DonutChartDataSet dataSet = new DonutChartDataSet();
		List<Number> values = new ArrayList<>();

		List<String> bgColors = new ArrayList<>();
		bgColors.add(ColorUtil.getColorsList().get(0));
		bgColors.add(ColorUtil.getColorsList().get(1));
		bgColors.add(ColorUtil.getColorsList().get(2));

		data.addChartDataSet(dataSet);
		List<String> labels = new ArrayList<>();

		Book book = null;
		try {
			book = new BookBusinessDelegate().getBookByName("Demo Book");
			bookContent = bookBusinessDelegate.getBookContent(book.getId());
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}

		if (bookContent != null & !bookContent.isEmpty()) {
			Map<String, BigDecimal> products = bookContent.get("Product");
			if (products != null && !products.isEmpty()) {
				for (Map.Entry<String, BigDecimal> entry : products.entrySet()) {
					labels.add(entry.getKey());
					values.add(entry.getValue().doubleValue());
				}
			}
		}

		dataSet.setBackgroundColor(bgColors);
		dataSet.setData(values);
		data.setLabels(labels);
		donutModel.setData(data);
	}

}