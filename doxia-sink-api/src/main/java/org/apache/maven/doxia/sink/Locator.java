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
package org.apache.maven.doxia.sink;

/**
 * Interface for associating a {@link Sink} event with a document location.
 * @since 2.0.0
 */
public interface Locator {

    /**
     * Returns the line number for the sink event (starting from 1).
     * If it is not known returns {@code -1}.
     *
     * @return the line number for the sink event
     */
    int getLineNumber();

    /**
     * Returns the column number for the sink event (starting from 1).
     * If it is not known returns {@code -1}.
     *
     * @return the column number for the sink event
     */
    int getColumnNumber();

    /**
     * Returns the underlying source reference (for file based documents a relative file path, otherwise an arbitrary string or {@code null}).
     * @return the source for the sink event (may be {@code null})
     */
    String getReference();
}
