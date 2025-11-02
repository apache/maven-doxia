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

import java.io.Writer;

import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.apache.maven.doxia.util.DoxiaStringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the <code>AptSink</code> class
 *
 * @see AptSink
 */
public class AptSinkTest extends AbstractSinkTest {
    /** {@inheritDoc} */
    protected String outputExtension() {
        return "apt";
    }

    /** {@inheritDoc} */
    protected Sink createSink(Writer writer) {
        return new AptSink(writer);
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
        return author;
    }

    /** {@inheritDoc} */
    protected String getDateBlock(String date) {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock() {
        return AptMarkup.HEADER_START_MARKUP
                + EOL
                + AptMarkup.HEADER_START_MARKUP
                + EOL
                + AptMarkup.HEADER_START_MARKUP
                + EOL
                + AptMarkup.HEADER_START_MARKUP
                + EOL;
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
    protected String getSection1Block(String title) {
        return EOL + title + EOL + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection2Block(String title) {
        return EOL + AptMarkup.SECTION_TITLE_START_MARKUP + title + EOL + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection3Block(String title) {
        return EOL + DoxiaStringUtils.repeat(AptMarkup.SECTION_TITLE_START_MARKUP, 2) + title + EOL + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection4Block(String title) {
        return EOL + DoxiaStringUtils.repeat(AptMarkup.SECTION_TITLE_START_MARKUP, 3) + title + EOL + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection5Block(String title) {
        return EOL + DoxiaStringUtils.repeat(AptMarkup.SECTION_TITLE_START_MARKUP, 4) + title + EOL + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection6Block(String title) {
        // Everything above level 5 is automatically converted to level 5 (as APT doesn't support deeper nesting)
        return EOL + DoxiaStringUtils.repeat(AptMarkup.SECTION_TITLE_START_MARKUP, 4) + title + EOL + EOL + EOL;
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
        return EOL + EOL + Markup.SPACE + "" + AptMarkup.LIST_START_MARKUP + "" + Markup.SPACE + item + EOL + EOL
                + Markup.SPACE + "" + AptMarkup.LIST_END_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock(String item) {
        return EOL + EOL + Markup.SPACE + "" + Markup.LEFT_SQUARE_BRACKET + ""
                + Markup.LEFT_SQUARE_BRACKET + AptMarkup.NUMBERING_LOWER_ROMAN_CHAR + ""
                + Markup.RIGHT_SQUARE_BRACKET + "" + Markup.RIGHT_SQUARE_BRACKET
                + Markup.SPACE + item + EOL + EOL + Markup.SPACE + "" + AptMarkup.LIST_END_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock(String definum, String definition) {
        return EOL + EOL + Markup.SPACE + "" + Markup.LEFT_SQUARE_BRACKET + definum
                + Markup.RIGHT_SQUARE_BRACKET + "" + Markup.SPACE + definition + EOL + EOL
                + Markup.SPACE + "" + AptMarkup.LIST_END_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock(String source, String caption) {
        String figureBlock = EOL + Markup.LEFT_SQUARE_BRACKET + source + Markup.RIGHT_SQUARE_BRACKET + Markup.SPACE;
        if (caption != null) {
            figureBlock += caption + EOL;
        }
        return figureBlock;
    }

    /** {@inheritDoc} */
    protected String getTableBlock(String cell, String caption) {
        return EOL
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP
                + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP
                + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP
                + EOL
                + cell
                + AptMarkup.TABLE_ROW_SEPARATOR_MARKUP
                + cell
                + AptMarkup.TABLE_ROW_SEPARATOR_MARKUP
                + cell
                + AptMarkup.TABLE_ROW_SEPARATOR_MARKUP
                + EOL
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP
                + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP
                + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP
                + EOL
                + caption
                + EOL;
    }

    @Override
    protected String getTableWithHeaderBlock(String... rowPrefixes) {
        return "\n"
                + "*----+--:--*\n"
                + "|" + rowPrefixes[0] + "0||" + rowPrefixes[0] + "1||" + rowPrefixes[0] + "2|\n"
                + "*----+--:--*\n"
                + rowPrefixes[1] + "0|" + rowPrefixes[1] + "1|" + rowPrefixes[1] + "2|\n"
                + "*----+--:--*\n"
                + rowPrefixes[2] + "0|" + rowPrefixes[2] + "1|" + rowPrefixes[2] + "2|\n"
                + "*----+--:--*\n";
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
    protected String getVerbatimBlock(String text) {
        return EOL + EOL + AptMarkup.VERBATIM_START_MARKUP + EOL + text + EOL + AptMarkup.VERBATIM_START_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getVerbatimSourceBlock(String text) {
        return EOL
                + EOL
                + AptMarkup.VERBATIM_SOURCE_START_MARKUP
                + EOL
                + text
                + EOL
                + AptMarkup.VERBATIM_SOURCE_START_MARKUP
                + EOL;
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock() {
        return EOL + AptMarkup.HORIZONTAL_RULE_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock() {
        return EOL + AptMarkup.PAGE_BREAK_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock(String anchor) {
        return AptMarkup.ANCHOR_START_MARKUP + anchor + AptMarkup.ANCHOR_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getLinkBlock(String link, String text) {
        String lnk = link.startsWith("#") ? link.substring(1) : link;
        return AptMarkup.LINK_START_1_MARKUP + lnk + AptMarkup.LINK_START_2_MARKUP + text + AptMarkup.LINK_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getInlineItalicBlock(String text) {
        return AptMarkup.ITALIC_START_MARKUP + text + AptMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineBoldBlock(String text) {
        return AptMarkup.BOLD_START_MARKUP + text + AptMarkup.BOLD_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineCodeBlock(String text) {
        return AptMarkup.MONOSPACED_START_MARKUP + text + AptMarkup.MONOSPACED_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getItalicBlock(String text) {
        return AptMarkup.ITALIC_START_MARKUP + text + AptMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getBoldBlock(String text) {
        return AptMarkup.BOLD_START_MARKUP + text + AptMarkup.BOLD_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock() {
        return AptMarkup.BACKSLASH + EOL;
    }

    /** {@inheritDoc} */
    protected String getLineBreakOpportunityBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock() {
        return AptMarkup.NON_BREAKING_SPACE_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getTextBlock(String text) {
        return AptSink.escapeAPT(text);
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getCommentBlock(String text) {
        return "~~" + text + EOL;
    }

    @Override
    protected String getCommentBlockFollowedByParagraph(String comment, String paragraph) {
        return getCommentBlock(comment) + getParagraphBlock(paragraph);
    }

    /* Overwrite the test from AbstractSinkTest as EOLs are part of getCommentBlock(...) */
    @Test
    public void testTwoConsecutiveBlockComments() {
        final Sink sink = getSink();
        String comment = "Simple comment";
        sink.comment(comment, true);
        sink.comment(comment, true);
        sink.flush();
        sink.close();
        assertEquals(getCommentBlock(comment) + getCommentBlock(comment), getSinkContent(), "Wrong comment!");
    }

    /**
     * test for DOXIA-497.
     */
    @Test
    public void testLinksAndParagraphsInTableCells() {
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
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.MINUS
                + AptMarkup.MINUS
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.STAR
                + EOL
                + AptMarkup.LEFT_CURLY_BRACKET
                + AptMarkup.LEFT_CURLY_BRACKET
                + AptMarkup.LEFT_CURLY_BRACKET
                + linkTarget
                + AptMarkup.RIGHT_CURLY_BRACKET
                + linkText
                + AptMarkup.RIGHT_CURLY_BRACKET
                + AptMarkup.RIGHT_CURLY_BRACKET
                + AptMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + paragraphText
                + AptMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + EOL
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.MINUS
                + AptMarkup.MINUS
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.STAR
                + EOL;

        assertEquals(expected, getSinkContent(), "Wrong link or paragraph markup in table cell");
    }

    @Test
    public void testTableCellsWithJustification() {
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
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.TABLE_COL_RIGHT_ALIGNED_MARKUP
                + AptMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP
                + EOL
                + AptMarkup.LINK_START_1_MARKUP
                + linkTarget
                + AptMarkup.LINK_START_2_MARKUP
                + linkText
                + AptMarkup.LINK_END_MARKUP
                + AptMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + paragraphText
                + AptMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + EOL
                + AptMarkup.TABLE_ROW_START_MARKUP
                + AptMarkup.TABLE_COL_RIGHT_ALIGNED_MARKUP
                + AptMarkup.TABLE_COL_LEFT_ALIGNED_MARKUP
                + EOL;

        assertEquals(expected, getSinkContent(), "Wrong justification in table cells");
    }
}
