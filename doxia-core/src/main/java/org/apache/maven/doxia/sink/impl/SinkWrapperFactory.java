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

import org.apache.maven.doxia.sink.Sink;

/**
 * A factory for a sink wrapping another sink.
 * The delegate may either be the original sink or another wrapper.
 * @since 2.0.0
 */
public interface SinkWrapperFactory {

    /**
     * By default all wrappers just delegate each method to the wrapped sink's method.
     * For certain Sink methods the wrapper may modify the sink before/after or instead of calling the delegate's method.
     * @param sink the delegate
     * @return a new sink wrapping the given one
     */
    Sink createWrapper(Sink sink);

    /**
     * Determines the order of sink wrappers. The wrapper factory with the highest priority is called first.
     * @return the priority of this factory
     */
    int getPriority();
}
