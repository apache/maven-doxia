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

import org.apache.maven.doxia.markup.TextMarkup;
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
@SuppressWarnings( "checkstyle:interfaceistype" )
public interface AptMarkup
    extends TextMarkup
{
    // ----------------------------------------------------------------------
    // Markup separators
    // ----------------------------------------------------------------------

    /** APT backslash markup char: '\\' */
    char BACKSLASH = '\\';

    /** APT comment markup char: '~' */
    char COMMENT = '~';

    /** APT numbering decimal markup char: '1' */
    char NUMBERING = '1';

    /** APT numbering lower alpha markup char: 'a' */
    char NUMBERING_LOWER_ALPHA_CHAR = 'a';

    /** APT numbering lower roman markup char: 'i' */
    char NUMBERING_LOWER_ROMAN_CHAR = 'i';

    /** APT numbering upper alpha markup char: 'A' */
    char NUMBERING_UPPER_ALPHA_CHAR = 'A';

    /** APT numbering upper roman markup char: 'I' */
    char NUMBERING_UPPER_ROMAN_CHAR = 'I';

    /** APT page break markup char: '\f' */
    char PAGE_BREAK = '\f';

    /** APT percent markup char: '%' */
    char PERCENT = '%';

    /** APT tab markup char: '\t' */
    char TAB = '\t';

    // ----------------------------------------------------------------------
    // Markup syntax
    // ----------------------------------------------------------------------

    /** Syntax for the anchor end: "}" */
    String ANCHOR_END_MARKUP = String.valueOf( RIGHT_CURLY_BRACKET );

    /** Syntax for the anchor start: "{" */
    String ANCHOR_START_MARKUP = String.valueOf( LEFT_CURLY_BRACKET );

    /** Syntax for the bold style end: "&gt;&gt;" */
    String BOLD_END_MARKUP = StringUtils.repeat( String.valueOf( GREATER_THAN ), 2 );

    /** Syntax for the bold style start: "&lt;&lt;" */
    String BOLD_START_MARKUP = StringUtils.repeat( String.valueOf( LESS_THAN ), 2 );

    /** Syntax for the boxed verbatim start: "+------+" */
    String BOXED_VERBATIM_START_MARKUP = PLUS + StringUtils.repeat( String.valueOf( MINUS ), 6 ) + PLUS;

    /** Syntax for the header start: " -----" */
    String HEADER_START_MARKUP = SPACE + StringUtils.repeat( String.valueOf( MINUS ), 5 );

    /** Syntax for the horizontal rule: "========" */
    String HORIZONTAL_RULE_MARKUP = StringUtils.repeat( String.valueOf( EQUAL ), 8 );

    /** Syntax for the italic style end: "&gt;" */
    String ITALIC_END_MARKUP = String.valueOf( GREATER_THAN );

    /** Syntax for the italic style start: "&lt;" */
    String ITALIC_START_MARKUP = String.valueOf( LESS_THAN );

    /** Syntax for the link end: "}}" */
    String LINK_END_MARKUP = StringUtils.repeat( String.valueOf( RIGHT_CURLY_BRACKET ), 2 );

    /** Syntax for the link start: "{{{" */
    String LINK_START_1_MARKUP = StringUtils.repeat( String.valueOf( LEFT_CURLY_BRACKET ), 3 );

    /** Syntax for the link start: "}" */
    String LINK_START_2_MARKUP = String.valueOf( RIGHT_CURLY_BRACKET );

    /** Syntax for the list end: "[]" */
    String LIST_END_MARKUP = String.valueOf( LEFT_SQUARE_BRACKET ) + RIGHT_SQUARE_BRACKET;

    /** Syntax for the list start: "*" */
    String LIST_START_MARKUP = String.valueOf( STAR );

    /** Syntax for the mono-spaced style end: "&gt;&gt;&gt;" */
    String MONOSPACED_END_MARKUP = StringUtils.repeat( String.valueOf( GREATER_THAN ), 3 );

    /** Syntax for the mono-spaced style start: "&lt;&lt;&lt;" */
    String MONOSPACED_START_MARKUP = StringUtils.repeat( String.valueOf( LESS_THAN ), 3 );

    /** Syntax for the non boxed verbatim start: "------" */
    String NON_BOXED_VERBATIM_START_MARKUP = StringUtils.repeat( String.valueOf( MINUS ), 6 );

    /** Syntax for the non breaking space: "\ " */
    String NON_BREAKING_SPACE_MARKUP = String.valueOf( BACKSLASH ) + SPACE;

    /** Syntax for the page break: "\f" */
    String PAGE_BREAK_MARKUP = String.valueOf( PAGE_BREAK );

    /** Syntax for the section title start: "*" */
    String SECTION_TITLE_START_MARKUP = String.valueOf( STAR );

    /** Syntax for the table cell start: "|" */
    String TABLE_CELL_SEPARATOR_MARKUP = String.valueOf( PIPE );

    /** Syntax for the table column, centered style: "-*" */
    String TABLE_COL_CENTERED_ALIGNED_MARKUP = StringUtils.repeat( String.valueOf( MINUS ), 2 ) + STAR;

    /** Syntax for the table column, left style: "-+" */
    String TABLE_COL_LEFT_ALIGNED_MARKUP = StringUtils.repeat( String.valueOf( MINUS ), 2 ) + PLUS;

    /** Syntax for the table column, right style: "-:" */
    String TABLE_COL_RIGHT_ALIGNED_MARKUP = StringUtils.repeat( String.valueOf( MINUS ), 2 ) + COLON;

    /** Syntax for the table row end: "|" */
    String TABLE_ROW_SEPARATOR_MARKUP = String.valueOf( PIPE );

    /** Syntax for the table row start: "*--" */
    String TABLE_ROW_START_MARKUP = STAR + StringUtils.repeat( String.valueOf( MINUS ), 2 );

    /** Syntax for the boxed verbatim end: "+------+" */
    String BOXED_VERBATIM_END_MARKUP = BOXED_VERBATIM_START_MARKUP;

    /** Syntax for the non boxed verbatim end: "------" */
    String NON_BOXED_VERBATIM_END_MARKUP = NON_BOXED_VERBATIM_START_MARKUP;
}
