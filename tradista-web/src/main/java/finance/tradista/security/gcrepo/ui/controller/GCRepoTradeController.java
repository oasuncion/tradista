package finance.tradista.security.gcrepo.ui.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import finance.tradista.core.action.constants.ActionConstants;
import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.currency.service.CurrencyBusinessDelegate;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.index.service.IndexBusinessDelegate;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.trade.model.Trade;
import finance.tradista.core.trade.model.Trade.Direction;
import finance.tradista.core.workflow.model.Action;
import finance.tradista.core.workflow.model.Status;
import finance.tradista.core.workflow.model.Workflow;
import finance.tradista.core.workflow.service.WorkflowBusinessDelegate;
import finance.tradista.legalentity.service.LegalEntityBusinessDelegate;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.gcrepo.model.GCBasket;
import finance.tradista.security.gcrepo.model.GCRepoTrade;
import finance.tradista.security.gcrepo.service.GCBasketBusinessDelegate;
import finance.tradista.security.gcrepo.service.GCRepoTradeBusinessDelegate;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/********************************************************************************
 * Copyright (c) 2023 Olivier Asuncion
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
public class GCRepoTradeController implements Serializable {

	private static final long serialVersionUID = -8633490083412044246L;

	private GCRepoTrade gcRepoTrade;

	private Set<LegalEntity> allCounterparties;

	private Set<Currency> allCurrencies;

	private Set<Index> allIndexes;

	private Set<Book> allBooks;

	private Direction[] allDirections;

	private Set<GCBasket> allBaskets;

	private GCRepoTradeBusinessDelegate gcRepoTradeBusinessDelegate;

	private CurrencyBusinessDelegate currencyBusinessDelegate;

	private BookBusinessDelegate bookBusinessDelegate;

	private LegalEntityBusinessDelegate legalEntityBusinessDelegate;

	private IndexBusinessDelegate indexBusinessDelegate;

	private GCBasketBusinessDelegate gcBasketBusinessDelegate;

	private WorkflowBusinessDelegate workflowBusinessDelegate;

	private String idToBeLoaded;

	private String[] allInterestTypes;

	private String interestType;

	private Tenor[] allIndexTenors;

	private Workflow workflow;

	private String action;

	private String[] allAvailableActions;

	// Used to get the reduction amount in case of partial termination
	private BigDecimal originalCashAmount;

	private static final String TRADE_MSG = "tradeMsg";

	@PostConstruct
	public void init() throws TradistaBusinessException {
		gcRepoTradeBusinessDelegate = new GCRepoTradeBusinessDelegate();
		gcBasketBusinessDelegate = new GCBasketBusinessDelegate();
		currencyBusinessDelegate = new CurrencyBusinessDelegate();
		bookBusinessDelegate = new BookBusinessDelegate();
		legalEntityBusinessDelegate = new LegalEntityBusinessDelegate();
		indexBusinessDelegate = new IndexBusinessDelegate();
		workflowBusinessDelegate = new WorkflowBusinessDelegate();
		allCurrencies = currencyBusinessDelegate.getAllCurrencies();
		allCounterparties = legalEntityBusinessDelegate.getAllCounterparties();
		allIndexes = indexBusinessDelegate.getAllIndexes();
		allBooks = bookBusinessDelegate.getAllBooks();
		allDirections = Trade.Direction.values();
		gcRepoTrade = new GCRepoTrade();
		allInterestTypes = new String[] { "Fixed", "Floating" };
		allIndexTenors = Arrays.asList(Tenor.values()).stream().filter(t -> !t.equals(Tenor.NO_TENOR))
				.toArray(Tenor[]::new);
		allBaskets = gcBasketBusinessDelegate.getAllGCBaskets();
		setTradeDate(LocalDate.now());
		setStartDate(LocalDate.now());
		workflow = workflowBusinessDelegate.getWorkflowByName(GCRepoTrade.GC_REPO);
	}

	public String getId() {
		return gcRepoTrade.getId() == 0 ? "" : Long.toString(gcRepoTrade.getId());
	}

	public void setId(String id) {
		gcRepoTrade.setId(Long.parseLong(id));
	}

	public Direction[] getAllDirections() {
		return allDirections;
	}

	public void setAllDirections(Direction[] allDirections) {
		this.allDirections = allDirections;
	}

	public BigDecimal getAmount() {
		return gcRepoTrade.getAmount();
	}

	public void setAmount(BigDecimal amount) {
		gcRepoTrade.setAmount(amount);
	}

	public Status getStatus() {
		return gcRepoTrade.getStatus();
	}

	public void setStatus(Status status) {
		gcRepoTrade.setStatus(status);
	}

	public GCRepoTrade getGcRepoTrade() {
		return gcRepoTrade;
	}

	public void setGcRepoTrade(GCRepoTrade gcRepoTrade) {
		this.gcRepoTrade = gcRepoTrade;
	}

	public BigDecimal getRepoRate() {
		return gcRepoTrade.getRepoRate();
	}

	public void setRepoRate(BigDecimal repoRate) {
		gcRepoTrade.setRepoRate(repoRate);
	}

	public Trade.Direction getDirection() {
		return (gcRepoTrade.isBuy() ? Direction.BUY : Direction.SELL);
	}

	public void setDirection(Trade.Direction direction) {
		gcRepoTrade.setBuySell(Direction.BUY.equals(direction));
	}

	public LocalDate getTradeDate() {
		return gcRepoTrade.getTradeDate();
	}

	public void setTradeDate(LocalDate tradeDate) {
		gcRepoTrade.setTradeDate(tradeDate);
	}

	public LocalDate getStartDate() {
		return gcRepoTrade.getSettlementDate();
	}

	public void setStartDate(LocalDate startDate) {
		gcRepoTrade.setSettlementDate(startDate);
	}

	public LocalDate getEndDate() {
		return gcRepoTrade.getEndDate();
	}

	public void setEndDate(LocalDate endDate) {
		gcRepoTrade.setEndDate(endDate);
	}

	public Currency getCurrency() {
		return gcRepoTrade.getCurrency();
	}

	public void setCurrency(Currency currency) {
		gcRepoTrade.setCurrency(currency);
	}

	public LegalEntity getCounterparty() {
		return gcRepoTrade.getCounterparty();
	}

	public void setCounterparty(LegalEntity legalEntity) {
		gcRepoTrade.setCounterparty(legalEntity);
	}

	public Book getBook() {
		return gcRepoTrade.getBook();
	}

	public void setBook(Book book) {
		gcRepoTrade.setBook(book);
	}

	public Index getIndex() {
		return gcRepoTrade.getIndex();
	}

	public void setIndex(Index index) {
		gcRepoTrade.setIndex(index);
	}

	public Tenor getIndexTenor() {
		return gcRepoTrade.getIndexTenor();
	}

	public void setIndexTenor(Tenor indexTenor) {
		gcRepoTrade.setIndexTenor(indexTenor);
	}

	public BigDecimal getIndexOffset() {
		return gcRepoTrade.getIndexOffset();
	}

	public void setIndexOffset(BigDecimal indexOffset) {
		gcRepoTrade.setIndexOffset(indexOffset);
	}

	public boolean getRightOfSubstitution() {
		return gcRepoTrade.isRightOfSubstitution();
	}

	public void setRightOfSubstitution(boolean rightOfSubstitution) {
		gcRepoTrade.setRightOfSubstitution(rightOfSubstitution);
	}

	public boolean getRightOfReuse() {
		return gcRepoTrade.isRightOfReuse();
	}

	public void setRightOfReuse(boolean rightOfReuse) {
		gcRepoTrade.setRightOfReuse(rightOfReuse);
	}

	public boolean getCrossCurrencyCollateral() {
		return gcRepoTrade.isCrossCurrencyCollateral();
	}

	public void setCrossCurrencyCollateral(boolean crossCurrencyCollateral) {
		gcRepoTrade.setCrossCurrencyCollateral(crossCurrencyCollateral);
	}

	public boolean getTerminableOnDemand() {
		return gcRepoTrade.isTerminableOnDemand();
	}

	public void setTerminableOnDemand(boolean terminableOnDemand) {
		gcRepoTrade.setTerminableOnDemand(terminableOnDemand);
	}

	public Short getNoticePeriod() {
		return gcRepoTrade.getNoticePeriod();
	}

	public void setNoticePeriod(Short noticePeriod) {
		gcRepoTrade.setNoticePeriod(noticePeriod);
	}

	public BigDecimal getMarginRate() {
		return gcRepoTrade.getMarginRate();
	}

	public void setMarginRate(BigDecimal marginRate) {
		gcRepoTrade.setMarginRate(marginRate);
	}

	public GCBasket getBasket() {
		return gcRepoTrade.getGcBasket();
	}

	public void setBasket(GCBasket gcBasket) {
		gcRepoTrade.setGcBasket(gcBasket);
	}

	public Set<Currency> getAllCurrencies() {
		return allCurrencies;
	}

	public void setAllCurrencies(Set<Currency> allCurrencies) {
		this.allCurrencies = allCurrencies;
	}

	public Set<Index> getAllIndexes() {
		return allIndexes;
	}

	public void setAllIndexes(Set<Index> allIndexes) {
		this.allIndexes = allIndexes;
	}

	public Set<LegalEntity> getAllCounterparties() {
		return allCounterparties;
	}

	public void setAllCounterparties(Set<LegalEntity> allCounterparties) {
		this.allCounterparties = allCounterparties;
	}

	public Set<Book> getAllBooks() {
		return allBooks;
	}

	public void setAllBooks(Set<Book> allBooks) {
		this.allBooks = allBooks;
	}

	public String getInterestType() {
		return interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	public String[] getAllInterestTypes() {
		return allInterestTypes;
	}

	public void setAllInterestTypes(String[] allInterestTypes) {
		this.allInterestTypes = allInterestTypes;
	}

	public Tenor[] getAllIndexTenors() {
		return allIndexTenors;
	}

	public void setAllIndexTenors(Tenor[] allIndexTenors) {
		this.allIndexTenors = allIndexTenors;
	}

	public Set<GCBasket> getAllBaskets() {
		return allBaskets;
	}

	public void setAllBaskets(Set<GCBasket> allBaskets) {
		this.allBaskets = allBaskets;
	}

	public String getIdToBeLoaded() {
		return idToBeLoaded;
	}

	public void setIdToBeLoaded(String idToBeLoaded) {
		this.idToBeLoaded = idToBeLoaded;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void save() {
		try {
			final String actionToApply = (action != null) ? action : Action.NEW;
			if (gcRepoTrade.getId() == 0) {
				gcRepoTrade.setCreationDate(LocalDate.now());
				gcRepoTrade.setStatus(workflowBusinessDelegate.getInitialStatus(workflow.getName()));

			}
			if (interestType == null || interestType.equals("Fixed")) {
				gcRepoTrade.setIndex(null);
				gcRepoTrade.setIndexTenor(null);
				gcRepoTrade.setIndexOffset(null);
			}
			long tradeId = gcRepoTradeBusinessDelegate.saveGCRepoTrade(gcRepoTrade, actionToApply);
			if (gcRepoTrade.getId() == 0) {
				gcRepoTrade.setId(tradeId);
			}
			gcRepoTrade = gcRepoTradeBusinessDelegate.getGCRepoTradeById(tradeId);
			Set<String> availableActions = workflowBusinessDelegate.getAvailableActionsFromStatus(workflow.getName(),
					gcRepoTrade.getStatus());
			if (availableActions != null && !availableActions.isEmpty()) {
				allAvailableActions = availableActions.toArray(new String[availableActions.size()]);
			}
			originalCashAmount = gcRepoTrade.getAmount();
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
					"Trade " + gcRepoTrade.getId() + " successfully saved"));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public void copy() {
		long oldId = gcRepoTrade.getId();
		Map<LocalDate, BigDecimal> oldPartialTerminations = gcRepoTrade.getPartialTerminations();
		Map<Security, Map<Book, BigDecimal>> oldCollateralToAdd = gcRepoTrade.getCollateralToAdd();
		Map<Security, Map<Book, BigDecimal>> oldCollateralToRemove = gcRepoTrade.getCollateralToRemove();
		try {
			gcRepoTrade.setCreationDate(LocalDate.now());
			gcRepoTrade.setId(0);
			gcRepoTrade.setStatus(workflowBusinessDelegate.getInitialStatus(workflow.getName()));
			if (interestType == null || interestType.equals("Fixed")) {
				gcRepoTrade.setIndex(null);
				gcRepoTrade.setIndexTenor(null);
				gcRepoTrade.setIndexOffset(null);
			}
			// Copied trades will not have added/removed collaterals and partial
			// terminations
			gcRepoTrade.setCollateralToAdd(null);
			gcRepoTrade.setCollateralToRemove(null);
			gcRepoTrade.setPartialTerminations(null);
			long tradeId = gcRepoTradeBusinessDelegate.saveGCRepoTrade(gcRepoTrade, Action.NEW);
			gcRepoTrade = gcRepoTradeBusinessDelegate.getGCRepoTradeById(tradeId);
			Set<String> availableActions = workflowBusinessDelegate.getAvailableActionsFromStatus(workflow.getName(),
					gcRepoTrade.getStatus());
			if (availableActions != null && !availableActions.isEmpty()) {
				allAvailableActions = availableActions.toArray(new String[availableActions.size()]);
			}
			originalCashAmount = gcRepoTrade.getAmount();
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
					"Trade " + gcRepoTrade.getId() + " successfully created"));
		} catch (TradistaBusinessException tbe) {
			gcRepoTrade.setId(oldId);
			gcRepoTrade.setCollateralToAdd(oldCollateralToAdd);
			gcRepoTrade.setCollateralToRemove(oldCollateralToRemove);
			gcRepoTrade.setPartialTerminations(oldPartialTerminations);
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public void load() {
		long tradeId;
		try {
			tradeId = Long.parseLong(idToBeLoaded);
			GCRepoTrade gcTrade = gcRepoTradeBusinessDelegate.getGCRepoTradeById(tradeId);
			if (gcTrade != null) {
				gcRepoTrade.setId(gcTrade.getId());
				gcRepoTrade.setBuySell(gcTrade.isBuy());
				gcRepoTrade.setCounterparty(gcTrade.getCounterparty());
				gcRepoTrade.setCurrency(gcTrade.getCurrency());
				gcRepoTrade.setProduct(gcTrade.getProduct());
				gcRepoTrade.setAmount(gcTrade.getAmount());
				gcRepoTrade.setSettlementDate(gcTrade.getSettlementDate());
				gcRepoTrade.setTradeDate(gcTrade.getTradeDate());
				gcRepoTrade.setBook(gcTrade.getBook());
				gcRepoTrade.setCreationDate(gcTrade.getCreationDate());
				gcRepoTrade.setCrossCurrencyCollateral(gcTrade.isCrossCurrencyCollateral());
				gcRepoTrade.setGcBasket(gcTrade.getGcBasket());
				gcRepoTrade.setEndDate(gcTrade.getEndDate());
				gcRepoTrade.setIndex(gcTrade.getIndex());
				gcRepoTrade.setIndexOffset(gcTrade.getIndexOffset());
				gcRepoTrade.setMarginRate(gcTrade.getMarginRate());
				gcRepoTrade.setNoticePeriod(gcTrade.getNoticePeriod());
				gcRepoTrade.setRepoRate(gcTrade.getRepoRate());
				gcRepoTrade.setRightOfReuse(gcTrade.isRightOfReuse());
				gcRepoTrade.setRightOfSubstitution(gcTrade.isRightOfSubstitution());
				gcRepoTrade.setTerminableOnDemand(gcTrade.isTerminableOnDemand());
				gcRepoTrade.setStatus(gcTrade.getStatus());
				gcRepoTrade.setPartialTerminations(gcTrade.getPartialTerminations());
				Set<String> availableActions = workflowBusinessDelegate
						.getAvailableActionsFromStatus(workflow.getName(), gcTrade.getStatus());
				if (availableActions != null && !availableActions.isEmpty()) {
					allAvailableActions = availableActions.toArray(new String[availableActions.size()]);
				}
				originalCashAmount = gcTrade.getAmount();
				FacesContext.getCurrentInstance().addMessage(TRADE_MSG, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Info", "Trade " + gcRepoTrade.getId() + " successfully loaded."));
			} else {
				FacesContext.getCurrentInstance().addMessage(TRADE_MSG, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error", "Trade " + idToBeLoaded + " was not found."));
			}
		} catch (NumberFormatException nfe) {
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please type a valid id."));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}

	}

	public void clear() {
		gcRepoTrade = new GCRepoTrade();
		setTradeDate(LocalDate.now());
		setStartDate(LocalDate.now());
		originalCashAmount = null;
		FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Form cleared"));
	}

	public void updateIndex() {
		if (interestType != null && interestType.equals("Floating")) {
			if (gcRepoTrade.getIndex() == null && allIndexes != null && !allIndexes.isEmpty()) {
				gcRepoTrade.setIndex(allIndexes.stream().findFirst().get());
			}
			if (gcRepoTrade.getIndexTenor() == null) {
				gcRepoTrade.setIndexTenor(allIndexTenors[0]);
			}
		}
		if (interestType != null && interestType.equals("Fixed")) {
			gcRepoTrade.setIndex(null);
			gcRepoTrade.setIndexTenor(null);
		}
	}

	public String[] getAllAvailableActions() {
		return allAvailableActions;
	}

	public void setAllAvailableActions(String[] allAvailableActions) {
		this.allAvailableActions = allAvailableActions;
	}

	public BigDecimal getOriginalCashAmount() {
		return originalCashAmount;
	}

	public void setOriginalCashAmount(BigDecimal originalCashAmount) {
		this.originalCashAmount = originalCashAmount;
	}

	public void updateTrade(Map<Security, Map<Book, BigDecimal>> securitiesToAdd,
			Map<Security, Map<Book, BigDecimal>> securitiesToRemove) {
		if (securitiesToAdd != null && !securitiesToAdd.isEmpty()) {
			gcRepoTrade.setCollateralToAdd(securitiesToAdd);
		}
		if (securitiesToRemove != null && !securitiesToRemove.isEmpty()) {
			gcRepoTrade.setCollateralToRemove(securitiesToRemove);
		}
		if (action != null && action.equals(ActionConstants.PARTIALLY_TERMINATE)) {
			gcRepoTrade.addParTialTermination(LocalDate.now(), originalCashAmount.subtract(gcRepoTrade.getAmount()));
		}
	}

}