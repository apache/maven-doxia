package org.apache.maven.doxia.module.apt;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.codehaus.plexus.util.StringUtils;

/**
 * This interface defines all markups and syntaxes used by the <b>APT</b> format.
 *
 * @see <a href="http://maven.apache.org/doxia/references/apt-format.html">http://maven.apache.org/doxia/references/apt-format.html</a>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public interface AptMarkup
{
    /** The vm line separator */
    /** TODO should be in a super interface */
    String EOL = System.getProperty( "line.separator" );

    // ----------------------------------------------------------------------
    // Markup separators
    // ----------------------------------------------------------------------

    /** APT comment markup char: '~' */
    char COMMENT_MARKUP = '~';

    /** APT space markup char: ' ' */
    char SPACE_MARKUP = ' ';

    /** APT tab markup char: '\t' */
    char TAB_MARKUP = '\t';

    /** APT backslash markup char: '\\' */
    char BACKSLASH_MARKUP = '\\';

    /** APT star markup char: '*' */
    char STAR_MARKUP = '*';

    /** APT plus markup char: '+' */
    char PLUS_MARKUP = '+';

    /** APT minus markup char: '-' */
    char MINUS_MARKUP = '-';

    /** APT equal markup char: '=' */
    char EQUAL_MARKUP = '=';

    /** APT pipe markup char: '|' */
    char PIPE_MARKUP = '|';

    /** APT left curly bracket markup char: '{' */
    char LEFT_CURLY_BRACKET_MARKUP = '{';

    /** APT right curly bracket markup char: '}' */
    char RIGHT_CURLY_BRACKET_MARKUP = '}';

    /** APT left square bracket markup char: '[' */
    char LEFT_SQUARE_BRACKET_MARKUP = '[';

    /** APT right square bracket markup char: ']' */
    char RIGHT_SQUARE_BRACKET_MARKUP = ']';

    /** APT less than markup char: '<' */
    char LESS_THAN_MARKUP = '<';

    /** APT greater than markup char: '>' */
    char GREATER_THAN_MARKUP = '>';

    /** APT numbering lower alpha markup char: 'a' */
    char NUMBERING_LOWER_ALPHA_MARKUP = 'a';

    /** APT numbering upper alpha markup char: 'A' */
    char NUMBERING_UPPER_ALPHA_MARKUP = 'A';

    /** APT numbering lower roman markup char: 'i' */
    char NUMBERING_LOWER_ROMAN_MARKUP = 'i';

    /** APT numbering upper roman markup char: 'I' */
    char NUMBERING_UPPER_ROMAN_MARKUP = 'I';

    /** APT numbering decimal markup char: '1' */
    char NUMBERING_MARKUP = '1';

    /** APT page break markup char: '\f' */
    char PAGE_BREAK_MARKUP = '\f';

    /** APT percent markup char: '%' */
    char PERCENT_MARKUP = '%';

    /** APT colon markup char: ':' */
    char COLON_MARKUP = ':';

    // ----------------------------------------------------------------------
    // Markup syntax
    // ----------------------------------------------------------------------

    /** Syntax for the header start: " -----" */
    String HEADER_START = SPACE_MARKUP + StringUtils.repeat( String.valueOf( MINUS_MARKUP ), 5 );

    /** Syntax for the section title start: "*" */
    String SECTION_TITLE_START = String.valueOf( STAR_MARKUP );

    /** Syntax for the list start: "*" */
    String LIST_START = String.valueOf( STAR_MARKUP );

    /** Syntax for the list end: "[]" */
    String LIST_END = String.valueOf( LEFT_SQUARE_BRACKET_MARKUP ) + String.valueOf( RIGHT_SQUARE_BRACKET_MARKUP );

    /** Syntax for the page break: "\f" */
    String PAGE_BREAK = String.valueOf( PAGE_BREAK_MARKUP );

    /** Syntax for the boxed verbatim start: "+------+" */
    String BOXED_VERBATIM_START = String.valueOf( PLUS_MARKUP )
        + StringUtils.repeat( String.valueOf( MINUS_MARKUP ), 6 ) + String.valueOf( PLUS_MARKUP );

    /** Syntax for the boxed verbatim end: "+------+" */
    String BOXED_VERBATIM_END = BOXED_VERBATIM_START;

    /** Syntax for the non boxed verbatim start: "------" */
    String NON_BOXED_VERBATIM_START = StringUtils.repeat( String.valueOf( MINUS_MARKUP ), 6 );

    /** Syntax for the non boxed verbatim end: "------" */
    String NON_BOXED_VERBATIM_END = NON_BOXED_VERBATIM_START;

    /** Syntax for the horizontal rule: "========" */
    String HORIZONTAL_RULE = StringUtils.repeat( String.valueOf( EQUAL_MARKUP ), 8 );

    /** Syntax for the table row start: "*--" */
    String TABLE_ROW_START = STAR_MARKUP + StringUtils.repeat( String.valueOf( MINUS_MARKUP ), 2 );

    /** Syntax for the table row end: "|" */
    String TABLE_ROW_SEPARATOR = String.valueOf( PIPE_MARKUP );

    /** Syntax for the table column, left style: "-+" */
    String TABLE_COL_LEFT_ALIGNED = StringUtils.repeat( String.valueOf( MINUS_MARKUP ), 2 )
        + String.valueOf( PLUS_MARKUP );

    /** Syntax for the table column, centered style: "-*" */
    String TABLE_COL_CENTERED_ALIGNED = StringUtils.repeat( String.valueOf( MINUS_MARKUP ), 2 )
        + String.valueOf( STAR_MARKUP );

    /** Syntax for the table column, right style: "-:" */
    String TABLE_COL_RIGHT_ALIGNED = StringUtils.repeat( String.valueOf( MINUS_MARKUP ), 2 )
        + String.valueOf( COLON_MARKUP );

    /** Syntax for the table cell start: "|" */
    String TABLE_CELL_SEPARATOR = String.valueOf( PIPE_MARKUP );

    /** Syntax for the anchor start: "{" */
    String ANCHOR_START = String.valueOf( LEFT_CURLY_BRACKET_MARKUP );

    /** Syntax for the anchor end: "}" */
    String ANCHOR_END = String.valueOf( RIGHT_CURLY_BRACKET_MARKUP );

    /** Syntax for the link start: "{{{" */
    String LINK_START_1 = StringUtils.repeat( String.valueOf( LEFT_CURLY_BRACKET_MARKUP ), 3 );

    /** Syntax for the link start: "}" */
    String LINK_START_2 = String.valueOf( RIGHT_CURLY_BRACKET_MARKUP );

    /** Syntax for the link end: "}}" */
    String LINK_END = StringUtils.repeat( String.valueOf( RIGHT_CURLY_BRACKET_MARKUP ), 2 );

    /** Syntax for the italic style start: "<" */
    String ITALIC_START = String.valueOf( LESS_THAN_MARKUP );

    /** Syntax for the italic style end: ">" */
    String ITALIC_END = String.valueOf( GREATER_THAN_MARKUP );

    /** Syntax for the bold style start: "<<" */
    String BOLD_START = StringUtils.repeat( String.valueOf( LESS_THAN_MARKUP ), 2 );

    /** Syntax for the bold style end: ">>" */
    String BOLD_END = StringUtils.repeat( String.valueOf( GREATER_THAN_MARKUP ), 2 );

    /** Syntax for the mono-spaced style start: "<<<" */
    String MONOSPACED_START = StringUtils.repeat( String.valueOf( LESS_THAN_MARKUP ), 3 );

    /** Syntax for the mono-spaced style end: ">>>" */
    String MONOSPACED_END = StringUtils.repeat( String.valueOf( GREATER_THAN_MARKUP ), 3 );

    /** Syntax for the non breaking space: "\ " */
    String NON_BREAKING_SPACE = String.valueOf( BACKSLASH_MARKUP ) + String.valueOf( SPACE_MARKUP );
}
