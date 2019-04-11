package org.apache.maven.doxia.module.confluence;

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

/**
 * This interface defines all markups and syntaxes used by the <b>Confluence</b> format.
 *
 * See <a href="http://confluence.atlassian.com/display/CONF25/Confluence+Notation+Guide+Overview">
 * Confluence Notation Guide Overview</a>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings( "checkstyle:interfaceistype" )
public interface ConfluenceMarkup
    extends TextMarkup
{
    // ----------------------------------------------------------------------
    // Confluence markups
    // ----------------------------------------------------------------------

    /** Syntax for the anchor : "{anchor:" */
    String ANCHOR_START_MARKUP = "{anchor:";

    /** Syntax for the anchor : "}" */
    String ANCHOR_END_MARKUP = "}";

    /** Syntax for the bold markup: "*" */
    String BOLD_END_MARKUP = "*";

    /** Syntax for the bold markup: "*" */
    String BOLD_START_MARKUP = "*";

    /** Syntax for the citation markup: "??" */
    String CITATION_END_MARKUP = "??";

    /** Syntax for the citation markup: "??" */
    String CITATION_START_MARKUP = "??";

    /** Syntax for the figure markup: "!" */
    String FIGURE_END_MARKUP = "!";

    /** Syntax for the figure markup: "!" */
    String FIGURE_START_MARKUP = "!";

    /** Syntax for the italic markup: "_" */
    String ITALIC_END_MARKUP = "_";

    /** Syntax for the italic markup: "_" */
    String ITALIC_START_MARKUP = "_";

    /** Syntax for the line break markup: "\\\\" */
    String LINE_BREAK_MARKUP = "\\\\";

    /** Syntax for the link end markup: "]" */
    String LINK_END_MARKUP = "]";

    /** Syntax for the link middle markup: "|" */
    String LINK_MIDDLE_MARKUP = "|";

    /** Syntax for the link start markup: "[" */
    String LINK_START_MARKUP = "[";

    /** Syntax for the list item markup: "* */
    String LIST_ITEM_MARKUP = "* ";

    /** Syntax for the mono-spaced style start: "{{" */
    String MONOSPACED_START_MARKUP = "{{";

    /** Syntax for the mono-spaced style end: "}}" */
    String MONOSPACED_END_MARKUP = "}}";

    /** Syntax for the numbering decimal markup char: "#" */
    String NUMBERING_MARKUP = "#";

    /** Syntax for the strikethrough markup start: "-" */
    String STRIKETHROUGH_START_MARKUP = "-";

    /** Syntax for the strikethrough markup end: "-" */
    String STRIKETHROUGH_END_MARKUP = "-";

    /** Syntax for the subscript markup start: "-" */
    String SUBSCRIPT_START_MARKUP = "~";

    /** Syntax for the subscript markup end: "-" */
    String SUBSCRIPT_END_MARKUP = "~";

    /** Syntax for the superscript markup start: "-" */
    String SUPERSCRIPT_START_MARKUP = "^";

    /** Syntax for the superscript markup end: "-" */
    String SUPERSCRIPT_END_MARKUP = "^";

    /** Syntax for the table cell header end markup: "|" */
    String TABLE_CELL_HEADER_END_MARKUP = "|";

    /** Syntax for the table cell header start markup: "|" */
    String TABLE_CELL_HEADER_START_MARKUP = "|";

    /** Syntax for the table cell markup: "|" */
    String TABLE_CELL_MARKUP = "|";

    /** Syntax for the table row markup: "|" */
    String TABLE_ROW_MARKUP = "|";

    /** Syntax for the underlined markup start: "-" */
    String UNDERLINED_START_MARKUP = "+";

    /** Syntax for the underlined markup end: "-" */
    String UNDERLINED_END_MARKUP = "+";
}
