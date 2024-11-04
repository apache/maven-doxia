/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.doxia.macro;

import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.sink.Sink;

/**
 * Interface implemented by all parsers that support macros and which can be used to overwrite macro execution via {@link Parser#setMacroExecutor(MacroExecutor)}.
 */
public interface MacroExecutor {

    /**
     * Execute a macro on the given sink.
     *
     * @param macroId an id to lookup the macro
     * @param request the corresponding MacroRequest
     * @param sink the sink to receive the events
     * @throws org.apache.maven.doxia.macro.MacroExecutionException if an error occurred during execution
     * @throws org.apache.maven.doxia.macro.manager.MacroNotFoundException if the macro could not be found
     */
    void executeMacro(String macroId, MacroRequest request, Sink sink)
            throws MacroExecutionException, MacroNotFoundException;

}