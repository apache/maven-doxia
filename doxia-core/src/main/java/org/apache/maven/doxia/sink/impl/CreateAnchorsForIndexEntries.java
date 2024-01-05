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

import org.apache.maven.doxia.index.IndexEntry;
import org.apache.maven.doxia.index.IndexingSink;
import org.apache.maven.doxia.macro.toc.TocMacro;
import org.apache.maven.doxia.sink.Sink;

/**
 * Sink wrapper which emits anchors for each entry detected by the underlying {@link IndexingSink}.
 * It only creates an anchor if there is no accompanying anchor detected for the according entry.
 * @see TocMacro
 */
public class CreateAnchorsForIndexEntries extends IndexingSink {

    public CreateAnchorsForIndexEntries(Sink delegate) {
        super(delegate);
    }

    @Override
    protected void onIndexEntry(IndexEntry entry) {
        if (!entry.hasAnchor()) {
            getWrappedSink().anchor(entry.getId());
            getWrappedSink().anchor_();
        }
    }
}
