package finance.tradista.security.gcrepo.workflow.mapping;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.tenor.model.Tenor;
import finance.tradista.core.workflow.model.mapping.StatusMapper;
import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.model.WorkflowObject;

/*
 * Copyright 2023 Olivier Asuncion
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

    public void setGcRepoTrade(finance.tradista.security.gcrepo.model.GCRepoTrade gcRepoTrade) {
	this.gcRepoTrade = gcRepoTrade;
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