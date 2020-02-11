package org.apache.maven.doxia.module.twiki;

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

import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.markup.TextMarkup;

/**
 * This interface defines all markups and syntaxes used by the <b>TWiki</b> format.
 *
 * @see <a href="http://twiki.org/cgi-bin/view/TWiki/TextFormattingRules">http://twiki.org/cgi-bin/view/TWiki/TextFormattingRules</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @since 1.0
 */
@SuppressWarnings( "checkstyle:interfaceistype" )
public interface TWikiMarkup
    extends TextMarkup
{
    // ----------------------------------------------------------------------
    // Twiki markups
    // ----------------------------------------------------------------------

    /** Syntax for the anchor : '#' */
    char ANCHOR_MARKUP = '#';

    /** Syntax for the start line separator: "   " */
    String THREE_SPACES_MARKUP = "   ";

    /** Syntax for the bold markup: "*" */
    String BOLD_END_MARKUP = "*";

    /** Syntax for the bold markup: "*" */
    String BOLD_START_MARKUP = "*";

    /** Syntax for the bold italic markup: "__" */
    String BOLD_ITALIC_END_MARKUP = "__";

    /** Syntax for the bold italic markup: "__" */
    String BOLD_ITALIC_START_MARKUP = "__";

    /** Syntax for the bold monospaced markup: "==" */
    String BOLD_MONOSPACED_END_MARKUP = "==";

    /** Syntax for the bold monospaced markup: "==" */
    String BOLD_MONOSPACED_START_MARKUP = "==";

    /** Syntax for the definition list item: "   $ " */
    String DEFINITION_LIST_ITEM_MARKUP = THREE_SPACES_MARKUP + "$ ";

    /** Syntax for the definition list definition: ": " */
    String DEFINITION_LIST_DEFINITION_MARKUP = ": ";

    /** Syntax for the horizontal rule markup: "---" */
    String HORIZONTAL_RULE_MARKUP = "---";

    /** Syntax for the italic markup: "_" */
    String ITALIC_END_MARKUP = "_";

    /** Syntax for the italic markup: "_" */
    String ITALIC_START_MARKUP = "_";

    /** Syntax for the link end markup: "]]" */
    String LINK_END_MARKUP = "]]";

    /** Syntax for the link middle markup: "][" */
    String LINK_MIDDLE_MARKUP = "][";

    /** Syntax for the link start markup: "[[" */
    String LINK_START_MARKUP = "[[";

    /** Syntax for the list item markup: <code>"* "</code> */
    /** Constant <code>LIST_ITEM_MARKUP="* "</code> */
    String LIST_ITEM_MARKUP = "* ";

    /** Syntax for the mono-spaced style end: "=" */
    String MONOSPACED_END_MARKUP = "=";

    /** Syntax for the mono-spaced style start: "=" */
    String MONOSPACED_START_MARKUP = "=";

    /** Syntax for the numbering decimal markup char: "1." */
    String NUMBERING_MARKUP = "1.";

    /** Syntax for the numbering lower alpha markup char: "a." */
    String NUMBERING_LOWER_ALPHA_MARKUP = "a.";

    /** Syntax for the numbering lower roman markup char: "i." */
    String NUMBERING_LOWER_ROMAN_MARKUP = "i.";

    /** Syntax for the numbering upper alpha markup char: "A." */
    String NUMBERING_UPPER_ALPHA_MARKUP = "A.";

    /** Syntax for the numbering upper roman markup char: "I." */
    String NUMBERING_UPPER_ROMAN_MARKUP = "I.";

    /** Syntax for the table cell header end markup: "* |" */
    String TABLE_CELL_HEADER_END_MARKUP = "* |";

    /** Syntax for the table cell header start markup: " *" */
    String TABLE_CELL_HEADER_START_MARKUP = " *";

    /** Syntax for the table cell markup: "| " */
    String TABLE_CELL_MARKUP = " |";

    /** Syntax for the table row markup: "|" */
    String TABLE_ROW_MARKUP = "|";

    // ----------------------------------------------------------------------
    // Specific Twiki tags
    // ----------------------------------------------------------------------

    /** TWiki tag for <code>verbatim</code> */
    Tag VERBATIM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "verbatim";
        }
    };
}
