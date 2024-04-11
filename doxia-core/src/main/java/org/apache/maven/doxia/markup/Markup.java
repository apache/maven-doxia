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
package org.apache.maven.doxia.markup;

/**
 * List of constants used by all markup syntax.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @since 1.0
 */
@SuppressWarnings("checkstyle:interfaceistype")
public interface Markup {
    /** The OS dependent line separator */
    String EOL = System.lineSeparator();

    // ----------------------------------------------------------------------
    // Generic separator characters
    // ----------------------------------------------------------------------

    /** equal character: '=' */
    char EQUAL = '=';

    /** end character: '&lt;' */
    char GREATER_THAN = '>';

    /** left curly bracket character: '{' */
    char LEFT_CURLY_BRACKET = '{';

    /** left square bracket character: '[' */
    char LEFT_SQUARE_BRACKET = '[';

    /** start character: '&gt;' */
    char LESS_THAN = '<';

    /** minus character: '-' */
    char MINUS = '-';

    /** plus character: '+' */
    char PLUS = '+';

    /** double quote character: '\"' */
    char QUOTE = '\"';

    /** right curly bracket character: '}' */
    char RIGHT_CURLY_BRACKET = '}';

    /** right square bracket character: ']' */
    char RIGHT_SQUARE_BRACKET = ']';

    /** slash character: '/' */
    char SLASH = '/';

    /** space character: ' ' */
    char SPACE = ' ';

    /** star character: '*' */
    char STAR = '*';

    /** colon character: ':' */
    char COLON = ':';

    /** semicolon character: ';' */
    char SEMICOLON = ';';
}
