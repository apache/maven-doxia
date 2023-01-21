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

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;

/**
 * A simple macro that prints out the key and value of some supplied parameters.
 */
@Singleton
@Named("echo")
public class EchoMacro extends AbstractMacro {
    /** {@inheritDoc} */
    public void execute(Sink sink, MacroRequest request) {
        sink.verbatim(SinkEventAttributeSet.BOXED);

        sink.text("echo" + EOL);

        for (String key : request.getParameters().keySet()) {
            // TODO: DOXIA-242: separate or define internal params
            if ("parser".equals(key) || "sourceContent".equals(key)) {
                continue;
            }

            sink.text(key + " ---> " + request.getParameter(key) + EOL);
        }

        sink.verbatim_();
    }
}
