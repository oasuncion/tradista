package finance.tradista.security.specificrepo.ui.controller;

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
import finance.tradista.security.common.service.SecurityBusinessDelegate;
import finance.tradista.security.specficrepo.service.SpecificRepoTradeBusinessDelegate;
import finance.tradista.security.specificrepo.model.SpecificRepoTrade;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

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

@Named
@ViewScoped
public class SpecificRepoTradeController implements Serializable {

	private static final long serialVersionUID = -2030826935731150653L;

	private SpecificRepoTrade specificRepoTrade;

	private Set<LegalEntity> allCounterparties;

	private Set<Currency> allCurrencies;

	private Set<Index> allIndexes;

	private Set<Book> allBooks;

	private Direction[] allDirections;

	private Set<Security> allSecurities;

	private SpecificRepoTradeBusinessDelegate specificRepoTradeBusinessDelegate;

	private CurrencyBusinessDelegate currencyBusinessDelegate;

	private BookBusinessDelegate bookBusinessDelegate;

	private LegalEntityBusinessDelegate legalEntityBusinessDelegate;

	private IndexBusinessDelegate indexBusinessDelegate;

	private WorkflowBusinessDelegate workflowBusinessDelegate;

	private SecurityBusinessDelegate securityBusinessDelegate;

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
		specificRepoTradeBusinessDelegate = new SpecificRepoTradeBusinessDelegate();
		currencyBusinessDelegate = new CurrencyBusinessDelegate();
		bookBusinessDelegate = new BookBusinessDelegate();
		legalEntityBusinessDelegate = new LegalEntityBusinessDelegate();
		indexBusinessDelegate = new IndexBusinessDelegate();
		workflowBusinessDelegate = new WorkflowBusinessDelegate();
		securityBusinessDelegate = new SecurityBusinessDelegate();
		allCurrencies = currencyBusinessDelegate.getAllCurrencies();
		allCounterparties = legalEntityBusinessDelegate.getAllCounterparties();
		allIndexes = indexBusinessDelegate.getAllIndexes();
		allBooks = bookBusinessDelegate.getAllBooks();
		allDirections = Trade.Direction.values();
		specificRepoTrade = new SpecificRepoTrade();
		allInterestTypes = new String[] { "Fixed", "Floating" };
		allIndexTenors = Arrays.asList(Tenor.values()).stream().filter(t -> !t.equals(Tenor.NO_TENOR))
				.toArray(Tenor[]::new);
		allSecurities = securityBusinessDelegate.getAllSecurities();
		setTradeDate(LocalDate.now());
		setStartDate(LocalDate.now());
		workflow = workflowBusinessDelegate.getWorkflowByName(SpecificRepoTrade.SPECIFIC_REPO);
	}

	public String getId() {
		return specificRepoTrade.getId() == 0 ? "" : Long.toString(specificRepoTrade.getId());
	}

	public void setId(String id) {
		specificRepoTrade.setId(Long.parseLong(id));
	}

	public Direction[] getAllDirections() {
		return allDirections;
	}

	public void setAllDirections(Direction[] allDirections) {
		this.allDirections = allDirections;
	}

	public BigDecimal getAmount() {
		return specificRepoTrade.getAmount();
	}

	public void setAmount(BigDecimal amount) {
		specificRepoTrade.setAmount(amount);
	}

	public Status getStatus() {
		return specificRepoTrade.getStatus();
	}

	public void setStatus(Status status) {
		specificRepoTrade.setStatus(status);
	}

	public SpecificRepoTrade getSpecificRepoTrade() {
		return specificRepoTrade;
	}

	public void setSpecificRepoTrade(SpecificRepoTrade specificRepoTrade) {
		this.specificRepoTrade = specificRepoTrade;
	}

	public BigDecimal getRepoRate() {
		return specificRepoTrade.getRepoRate();
	}

	public void setRepoRate(BigDecimal repoRate) {
		specificRepoTrade.setRepoRate(repoRate);
	}

	public Trade.Direction getDirection() {
		return (specificRepoTrade.isBuy() ? Direction.BUY : Direction.SELL);
	}

	public void setDirection(Trade.Direction direction) {
		specificRepoTrade.setBuySell(Direction.BUY.equals(direction));
	}

	public LocalDate getTradeDate() {
		return specificRepoTrade.getTradeDate();
	}

	public void setTradeDate(LocalDate tradeDate) {
		specificRepoTrade.setTradeDate(tradeDate);
	}

	public LocalDate getStartDate() {
		return specificRepoTrade.getSettlementDate();
	}

	public void setStartDate(LocalDate startDate) {
		specificRepoTrade.setSettlementDate(startDate);
	}

	public LocalDate getEndDate() {
		return specificRepoTrade.getEndDate();
	}

	public void setEndDate(LocalDate endDate) {
		specificRepoTrade.setEndDate(endDate);
	}

	public Currency getCurrency() {
		return specificRepoTrade.getCurrency();
	}

	public void setCurrency(Currency currency) {
		specificRepoTrade.setCurrency(currency);
	}

	public LegalEntity getCounterparty() {
		return specificRepoTrade.getCounterparty();
	}

	public void setCounterparty(LegalEntity legalEntity) {
		specificRepoTrade.setCounterparty(legalEntity);
	}

	public Book getBook() {
		return specificRepoTrade.getBook();
	}

	public void setBook(Book book) {
		specificRepoTrade.setBook(book);
	}

	public Index getIndex() {
		return specificRepoTrade.getIndex();
	}

	public void setIndex(Index index) {
		specificRepoTrade.setIndex(index);
	}

	public Tenor getIndexTenor() {
		return specificRepoTrade.getIndexTenor();
	}

	public void setIndexTenor(Tenor indexTenor) {
		specificRepoTrade.setIndexTenor(indexTenor);
	}

	public BigDecimal getIndexOffset() {
		return specificRepoTrade.getIndexOffset();
	}

	public void setIndexOffset(BigDecimal indexOffset) {
		specificRepoTrade.setIndexOffset(indexOffset);
	}

	public boolean getRightOfSubstitution() {
		return specificRepoTrade.isRightOfSubstitution();
	}

	public void setRightOfSubstitution(boolean rightOfSubstitution) {
		specificRepoTrade.setRightOfSubstitution(rightOfSubstitution);
	}

	public boolean getRightOfReuse() {
		return specificRepoTrade.isRightOfReuse();
	}

	public void setRightOfReuse(boolean rightOfReuse) {
		specificRepoTrade.setRightOfReuse(rightOfReuse);
	}

	public boolean getCrossCurrencyCollateral() {
		return specificRepoTrade.isCrossCurrencyCollateral();
	}

	public void setCrossCurrencyCollateral(boolean crossCurrencyCollateral) {
		specificRepoTrade.setCrossCurrencyCollateral(crossCurrencyCollateral);
	}

	public boolean getTerminableOnDemand() {
		return specificRepoTrade.isTerminableOnDemand();
	}

	public void setTerminableOnDemand(boolean terminableOnDemand) {
		specificRepoTrade.setTerminableOnDemand(terminableOnDemand);
	}

	public Short getNoticePeriod() {
		return specificRepoTrade.getNoticePeriod();
	}

	public void setNoticePeriod(Short noticePeriod) {
		specificRepoTrade.setNoticePeriod(noticePeriod);
	}

	public BigDecimal getMarginRate() {
		return specificRepoTrade.getMarginRate();
	}

	public void setMarginRate(BigDecimal marginRate) {
		specificRepoTrade.setMarginRate(marginRate);
	}

	public Security getSecurity() {
		return specificRepoTrade.getSecurity();
	}

	public void setSecurity(Security security) {
		specificRepoTrade.setSecurity(security);
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

	public Set<Security> getAllSecurities() {
		return allSecurities;
	}

	public void setAllBaskets(Set<Security> allSecurities) {
		this.allSecurities = allSecurities;
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
			if (specificRepoTrade.getId() == 0) {
				specificRepoTrade.setCreationDate(LocalDate.now());
				specificRepoTrade.setStatus(workflowBusinessDelegate.getInitialStatus(workflow.getName()));

			}
			if (interestType == null || interestType.equals("Fixed")) {
				specificRepoTrade.setIndex(null);
				specificRepoTrade.setIndexTenor(null);
				specificRepoTrade.setIndexOffset(null);
			}
			long tradeId = specificRepoTradeBusinessDelegate.saveSpecificRepoTrade(specificRepoTrade, actionToApply);
			if (specificRepoTrade.getId() == 0) {
				specificRepoTrade.setId(tradeId);
			}
			specificRepoTrade = specificRepoTradeBusinessDelegate.getSpecificRepoTradeById(tradeId);
			Set<String> availableActions = workflowBusinessDelegate.getAvailableActionsFromStatus(workflow.getName(),
					specificRepoTrade.getStatus());
			if (availableActions != null && !availableActions.isEmpty()) {
				allAvailableActions = availableActions.toArray(new String[availableActions.size()]);
			}
			originalCashAmount = specificRepoTrade.getAmount();
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
					"Trade " + specificRepoTrade.getId() + " successfully saved"));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public void copy() {
		long oldId = specificRepoTrade.getId();
		Map<LocalDate, BigDecimal> oldPartialTerminations = specificRepoTrade.getPartialTerminations();
		Map<Security, Map<Book, BigDecimal>> oldCollateralToAdd = specificRepoTrade.getCollateralToAdd();
		Map<Security, Map<Book, BigDecimal>> oldCollateralToRemove = specificRepoTrade.getCollateralToRemove();
		try {
			specificRepoTrade.setCreationDate(LocalDate.now());
			specificRepoTrade.setId(0);
			specificRepoTrade.setStatus(workflowBusinessDelegate.getInitialStatus(workflow.getName()));
			if (interestType == null || interestType.equals("Fixed")) {
				specificRepoTrade.setIndex(null);
				specificRepoTrade.setIndexTenor(null);
				specificRepoTrade.setIndexOffset(null);
			}
			// Copied trades will not have added/removed collaterals and partial
			// terminations
			specificRepoTrade.setCollateralToAdd(null);
			specificRepoTrade.setCollateralToRemove(null);
			specificRepoTrade.setPartialTerminations(null);
			long tradeId = specificRepoTradeBusinessDelegate.saveSpecificRepoTrade(specificRepoTrade, Action.NEW);
			specificRepoTrade = specificRepoTradeBusinessDelegate.getSpecificRepoTradeById(tradeId);
			Set<String> availableActions = workflowBusinessDelegate.getAvailableActionsFromStatus(workflow.getName(),
					specificRepoTrade.getStatus());
			if (availableActions != null && !availableActions.isEmpty()) {
				allAvailableActions = availableActions.toArray(new String[availableActions.size()]);
			}
			originalCashAmount = specificRepoTrade.getAmount();
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
					"Trade " + specificRepoTrade.getId() + " successfully created"));
		} catch (TradistaBusinessException tbe) {
			specificRepoTrade.setId(oldId);
			specificRepoTrade.setCollateralToAdd(oldCollateralToAdd);
			specificRepoTrade.setCollateralToRemove(oldCollateralToRemove);
			specificRepoTrade.setPartialTerminations(oldPartialTerminations);
			FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public void load() {
		long tradeId;
		try {
			tradeId = Long.parseLong(idToBeLoaded);
			SpecificRepoTrade specialTrade = specificRepoTradeBusinessDelegate.getSpecificRepoTradeById(tradeId);
			if (specialTrade != null) {
				specificRepoTrade.setId(specialTrade.getId());
				specificRepoTrade.setBuySell(specialTrade.isBuy());
				specificRepoTrade.setCounterparty(specialTrade.getCounterparty());
				specificRepoTrade.setCurrency(specialTrade.getCurrency());
				specificRepoTrade.setProduct(specialTrade.getProduct());
				specificRepoTrade.setAmount(specialTrade.getAmount());
				specificRepoTrade.setSettlementDate(specialTrade.getSettlementDate());
				specificRepoTrade.setTradeDate(specialTrade.getTradeDate());
				specificRepoTrade.setBook(specialTrade.getBook());
				specificRepoTrade.setCreationDate(specialTrade.getCreationDate());
				specificRepoTrade.setCrossCurrencyCollateral(specialTrade.isCrossCurrencyCollateral());
				specificRepoTrade.setSecurity(specialTrade.getSecurity());
				specificRepoTrade.setEndDate(specialTrade.getEndDate());
				specificRepoTrade.setIndex(specialTrade.getIndex());
				specificRepoTrade.setIndexOffset(specialTrade.getIndexOffset());
				specificRepoTrade.setMarginRate(specialTrade.getMarginRate());
				specificRepoTrade.setNoticePeriod(specialTrade.getNoticePeriod());
				specificRepoTrade.setRepoRate(specialTrade.getRepoRate());
				specificRepoTrade.setRightOfReuse(specialTrade.isRightOfReuse());
				specificRepoTrade.setRightOfSubstitution(specialTrade.isRightOfSubstitution());
				specificRepoTrade.setTerminableOnDemand(specialTrade.isTerminableOnDemand());
				specificRepoTrade.setStatus(specialTrade.getStatus());
				specificRepoTrade.setPartialTerminations(specialTrade.getPartialTerminations());
				Set<String> availableActions = workflowBusinessDelegate
						.getAvailableActionsFromStatus(workflow.getName(), specialTrade.getStatus());
				if (availableActions != null && !availableActions.isEmpty()) {
					allAvailableActions = availableActions.toArray(new String[availableActions.size()]);
				}
				originalCashAmount = specialTrade.getAmount();
				FacesContext.getCurrentInstance().addMessage(TRADE_MSG, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Info", "Trade " + specificRepoTrade.getId() + " successfully loaded."));
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
		specificRepoTrade = new SpecificRepoTrade();
		setTradeDate(LocalDate.now());
		setStartDate(LocalDate.now());
		originalCashAmount = null;
		action = null;
		allAvailableActions = null;
		FacesContext.getCurrentInstance().addMessage(TRADE_MSG,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Form cleared"));
	}

	public void updateIndex() {
		if (interestType != null && interestType.equals("Floating")) {
			if (specificRepoTrade.getIndex() == null && allIndexes != null && !allIndexes.isEmpty()) {
				specificRepoTrade.setIndex(allIndexes.stream().findFirst().get());
			}
			if (specificRepoTrade.getIndexTenor() == null) {
				specificRepoTrade.setIndexTenor(allIndexTenors[0]);
			}
		}
		if (interestType != null && interestType.equals("Fixed")) {
			specificRepoTrade.setIndex(null);
			specificRepoTrade.setIndexTenor(null);
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
			specificRepoTrade.setCollateralToAdd(securitiesToAdd);
		}
		if (securitiesToRemove != null && !securitiesToRemove.isEmpty()) {
			specificRepoTrade.setCollateralToRemove(securitiesToRemove);
		}
		if (action != null && action.equals(ActionConstants.PARTIALLY_TERMINATE)) {
			specificRepoTrade.addParTialTermination(LocalDate.now(),
					originalCashAmount.subtract(specificRepoTrade.getAmount()));
		}
	}

}