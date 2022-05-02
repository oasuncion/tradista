package asp4j.lang;

import java.util.Arrays;
import java.util.Objects;

/*Copyright (c) 2013, Harald Beck
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

  1. Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
  
  2. Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
  
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/

/**
 *
 * @author hbeck May 14, 2013
 */
public class AtomImpl implements Atom {

	private final String predicateSymbol;
	private final Term[] args;

	public AtomImpl(String predicateSymbol, Term... args) {
		this.predicateSymbol = predicateSymbol;
		if (args == null || args.length == 0) {
			this.args = null;
		} else {
			this.args = args;
		}
	}

	@Override
	public int arity() {
		if (args == null) {
			return 0;
		}
		return args.length;
	}

	@Override
	public Term getArg(int idx) {
		if (args == null) {
			return null;
		}
		return args[idx];
	}

	@Override
	public String symbol() {
		return predicateSymbol;
	}

	/**
	 * 
	 * @return (grounded) atom representation in standard syntax (with closing dot)
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(symbol());
		if (args != null && args.length > 0) {
			sb.append("(").append(args[0].toString());
			for (int i = 1; i < args.length; i++) {
				sb.append(",").append(args[i].toString());
			}
			sb.append(").");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.predicateSymbol);
		hash = 53 * hash + Arrays.deepHashCode(this.args);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Atom)) {
			return false;
		}
		final Atom other = (Atom) obj;
		if (!Objects.equals(this.symbol(), other.symbol())) {
			return false;
		}
		if (this.arity() != other.arity()) {
			return false;
		}
		if (this.args != null) {
			for (int i = 0; i < args.length; i++) {
				if (!this.getArg(i).equals(other.getArg(i))) {
					return false;
				}
			}
		}
		return true;
	}
}
