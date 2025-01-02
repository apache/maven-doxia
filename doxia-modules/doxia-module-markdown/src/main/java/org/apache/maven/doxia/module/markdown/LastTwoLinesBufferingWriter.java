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

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;

/**
 * Decorates an existing writer to additionally temporarily buffer the last two lines written.
 * Useful to collapse subsequent new lines or blank lines by evaluating {@link #isWriterAfterBlankLine()} and {@link #isWriterAfterBlankLine()}.
 * The buffering does not affect or defer delegation to the underlying writer, though.
 */
public class LastTwoLinesBufferingWriter extends Writer {

    private final Writer out;
    private String previousLine;
    private StringBuilder currentLine;
    private final String lineSeparator;

    public LastTwoLinesBufferingWriter(Writer out) {
        // don't use System.lineSeparator, as overwritten in AbstractModuleTest
        this(out, System.getProperty("line.separator"));
    }

    LastTwoLinesBufferingWriter(Writer out, String lineSeparator) {
        super();
        this.out = out;
        this.previousLine = "";
        this.currentLine = new StringBuilder();
        this.lineSeparator = lineSeparator;
    }

    public boolean isWriterAtStartOfNewLine() {
        return currentLine.length() == 0;
    }

    public boolean isWriterAfterBlankLine() {
        return StringUtils.isAllBlank(currentLine) && StringUtils.isAllBlank(previousLine);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        int offsetWrittenInLineBuffer = off;
        int index = 0;
        while (index < len) {
            // potentially a line break...
            if (cbuf[off + index] == '\r' || cbuf[off + index] == '\n') {
                int lenToWrite = index + 1 - (offsetWrittenInLineBuffer - off);
                flushLine(cbuf, offsetWrittenInLineBuffer, lenToWrite);
                offsetWrittenInLineBuffer += lenToWrite;
            }
            index++;
        }
        flushLine(cbuf, offsetWrittenInLineBuffer, index - (offsetWrittenInLineBuffer - off));
        out.write(cbuf, off, len);
    }

    private void flushLine(char[] cbuf, int off, int len) {
        this.currentLine.append(cbuf, off, len);
        // really a line break?
        if (currentLine.toString().endsWith(lineSeparator)) {
            previousLine = currentLine.toString();
            currentLine.setLength(0);
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public boolean isAfterDigit() {
        return currentLine.length() > 1 && Character.isDigit(currentLine.charAt(currentLine.length() - 1));
    }
}
