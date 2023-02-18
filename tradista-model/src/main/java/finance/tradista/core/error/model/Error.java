package finance.tradista.core.error.model;

import java.time.LocalDateTime;

import finance.tradista.core.common.model.TradistaObject;

/*
 * Copyright 2016 Olivier Asuncion
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
/**
 * Business error viewable by the user.
 * @author OA
 *
 */
public abstract class Error extends TradistaObject {
	
	private LocalDateTime errorDate;
	
	private LocalDateTime solvingDate;

	private String message;
	
	private String type;
	
	private Status status;
	
	public static enum Status {
		SOLVED, UNSOLVED;
		public String toString() {
			switch (this) {
			case SOLVED:
				return "Solved";
			case UNSOLVED:
				return "Unsolved";
			}
			return super.toString();
		}

		public static Status getStatus(String displayValue) {
			switch (displayValue) {
			case "Solved":
				return SOLVED;
			case "Unsolved":
				return UNSOLVED;
			}
			return null;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2187943264623829665L;
	
	public Error() {
		status = Status.UNSOLVED;
	}	
	
	public LocalDateTime getSolvingDate() {
		return solvingDate;
	}

	public void setSolvingDate(LocalDateTime solvingDate) {
		this.solvingDate = solvingDate;
	}
	
	public String getType() {
		return type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public LocalDateTime getErrorDate() {
		return errorDate;
	}

	public void setErrorDate(LocalDateTime errorDate) {
		this.errorDate = errorDate;
	}
	
	/**
	 * Key used to identify the error subject. Using this key, the system should be able to automatically change the status of a solved error 
	 * @return the identifier of the error subject
	 */
	public abstract String getSubjectKey();

}
