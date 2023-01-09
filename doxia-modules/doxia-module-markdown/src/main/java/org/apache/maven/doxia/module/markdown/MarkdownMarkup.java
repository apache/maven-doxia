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

import org.apache.maven.doxia.markup.TextMarkup;
import org.codehaus.plexus.util.StringUtils;

/**
 * This interface defines all markups and syntaxes used by the <b>Markdown</b> format.
 */
@SuppressWarnings("checkstyle:interfaceistype")
public interface MarkdownMarkup extends TextMarkup {
    // ----------------------------------------------------------------------
    // Markup separators
    // ----------------------------------------------------------------------

    /** backslash markup char: '\\' */
    char BACKSLASH = '\\';

    String COMMENT_START = "<!-- ";
    String COMMENT_END = " -->";

    /** numbering decimal markup char: '1' */
    char NUMBERING = '1';

    /** numbering lower alpha markup char: 'a' */
    char NUMBERING_LOWER_ALPHA_CHAR = 'a';

    /** numbering lower roman markup char: 'i' */
    char NUMBERING_LOWER_ROMAN_CHAR = 'i';

    /** numbering upper alpha markup char: 'A' */
    char NUMBERING_UPPER_ALPHA_CHAR = 'A';

    /** numbering upper roman markup char: 'I' */
    char NUMBERING_UPPER_ROMAN_CHAR = 'I';

    /** page break markup char: '\f' */
    char PAGE_BREAK = '\f';

    // ----------------------------------------------------------------------
    // Markup syntax
    // ----------------------------------------------------------------------

    /** Syntax for the anchor end: "\"&gt;&lt;/a&gt;" */
    String ANCHOR_END_MARKUP = "\"></a>";

    /** Syntax for the anchor start: "&lt;a id=\"" */
    String ANCHOR_START_MARKUP = "<a id=\"";

    /** Syntax for the bold style end: "**" */
    String BOLD_END_MARKUP = "**";

    /** Syntax for the bold style start: "**" */
    String BOLD_START_MARKUP = "**";

    /** Syntax for the header start: "---" */
    String METADATA_MARKUP = StringUtils.repeat(String.valueOf(MINUS), 3);

    /** Syntax for the horizontal rule: "========" */
    String HORIZONTAL_RULE_MARKUP = StringUtils.repeat(String.valueOf(EQUAL), 8);

    /** Syntax for the italic style end: "_" */
    String ITALIC_END_MARKUP = "_";

    /** Syntax for the italic style start: "_" */
    String ITALIC_START_MARKUP = "_";

    /** Syntax for the link end: ")" */
    String LINK_END_MARKUP = ")";

    /** Syntax for the link start: "[" */
    String LINK_START_1_MARKUP = "[";

    /** Syntax for the link start: "](" */
    String LINK_START_2_MARKUP = "](";

    /** Syntax for the list start: "-" */
    String LIST_START_MARKUP = "-";

    /** Syntax for the mono-spaced style end: "`" */
    String MONOSPACED_END_MARKUP = "`";

    /** Syntax for the mono-spaced style start: "`" */
    String MONOSPACED_START_MARKUP = "`";

    /** Syntax for the verbatim start: "```" */
    String VERBATIM_START_MARKUP = "```";

    /** Syntax for the non breaking space: "\ " */
    String NON_BREAKING_SPACE_MARKUP = String.valueOf(BACKSLASH) + SPACE;

    /** Syntax for the page break: "\f" */
    String PAGE_BREAK_MARKUP = String.valueOf(PAGE_BREAK);

    /** Syntax for the section title start: "#" */
    String SECTION_TITLE_START_MARKUP = "#";

    /** Syntax for the table cell start: "|" */
    String TABLE_CELL_SEPARATOR_MARKUP = String.valueOf(PIPE);

    /** Syntax for the table column, centered style: "---|" */
    String TABLE_COL_DEFAULT_ALIGNED_MARKUP = StringUtils.repeat(String.valueOf(MINUS), 3) + PIPE;

    /** Syntax for the table column, left style: "---+" */
    String TABLE_COL_LEFT_ALIGNED_MARKUP = StringUtils.repeat(String.valueOf(MINUS), 3) + PLUS;

    /** Syntax for the table column, right style: "---:" */
    String TABLE_COL_RIGHT_ALIGNED_MARKUP = StringUtils.repeat(String.valueOf(MINUS), 3) + COLON;

    /** Syntax for the table row end: "|" */
    String TABLE_ROW_SEPARATOR_MARKUP = String.valueOf(PIPE);

    /** Syntax for the verbatim end: "```" */
    String VERBATIM_END_MARKUP = "```";
}
