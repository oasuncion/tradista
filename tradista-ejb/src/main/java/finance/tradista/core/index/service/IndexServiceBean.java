package finance.tradista.core.index.service;

import java.util.Set;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.index.persistence.IndexSQL;
import jakarta.ejb.Stateless;

/*
 * Copyright 2019 Olivier Asuncion
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

@Stateless
public class IndexServiceBean implements IndexService {

	@Override
	public long saveIndex(Index index) throws TradistaBusinessException {
		if (index.getId() == 0) {
			checkIndexNameExistence(index);
		} else {
			Index oldIndex = IndexSQL.getIndexById(index.getId());
			if (!oldIndex.getName().equals(oldIndex.getName())) {
				checkIndexNameExistence(index);
			}
		}
		return IndexSQL.saveIndex(index);
	}

	private void checkIndexNameExistence(Index index) throws TradistaBusinessException {
		if (IndexSQL.getIndexByName(index.getName()) != null) {
			throw new TradistaBusinessException(
					String.format("An index named %s already exists in the system.", index.getName()));
		}
	}

	@Override
	public Set<Index> getAllIndexes() {
		return IndexSQL.getAllIndexes();
	}

	@Override
	public Index getIndexByName(String indexName) {
		return IndexSQL.getIndexByName(indexName);
	}

}