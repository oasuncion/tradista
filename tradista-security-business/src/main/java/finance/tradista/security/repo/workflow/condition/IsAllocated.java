package finance.tradista.security.repo.workflow.condition;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.flow.model.Condition;
import finance.tradista.security.repo.pricer.RepoPricerUtil;
import finance.tradista.security.repo.workflow.mapping.RepoTrade;
import jakarta.persistence.Entity;

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

@Entity
public class IsAllocated extends Condition<RepoTrade> {

	private static final long serialVersionUID = -1790346124051863865L;

	public IsAllocated() {
		setFunction(trade -> {
			// Calculate the total MTM value of the collateral
			BigDecimal mtm = RepoPricerUtil.getCurrentCollateralMarketToMarket(trade.getOriginalRepoTrade());

			if (trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty()) {
				mtm = mtm.add(RepoPricerUtil.getCollateralMarketToMarket(trade.getCollateralToAdd(),
						trade.getBook().getProcessingOrg(), LocalDate.now()));
			}

			// Calculate the exposure (required collateral)
			BigDecimal exposure = RepoPricerUtil.getCurrentExposure(trade.getOriginalRepoTrade());

			// Compare the collateral value and the required collateral

			return exposure.compareTo(mtm) != -1 ? 1 : 2;

		});
	}

}