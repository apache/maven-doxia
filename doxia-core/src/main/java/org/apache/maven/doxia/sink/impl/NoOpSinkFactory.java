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

import javax.inject.Named;
import javax.inject.Singleton;

import java.io.File;
import java.io.OutputStream;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;

/**
 * NoOp implementation of the Sink factory.
 */
@Singleton
@Named("noop")
public final class NoOpSinkFactory implements SinkFactory {
    Sink createSink() {
        return new SinkAdapter();
    }

    @Override
    public Sink createSink(File ignoredOutputDir, String ignoredOutputName) {
        return createSink();
    }

    @Override
    public Sink createSink(File ignoredOutputDir, String ignoredOutputName, String ignoredEncoding) {
        return createSink();
    }

    @Override
    public Sink createSink(OutputStream ignoredOut) {
        return createSink();
    }

    @Override
    public Sink createSink(OutputStream ignoredOut, String ignoredEncoding) {
        return createSink();
    }
}
