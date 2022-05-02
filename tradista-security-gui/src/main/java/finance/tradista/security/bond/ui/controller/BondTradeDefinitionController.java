package finance.tradista.security.bond.ui.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.book.ui.controller.TradistaBookPieChart;
import finance.tradista.core.cashflow.model.CashFlow;
import finance.tradista.core.cashflow.ui.controller.CashFlowProperty;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.ui.controller.TradistaTradeBookingController;
import finance.tradista.core.common.ui.publisher.TradistaPublisher;
import finance.tradista.core.common.ui.util.TradistaGUIUtil;
import finance.tradista.core.common.ui.view.TradistaAlert;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.marketdata.model.InterestRateCurve;
import finance.tradista.core.marketdata.model.QuoteSet;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.ui.controller.QuoteProperty;
import finance.tradista.core.marketdata.ui.publisher.MarketDataPublisher;
import finance.tradista.core.pricing.pricer.Parameterizable;
import finance.tradista.core.pricing.pricer.Pricer;
import finance.tradista.core.pricing.pricer.PricerMeasure;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.service.PricerBusinessDelegate;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.legalentity.service.LegalEntityBusinessDelegate;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.bond.model.BondTrade;
import finance.tradista.security.bond.service.BondBusinessDelegate;
import finance.tradista.security.bond.service.BondPricerBusinessDelegate;
import finance.tradista.security.bond.service.BondTradeBusinessDelegate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

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

public class BondTradeDefinitionController extends TradistaTradeBookingController {

	@FXML
	private ComboBox<Trade.Direction> buySell;

	@FXML
	private DatePicker tradeDate;

	@FXML
	private DatePicker settlementDate;

	@FXML
	private ComboBox<Bond> bond;

	@FXML
	private TextField tradePrice;

	@FXML
	private TextField quantity;

	@FXML
	private ComboBox<LegalEntity> counterparty;

	@FXML
	private ComboBox<PricingParameter> pricingParameter;

	@FXML
	private ComboBox<PricerMeasure> pricingMeasure;

	@FXML
	private ComboBox<String> pricingMethod;

	@FXML
	private ComboBox<Currency> pricingCurrency;

	@FXML
	private ComboBox<Book> book;

	@FXML
	private DatePicker pricingDate;

	@FXML
	private Label pricerLabel;

	@FXML
	private Label result;

	@FXML
	private Label pricerQuoteSetLabel;

	@FXML
	private Label priceWarning;

	// Quotes

	@FXML
	private TableColumn<QuoteProperty, String> quoteName;

	@FXML
	private TableColumn<QuoteProperty, String> quoteDate;

	@FXML
	private TableColumn<QuoteProperty, String> quoteType;

	@FXML
	private TableColumn<QuoteProperty, String> quoteBid;

	@FXML
	private TableColumn<QuoteProperty, String> quoteAsk;

	@FXML
	private TableColumn<QuoteProperty, String> quoteOpen;

	@FXML
	private TableColumn<QuoteProperty, String> quoteClose;

	@FXML
	private TableColumn<QuoteProperty, String> quoteHigh;

	@FXML
	private TableColumn<QuoteProperty, String> quoteLow;

	@FXML
	private TableColumn<QuoteProperty, String> quoteLast;

	@FXML
	private TableColumn<QuoteProperty, String> quoteEnteredDate;

	@FXML
	private TableColumn<QuoteProperty, String> quoteSourceName;

	@FXML
	private TableView<CashFlowProperty> cashFlowsTable;

	@FXML
	private TableColumn<CashFlowProperty, String> cfDate;

	@FXML
	private TableColumn<CashFlowProperty, String> cfPurpose;

	@FXML
	private TableColumn<CashFlowProperty, String> cfDirection;

	@FXML
	private TableColumn<CashFlowProperty, String> cfAmount;

	@FXML
	private TableColumn<CashFlowProperty, String> cfCurrency;

	@FXML
	private TableColumn<CashFlowProperty, String> cfDiscountedAmount;

	@FXML
	private TableColumn<CashFlowProperty, String> cfDiscountFactor;

	private BondBusinessDelegate bondBusinessDelegate;

	private LegalEntityBusinessDelegate legalEntityBusinessDelegate;

	private PricerBusinessDelegate pricerBusinessDelegate;

	private BondTradeBusinessDelegate bondTradeBusinessDelegate;

	private BondPricerBusinessDelegate bondPricerBusinessDelegate;

	private BookBusinessDelegate bookBusinessDelegate;

	@FXML
	private TextField load;

	@FXML
	private Label tradeId;

	private BondTrade trade;

	@FXML
	private Label tradeType;

	@FXML
	private Label cfPricingDate;

	@FXML
	private Label cfDiscountCurve;

	@FXML
	private Button generate;

	@FXML
	private TradistaBookPieChart bookChartPane;

	private ChangeListener<Bond> bondListener;

	private ChangeListener<LocalDate> tradeDateListener;

	private ChangeListener<Trade.Direction> directionListener;

	// This method is called by the FXMLLoader when initialization is complete
	public void initialize() {
		super.initialize();
		ExecutorService exec = Executors.newSingleThreadExecutor();
		quoteValues = Collections.synchronizedSet(new HashSet<QuoteValue>(1));
		tradeType.setText("Bond Trade");

		bondBusinessDelegate = new BondBusinessDelegate();
		legalEntityBusinessDelegate = new LegalEntityBusinessDelegate();
		pricerBusinessDelegate = new PricerBusinessDelegate();
		bondTradeBusinessDelegate = new BondTradeBusinessDelegate();
		bookBusinessDelegate = new BookBusinessDelegate();
		bondPricerBusinessDelegate = new BondPricerBusinessDelegate();

		tradeDate.setValue(LocalDate.now());

		// Quotes initialization
		quoteName.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("name"));
		quoteDate.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("date"));
		quoteType.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("type"));

		quoteBid.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("bid"));
		quoteAsk.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("ask"));
		quoteOpen.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("open"));
		quoteClose.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("close"));
		quoteHigh.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("high"));
		quoteLow.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("low"));
		quoteLast.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("last"));
		quoteEnteredDate.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("enteredDate"));
		quoteSourceName.setCellValueFactory(new PropertyValueFactory<QuoteProperty, String>("sourceName"));

		// CashFlows table
		cfDate.setCellValueFactory(new PropertyValueFactory<CashFlowProperty, String>("date"));
		cfAmount.setCellValueFactory(new PropertyValueFactory<CashFlowProperty, String>("amount"));
		cfCurrency.setCellValueFactory(new PropertyValueFactory<CashFlowProperty, String>("currency"));
		cfPurpose.setCellValueFactory(new PropertyValueFactory<CashFlowProperty, String>("purpose"));
		cfDirection.setCellValueFactory(new PropertyValueFactory<CashFlowProperty, String>("direction"));
		cfDiscountedAmount.setCellValueFactory(new PropertyValueFactory<CashFlowProperty, String>("discountedAmount"));
		cfDiscountFactor.setCellValueFactory(new PropertyValueFactory<CashFlowProperty, String>("discountFactor"));

		selectedQuoteSet.valueProperty().addListener(new ChangeListener<QuoteSet>() {
			@Override
			public void changed(ObservableValue<? extends QuoteSet> observableValue, QuoteSet oldValue,
					QuoteSet newValue) {
				if (newValue != null && bond.getValue() != null && selectedQuoteDate.getValue() != null) {
					String bondQuote = Bond.BOND + "." + bond.getValue().getIsin() + "."
							+ bond.getValue().getExchange();
					String bondIndex = null;
					if (bond.getValue().getCouponType().equals("Float")) {
						bondIndex = Index.INDEX + "." + bond.getValue().getReferenceRateIndex().getName() + "."
								+ bond.getValue().getCouponFrequency() + "%";
					}
					fillQuotesTable(newValue, selectedQuoteDate.getValue(), bondQuote, bondIndex);
				}
			}
		});

		selectedQuoteDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate oldValue,
					LocalDate newValue) {
				if (newValue != null && bond.getValue() != null) {
					String bondQuote = Bond.BOND + "." + bond.getValue().getIsin() + "."
							+ bond.getValue().getExchange();
					String bondIndex = null;
					if (bond.getValue().getCouponType().equals("Float")) {
						bondIndex = Index.INDEX + "." + bond.getValue().getReferenceRateIndex().getName() + "."
								+ bond.getValue().getCouponFrequency() + "%";
					}
					fillQuotesTable(selectedQuoteSet.getValue(), newValue, bondQuote, bondIndex);
				}
			}
		});

		bond.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Bond>() {
			@Override
			public void changed(ObservableValue<? extends Bond> observableValue, Bond oldValue, Bond newValue) {
				// newValue is null on first call to refresh.
				if (newValue != null) {
					String bondQuote = Bond.BOND + "." + newValue.getIsin() + "." + newValue.getExchange();
					String bondIndex = null;
					if (newValue.getCouponType().equals("Float")) {
						bondIndex = Index.INDEX + "." + newValue.getReferenceRateIndex().getName() + "."
								+ newValue.getCouponFrequency() + "%";
					}
					fillQuotesTable(selectedQuoteSet.getValue(), selectedQuoteDate.getValue(), bondQuote, bondIndex);
					if (pricingParameter.getValue() != null) {
						InterestRateCurve discountCurve = pricingParameter.getValue()
								.getDiscountCurve(newValue.getCurrency());
						if (discountCurve != null) {
							cfDiscountCurve.setText(discountCurve.getName());
							TradistaGUIUtil.unapplyWarningStyle(cfDiscountCurve);
						} else {
							cfDiscountCurve.setText(String.format(
									"Pricing Parameters Set '%s' doesn't contain a discount curve for currency %s.",
									pricingParameter.getValue().getName(), newValue.getCurrency()));
							TradistaGUIUtil.applyWarningStyle(cfDiscountCurve);
						}
					}
				}
			}
		});

		pricingMeasure.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PricerMeasure>() {
			@Override
			public void changed(ObservableValue<? extends PricerMeasure> observableValue,
					PricerMeasure oldPricerMeasure, PricerMeasure newPricerMeasure) {
				// newPricerMeasure is null when we do "setItems" in
				// the first call of the refresh method
				if (newPricerMeasure != null) {
					TradistaGUIUtil.fillComboBox(pricerBusinessDelegate.getAllPricingMethods(newPricerMeasure),
							pricingMethod);
				}
			}
		});

		TradistaGUIUtil.fillComboBox(bondBusinessDelegate.getAllBonds(), bond);

		pricingParameter.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PricingParameter>() {
			@Override
			public void changed(ObservableValue<? extends PricingParameter> observableValue,
					PricingParameter oldPricingParameter, PricingParameter newPricingParam) {
				// newPricingParam is null when we do "setItems" in
				// the first call of the refresh method
				if (newPricingParam != null) {
					Pricer pricer = null;
					try {
						pricer = pricerBusinessDelegate.getPricer(Bond.BOND, newPricingParam);
					} catch (TradistaBusinessException tbe) {
						// Will never happen in this case.
					}
					TradistaGUIUtil.fillComboBox(pricer.getPricerMeasures(), pricingMeasure);
					pricerLabel.setText(pricer.getClass().getAnnotation(Parameterizable.class).name());
					pricerQuoteSetLabel.setText(newPricingParam.getQuoteSet().getName());

					if (bond.getValue() != null) {
						InterestRateCurve discountCurve = newPricingParam
								.getDiscountCurve(bond.getValue().getCurrency());
						if (discountCurve != null) {
							cfDiscountCurve.setText(discountCurve.getName());
							TradistaGUIUtil.unapplyWarningStyle(cfDiscountCurve);
						} else {
							cfDiscountCurve.setText(String.format(
									"Pricing Parameters Set '%s' doesn't contain a discount curve for currency %s.",
									newPricingParam.getName(), bond.getValue().getCurrency()));
							TradistaGUIUtil.applyWarningStyle(cfDiscountCurve);
						}
					}
				}
			}
		});

		pricingDate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cfPricingDate.setText(pricingDate.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			}
		});

		pricingDate.setValue(LocalDate.now());
		cfPricingDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		book.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Book>() {
			@Override
			public void changed(ObservableValue<? extends Book> arg0, Book oldValue, Book newValue) {
				if (newValue != null) {
					bookChartPane.updateBookChart(newValue);
				}
			}
		});

		final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
			public DateCell call(final DatePicker datePicker) {
				return new DateCell() {

					BondTrade bondTrade;

					private boolean isAvailable(LocalDate date) {
						if (bondTrade == null) {
							bondTrade = new BondTrade();
							bondTrade.setProduct(bond.getValue());
						}
						if (bondTrade.getProduct() != null) {
							try {
								return bondTradeBusinessDelegate.isBusinessDay(bondTrade, date);
							} catch (TradistaBusinessException tbe) {
								// TODO Auto-generated catch block
								tbe.printStackTrace();
							}
							return false;
						} else {
							return true;
						}
					}

					@Override
					public void updateItem(LocalDate item, boolean empty) {
						super.updateItem(item, empty);
						if (!isAvailable(item)) {
							setDisable(true);
						}
					}
				};
			}
		};

		tradeDate.setDayCellFactory(dayCellFactory);
		selectedQuoteDate.setDayCellFactory(dayCellFactory);

		tradeDateListener = new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> arg0, LocalDate arg1, LocalDate newDate) {
				if (newDate != null && bond.getValue() != null && selectedQuoteSet.getValue() != null) {

					if (!newDate.isAfter(LocalDate.now())) {
						Task<Void> task = new Task<Void>() {
							@Override
							public Void call() {
								try {
									BigDecimal price = getTradePrice(newDate, buySell.getValue(), bond.getValue(),
											selectedQuoteSet.getValue().getId());
									isQuoteSetServiceError = false;
									if (price == null) {
										Platform.runLater(() -> {
											TradistaGUIUtil.unapplyErrorStyle(priceWarning);
											TradistaGUIUtil.applyWarningStyle(priceWarning);
											priceWarning.setVisible(true);
											priceWarning.setText(String.format(
													"Bond %s price is not available as of Trade Date (%tD) in QuoteSet %s.",
													bond.getValue(), newDate, selectedQuoteSet.getValue()));
										});
									} else {
										Platform.runLater(() -> {
											tradePrice.setText(TradistaGUIUtil.formatAmount(price));
											priceWarning.setVisible(false);
										});
									}
								} catch (TradistaTechnicalException tte) {
									isQuoteSetServiceError = true;
									Platform.runLater(() -> {
										TradistaGUIUtil.applyErrorStyle(priceWarning);
										TradistaGUIUtil.unapplyWarningStyle(priceWarning);
										priceWarning.setText(String.format(
												"Cannot get bond %s price as of Trade Date (%tD) in QuoteSet %s, please contact support.",
												bond.getValue(), newDate, selectedQuoteSet.getValue()));
										priceWarning.setVisible(true);
									});
								}
								return null;
							}
						};
						exec.submit(task);
					}

				}
			}
		};

		directionListener = new ChangeListener<Trade.Direction>() {
			@Override
			public void changed(ObservableValue<? extends Trade.Direction> arg0, Trade.Direction arg1,
					Trade.Direction newDirection) {
				if (tradeDate.getValue() != null && bond.getValue() != null && selectedQuoteSet.getValue() != null) {

					if (!tradeDate.getValue().isAfter(LocalDate.now())) {
						Task<Void> task = new Task<Void>() {
							@Override
							public Void call() {
								try {
									BigDecimal price = getTradePrice(tradeDate.getValue(), newDirection,
											bond.getValue(), selectedQuoteSet.getValue().getId());
									isQuoteSetServiceError = false;
									if (price == null) {
										Platform.runLater(() -> {
											TradistaGUIUtil.unapplyErrorStyle(priceWarning);
											TradistaGUIUtil.applyWarningStyle(priceWarning);
											priceWarning.setVisible(true);
											priceWarning.setText(String.format(
													"Bond %s price is not available as of Trade Date (%tD) in QuoteSet %s.",
													bond.getValue(), tradeDate.getValue(),
													selectedQuoteSet.getValue()));
										});
									} else {
										Platform.runLater(() -> {
											tradePrice.setText(TradistaGUIUtil.formatAmount(price));
											priceWarning.setVisible(false);
										});
									}
								} catch (TradistaTechnicalException tte) {
									isQuoteSetServiceError = true;
									Platform.runLater(() -> {
										TradistaGUIUtil.applyErrorStyle(priceWarning);
										TradistaGUIUtil.unapplyWarningStyle(priceWarning);
										priceWarning.setText(String.format(
												"Cannot get bond %s price as of Trade Date (%tD) in QuoteSet %s, please contact support.",
												bond.getValue(), tradeDate.getValue(), selectedQuoteSet.getValue()));
										priceWarning.setVisible(true);
									});
								}
								return null;
							}
						};
						exec.submit(task);
					}
				}
			}
		};

		bondListener = new ChangeListener<Bond>() {
			@Override
			public void changed(ObservableValue<? extends Bond> arg0, Bond arg1, Bond newBond) {
				if (tradeDate.getValue() != null && newBond != null && selectedQuoteSet.getValue() != null) {

					if (!tradeDate.getValue().isAfter(LocalDate.now())) {
						Task<Void> task = new Task<Void>() {
							@Override
							public Void call() {
								try {
									BigDecimal price = getTradePrice(tradeDate.getValue(), buySell.getValue(), newBond,
											selectedQuoteSet.getValue().getId());
									isQuoteSetServiceError = false;
									if (price == null) {
										Platform.runLater(() -> {
											TradistaGUIUtil.unapplyErrorStyle(priceWarning);
											TradistaGUIUtil.applyWarningStyle(priceWarning);
											priceWarning.setVisible(true);
											priceWarning.setText(String.format(
													"Bond %s price is not available as of Trade Date (%tD) in QuoteSet %s.",
													newBond, tradeDate.getValue(), selectedQuoteSet.getValue()));
										});
									} else {
										Platform.runLater(() -> {
											tradePrice.setText(TradistaGUIUtil.formatAmount(price));
											priceWarning.setVisible(false);
										});
									}
								} catch (TradistaTechnicalException tte) {
									isQuoteSetServiceError = true;
									Platform.runLater(() -> {
										TradistaGUIUtil.applyErrorStyle(priceWarning);
										TradistaGUIUtil.unapplyWarningStyle(priceWarning);
										priceWarning.setText(String.format(
												"Cannot get bond %s price as of Trade Date (%tD) in QuoteSet %s, please contact support.",
												newBond, tradeDate.getValue(), selectedQuoteSet.getValue()));
										priceWarning.setVisible(true);
									});
								}
								return null;
							}
						};
						exec.submit(task);
					}

				}
			}
		};

		addListeners();

		TradistaGUIUtil.fillPricingParameterComboBox(pricingParameter);
		TradistaGUIUtil.fillCurrencyComboBox(pricingCurrency);
		TradistaGUIUtil.fillComboBox(legalEntityBusinessDelegate.getAllLegalEntities(), counterparty);
		TradistaGUIUtil.fillComboBox(bookBusinessDelegate.getAllBooks(), book);
		TradistaGUIUtil.fillTradeDirectionComboBox(buySell);

	}

	private void addListeners() {
		tradeDate.valueProperty().addListener(tradeDateListener);
		buySell.valueProperty().addListener(directionListener);
		bond.valueProperty().addListener(bondListener);
	}

	private BigDecimal getTradePrice(LocalDate tradeDate, Trade.Direction direction, Bond bond, long quoteSetId) {
		QuoteValue qv = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(quoteSetId,
				Bond.BOND + "." + bond.getIsin() + "." + bond.getExchange(), QuoteType.BOND_PRICE, tradeDate);
		BigDecimal bondPrice = null;
		if (qv == null) {
			return null;
		}
		if (qv.getClose() != null) {
			bondPrice = qv.getClose();
		}
		if (direction.equals(Trade.Direction.BUY)) {
			if (qv.getAsk() != null) {
				bondPrice = qv.getAsk();
			}
		} else {
			if (qv.getBid() != null) {
				bondPrice = qv.getBid();
			}
		}
		if (bondPrice == null) {
			bondPrice = qv.getLast();
		}
		if (bondPrice != null) {
			return bondPrice;
		}
		return null;
	}

	@FXML
	protected void save() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Save Trade");
		confirmation.setHeaderText("Save Trade");
		confirmation.setContentText("Do you want to save this Trade?");

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				checkAmounts();

				buildTrade();

				trade.setId(bondTradeBusinessDelegate.saveBondTrade(trade));
				tradeId.setText(String.valueOf(trade.getId()));
			} catch (TradistaBusinessException tbe) {
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void generate() {
		try {
			checkAmounts();

			buildTrade();

			List<CashFlow> cashFlows = bondPricerBusinessDelegate.generateCashFlows(trade, pricingParameter.getValue(),
					pricingDate.getValue());
			if (cashFlows != null) {
				cashFlowsTable.setItems(
						FXCollections.observableArrayList(CashFlowProperty.toCashFlowPropertyList(cashFlows)));
				generate.setText("Refresh");
			}
		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	@Override
	@FXML
	public void clear() {
		trade = null;
		settlementDate.setValue(null);
		tradePrice.clear();
		quantity.clear();
		tradeId.setText("");
	}

	@FXML
	protected void copy() {
		TradistaAlert confirmation = new TradistaAlert(AlertType.CONFIRMATION);
		confirmation.setTitle("Copy Trade");
		confirmation.setHeaderText("Copy Trade");
		confirmation.setContentText("Do you want to copy this Trade?");
		long oldTradeId = 0;
		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				checkAmounts();

				buildTrade();
				oldTradeId = trade.getId();
				trade.setId(0);
				trade.setId(bondTradeBusinessDelegate.saveBondTrade(trade));
				tradeId.setText(String.valueOf(trade.getId()));
			} catch (TradistaBusinessException tbe) {
				trade.setId(oldTradeId);
				TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	protected void load() {
		BondTrade bondTrade;
		long tradeId = 0;
		try {
			try {
				if (!load.getText().isEmpty()) {
					tradeId = Long.parseLong(load.getText());
				} else {
					throw new TradistaBusinessException("Please specify a trade id.");
				}
			} catch (NumberFormatException nfe) {
				throw new TradistaBusinessException(String.format("The trade id is incorrect: %s", load.getText()));
			}

			bondTrade = bondTradeBusinessDelegate.getBondTradeById(tradeId);
			if (bondTrade == null) {
				throw new TradistaBusinessException(
						String.format("The %s trade %s was not found.", Bond.BOND, load.getText()));
			}
			load(bondTrade);
		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	private void load(BondTrade trade) {
		this.trade = trade;
		tradeId.setText(String.valueOf(trade.getId()));
		tradeDate.setValue(trade.getTradeDate());
		buySell.setValue(trade.isBuy() ? Trade.Direction.BUY : Trade.Direction.SELL);
		counterparty.setValue(trade.getCounterparty());
		book.setValue(trade.getBook());
		bond.setValue(trade.getProduct());
		quantity.setText(TradistaGUIUtil.formatAmount(trade.getQuantity()));
		settlementDate.setValue(trade.getSettlementDate());
		tradePrice.setText(TradistaGUIUtil.formatAmount(trade.getAmount()));
	}

	private void buildTrade() {
		if (this.trade == null) {
			trade = new BondTrade();
			trade.setCreationDate(LocalDate.now());
		}
		try {
			if (!tradePrice.getText().isEmpty()) {
				trade.setAmount(TradistaGUIUtil.parseAmount(tradePrice.getText(), "Trade Price"));
			}
			if (!quantity.getText().isEmpty()) {
				trade.setQuantity(TradistaGUIUtil.parseAmount(quantity.getText(), "Quantity"));
			}
			trade.setProduct(bond.getValue());
			trade.setTradeDate(tradeDate.getValue());
			trade.setSettlementDate(settlementDate.getValue());
			trade.setBuySell(buySell.getValue().equals(Trade.Direction.BUY));
			trade.setCounterparty(counterparty.getValue());
			trade.setBook(book.getValue());

		} catch (TradistaBusinessException tbe) {
			// Should not appear here.
		}
	}

	@FXML
	protected void price() {
		try {
			checkAmounts();

			buildTrade();

			result.setText(TradistaGUIUtil.formatAmount(
					pricerBusinessDelegate.calculate(trade, pricingParameter.getValue(), pricingCurrency.getValue(),
							pricingDate.getValue(), pricingMeasure.getValue(), pricingMethod.getValue())));

		} catch (TradistaBusinessException tbe) {
			TradistaAlert alert = new TradistaAlert(AlertType.ERROR, tbe.getMessage());
			alert.showAndWait();
		}
	}

	@Override
	@FXML
	public void refresh() {
		super.refresh();
		TradistaGUIUtil.fillPricingParameterComboBox(pricingParameter);
		TradistaGUIUtil.fillCurrencyComboBox(pricingCurrency);
		TradistaGUIUtil.fillComboBox(legalEntityBusinessDelegate.getAllLegalEntities(), counterparty);
		TradistaGUIUtil.fillBookComboBox(book);
		TradistaGUIUtil.fillComboBox(bondBusinessDelegate.getAllBonds(), bond);
	}

	@Override
	public void update(TradistaPublisher publisher) {
		super.update(publisher);
		if (publisher instanceof MarketDataPublisher) {
			if (!publisher.isError()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Set<QuoteValue> quoteValues = ((MarketDataPublisher) publisher).getQuoteValues();
						if (quoteValues != null && !quoteValues.isEmpty()) {
							for (QuoteValue qv : quoteValues) {
								if (qv.getQuoteSet().equals(selectedQuoteSet.getValue())) {
									if (qv.getQuote().getName().equals(Bond.BOND + "." + bond.getValue().getIsin() + "."
											+ bond.getValue().getExchange())) {
										if (qv.getDate().equals(selectedQuoteDate.getValue())) {
											if (qv.getQuote().getType().equals(QuoteType.EXCHANGE_RATE)) {
												if (BondTradeDefinitionController.this.quoteValues.contains(qv)) {
													BondTradeDefinitionController.this.quoteValues.remove(qv);
												}
												BondTradeDefinitionController.this.quoteValues.add(qv);
											}
										}
									}
								}
							}
						}
						quotesTable.setItems(FXCollections.observableArrayList(
								QuoteProperty.toQuotePropertyList(BondTradeDefinitionController.this.quoteValues)));
					}
				});
			}
		}
	}

	@Override
	public void checkAmounts() throws TradistaBusinessException {
		StringBuilder errMsg = new StringBuilder();
		try {
			TradistaGUIUtil.checkAmount(tradePrice.getText(), "Trade Price");
		} catch (TradistaBusinessException tbe) {
			errMsg.append(tbe.getMessage());
		}
		try {
			TradistaGUIUtil.checkAmount(quantity.getText(), "Quantity");
		} catch (TradistaBusinessException tbe) {
			errMsg.append(tbe.getMessage());
		}
		if (errMsg.length() > 0) {
			throw new TradistaBusinessException(errMsg.toString());
		}
	}

}