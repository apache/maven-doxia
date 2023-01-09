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

import org.apache.maven.doxia.parser.ParseException;

/**
 * Wraps an exception when parsing apt source documents.
 *
 * @since 1.0
 */
public class AptParseException extends ParseException {
    /** serialVersionUID */
    static final long serialVersionUID = 1694654412921168623L;

    /**
     * Construct a new AptParseException with the cause.
     *
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public AptParseException(Exception e) {
        super(e);
    }

    /**
     * Construct a new AptParseException with the specified detail message.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     */
    public AptParseException(String message) {
        super(message);
    }

    /**
     * Construct a new AptParseException with the specified detail message and cause.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public AptParseException(String message, Exception e) {
        super(message, e);
    }

    /**
     * Construct a new AptParseException with the specified cause, detail message,
     * filename, line number and column number.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param name Name of a file that couldn't be parsed.
     * This can later be retrieved by the getFileName() method.
     * @param line The line number where the parsing failed.
     * This can later be retrieved by the getLineNumber() method.
     * @param column The column number where the parsing failed.
     * This can later be retrieved by the getColumnNumber() method.
     */
    public AptParseException(String message, Exception e, String name, int line, int column) {
        super(message, e, name, line, column);
    }

    /**
     * Construct a new AptParseException with the specified detail message and cause.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param line The line number where the parsing failed.
     * This can later be retrieved by the getLineNumber() method.
     * @param column The column number where the parsing failed.
     * This can later be retrieved by the getColumnNumber() method.
     */
    public AptParseException(String message, Exception e, int line, int column) {
        super(message, e, line, column);
    }
}
