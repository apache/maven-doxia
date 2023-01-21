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
package org.apache.maven.doxia.sink.impl;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.junit.jupiter.api.Test;

/**
 *
 * @author ltheussl
 */
public class SinkAdapterTest {
    private final SinkAdapter instance = new SinkAdapter();

    /**
     * Test of head method, of class SinkAdapter.
     */
    @Test
    public void testHead() {
        instance.head();
        instance.head(null);
        instance.head_();
    }

    /**
     * Test of body method, of class SinkAdapter.
     */
    @Test
    public void testBody() {
        instance.body();
        instance.body(null);
        instance.body_();
    }

    /**
     * Test of article method, of class SinkAdapter.
     */
    @Test
    public void testArticle() {
        instance.article();
        instance.article(null);
        instance.article_();
    }

    /**
     * Test of navigation method, of class SinkAdapter.
     */
    @Test
    public void testNavigation() {
        instance.navigation();
        instance.navigation(null);
        instance.navigation_();
    }

    /**
     * Test of sidebar method, of class SinkAdapter.
     */
    @Test
    public void testSidebar() {
        instance.sidebar();
        instance.sidebar(null);
        instance.sidebar_();
    }

    /**
     * Test of section1 method, of class SinkAdapter.
     */
    @Test
    public void testSection1() {
        final int level = SinkAdapter.SECTION_LEVEL_1;
        instance.section1();
        instance.section1_();
        instance.section(level, null);
        instance.section_(level);
    }

    /**
     * Test of section2 method, of class SinkAdapter.
     */
    @Test
    public void testSection2() {
        final int level = SinkAdapter.SECTION_LEVEL_2;
        instance.section2();
        instance.section2_();
        instance.section(level, null);
        instance.section_(level);
    }

    /**
     * Test of section3 method, of class SinkAdapter.
     */
    @Test
    public void testSection3() {
        final int level = SinkAdapter.SECTION_LEVEL_3;
        instance.section3();
        instance.section3_();
        instance.section(level, null);
        instance.section_(level);
    }

    /**
     * Test of section4 method, of class SinkAdapter.
     */
    @Test
    public void testSection4() {
        final int level = SinkAdapter.SECTION_LEVEL_4;
        instance.section4();
        instance.section4_();
        instance.section(level, null);
        instance.section_(level);
    }

    /**
     * Test of section5 method, of class SinkAdapter.
     */
    @Test
    public void testSection5() {
        final int level = SinkAdapter.SECTION_LEVEL_5;
        instance.section5();
        instance.section5_();
        instance.section(level, null);
        instance.section_(level);
    }

    /**
     * Test of list method, of class SinkAdapter.
     */
    @Test
    public void testList() {
        instance.list();
        instance.list(null);
        instance.list_();
    }

    /**
     * Test of listItem method, of class SinkAdapter.
     */
    @Test
    public void testListItem() {
        instance.listItem();
        instance.listItem(null);
        instance.listItem_();
    }

    /**
     * Test of numberedList method, of class SinkAdapter.
     */
    @Test
    public void testNumberedList() {
        final int numbering = SinkAdapter.NUMBERING_DECIMAL;
        instance.numberedList(numbering);
        instance.numberedList(numbering, null);
        instance.numberedList_();
    }

    /**
     * Test of numberedListItem method, of class SinkAdapter.
     */
    @Test
    public void testNumberedListItem() {
        instance.numberedListItem();
        instance.numberedListItem(null);
        instance.numberedListItem_();
    }

    /**
     * Test of definitionList method, of class SinkAdapter.
     */
    @Test
    public void testDefinitionList() {
        instance.definitionList();
        instance.definitionList(null);
        instance.definitionList_();
    }

    /**
     * Test of definitionListItem method, of class SinkAdapter.
     */
    @Test
    public void testDefinitionListItem() {
        instance.definitionListItem();
        instance.definitionListItem(null);
        instance.definitionListItem_();
    }

    /**
     * Test of definition method, of class SinkAdapter.
     */
    @Test
    public void testDefinition() {
        instance.definition();
        instance.definition(null);
        instance.definition_();
    }

    /**
     * Test of figure method, of class SinkAdapter.
     */
    @Test
    public void testFigure() {
        instance.figure();
        instance.figure(null);
        instance.figure_();
    }

    /**
     * Test of table method, of class SinkAdapter.
     */
    @Test
    public void testTable() {
        instance.table();
        instance.table(null);
        instance.table_();
    }

    /**
     * Test of tableRows method, of class SinkAdapter.
     */
    @Test
    public void testTableRows() {
        instance.tableRows();
        instance.tableRows_();
    }

    /**
     * Test of tableRow method, of class SinkAdapter.
     */
    @Test
    public void testTableRow() {
        instance.tableRow();
        instance.tableRow(null);
        instance.tableRow_();
    }

    /**
     * Test of title method, of class SinkAdapter.
     */
    @Test
    public void testTitle() {
        instance.title();
        instance.title(null);
        instance.title_();
    }

    /**
     * Test of author method, of class SinkAdapter.
     */
    @Test
    public void testAuthor() {
        instance.author();
        instance.author(null);
        instance.author_();
    }

    /**
     * Test of date method, of class SinkAdapter.
     */
    @Test
    public void testDate() {
        instance.date();
        instance.date(null);
        instance.date_();
    }

    /**
     * Test of sectionTitle method, of class SinkAdapter.
     */
    @Test
    public void testSectionTitle() {
        final int level = SinkAdapter.SECTION_LEVEL_1;
        instance.sectionTitle();
        instance.sectionTitle_();
        instance.sectionTitle(level, null);
        instance.sectionTitle_(level);
    }

    /**
     * Test of sectionTitle1 method, of class SinkAdapter.
     */
    @Test
    public void testSectionTitle1() {
        final int level = SinkAdapter.SECTION_LEVEL_1;
        instance.sectionTitle1();
        instance.sectionTitle1_();
        instance.sectionTitle(level, null);
        instance.sectionTitle_(level);
    }

    /**
     * Test of sectionTitle2 method, of class SinkAdapter.
     */
    @Test
    public void testSectionTitle2() {
        final int level = SinkAdapter.SECTION_LEVEL_2;
        instance.sectionTitle2();
        instance.sectionTitle2_();
        instance.sectionTitle(level, null);
        instance.sectionTitle_(level);
    }

    /**
     * Test of sectionTitle3 method, of class SinkAdapter.
     */
    @Test
    public void testSectionTitle3() {
        final int level = SinkAdapter.SECTION_LEVEL_3;
        instance.sectionTitle3();
        instance.sectionTitle3_();
        instance.sectionTitle(level, null);
        instance.sectionTitle_(level);
    }

    /**
     * Test of sectionTitle4 method, of class SinkAdapter.
     */
    @Test
    public void testSectionTitle4() {
        final int level = SinkAdapter.SECTION_LEVEL_4;
        instance.sectionTitle4();
        instance.sectionTitle4_();
        instance.sectionTitle(level, null);
        instance.sectionTitle_(level);
    }

    /**
     * Test of sectionTitle5 method, of class SinkAdapter.
     */
    @Test
    public void testSectionTitle5() {
        final int level = SinkAdapter.SECTION_LEVEL_5;
        instance.sectionTitle5();
        instance.sectionTitle5_();
        instance.sectionTitle(level, null);
        instance.sectionTitle_(level);
    }

    /**
     * Test of header method, of class SinkAdapter.
     */
    @Test
    public void testHeader() {
        instance.header();
        instance.header(null);
        instance.header_();
    }

    /**
     * Test of content method, of class SinkAdapter.
     */
    @Test
    public void testContent() {
        instance.content();
        instance.content(null);
        instance.content_();
    }

    /**
     * Test of footer method, of class SinkAdapter.
     */
    @Test
    public void testFooter() {
        instance.footer();
        instance.footer(null);
        instance.footer_();
    }

    /**
     * Test of paragraph method, of class SinkAdapter.
     */
    @Test
    public void testParagraph() {
        instance.paragraph();
        instance.paragraph(null);
        instance.paragraph_();
    }

    /**
     * Test of data method, of class SinkAdapter.
     */
    @Test
    public void testData() {
        String value = "";
        instance.data(value);
        instance.data(value, null);
        instance.data_();
    }

    /**
     * Test of time method, of class SinkAdapter.
     */
    @Test
    public void testTime() {
        String datetime = "";
        instance.time(datetime);
        instance.time(datetime, null);
        instance.time_();
    }

    /**
     * Test of address method, of class SinkAdapter.
     */
    @Test
    public void testAddress() {
        instance.address();
        instance.address(null);
        instance.address_();
    }

    /**
     * Test of blockquote method, of class SinkAdapter.
     */
    @Test
    public void testBlockquote() {
        instance.blockquote();
        instance.blockquote(null);
        instance.blockquote_();
    }

    /**
     * Test of division method, of class SinkAdapter.
     */
    @Test
    public void testDivision() {
        instance.division();
        instance.division(null);
        instance.division_();
    }

    /**
     * Test of verbatim method, of class SinkAdapter.
     */
    @Test
    public void testVerbatim() {
        instance.verbatim(null);
        instance.verbatim_();
    }

    /**
     * Test of definedTerm method, of class SinkAdapter.
     */
    @Test
    public void testDefinedTerm() {
        instance.definedTerm();
        instance.definedTerm(null);
        instance.definedTerm_();
    }

    /**
     * Test of figureCaption method, of class SinkAdapter.
     */
    @Test
    public void testFigureCaption() {
        instance.figureCaption();
        instance.figureCaption(null);
        instance.figureCaption_();
    }

    /**
     * Test of tableCell method, of class SinkAdapter.
     */
    @Test
    public void testTableCell() {
        instance.tableCell();
        instance.tableCell((SinkEventAttributes) null);
        instance.tableCell_();
    }

    /**
     * Test of tableCaption method, of class SinkAdapter.
     */
    @Test
    public void testTableCaption() {
        instance.tableCaption();
        instance.tableCaption(null);
        instance.tableCaption_();
    }

    /**
     * Test of figureGraphics method, of class SinkAdapter.
     */
    @Test
    public void testFigureGraphics() {
        String name = "";
        instance.figureGraphics(name);
        instance.figureGraphics(name, null);
    }

    /**
     * Test of horizontalRule method, of class SinkAdapter.
     */
    @Test
    public void testHorizontalRule() {
        instance.horizontalRule();
        instance.horizontalRule(null);
    }

    /**
     * Test of pageBreak method, of class SinkAdapter.
     */
    @Test
    public void testPageBreak() {
        instance.pageBreak();
    }

    /**
     * Test of anchor method, of class SinkAdapter.
     */
    @Test
    public void testAnchor() {
        String name = "";
        instance.anchor(name);
        instance.anchor(name, null);
        instance.anchor_();
    }

    /**
     * Test of link method, of class SinkAdapter.
     */
    @Test
    public void testLink() {
        String name = "";
        instance.link(name);
        instance.link(name, null);
        instance.link_();
    }

    /**
     * Test of inline method, of class SinkAdapter.
     */
    @Test
    public void testInline() {
        instance.inline();
        instance.inline(null);
        instance.inline_();
    }

    /**
     * Test of italic method, of class SinkAdapter.
     */
    @Test
    public void testItalic() {
        instance.inline(SinkEventAttributeSet.Semantics.ITALIC);
        instance.inline_();
    }

    /**
     * Test of bold method, of class SinkAdapter.
     */
    @Test
    public void testBold() {
        instance.inline(SinkEventAttributeSet.Semantics.BOLD);
        instance.inline_();
    }

    /**
     * Test of monospaced method, of class SinkAdapter.
     */
    @Test
    public void testMonospaced() {
        instance.inline(SinkEventAttributeSet.Semantics.MONOSPACED);
        instance.inline_();
    }

    /**
     * Test of lineBreak method, of class SinkAdapter.
     */
    @Test
    public void testLineBreaks() {
        instance.lineBreak();
        instance.lineBreak(null);
    }

    /**
     * Test of lineBreakOpportunity method, of class SinkAdapter.
     */
    @Test
    public void testLineBreakOpportunities() {
        instance.lineBreakOpportunity();
        instance.lineBreakOpportunity(null);
    }

    /**
     * Test of nonBreakingSpace method, of class SinkAdapter.
     */
    @Test
    public void testNonBreakingSpace() {
        instance.nonBreakingSpace();
    }

    /**
     * Test of text method, of class SinkAdapter.
     */
    @Test
    public void testText() {
        String text = "";
        instance.text(text);
        instance.text(text, null);
    }

    /**
     * Test of rawText method, of class SinkAdapter.
     */
    @Test
    public void testRawText() {
        String text = "";
        instance.rawText(text);
    }

    /**
     * Test of comment method, of class SinkAdapter.
     */
    @Test
    public void testComment() {
        instance.comment("");
    }

    /**
     * Test of flush method, of class SinkAdapter.
     */
    @Test
    public void testFlush() {
        instance.flush();
    }

    /**
     * Test of close method, of class SinkAdapter.
     */
    @Test
    public void testClose() {
        instance.close();
    }

    /**
     * Test of section method, of class SinkAdapter.
     */
    @Test
    public void testSection() {
        int level = 0;
        instance.section(level, null);
        instance.section_(level);
    }

    /**
     * Test of unknown method, of class SinkAdapter.
     */
    @Test
    public void testUnknown() {
        String name = "";
        Object[] requiredParams = null;
        instance.unknown(name, requiredParams, null);
    }
}
