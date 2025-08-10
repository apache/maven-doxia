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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.maven.doxia.markup.Markup;
import org.codehaus.plexus.testing.PlexusTest;
import org.codehaus.plexus.util.xml.XmlStreamWriter;

import static org.codehaus.plexus.testing.PlexusExtension.getBasedir;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Provide some common convenience methods to test Doxia modules (parsers and sinks).
 *
 * @since 1.0
 */
@PlexusTest
public abstract class AbstractModuleTest implements Markup {

    /*
     * Set the system properties:
     * <ul>
     * <li><code>line.separator</code> to <code>\n</code> (Unix) to prevent
     * failure on windows.</li>
     * </ul>
     */
    static {
        // Safety
        System.setProperty("line.separator", "\n");
    }

    // ----------------------------------------------------------------------
    // Methods for creating test reader and writer
    // ----------------------------------------------------------------------

    /**
     * Returns a FileWriter to write to a file with the given name
     * in the test target output directory.
     *
     * @param baseName The name of the target file.
     * @param extension The file extension of the file to write.
     * @return A FileWriter.
     * @throws IOException If the FileWriter could not be generated.
     */
    protected Writer getTestWriter(String baseName, String extension) throws IOException {
        File outputFile = getTestWriterFile(baseName, extension);

        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        return Files.newBufferedWriter(outputFile.toPath(), StandardCharsets.UTF_8);
    }

    protected File getTestWriterFile(String baseName, String extension) {
        File outputDirectory = new File(getBasedirFile(), outputBaseDir() + getOutputDir());
        return new File(outputDirectory, baseName + '.' + extension);
    }

    /**
     * Returns an XML FileWriter to write to a file with the given name
     * in the test target output directory.
     *
     * @param baseName The name of the target file.
     * @return An XML FileWriter.
     * @throws IOException If the FileWriter could not be generated.
     * @see #getXmlTestWriter(String, String)
     */
    protected Writer getXmlTestWriter(String baseName) throws IOException {
        return getXmlTestWriter(baseName, outputExtension());
    }

    protected static String normalizeLineEnds(String s) {
        if (s != null) {
            return s.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
        } else {
            return null;
        }
    }

    /**
     * Returns an XML FileWriter to write to a file with the given name
     * in the test target output directory.
     *
     * @param baseName The name of the target file.
     * @param extension The file extension of the file to write.
     * @return An XML FileWriter.
     * @throws IOException If the FileWriter could not be generated.
     */
    protected Writer getXmlTestWriter(String baseName, String extension) throws IOException {
        File outputFile = getTestWriterFile(baseName, extension);

        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        return new XmlStreamWriter(outputFile);
    }

    /**
     * Returns a FileWriter to write to a file with the given name
     * in the test target output directory.
     *
     * @param baseName The name of the target file.
     * @return A FileWriter.
     * @throws IOException If the FileWriter could not be generated.
     * @see #getTestWriter(String, String)
     */
    protected Writer getTestWriter(String baseName) throws IOException {
        return getTestWriter(baseName, outputExtension());
    }

    protected File getTestWriterFile(String baseName) {
        return getTestWriterFile(baseName, outputExtension());
    }

    /**
     * Returns an InputStreamReader to read a resource from a file
     * in the test target output directory.
     *
     * @param baseName The name of the resource file to read.
     * @param extension The file extension of the resource file to read.
     * @return An InputStreamReader.
     */
    protected Reader getTestReader(String baseName, String extension) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(baseName + "." + extension);

        assertNotNull(is, "Could not find resource: " + baseName + "." + extension);

        return new InputStreamReader(is, StandardCharsets.UTF_8);
    }

    /**
     * Returns an InputStreamReader to read a resource from a file
     * in the test target output directory.
     *
     * @param baseName The name of the resource file to read.
     * @return An InputStreamReader.
     * @see #getTestReader(String, String)
     */
    protected Reader getTestReader(String baseName) {
        return getTestReader(baseName, outputExtension());
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    /**
     * Returns the common base directory.
     *
     * @return The common base directory as a File.
     */
    protected File getBasedirFile() {
        return new File(getBasedir());
    }

    /**
     * Returns the base directory where all test output will go.
     *
     * @return The test output directory.
     */
    protected final String outputBaseDir() {
        return "target/test-output/";
    }

    // ----------------------------------------------------------------------
    // Abstract methods the individual ModuleTests must provide
    // ----------------------------------------------------------------------

    /**
     * Determines the default file extension for the current module.
     *
     * @return The default file extension.
     */
    protected abstract String outputExtension();

    /**
     * Returns the directory where test output will go.
     * Should be relative to outputBaseDir().
     *
     * @return The test output directory, relative to outputBaseDir().
     */
    protected abstract String getOutputDir();
}
