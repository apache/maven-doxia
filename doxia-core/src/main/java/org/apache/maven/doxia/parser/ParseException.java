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
package org.apache.maven.doxia.parser;

/**
 * Encapsulate a Doxia parse error.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
public class ParseException extends Exception {
    /** serialVersionUID */
    static final long serialVersionUID = 295967936746221567L;

    /** The file that caused the ParseException. */
    private String fileName;

    /** Line number where the parse exception occurred. */
    private int lineNumber;

    /** Column number where the parse exception occurred. */
    private int columnNumber;

    /**
     * Construct a new <code>ParseException</code> with the specified cause.
     * <br>
     * <b>Note</b>: no line or column number will be used.
     *
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParseException(Exception e) {
        this(null, e, null, -1, -1);
    }

    /**
     * Construct a new <code>ParseException</code> with the specified detail message.
     * <br>
     * <b>Note</b>: no line or column number will be used.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     */
    public ParseException(String message) {
        this(message, null, null, -1, -1);
    }

    /**
     * Construct a new <code>ParseException</code> with the specified detail message and cause.
     * <br>
     * <b>Note</b>: no line or column number will be used.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParseException(String message, Exception e) {
        this(message, e, null, -1, -1);
    }

    /**
     * Construct a new <code>ParseException</code> with the specified detail message,
     * line number and column number.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param line The line number where the parsing failed.
     * This can later be retrieved by the getLineNumber() method.
     * @param column The column number where the parsing failed.
     * This can later be retrieved by the getColumnNumber() method.
     * @since 1.1
     */
    public ParseException(String message, int line, int column) {
        this(message, null, null, line, column);
    }

    /**
     * Construct a new <code>ParseException</code> with the specified detail message and cause,
     * line number and column number.
     *
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param line The line number where the parsing failed.
     * This can later be retrieved by the getLineNumber() method.
     * @param column The column number where the parsing failed.
     * This can later be retrieved by the getColumnNumber() method.
     * @since 1.1
     */
    public ParseException(String message, Exception e, int line, int column) {
        this(message, e, null, line, column);
    }

    /**
     * Constructs a new exception with the specified cause, line number and column number. The error message is
     *  (cause == null ? null : cause.toString()).
     *
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param line The line number where the parsing failed.
     * This can later be retrieved by the getLineNumber() method.
     * @param column The column number where the parsing failed.
     * This can later be retrieved by the getColumnNumber() method.
     * @since 1.1
     */
    public ParseException(Exception e, int line, int column) {
        this(null, e, null, line, column);
    }

    /**
     * Construct a new <code>ParseException</code> with the specified cause,
     * filename, line number and column number.
     *
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param file Name of a file that couldn't be parsed.
     * This can later be retrieved by the getFileName() method.
     * @param line The line number where the parsing failed.
     * This can later be retrieved by the getLineNumber() method.
     * @param column The column number where the parsing failed.
     * This can later be retrieved by the getColumnNumber() method.
     */
    public ParseException(Exception e, String file, int line, int column) {
        this(null, e, file, line, column);
    }

    /**
     * Construct a new <code>ParseException</code> with the specified cause, detail message,
     * filename, line number and column number.
     * @param message The detailed message.
     * This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param e the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param file Name of a file that couldn't be parsed.
     * This can later be retrieved by the getFileName() method.
     * @param line The line number where the parsing failed.
     * This can later be retrieved by the getLineNumber() method.
     * @param column The column number where the parsing failed.
     * This can later be retrieved by the getColumnNumber() method.
     *
     * @since 1.1
     */
    public ParseException(String message, Exception e, String file, int line, int column) {
        super(message, e);

        this.fileName = file;
        this.lineNumber = line;
        this.columnNumber = column;
    }

    /**
     * <p>Getter for the field <code>fileName</code>.</p>
     *
     * @return the file name that caused the <code>ParseException</code>.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * <p>Getter for the field <code>lineNumber</code>.</p>
     *
     * @return the line number where the <code>ParseException</code> occurred.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * <p>Getter for the field <code>columnNumber</code>.</p>
     *
     * @return the column number where the <code>ParseException</code> occurred.
     * @since 1.1
     */
    public int getColumnNumber() {
        return columnNumber;
    }
}
