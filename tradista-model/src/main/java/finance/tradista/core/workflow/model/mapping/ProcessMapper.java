package finance.tradista.core.workflow.model.mapping;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.workflow.model.Process;

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

public final class ProcessMapper {

    public static Process map(finance.tradista.flow.model.Process process) {
	Process processResult = null;
	if (process != null) {
	    processResult = new Process();
	    processResult.setId(process.getId());
	    processResult.setName(process.getName());
	    processResult.setLongName(process.getClass().getName());
	}
	return processResult;
    }

    public static finance.tradista.flow.model.Process map(Process process) {
	finance.tradista.flow.model.Process processResult = null;
	if (process != null) {
	    processResult = TradistaModelUtil.getInstance(finance.tradista.flow.model.Process.class,
		    process.getLongName());
	    processResult.setId(process.getId());
	}
	return processResult;
    }

}