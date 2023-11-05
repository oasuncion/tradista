package finance.tradista.core.workflow.model.mapping;

import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.workflow.model.Guard;

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

public final class GuardMapper {

    public static Guard map(finance.tradista.flow.model.Guard guard) {
	Guard guardResult = null;
	if (guard != null) {
	    guardResult = new Guard();
	    guardResult.setId(guard.getId());
	    guardResult.setName(guard.getName());
	    guardResult.setLongName(guard.getClass().getName());
	}
	return guardResult;
    }

    public static finance.tradista.flow.model.Guard map(Guard guard) {
	finance.tradista.flow.model.Guard guardResult = null;
	if (guard != null) {
	    guardResult = TradistaModelUtil.getInstance(finance.tradista.flow.model.Guard.class, guard.getLongName());
	    guardResult.setId(guard.getId());
	}
	return guardResult;
    }

}
