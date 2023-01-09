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
package org.apache.maven.doxia.util;

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

/*
 * Originally from org.apache.doxia.module.apt.AptReaderSource. It was modified
 * to get unget support
 */

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Objects;

import org.apache.maven.doxia.parser.ParseException;
import org.codehaus.plexus.util.IOUtil;

/**
 * {@link ByLineSource} default implementation
 */
public class ByLineReaderSource implements ByLineSource {
    /**
     * reader
     */
    private LineNumberReader reader;

    /**
     * current line number
     */
    private int lineNumber;

    /**
     * holds the last line returned by getNextLine()
     */
    private String lastLine;

    /**
     * <code>true</code> if ungetLine() was called and no getNextLine() was
     * called
     */
    private boolean ungetted = false;

    private String name;

    /**
     * Creates the ByLineReaderSource.
     *
     * @param in real source :)
     */
    public ByLineReaderSource(final Reader in) {
        this(in, "");
    }

    /**
     * <p>Constructor for ByLineReaderSource.</p>
     *
     * @param in a {@link java.io.Reader} object.
     * @param name a {@link java.lang.String} object.
     */
    public ByLineReaderSource(final Reader in, final String name) {
        this.reader = new LineNumberReader(in);

        this.name = name;

        this.lineNumber = -1;
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link java.lang.String} object.
     * @throws org.apache.maven.doxia.parser.ParseException if any.
     */
    public final String getNextLine() throws ParseException {
        if (reader == null) {
            return null;
        }

        if (ungetted) {
            ungetted = false;
            return lastLine;
        }

        String line;

        try {
            line = reader.readLine();
            if (line == null) {
                reader.close();
                reader = null;
            } else {
                lineNumber = reader.getLineNumber();
            }
        } catch (IOException e) {
            throw new ParseException(e, lineNumber, 0);
        }

        lastLine = line;

        return line;
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link java.lang.String} object.
     */
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @return a int.
     */
    public final int getLineNumber() {
        return lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    public final void close() {
        IOUtil.close(reader);
        reader = null;
    }

    /**
     * {@inheritDoc}
     */
    public final void ungetLine() {
        if (ungetted) {
            throw new IllegalStateException("we support only one level of ungetLine()");
        }
        ungetted = true;
    }

    /** {@inheritDoc} */
    public final void unget(final String s) {
        Objects.requireNonNull(s, "s cannot be null");

        if (s.length() != 0) {
            ungetLine();
            lastLine = s;
        }
    }
}
