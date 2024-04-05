package finance.tradista.security.gcrepo.ui.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.primefaces.model.DualListModel;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.book.service.BookBusinessDelegate;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.security.gcrepo.model.AllocationConfiguration;
import finance.tradista.security.gcrepo.service.AllocationConfigurationBusinessDelegate;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/*
 * Copyright 2024 Olivier Asuncion
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
public class AllocationConfigurationController implements Serializable {

	private static final long serialVersionUID = -1777343124539352925L;

	private AllocationConfiguration allocationConfiguration;

	private String loadingCriterion;

	private String idOrName;

	private String copyAllocationConfigurationName;

	private String[] allLoadingCriteria = { "Id", "Name" };

	private DualListModel<Book> books;

	private AllocationConfigurationBusinessDelegate allocationConfigurationBusinessDelegate;

	private BookBusinessDelegate bookBusinessDelegate;

	private List<Book> availableBooks;

	@PostConstruct
	public void init() {
		allocationConfigurationBusinessDelegate = new AllocationConfigurationBusinessDelegate();
		bookBusinessDelegate = new BookBusinessDelegate();
		allocationConfiguration = new AllocationConfiguration();
		availableBooks = new ArrayList<>();
		Set<Book> books = bookBusinessDelegate.getAllBooks();
		if (books != null) {
			availableBooks.addAll(books);
		}
		initModel();
	}

	private void initModel() {
		books = new DualListModel<>(availableBooks, new ArrayList<>());
	}

	public String getLoadingCriterion() {
		return loadingCriterion;
	}

	public void setLoadingCriterion(String loadingCriterion) {
		this.loadingCriterion = loadingCriterion;
	}

	public AllocationConfiguration getAllocationConfiguration() {
		return allocationConfiguration;
	}

	public void setAllocationConfiguration(AllocationConfiguration allocationConfiguration) {
		this.allocationConfiguration = allocationConfiguration;
	}

	public String[] getAllLoadingCriteria() {
		return allLoadingCriteria;
	}

	public void setAllLoadingCriteria(String[] allLoadingCriteria) {
		this.allLoadingCriteria = allLoadingCriteria;
	}

	public DualListModel<Book> getBooks() {
		return books;
	}

	public void setBooks(DualListModel<Book> books) {
		this.books = books;
	}

	public String getIdOrName() {
		return idOrName;
	}

	public void setIdOrName(String idOrName) {
		this.idOrName = idOrName;
	}

	public String getCopyAllocationConfigurationName() {
		return copyAllocationConfigurationName;
	}

	public void setCopyAllocationConfigurationName(String copyAllocationConfigurationName) {
		this.copyAllocationConfigurationName = copyAllocationConfigurationName;
	}

	public void save() {
		try {
			Set<Book> bookSet = null;
			if (books.getTarget() != null && !books.getTarget().isEmpty()) {
				bookSet = new HashSet<>(books.getTarget());
			}
			allocationConfiguration.setBooks(bookSet);
			long allocConfigId = allocationConfigurationBusinessDelegate
					.saveAllocationConfiguration(allocationConfiguration);
			if (allocationConfiguration.getId() == 0) {
				allocationConfiguration.setId(allocConfigId);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
					"Allocation Configuration " + allocationConfiguration.getId() + " successfully saved"));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}
	}

	public void copy() {
		if (copyAllocationConfigurationName.equals(allocationConfiguration.getName())) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"The name of the copied Allocation Configuration must be different than the original one."));
			return;
		}
		try {
			AllocationConfiguration copyAllocationConfiguration = new AllocationConfiguration();
			copyAllocationConfiguration.setName(copyAllocationConfigurationName);
			Set<Book> bookSet = null;
			if (books.getTarget() != null && !books.getTarget().isEmpty()) {
				bookSet = new HashSet<>(books.getTarget());
			}
			copyAllocationConfiguration.setBooks(bookSet);
			long allocConfigId = allocationConfigurationBusinessDelegate
					.saveAllocationConfiguration(copyAllocationConfiguration);
			copyAllocationConfiguration.setId(allocConfigId);
			allocationConfiguration.setId(copyAllocationConfiguration.getId());
			allocationConfiguration.setName(copyAllocationConfiguration.getName());
			allocationConfiguration.setBooks(copyAllocationConfiguration.getBooks());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
					"Allocation Configuration " + allocationConfiguration.getId() + " successfully created"));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		} finally {
			copyAllocationConfigurationName = null;
		}
	}

	public void load() {
		AllocationConfiguration allocationConfiguration;
		try {
			if (loadingCriterion.equals("Id")) {
				allocationConfiguration = allocationConfigurationBusinessDelegate
						.getAllocationConfigurationById(Long.parseLong(idOrName));
			} else {
				allocationConfiguration = allocationConfigurationBusinessDelegate
						.getAllocationConfigurationByName(idOrName);
			}
			if (allocationConfiguration != null) {
				this.allocationConfiguration.setId(allocationConfiguration.getId());
				this.allocationConfiguration.setName(allocationConfiguration.getName());
				this.allocationConfiguration.setBooks(allocationConfiguration.getBooks());
				List<Book> allocConfigBooks = new ArrayList<>();
				if (allocationConfiguration.getBooks() != null) {
					allocConfigBooks = new ArrayList<>(allocationConfiguration.getBooks());
					final List<Book> tmpAllocConfigBooks = allocConfigBooks;
					books.setSource(books.getSource().stream().filter(s -> !tmpAllocConfigBooks.contains(s)).toList());
				} else {
					books.setSource(availableBooks);
				}
				books.setTarget(allocConfigBooks);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
						"Allocation Configuration " + allocationConfiguration.getId() + " successfully loaded."));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error", "Allocation Configuration " + idOrName + " was not found."));
			}

		} catch (NumberFormatException nfe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please type a valid id."));
		} catch (TradistaBusinessException tbe) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", tbe.getMessage()));
		}

	}

	public void clear() {
		allocationConfiguration = new AllocationConfiguration();
		initModel();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Form cleared"));
	}

}