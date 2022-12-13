package finance.tradista.core.index.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.servicelocator.TradistaServiceLocator;
import finance.tradista.core.common.util.SecurityUtil;
import finance.tradista.core.index.model.Index;

/*
 * Copyright 2018 Olivier Asuncion
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

public class IndexBusinessDelegate {

	private IndexService indexService;

	public IndexBusinessDelegate() {
		indexService = TradistaServiceLocator.getInstance().getIndexService();
	}

	public Index getIndexByName(String indexName) throws TradistaBusinessException {
		if (StringUtils.isEmpty(indexName)) {
			throw new TradistaBusinessException("The index name cannot be null.");
		}
		return SecurityUtil.run(() -> indexService.getIndexByName(indexName));
	}

	public Set<Index> getAllIndexes() {
		return SecurityUtil.run(() -> indexService.getAllIndexes());
	}

	public long saveIndex(Index index) throws TradistaBusinessException {
		if (StringUtils.isBlank(index.getName())) {
			throw new TradistaBusinessException("The name cannot be empty.");
		}
		return SecurityUtil.runEx(() -> indexService.saveIndex(index));
	}

}