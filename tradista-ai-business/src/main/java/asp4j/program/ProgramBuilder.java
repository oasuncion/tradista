package asp4j.program;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
 * @author hbeck May 20, 2013
 */
public class ProgramBuilder<T> {

	private Set<T> input;
	private Set<File> files;

	public ProgramBuilder() {
		this.input = new HashSet<>();
		this.files = new HashSet<>();
	}

	public ProgramBuilder(Program<T> program) {
		this.input = new HashSet<>(program.getInput());
		this.files = new HashSet<>(program.getFiles());
	}

	public Program<T> build() {
		return new Program<T>() {
			private final Collection<T> programInput = Collections.unmodifiableCollection(input);
			private final Collection<File> programFiles = Collections.unmodifiableCollection(files);

			@Override
			public Collection<T> getInput() {
				return programInput;
			}

			@Override
			public Collection<File> getFiles() {
				return programFiles;
			}
		};
	}

	public ProgramBuilder<T> add(T t) {
		this.input.add(t);
		return this;
	}

	public ProgramBuilder<T> add(File file) {
		this.files.add(file);
		return this;
	}

	public ProgramBuilder<T> addInputs(Collection<T> input) {
		this.input.addAll(input);
		return this;
	}

	public ProgramBuilder<T> addFiles(Collection<File> files) {
		this.files.addAll(files);
		return this;
	}

	public ProgramBuilder<T> remove(T t) {
		input.remove(t);
		return this;
	}

	public ProgramBuilder<T> remove(File file) {
		files.remove(file);
		return this;
	}
}
