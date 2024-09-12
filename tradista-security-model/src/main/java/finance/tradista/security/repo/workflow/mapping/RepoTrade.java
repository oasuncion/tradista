package finance.tradista.security.repo.workflow.mapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.currency.model.Currency;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.workflow.model.mapping.StatusMapper;
import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.model.WorkflowObject;
import finance.tradista.security.common.model.Security;

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

public abstract class RepoTrade implements WorkflowObject {

	protected finance.tradista.security.repo.model.RepoTrade repoTrade;

	private Workflow wkf;

	public RepoTrade(Workflow wkf) {
		this.wkf = wkf;
	}

	@Override
	public Status getStatus() {
		Status status = null;
		if (repoTrade != null) {
			status = StatusMapper.map(repoTrade.getStatus(), wkf);
		}
		return status;
	}

	@Override
	public String getWorkflow() {
		String wkf = null;
		if (repoTrade != null) {
			wkf = repoTrade.getWorkflow();
		}
		return wkf;
	}

	@Override
	public void setStatus(Status status) {
		if (repoTrade != null) {
			repoTrade.setStatus(StatusMapper.map(status));
		}
	}

	public long getId() {
		long id = 0;
		if (repoTrade != null) {
			id = repoTrade.getId();
		}
		return id;
	}

	public Book getBook() {
		Book book = null;
		if (repoTrade != null) {
			book = repoTrade.getBook();
		}
		return book;
	}

	public boolean isFixedRepoRate() {
		boolean isFixedRepoRate = false;
		if (repoTrade != null) {
			isFixedRepoRate = repoTrade.isFixedRepoRate();
		}
		return isFixedRepoRate;
	}

	public BigDecimal getRepoRate() {
		BigDecimal repoRate = null;
		if (repoTrade != null) {
			repoRate = repoTrade.getRepoRate();
		}
		return repoRate;
	}

	public Currency getCurrency() {
		Currency currency = null;
		if (repoTrade != null) {
			currency = repoTrade.getCurrency();
		}
		return currency;
	}

	public Index getIndex() {
		Index index = null;
		if (repoTrade != null) {
			index = repoTrade.getIndex();
		}
		return index;
	}

	public Tenor getIndexTenor() {
		Tenor tenor = null;
		if (repoTrade != null) {
			tenor = repoTrade.getIndexTenor();
		}
		return tenor;
	}

	public BigDecimal getIndexOffset() {
		BigDecimal indexOffset = null;
		if (repoTrade != null) {
			indexOffset = repoTrade.getIndexOffset();
		}
		return indexOffset;
	}

	public BigDecimal getCashAmount() {
		BigDecimal cashAmount = null;
		if (repoTrade != null) {
			cashAmount = repoTrade.getAmount();
		}
		return cashAmount;
	}

	public LocalDate getEndDate() {
		LocalDate endDate = null;
		if (repoTrade != null) {
			endDate = repoTrade.getEndDate();
		}
		return endDate;
	}

	public BigDecimal getMarginRate() {
		BigDecimal marginRate = null;
		if (repoTrade != null) {
			marginRate = repoTrade.getMarginRate();
		}
		return marginRate;
	}

	public Map<LocalDate, BigDecimal> getPartialTerminations() {
		Map<LocalDate, BigDecimal> partialTerminations = null;
		if (repoTrade != null) {
			partialTerminations = repoTrade.getPartialTerminations();
		}
		return partialTerminations;
	}

	public Map<Security, Map<Book, BigDecimal>> getCollateralToAdd() {
		Map<Security, Map<Book, BigDecimal>> collateralToAdd = null;
		if (repoTrade != null) {
			collateralToAdd = repoTrade.getCollateralToAdd();
		}
		return collateralToAdd;
	}

	public Map<Security, Map<Book, BigDecimal>> getCollateralToRemove() {
		Map<Security, Map<Book, BigDecimal>> collateralToRemove = null;
		if (repoTrade != null) {
			collateralToRemove = repoTrade.getCollateralToRemove();
		}
		return collateralToRemove;
	}

	public boolean isRightOfSubstitution() {
		boolean isRightOfSubstitution = false;
		;
		if (repoTrade != null) {
			isRightOfSubstitution = repoTrade.isRightOfSubstitution();
		}
		return isRightOfSubstitution;
	}

	@Override
	public finance.tradista.flow.model.WorkflowObject clone() throws java.lang.CloneNotSupportedException {
		RepoTrade repoTrade = (RepoTrade) super.clone();
		if (this.repoTrade != null) {
			repoTrade.repoTrade = (finance.tradista.security.repo.model.RepoTrade) this.repoTrade.clone();
		}
		if (this.wkf != null) {
			repoTrade.wkf = this.wkf.clone();
		}
		return repoTrade;
	}

	public finance.tradista.security.repo.model.RepoTrade getOriginalRepoTrade() {
		return (finance.tradista.security.repo.model.RepoTrade) TradistaModelUtil.clone(repoTrade);
	}

}