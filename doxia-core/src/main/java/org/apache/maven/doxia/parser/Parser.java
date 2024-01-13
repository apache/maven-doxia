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
package org.apache.maven.doxia.parser;

import java.io.Reader;
import java.util.List;

import org.apache.maven.doxia.index.IndexingSink;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkWrapperFactory;

/**
 * A Parser is responsible for parsing any document in a supported front-end
 * format, and emitting the standard Doxia events, which can then be consumed
 * by any Doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
public interface Parser {

    /** Unknown parser type */
    int UNKNOWN_TYPE = 0;

    /** Text parser type */
    int TXT_TYPE = 1;

    /** XML parser type */
    int XML_TYPE = 2;

    /**
     * Parses the given source model and emits Doxia events into the given sink.
     *
     * @param source not null reader that provides the source document.
     * You could use <code>newReader</code> methods from {@link org.codehaus.plexus.util.ReaderFactory}.
     * @param sink A sink that consumes the Doxia events.
     * @throws org.apache.maven.doxia.parser.ParseException if the model could not be parsed.
     */
    void parse(Reader source, Sink sink) throws ParseException;

    /**
     * Parses the given source model and emits Doxia events into the given sink.
     *
     * @param source not null reader that provides the source document.
     * You could use <code>newReader</code> methods from {@link org.codehaus.plexus.util.ReaderFactory}.
     * @param sink A sink that consumes the Doxia events.
     * @param reference the reference
     * @throws org.apache.maven.doxia.parser.ParseException if the model could not be parsed.
     */
    void parse(Reader source, Sink sink, String reference) throws ParseException;

    /**
     * The parser type value could be {@link #UNKNOWN_TYPE}, {@link #TXT_TYPE} or
     * {@link #XML_TYPE}.
     *
     * @return the type of Parser
     */
    int getType();

    /**
     * When comments are found in source markup, emit comment Doxia events or just ignore?
     *
     * @param emitComments <code>true</code> (default value) to emit comment Doxia events
     */
    void setEmitComments(boolean emitComments);

    /**
     * Does the parser emit Doxia comments event when comments found in source?
     *
     * @return <code>true</code> (default value) if comment Doxia events are emitted
     */
    boolean isEmitComments();

    /**
     * Registers a given {@link SinkWrapperFactory} with the parser used in subsequent calls of {@code parse(...)}
     * @param factory the factory to create the sink wrapper
     * @since 2.0.0
     */
    void addSinkWrapperFactory(SinkWrapperFactory factory);

    /**
     * Returns all sink wrapper factories (both registered automatically and manually). The collection is ordered in a way that
     * the factories having the lowest priority come first (i.e. in reverse order).
     * @return all sink wrapper factories in the reverse order
     * @since 2.0.0
     */
    List<SinkWrapperFactory> getSinkWrapperFactories();

    /**
     * Determines whether to automatically generate anchors for each index entry found by {@link IndexingSink} or not.
     * By default no anchors are generated.
     *
     * @param emitAnchors {@code true} to emit anchors otherwise {@code false} (the default)
     * @since 2.0.0
     */
    void setEmitAnchorsForIndexableEntries(boolean emitAnchors);

    /**
     * Returns whether anchors are automatically generated for each index entry found by {@link IndexingSink} or not.
     * @return  {@code true} if anchors are emitted otherwise {@code false}
     * @since 2.0.0
     */
    boolean isEmitAnchorsForIndexableEntries();
}
