package finance.tradista.core.workflow.model.mapping;

import java.util.Map;
import java.util.stream.Collectors;

import finance.tradista.core.workflow.model.ConditionalAction;
import finance.tradista.flow.model.Workflow;

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

public final class ConditionalActionMapper {

	private ConditionalActionMapper() {
	}

	public static ConditionalAction map(finance.tradista.flow.model.ConditionalAction action) {
		ConditionalAction actionResult = null;
		if (action != null) {
			actionResult = new ConditionalAction();
			actionResult.setId(action.getId());
			actionResult.setName(action.getName());
			actionResult.setChoicePseudoStatus(StatusMapper.map(action.getChoicePseudoStatus()));
			if (action.getConditionalActions() != null) {
				actionResult.setConditionalActions(action.getConditionalActions().stream()
						.map(a -> SimpleActionMapper.map(a)).collect(Collectors.toSet()));
			}
			if (action.getConditionalProcesses() != null) {
				actionResult.setConditionalProcesses(action.getConditionalProcesses().entrySet().stream().collect(
						Collectors.toMap(e -> StatusMapper.map(e.getKey()), e -> ProcessMapper.map(e.getValue()))));

			}
			if (action.getConditionalRouting() != null) {
				actionResult.setConditionalRouting(action.getConditionalRouting().entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, e -> StatusMapper.map(e.getValue()))));
			}
			actionResult.setDepartureStatus(StatusMapper.map(action.getDepartureStatus()));
			actionResult.setGuard(GuardMapper.map(action.getGuard()));
			actionResult.setCondition(ConditionMapper.map(action.getCondition()));
		}
		return actionResult;
	}

	public static finance.tradista.flow.model.ConditionalAction map(ConditionalAction action, Workflow workflow) {
		finance.tradista.flow.model.ConditionalAction actionResult = null;
		if (action != null) {
			actionResult = new finance.tradista.flow.model.ConditionalAction();
			actionResult.setId(action.getId());
			actionResult.setName(action.getName());
			actionResult.setChoicePseudoStatus(StatusMapper.map(action.getChoicePseudoStatus(), workflow));
			if (action.getConditionalActions() != null) {
				actionResult.setConditionalActions(action.getConditionalActions().stream()
						.map(a -> SimpleActionMapper.map(a, workflow)).collect(Collectors.toSet()));
			}
			if (action.getConditionalProcesses() != null) {
				actionResult.setConditionalProcesses(
						action.getConditionalProcesses().entrySet().stream().collect(Collectors.toMap(
								e -> StatusMapper.map(e.getKey(), workflow), e -> ProcessMapper.map(e.getValue()))));

			}
			if (action.getConditionalRouting() != null) {
				actionResult.setConditionalRouting(action.getConditionalRouting().entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, e -> StatusMapper.map(e.getValue(), workflow))));
			}
			actionResult.setDepartureStatus(StatusMapper.map(action.getDepartureStatus(), workflow));
			actionResult.setGuard(GuardMapper.map(action.getGuard()));
			actionResult.setWorkflow(workflow);
			actionResult.setCondition(ConditionMapper.map(action.getCondition()));
		}
		return actionResult;
	}

}