package finance.tradista.security.gcrepo.workflow.mapping;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.flow.model.Workflow;
import finance.tradista.security.repo.workflow.mapping.RepoTrade;

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

public class GCRepoTrade extends RepoTrade {

	public GCRepoTrade(Workflow wkf) {
		super(wkf);
	}

	public void setRepoTrade(finance.tradista.security.gcrepo.model.GCRepoTrade repoTrade) {
		this.repoTrade = repoTrade;
	}

	public finance.tradista.security.gcrepo.model.GCRepoTrade getOriginalRepoTrade() {
		return (finance.tradista.security.gcrepo.model.GCRepoTrade) TradistaModelUtil.clone(repoTrade);
	}

}