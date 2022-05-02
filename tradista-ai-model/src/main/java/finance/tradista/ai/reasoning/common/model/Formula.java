package finance.tradista.ai.reasoning.common.model;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import finance.tradista.ai.reasoning.fol.mapping.function.FunctionFactory;
import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.model.TradistaObject;
import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.Program;

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

public class Formula extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -758453850630370850L;

	private String formula;

	private Program program;

	public Formula(String formula) throws TradistaBusinessException {
		this.formula = formula;
	}

	public Formula(String formula, long id) throws TradistaBusinessException {
		this(formula);
		setId(id);
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
		formula = "";
		Set<ASPRule> rules = program.getRules();
		for (ASPRule r : rules) {
			formula += r;
		}
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
		ASPCore2Parser parser = new ASPCore2Parser(new StringReader(formula));
		InstantiateVisitor visitor = new InstantiateVisitor();
		try {
			program = visitor.visit(parser.Program(), null);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isValidated() {
		return program != null;
	}

	public Set<Function<?>> getFunctions() {
		if (formula != null && formula.contains("(")) {
			String[] splitFormula = formula.split("\\(");
			Set<Function<?>> functions = null;
			if (splitFormula != null && splitFormula.length > 0) {
				splitFormula = ArrayUtils.remove(splitFormula, splitFormula.length - 1);
				for (String f : splitFormula) {
					if (functions == null) {
						functions = new HashSet<Function<?>>(splitFormula.length);
					}
					int startIdx = f.contains(" ") ? f.lastIndexOf(" ") + 1 : 0;
					String fct = f.substring(startIdx);
					Function<?> function = null;
					try {
						function = FunctionFactory.createFunction(fct);
					} catch (TradistaBusinessException tbe) {
						// It should not appear at this stage
					}
					if (function != null) {
						functions.add(function);
					} else {
						// TODO Log function is null
					}
				}
			}
			return functions;
		} else {
			return null;
		}
	}

}