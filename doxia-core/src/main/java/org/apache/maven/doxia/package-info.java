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

/**
 * The Doxia API is based on {@link org.apache.maven.doxia.sink.Sink} and {@link org.apache.maven.doxia.parser.Parser} objects: its goal is to parse a given source model using a given parser, and emits Doxia events
 * into the given sink.
 * Further information about the Sink API in {@link org.apache.maven.doxia.sink} and the Parser API in {@link org.apache.maven.doxia.parser}.
 * The {@link Doxia} interface is just a high-level API to ease using a parser.
 *
 * <h2>Using Maven Doxia API</h2>
 * <pre>
 * File userDir = new File( System.getProperty ( "user.dir" ) );
 * File inputFile = new File( userDir, "test.md" );
 * File outputFile = new File( userDir, "test.html" );
 *
 * Reader source = ReaderFactory.newReader( inputFile, "UTF-8" );
 *
 * {@code @Inject @Named("html")}
 * SinkFactory sinkFactory;
 * {@code @Inject}
 * Doxia doxia
 * try (Sink sink = sinkFactory.createSink(outputFile.getParentFile(), outputFile.getName())) {
 * doxia.parse( source, "apt", sink );
 * }
 * </pre>
 * @see <a href="https://eclipse.dev/sisu/org.eclipse.sisu.inject/index.html">Eclipse Sisu (JSR 330 DI container)</a>
 * @see <a href="https://maven.apache.org/doxia/">Maven Doxia Website</a>
 */
package org.apache.maven.doxia;
