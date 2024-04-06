package finance.tradista.security.gcrepo.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.legalentity.model.LegalEntity;
import finance.tradista.security.gcrepo.model.AllocationConfiguration;

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

public class AllocationConfigurationBusinessDelegate {

	private AllocationConfigurationService allocationConfigurationService;

	public AllocationConfigurationBusinessDelegate() {
		allocationConfigurationService = TradistaServiceLocator.getInstance().getAllocationConfigurationService();
	}

	public long saveAllocationConfiguration(AllocationConfiguration allocationConfiguration)
			throws TradistaBusinessException {

		StringBuilder errMsg = new StringBuilder();
		if (allocationConfiguration == null) {
			throw new TradistaBusinessException("the Allocation Configuration cannot be null.");
		} else {
			if (StringUtils.isEmpty(allocationConfiguration.getName())) {
				errMsg.append(String.format("the Allocation Configuration name is mandatory.%n"));
			}
		}
		LegalEntity po = allocationConfiguration.getProcessingOrg();
		Set<Book> books = allocationConfiguration.getBooks();
		if (po != null) {
			if (books != null && !books.isEmpty()) {
				List<Book> wrongBooks = books.stream()
						.filter(b -> b.getProcessingOrg() != null && !b.getProcessingOrg().equals(po)).toList();
				if (!wrongBooks.isEmpty()) {
					errMsg.append(String.format("the following books are not from the %s Processing Org: %s.%n", po,
							wrongBooks));
				}
			}
		} else {
			errMsg.append(String.format(
					"the Allocation Configuration cannot be global, it should be linked to a Processing Org.%n"));
		}

		if (!errMsg.isEmpty()) {
			throw new TradistaBusinessException(errMsg.toString());
		}
		return SecurityUtil
				.runEx(() -> allocationConfigurationService.saveAllocationConfiguration(allocationConfiguration));
	}

	public AllocationConfiguration getAllocationConfigurationById(long id) throws TradistaBusinessException {
		if (id <= 0) {
			throw new TradistaBusinessException("the Allocation Configuration id must be positive.");
		}
		return SecurityUtil.run(() -> allocationConfigurationService.getAllocationConfigurationById(id));
	}

	public Set<AllocationConfiguration> getAllAllocationConfigurations() {
		return SecurityUtil.run(() -> allocationConfigurationService.getAllAllocationConfigurations());
	}

	public AllocationConfiguration getAllocationConfigurationByName(String name) throws TradistaBusinessException {
		if (StringUtils.isEmpty(name)) {
			throw new TradistaBusinessException("the Allocation Configuration name is mandatory.");
		}
		return SecurityUtil.run(() -> allocationConfigurationService.getAllocationConfigurationByName(name));
	}

}