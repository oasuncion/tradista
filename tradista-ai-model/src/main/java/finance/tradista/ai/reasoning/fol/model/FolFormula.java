package finance.tradista.ai.reasoning.fol.model;

import java.util.HashSet;
import java.util.Set;

import finance.tradista.ai.reasoning.common.model.Function;
import finance.tradista.ai.reasoning.fol.mapping.function.FunctionFactory;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.model.TradistaObject;
import net.sf.tweety.logics.commons.syntax.Functor;

/*
 * Copyright 2017 Olivier Asuncion
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

public class FolFormula extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5763667264058331995L;

	private String formula;

	private net.sf.tweety.logics.fol.syntax.FolFormula folFormula;

	public FolFormula(String formula) throws TradistaBusinessException {
		this.formula = formula;
	}

	public FolFormula(String formula, long id) throws TradistaBusinessException {
		this(formula);
		setId(id);
	}

	public net.sf.tweety.logics.fol.syntax.FolFormula getFolFormula() {
		if (folFormula == null) {
			return null;
		}
		return folFormula.clone();
	}

	public void setFolFormula(net.sf.tweety.logics.fol.syntax.FolFormula folFormula) {
		this.folFormula = folFormula;
		formula = folFormula.toString();
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
		folFormula = null;
	}

	public boolean isValidated() {
		return folFormula != null;
	}

	public Set<Function<?>> getFunctions() {
		Set<Functor> functors = folFormula.getFunctors();
		Set<Function<?>> functions = null;
		if (functors != null && !functors.isEmpty()) {
			for (Functor functor : functors) {
				if (functions == null) {
					functions = new HashSet<Function<?>>(functors.size());
				}
				Function<?> function = null;
				try {
					function = FunctionFactory.createFunction(functor.getName());
				} catch (TradistaBusinessException abe) {
					// It should not appear at this stage
				}
				functions.add(function);
			}
		}
		return functions;
	}

	@Override
	public FolFormula clone() {
		FolFormula folFormula = (FolFormula) super.clone();
		if (this.folFormula != null) {
			folFormula.folFormula = this.folFormula.clone();
		}
		return folFormula;
	}

}