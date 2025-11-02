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
package org.apache.maven.doxia.module.markdown;

import org.apache.commons.io.output.NullWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LastTwoLinesBufferingWriterTest {

    private LastTwoLinesBufferingWriter writer;

    @BeforeEach
    void setup() {
        writer = new LastTwoLinesBufferingWriter(NullWriter.INSTANCE);
    }

    @Test
    void emptyWriter() {
        assertTrue(writer.isWriterAfterBlankLine());
        assertTrue(writer.isWriterAtStartOfNewLine());
    }

    @Test
    void writerWithOneBlankLine() throws Exception {
        writer.write("  " + System.lineSeparator());
        assertTrue(writer.isWriterAfterBlankLine());
        assertTrue(writer.isWriterAtStartOfNewLine());
    }

    @Test
    void writerWithOneNonBlankLine() throws Exception {
        writer.write("not blank  " + System.lineSeparator());
        assertFalse(writer.isWriterAfterBlankLine());
        assertTrue(writer.isWriterAtStartOfNewLine());
    }

    @Test
    void writerWithTwoBlankLines() throws Exception {
        writer.write("  " + System.lineSeparator());
        writer.write("  " + System.lineSeparator());
        assertTrue(writer.isWriterAfterBlankLine());
        assertTrue(writer.isWriterAtStartOfNewLine());
        // try writing the same two lines in other blocks
        writer.write("  ");
        writer.write(System.lineSeparator());
        writer.write("  ");
        writer.write(System.lineSeparator());
        assertTrue(writer.isWriterAfterBlankLine());
        assertTrue(writer.isWriterAtStartOfNewLine());
    }

    @Test
    void writerWithNonBlankLineFollowingBlankLine() throws Exception {
        writer.write("\t" + System.lineSeparator());
        writer.write("test");
        assertFalse(writer.isWriterAfterBlankLine());
        assertFalse(writer.isWriterAtStartOfNewLine());
    }

    @Test
    void writerWithBlankLineFollowingNonBlankLine() throws Exception {
        writer.write("test");
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());
        assertTrue(writer.isWriterAfterBlankLine());
        assertTrue(writer.isWriterAtStartOfNewLine());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"\n", "\r\n"})
    void differentLineSeparators(String lineSeparator) throws Exception {
        writer = new LastTwoLinesBufferingWriter(NullWriter.INSTANCE, lineSeparator);
        writer.write("text" + lineSeparator);
        assertFalse(writer.isWriterAfterBlankLine());
        assertTrue(writer.isWriterAtStartOfNewLine());
        writer.write("text" + lineSeparator + lineSeparator + " ");
        assertTrue(writer.isWriterAfterBlankLine());
        assertFalse(writer.isWriterAtStartOfNewLine());
    }
}
