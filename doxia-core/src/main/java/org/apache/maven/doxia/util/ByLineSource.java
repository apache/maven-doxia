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

import org.apache.maven.doxia.parser.ParseException;

/**
 * The token are the new lines :)
 *
 * @author Juan F. Codagnone
 * @since Nov 4, 2005
 */
public interface ByLineSource {
    /**
     * <p>getNextLine.</p>
     *
     * @return the next line. <code>null</code> if we reached the end.
     * @throws org.apache.maven.doxia.parser.ParseException on I/O error
     */
    String getNextLine() throws ParseException;

    /**
     * <p>getName.</p>
     *
     * @return the name of the input. could be the filename for example.
     */
    String getName();

    /**
     * <p>getLineNumber.</p>
     *
     * @return the current line number.
     */
    int getLineNumber();

    /**
     * <p>ungetLine.</p>
     *
     * This should throw a java.lang.IllegalStateException if called more than
     *                               one time without calling getNextLine().
     */
    void ungetLine();

    /**
     * <p>unget.</p>
     *
     * @param s some text to push back to the parser.
     * This should throw a java.lang.IllegalStateException if called more than
     *                               one time without calling getNextLine().
     */
    void unget(String s);

    /**
     * close the source.
     */
    void close();
}
