package asp4j.solver.object;

import java.util.Arrays;
import java.util.Collection;
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
 * @author hbeck Jun 1, 2013
 */
public class Filter {

    private Set<Class<?>> classes;

    public Filter() {
        this.classes = new HashSet<>();
    }

    public Filter(Class<?> clazz) {
        this.classes = new HashSet<>();
        if (clazz != null) {
            this.classes.add(clazz);
        }
    }

    public Filter(Class<?>... classes) {
        this.classes = new HashSet<>();
        if (classes != null && classes.length>0 ) {
            this.classes.addAll(Arrays.asList(classes));
        }
    }
    
    public Filter(Collection<Class<?>> classes) {        
        this.classes = new HashSet<>();
        if (classes!=null) {
            this.classes.addAll(classes);
        }
    }    

    /**
     * test whether given Class is accepted by this filter
     *
     * @param clazz
     * @return
     */
    public boolean accepts(Class<?> clazz) {
        if (clazz==null) {
            return false;
        }
        return classes.contains(clazz);
    }

    public Filter add(Class<?> clazz) {
        this.classes.add(clazz);
        return this;
    }
    
    public Filter addAll(Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            add(clazz);
        }
        return this;
    }

    public Filter remove(Class<?> clazz) {
        this.classes.remove(clazz);
        return this;
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }

    public void setClasses(Set<Class<?>> classes) {
        this.classes = classes;
        if (this.classes==null) {
            this.classes = new HashSet<>();
        }
    }
}
