package finance.tradista.security.gcrepo.workflow.mapping;

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

public class GCRepoTrade implements WorkflowObject {

	private finance.tradista.security.gcrepo.model.GCRepoTrade gcRepoTrade;

	private Workflow wkf;

	public GCRepoTrade(Workflow wkf) {
		this.wkf = wkf;
	}

	@Override
	public Status getStatus() {
		Status status = null;
		if (gcRepoTrade != null) {
			status = StatusMapper.map(gcRepoTrade.getStatus(), wkf);
		}
		return status;
	}

	@Override
	public String getWorkflow() {
		String wkf = null;
		if (gcRepoTrade != null) {
			wkf = gcRepoTrade.getWorkflow();
		}
		return wkf;
	}

	@Override
	public void setStatus(Status status) {
		if (gcRepoTrade != null) {
			gcRepoTrade.setStatus(StatusMapper.map(status));
		}
	}

	public long getId() {
		long id = 0;
		if (gcRepoTrade != null) {
			id = gcRepoTrade.getId();
		}
		return id;
	}

	public Book getBook() {
		Book book = null;
		if (gcRepoTrade != null) {
			book = gcRepoTrade.getBook();
		}
		return book;
	}

	public boolean isFixedRepoRate() {
		boolean isFixedRepoRate = false;
		if (gcRepoTrade != null) {
			isFixedRepoRate = gcRepoTrade.isFixedRepoRate();
		}
		return isFixedRepoRate;
	}

	public BigDecimal getRepoRate() {
		BigDecimal repoRate = null;
		if (gcRepoTrade != null) {
			repoRate = gcRepoTrade.getRepoRate();
		}
		return repoRate;
	}

	public Currency getCurrency() {
		Currency currency = null;
		if (gcRepoTrade != null) {
			currency = gcRepoTrade.getCurrency();
		}
		return currency;
	}

	public Index getIndex() {
		Index index = null;
		if (gcRepoTrade != null) {
			index = gcRepoTrade.getIndex();
		}
		return index;
	}

	public Tenor getIndexTenor() {
		Tenor tenor = null;
		if (gcRepoTrade != null) {
			tenor = gcRepoTrade.getIndexTenor();
		}
		return tenor;
	}

	public BigDecimal getIndexOffset() {
		BigDecimal indexOffset = null;
		if (gcRepoTrade != null) {
			indexOffset = gcRepoTrade.getIndexOffset();
		}
		return indexOffset;
	}

	public BigDecimal getCashAmount() {
		BigDecimal cashAmount = null;
		if (gcRepoTrade != null) {
			cashAmount = gcRepoTrade.getAmount();
		}
		return cashAmount;
	}

	public LocalDate getEndDate() {
		LocalDate endDate = null;
		if (gcRepoTrade != null) {
			endDate = gcRepoTrade.getEndDate();
		}
		return endDate;
	}

	public BigDecimal getMarginRate() {
		BigDecimal marginRate = null;
		if (gcRepoTrade != null) {
			marginRate = gcRepoTrade.getMarginRate();
		}
		return marginRate;
	}

	public Map<LocalDate, BigDecimal> getPartialTerminations() {
		Map<LocalDate, BigDecimal> partialTerminations = null;
		if (gcRepoTrade != null) {
			partialTerminations = gcRepoTrade.getPartialTerminations();
		}
		return partialTerminations;
	}

	public void setGcRepoTrade(finance.tradista.security.gcrepo.model.GCRepoTrade gcRepoTrade) {
		this.gcRepoTrade = gcRepoTrade;
	}

	public Map<Security, Map<Book, BigDecimal>> getCollateralToAdd() {
		Map<Security, Map<Book, BigDecimal>> collateralToAdd = null;
		if (gcRepoTrade != null) {
			collateralToAdd = gcRepoTrade.getCollateralToAdd();
		}
		return collateralToAdd;
	}

	public Map<Security, Map<Book, BigDecimal>> getCollateralToRemove() {
		Map<Security, Map<Book, BigDecimal>> collateralToRemove = null;
		if (gcRepoTrade != null) {
			collateralToRemove = gcRepoTrade.getCollateralToRemove();
		}
		return collateralToRemove;
	}

	public finance.tradista.security.gcrepo.model.GCRepoTrade getOriginalGCRepoTrade() {
		return TradistaModelUtil.clone(gcRepoTrade);
	}

	@Override
	public finance.tradista.flow.model.WorkflowObject clone() throws java.lang.CloneNotSupportedException {
		GCRepoTrade gcRepoTrade = (GCRepoTrade) super.clone();
		if (this.gcRepoTrade != null) {
			gcRepoTrade.gcRepoTrade = (finance.tradista.security.gcrepo.model.GCRepoTrade) this.gcRepoTrade.clone();
		}
		if (this.wkf != null) {
			gcRepoTrade.wkf = this.wkf.clone();
		}
		return gcRepoTrade;
	}

}