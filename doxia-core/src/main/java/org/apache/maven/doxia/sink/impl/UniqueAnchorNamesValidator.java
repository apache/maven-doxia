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
package org.apache.maven.doxia.sink.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * Validates that each anchor name only appears once per document. Otherwise fails with an {@link IllegalStateException}.
 */
public class UniqueAnchorNamesValidator extends SinkWrapper {

    private final Set<String> usedAnchorNames;

    public UniqueAnchorNamesValidator(Sink sink) {
        super(sink);
        usedAnchorNames = new HashSet<>();
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        // assume that other anchor method signature calls this method under the hood in all relevant sink
        // implementations
        super.anchor(name, attributes);
        enforceUniqueAnchor(name);
    }

    private void enforceUniqueAnchor(String name) {
        if (!usedAnchorNames.add(name)) {
            throw new IllegalStateException(
                    getDocumentLocator().getLogPrefix() + "Anchor name \"" + name + "\" used more than once");
        }
    }
}
