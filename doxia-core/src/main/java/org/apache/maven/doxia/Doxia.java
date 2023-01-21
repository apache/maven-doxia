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
package org.apache.maven.doxia;

import java.io.Reader;

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.sink.Sink;

/**
 * Basic interface of the Doxia framework.
 *
 * @author Jason van Zyl
 * @since 1.0
 */
public interface Doxia {

    /**
     * Parses the given source model using a parser with given id,
     * and emits Doxia events into the given sink.
     *
     * @param source not null reader that provides the source document
     * @param parserId identifier for the parser to use
     * @param sink a sink that consumes the Doxia events
     * @throws ParserNotFoundException if no parser could be found for the given id
     * @throws ParseException if the model could not be parsed
     */
    void parse(Reader source, String parserId, Sink sink) throws ParserNotFoundException, ParseException;

    /**
     * Parses the given source model using a parser with given id,
     * and emits Doxia events into the given sink.
     *
     * @param source not null reader that provides the source document
     * @param parserId identifier for the parser to use
     * @param sink a sink that consumes the Doxia events
     * @param reference string containing the reference to the source (e.g. filename)
     * @throws ParserNotFoundException if no parser could be found for the given id
     * @throws ParseException if the model could not be parsed
     */
    void parse(Reader source, String parserId, Sink sink, String reference)
            throws ParserNotFoundException, ParseException;

    /**
     * Return a parser for the given <code>parserId</code>.
     *
     * @param parserId identifier for the parser to use
     * @return the parser identified by parserId
     * @throws ParserNotFoundException if no parser could be found for the given id
     */
    Parser getParser(String parserId) throws ParserNotFoundException;
}
