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
package org.apache.maven.doxia.module.apt;

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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.codehaus.plexus.util.IOUtil;

/**
 * Reader for apt source documents.
 */
public class AptReaderSource implements AptSource {
    /** A reader. */
    private LineNumberReader reader;

    /** lineNumber. */
    private int lineNumber;

    /** The name, e.g. the filename. */
    private String name;

    /**
     * Constructor: initialize reader.
     *
     * @param in the reader.
     */
    public AptReaderSource(Reader in) {
        reader = new LineNumberReader(in);

        lineNumber = -1;
    }

    /**
     * Constructor: initialize reader.
     *
     * @param in the reader.
     * @param name the name of the source
     */
    public AptReaderSource(Reader in, String name) {
        this(in);

        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link java.lang.String} object.
     * @throws org.apache.maven.doxia.module.apt.AptParseException if any.
     */
    public String getNextLine() throws AptParseException {
        if (reader == null) {
            return null;
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
            // TODO handle column number
            throw new AptParseException(null, e, lineNumber, -1);
        }

        return line;
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        // never return null
        return name != null ? name : "";
    }

    /**
     * {@inheritDoc}
     *
     * @return a int.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Closes the reader associated with this AptReaderSource.
     */
    public void close() {
        IOUtil.close(reader);
        reader = null;
    }
}
