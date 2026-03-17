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
 * The Doxia API is based on {@link Sink} objects: each Sink consumes Doxia events to produce a resultant output format (in most cases XHTML).
 * Consumers just create a Sink object from the {@link SinkFactory} for the desired format and call its methods.
 *
 * Providers implement the {@link Sink} and {@link SinkFactory} interface to write the desired output.
 * Those need to leverage JSR 330 annotations to be discoverable by the Eclipse Sisu container.
 * Consumers can then inject the desired SinkFactory using the {@code @Inject} and {@code @Named} annotation with the name of the format (e.g. "xhtml", "fml", "markdown", etc.).
 *
 * <h2>Using Maven Doxia Sink API (without a parser)</h2>
 * <pre>
 *
 * File userDir = new File( System.getProperty ( "user.dir" ) );
 * File outputFile = new File( userDir, "test.html" );
 *
 * {@code @Inject @Named("driver")}
 * SinkFactory sinkFactory;
 *
 * try (Sink sink = sinkFactory.createSink(outputFile.getParentFile(), outputFile.getName())) {
 *
 *   // Sink head
 *   sink.head();
 *
 *   sink.title();
 *   sink.text("Title");
 *   sink.title_();
 *
 *   sink.author();
 *   sink.text("Author");
 *   sink.author_();
 *
 *   sink.date();
 *   sink.text("Date");
 *   sink.date_();
 *
 *   sink.head_();
 *   // Sink head
 *
 *   sink.body();
 *
 *   sink.paragraph();
 *   sink.text("Paragraph 1, line 1.");
 *   sink.paragraph_();
 *
 *   ...
 *
 *   sink.body_();
 * }
 * </pre>
 *
 * Alternatively one can populate a Sink through a {@code org.apache.maven.doxia.parser.Parser} or the {@code org.apache.maven.doxia.Doxia} interface, which is a high-level API to ease using a parser.
 *
 * @see <a href="https://eclipse.dev/sisu/org.eclipse.sisu.inject/index.html">Eclipse Sisu (JSR 330 DI container)</a>
 * @see <a href="https://maven.apache.org/doxia/">Maven Doxia Website</a>
 *
 */
package org.apache.maven.doxia.sink;
