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

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.testing.PlexusTest;
import org.junit.jupiter.api.Test;

import static org.codehaus.plexus.testing.PlexusExtension.getBasedir;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
@PlexusTest
public abstract class AbstractSinkTestCase {
    private Writer testWriter;

    // ---------------------------------------------------------------------
    // Test case
    // ----------------------------------------------------------------------

    /**
     * Parses the test apt document (obtained via {@link #getTestReader()}) with the Parser returned
     * by {@link #createParser()} into the Sink returned by {@link #createSink()}.
     *
     * @throws java.lang.Exception if anything goes wrong.
     */
    @Test
    public void testApt() throws Exception {
        Parser parser = createParser();

        parser.parse(getTestReader(), createSink());
    }

    // ----------------------------------------------------------------------
    // Abstract methods the individual SinkTests must provide
    // ----------------------------------------------------------------------

    /**
     * Return the default extension of files created by the test Sink.
     *
     * @return the extension of files created by the test Sink.
     * @see #createSink()
     */
    protected abstract String outputExtension();

    /**
     * Return a Parser for testing.
     *
     * @return a test Parser.
     */
    protected abstract Parser createParser();

    /**
     * Return a Sink for testing.
     *
     * @return a test Sink.
     * @throws java.lang.Exception if the Sink cannot be constructed.
     */
    protected abstract Sink createSink() throws Exception;

    // ----------------------------------------------------------------------
    // Methods for creating the test reader and writer
    // ----------------------------------------------------------------------

    /**
     * Returns a Writer to write a test output result. The Writer writes to a File
     * <code>"target/output/test. + extension"</code>, where extension is returned by
     * {@link #outputExtension()}, in the current base directory.
     *
     * @return a Writer to write a test output result.
     * @throws java.lang.Exception if the Writer cannot be constructed.
     */
    protected Writer getTestWriter() throws Exception {
        if (testWriter == null) {
            File outputDirectory = new File(getBasedirFile(), "target/output");

            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            testWriter = new FileWriter(new File(outputDirectory, "test." + outputExtension()));
        }

        return testWriter;
    }

    /**
     * Returns a Reader that gives access to a common test apt file.
     *
     * @return a Reader to access the test apt resource file.
     */
    protected Reader getTestReader() {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.apt");

        return new InputStreamReader(Objects.requireNonNull(is));
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    /**
     * Return the current base diretory as a File.
     *
     * @return the current base diretory as a File.
     */
    public File getBasedirFile() {
        return new File(getBasedir());
    }
}
