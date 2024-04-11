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

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.markup.TextMarkup;

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

    String BLANK_LINE = EOL + EOL;

    /** indentation e.g. for paragraphs inside lists */
    String INDENT = StringUtils.repeat(String.valueOf(SPACE), 4);
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

    /** Syntax for the horizontal rule: "***" */
    String HORIZONTAL_RULE_MARKUP = "***";

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

    /** Syntax for the ordered list item: '1. ' */
    String LIST_ORDERED_ITEM_START_MARKUP = "1. ";

    /** Syntax for the unordered list item: "- " */
    String LIST_UNORDERED_ITEM_START_MARKUP = "- ";

    /** Syntax for the mono-spaced style end: "`" */
    String MONOSPACED_END_MARKUP = "`";

    /** Syntax for the mono-spaced style start: "`" */
    String MONOSPACED_START_MARKUP = "`";

    /** Syntax for the verbatim start: "```" */
    String VERBATIM_START_MARKUP = "```";

    /** Syntax for the verbatim end: "```" */
    String VERBATIM_END_MARKUP = "```";

    /** Syntax for the blockquote start: "&gt; " */
    String BLOCKQUOTE_START_MARKUP = "> ";

    /** Syntax for the non breaking space: entity from HTML */
    String NON_BREAKING_SPACE_MARKUP = "&nbsp;";

    /** Syntax for the section title start: "#" */
    String SECTION_TITLE_START_MARKUP = "#";

    /* Table markup according to https://github.github.com/gfm/#tables-extension- */
    /** Syntax for the table cell start: "|" */
    String TABLE_CELL_SEPARATOR_MARKUP = String.valueOf(PIPE);

    /** Syntax for the table column, left alignment (default): "---" */
    String TABLE_COL_LEFT_ALIGNED_MARKUP = StringUtils.repeat(String.valueOf(MINUS), 3);

    /** Syntax for the table column, right alignment: "---:" */
    String TABLE_COL_RIGHT_ALIGNED_MARKUP = StringUtils.repeat(String.valueOf(MINUS), 3) + COLON;

    /** Syntax for the table column, center alignment: ":---:" */
    String TABLE_COL_CENTER_ALIGNED_MARKUP = COLON + StringUtils.repeat(String.valueOf(MINUS), 3) + COLON;

    /** Syntax for the table row prefix (the other separators in the same row are regular {@link #TABLE_CELL_SEPARATOR_MARKUP} characters) */
    String TABLE_ROW_PREFIX = String.valueOf(PIPE);
}
