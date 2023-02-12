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

import java.io.Writer;

import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.codehaus.plexus.util.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the <code>MarkdownSink</code> class
 */
public class MarkdownSinkTest extends AbstractSinkTest {
    /** {@inheritDoc} */
    protected String outputExtension() {
        return "md";
    }

    /** {@inheritDoc} */
    protected Sink createSink(Writer writer) {
        return new MarkdownSink(writer);
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink() {
        return false;
    }

    /** {@inheritDoc} */
    protected String getTitleBlock(String title) {
        return title;
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock(String author) {
        return getEscapedText(author);
    }

    /** {@inheritDoc} */
    protected String getDateBlock(String date) {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getArticleBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNavigationBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSidebarBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock(String title) {
        return title;
    }

    protected String getSectionBlock(String title, int level) {
        return EOL
                + StringUtils.repeat(MarkdownMarkup.SECTION_TITLE_START_MARKUP, level)
                + SPACE
                + title
                + EOL
                + EOL
                + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection1Block(String title) {
        return getSectionBlock(title, 1);
    }

    /** {@inheritDoc} */
    protected String getSection2Block(String title) {
        return getSectionBlock(title, 2);
    }

    /** {@inheritDoc} */
    protected String getSection3Block(String title) {
        return getSectionBlock(title, 3);
    }

    /** {@inheritDoc} */
    protected String getSection4Block(String title) {
        return getSectionBlock(title, 4);
    }

    /** {@inheritDoc} */
    protected String getSection5Block(String title) {
        return getSectionBlock(title, 5);
    }

    /** {@inheritDoc} */
    protected String getHeaderBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getContentBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getFooterBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getListBlock(String item) {
        return EOL + EOL + Markup.SPACE + "" + MarkdownMarkup.LIST_UNORDERED_ITEM_START_MARKUP + "" + Markup.SPACE
                + getEscapedText(item) + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock(String item) {
        return EOL + EOL + Markup.SPACE + ""
                + MarkdownMarkup.LIST_ORDERED_ITEM_START_MARKUP + ""
                + Markup.SPACE + getEscapedText(item) + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock(String definum, String definition) {
        return EOL + EOL + Markup.SPACE + "" + Markup.LEFT_SQUARE_BRACKET + definum + Markup.RIGHT_SQUARE_BRACKET + ""
                + Markup.SPACE + definition + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock(String source, String caption) {
        String figureBlock = "<img src=\"" + source + "\" />";
        if (caption != null) {
            figureBlock += getEscapedText(caption) + EOL;
        }
        return figureBlock;
    }

    /** {@inheritDoc} */
    protected String getTableBlock(String cell, String caption) {
        return EOL + cell + MarkdownMarkup.TABLE_ROW_SEPARATOR_MARKUP + EOL + caption + EOL;
    }

    @Override
    public void testTable() {
        // TODO re-enable test from parent
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock(String text) {
        return EOL + Markup.SPACE + text + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getDataBlock(String value, String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getTimeBlock(String datetime, String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getAddressBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getBlockquoteBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getDivisionBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getVerbatimSourceBlock(String text) {
        return EOL
                + EOL
                + MarkdownMarkup.VERBATIM_START_MARKUP
                + EOL
                + text
                + EOL
                + MarkdownMarkup.VERBATIM_START_MARKUP
                + EOL;
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock() {
        return EOL + MarkdownMarkup.HORIZONTAL_RULE_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock() {
        return EOL + MarkdownMarkup.PAGE_BREAK_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock(String anchor) {
        return anchor;
    }

    /** {@inheritDoc} */
    protected String getLinkBlock(String link, String text) {
        String lnk = link.startsWith("#") ? link.substring(1) : link;
        return MarkdownMarkup.LINK_START_1_MARKUP
                + text
                + MarkdownMarkup.LINK_START_2_MARKUP
                + lnk
                + MarkdownMarkup.LINK_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getInlineItalicBlock(String text) {
        return MarkdownMarkup.ITALIC_START_MARKUP + text + MarkdownMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineBoldBlock(String text) {
        return MarkdownMarkup.BOLD_START_MARKUP + text + MarkdownMarkup.BOLD_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineCodeBlock(String text) {
        return MarkdownMarkup.MONOSPACED_START_MARKUP + text + MarkdownMarkup.MONOSPACED_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getItalicBlock(String text) {
        return MarkdownMarkup.ITALIC_START_MARKUP + text + MarkdownMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getBoldBlock(String text) {
        return MarkdownMarkup.BOLD_START_MARKUP + text + MarkdownMarkup.BOLD_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock() {
        return MarkdownMarkup.BACKSLASH + EOL;
    }

    /** {@inheritDoc} */
    protected String getLineBreakOpportunityBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock() {
        return MarkdownMarkup.NON_BREAKING_SPACE_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getTextBlock(String text) {
        return getEscapedText(text);
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock(String text) {
        return text;
    }

    /**
     * Escapes special characters outlined in <a href="https://daringfireball.net/projects/markdown/syntax#backslash">Markdown Spec</a>
     * @param text
     * @return the text with all special characters escaped
     */
    private String getEscapedText(String text) {
        return text.replaceAll("\\\\|\\`|\\*|_|\\{|\\}|\\[|\\]|\\(|\\)|#|\\+|\\-|\\.|\\!", "\\\\$0");
    }

    /**
     * Add a backslash for a special markup character
     *
     * @param c
     * @return the character with a backslash before
     */
    private static String getSpecialCharacters(char c) {
        return MarkdownMarkup.BACKSLASH + "" + c;
    }

    /** {@inheritDoc} */
    protected String getCommentBlock(String text) {
        return "<!-- " + text + " -->";
    }

    @Test
    public void testMultipleAuthors() {
        final Sink sink = getSink();
        sink.head();
        sink.author();
        sink.text("first author");
        sink.author_();
        sink.author();
        sink.text("second author");
        sink.author_();
        sink.head_();
        sink.flush();
        sink.close();

        String expected = MarkdownMarkup.METADATA_MARKUP + EOL
                + "author: " + EOL
                + "  - first author" + EOL
                + "  - second author" + EOL
                + MarkdownMarkup.METADATA_MARKUP + EOL;

        assertEquals(expected, getSinkContent(), "Wrong metadata section");
    }

    /**
     * test for DOXIA-497.
     */
    public void _testLinksAndParagraphsInTableCells() {
        final String linkTarget = "target";
        final String linkText = "link";
        final String paragraphText = "paragraph text";
        final Sink sink = getSink();
        sink.table();
        sink.tableRow();
        sink.tableCell();
        sink.link(linkTarget);
        sink.text(linkText);
        sink.link_();
        sink.tableCell_();
        sink.tableCell();
        sink.paragraph();
        sink.text(paragraphText);
        sink.paragraph_();
        sink.tableCell_();
        sink.tableRow_();
        sink.table_();
        sink.flush();
        sink.close();

        String expected = EOL
                + MarkdownMarkup.LEFT_CURLY_BRACKET
                + MarkdownMarkup.LEFT_CURLY_BRACKET
                + MarkdownMarkup.LEFT_CURLY_BRACKET
                + linkTarget
                + MarkdownMarkup.RIGHT_CURLY_BRACKET
                + linkText
                + MarkdownMarkup.RIGHT_CURLY_BRACKET
                + MarkdownMarkup.RIGHT_CURLY_BRACKET
                + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + paragraphText
                + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + EOL;

        assertEquals(expected, getSinkContent(), "Wrong link or paragraph markup in table cell");
    }

    public void _testTableCellsWithJustification() {
        final String linkTarget = "target";
        final String linkText = "link";
        final String paragraphText = "paragraph text";
        final Sink sink = getSink();
        sink.table();
        sink.tableRows(new int[] {Sink.JUSTIFY_RIGHT, Sink.JUSTIFY_LEFT}, false);
        sink.tableRow();
        sink.tableCell();
        sink.link(linkTarget);
        sink.text(linkText);
        sink.link_();
        sink.tableCell_();
        sink.tableCell();
        sink.paragraph();
        sink.text(paragraphText);
        sink.paragraph_();
        sink.tableCell_();
        sink.tableRow_();
        sink.tableRows_();
        sink.table_();
        sink.flush();
        sink.close();

        String expected = EOL
                + MarkdownMarkup.TABLE_COL_RIGHT_ALIGNED_MARKUP
                + MarkdownMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP
                + EOL
                + MarkdownMarkup.LINK_START_1_MARKUP
                + linkTarget
                + MarkdownMarkup.LINK_START_2_MARKUP
                + linkText
                + MarkdownMarkup.LINK_END_MARKUP
                + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + paragraphText
                + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + EOL
                + MarkdownMarkup.TABLE_COL_RIGHT_ALIGNED_MARKUP
                + MarkdownMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP
                + EOL;

        assertEquals(expected, getSinkContent(), "Wrong justification in table cells");
    }
}
