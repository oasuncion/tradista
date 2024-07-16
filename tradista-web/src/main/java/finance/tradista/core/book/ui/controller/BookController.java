package finance.tradista.core.book.ui.controller;

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
import finance.tradista.core.common.util.ColorUtil;

/********************************************************************************
 * Copyright (c) 2022 Olivier Asuncion
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

@Named
@ViewScoped
public class BookController implements Serializable {

	private static final long serialVersionUID = -7107650532883558795L;

	private DonutChartModel productDonutModel;

	private DonutChartModel cashDonutModel;

	private BookBusinessDelegate bookBusinessDelegate;

	@PostConstruct
	public void init() {
		bookBusinessDelegate = new BookBusinessDelegate();
		productDonutModel = new DonutChartModel();
		cashDonutModel = new DonutChartModel();
		loadBook();
	}

	public DonutChartModel getProductDonutModel() {
		return productDonutModel;
	}

	public void setProductDonutModel(DonutChartModel productDonutModel) {
		this.productDonutModel = productDonutModel;
	}

	public DonutChartModel getCashDonutModel() {
		return cashDonutModel;
	}

	public void setCashDonutModel(DonutChartModel cashDonutModel) {
		this.cashDonutModel = cashDonutModel;
	}

	public void loadBook() {
		try {
			refresh(bookBusinessDelegate.getBookByName("Demo Book"));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public void refresh(Book book) {
		ChartData productData = new ChartData();
		ChartData currencyData = new ChartData();
		Map<String, Map<String, BigDecimal>> bookContent = null;

		DonutChartDataSet productDataSet = new DonutChartDataSet();
		DonutChartDataSet currencyDataSet = new DonutChartDataSet();
		List<Number> productValues = new ArrayList<>();
		List<Number> currencyValues = new ArrayList<>();

		List<String> blueColors = new ArrayList<>();
		blueColors.addAll(ColorUtil.getBlueColorsList());

		List<String> redColors = new ArrayList<>();
		redColors.addAll(ColorUtil.getRedColorsList());

		productData.addChartDataSet(productDataSet);
		currencyData.addChartDataSet(currencyDataSet);
		List<String> productLabels = new ArrayList<>();
		List<String> currencyLabels = new ArrayList<>();

		try {
			bookContent = bookBusinessDelegate.getBookContent(book.getId());
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}

		if (bookContent != null && !bookContent.isEmpty()) {
			Map<String, BigDecimal> products = bookContent.get("Product");
			if (products != null && !products.isEmpty()) {
				for (Map.Entry<String, BigDecimal> entry : products.entrySet()) {
					productLabels.add(entry.getKey());
					productValues.add(entry.getValue().doubleValue());
				}
			}
			Map<String, BigDecimal> currencies = bookContent.get("Currency");
			if (currencies != null && !currencies.isEmpty()) {
				for (Map.Entry<String, BigDecimal> entry : currencies.entrySet()) {
					currencyLabels.add(entry.getKey());
					currencyValues.add(entry.getValue().doubleValue());
				}
			}
		}

		productDataSet.setBackgroundColor(blueColors);
		productDataSet.setData(productValues);
		currencyDataSet.setBackgroundColor(redColors);
		currencyDataSet.setData(currencyValues);
		productData.setLabels(productLabels);
		currencyData.setLabels(currencyLabels);
		productDonutModel.setData(productData);
		cashDonutModel.setData(currencyData);
	}

}