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
class SinkAdapterTest {
    private final SinkAdapter instance = new SinkAdapter();

    /**
     * Test of head method, of class SinkAdapter.
     */
    @Test
    void head() {
        instance.head();
        instance.head(null);
        instance.head_();
    }

    /**
     * Test of body method, of class SinkAdapter.
     */
    @Test
    void body() {
        instance.body();
        instance.body(null);
        instance.body_();
    }

    /**
     * Test of article method, of class SinkAdapter.
     */
    @Test
    void article() {
        instance.article();
        instance.article(null);
        instance.article_();
    }

    /**
     * Test of navigation method, of class SinkAdapter.
     */
    @Test
    void navigation() {
        instance.navigation();
        instance.navigation(null);
        instance.navigation_();
    }

    /**
     * Test of sidebar method, of class SinkAdapter.
     */
    @Test
    void sidebar() {
        instance.sidebar();
        instance.sidebar(null);
        instance.sidebar_();
    }

    /**
     * Test of section1 method, of class SinkAdapter.
     */
    @Test
    void section1() {
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
    void section2() {
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
    void section3() {
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
    void section4() {
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
    void section5() {
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
    void list() {
        instance.list();
        instance.list(null);
        instance.list_();
    }

    /**
     * Test of listItem method, of class SinkAdapter.
     */
    @Test
    void listItem() {
        instance.listItem();
        instance.listItem(null);
        instance.listItem_();
    }

    /**
     * Test of numberedList method, of class SinkAdapter.
     */
    @Test
    void numberedList() {
        final int numbering = SinkAdapter.NUMBERING_DECIMAL;
        instance.numberedList(numbering);
        instance.numberedList(numbering, null);
        instance.numberedList_();
    }

    /**
     * Test of numberedListItem method, of class SinkAdapter.
     */
    @Test
    void numberedListItem() {
        instance.numberedListItem();
        instance.numberedListItem(null);
        instance.numberedListItem_();
    }

    /**
     * Test of definitionList method, of class SinkAdapter.
     */
    @Test
    void definitionList() {
        instance.definitionList();
        instance.definitionList(null);
        instance.definitionList_();
    }

    /**
     * Test of definitionListItem method, of class SinkAdapter.
     */
    @Test
    void definitionListItem() {
        instance.definitionListItem();
        instance.definitionListItem(null);
        instance.definitionListItem_();
    }

    /**
     * Test of definition method, of class SinkAdapter.
     */
    @Test
    void definition() {
        instance.definition();
        instance.definition(null);
        instance.definition_();
    }

    /**
     * Test of figure method, of class SinkAdapter.
     */
    @Test
    void figure() {
        instance.figure();
        instance.figure(null);
        instance.figure_();
    }

    /**
     * Test of table method, of class SinkAdapter.
     */
    @Test
    void table() {
        instance.table();
        instance.table(null);
        instance.table_();
    }

    /**
     * Test of tableRows method, of class SinkAdapter.
     */
    @Test
    void tableRows() {
        instance.tableRows();
        instance.tableRows_();
    }

    /**
     * Test of tableRow method, of class SinkAdapter.
     */
    @Test
    void tableRow() {
        instance.tableRow();
        instance.tableRow(null);
        instance.tableRow_();
    }

    /**
     * Test of title method, of class SinkAdapter.
     */
    @Test
    void title() {
        instance.title();
        instance.title(null);
        instance.title_();
    }

    /**
     * Test of author method, of class SinkAdapter.
     */
    @Test
    void author() {
        instance.author();
        instance.author(null);
        instance.author_();
    }

    /**
     * Test of date method, of class SinkAdapter.
     */
    @Test
    void date() {
        instance.date();
        instance.date(null);
        instance.date_();
    }

    /**
     * Test of sectionTitle1 method, of class SinkAdapter.
     */
    @Test
    void sectionTitle1() {
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
    void sectionTitle2() {
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
    void sectionTitle3() {
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
    void sectionTitle4() {
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
    void sectionTitle5() {
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
    void header() {
        instance.header();
        instance.header(null);
        instance.header_();
    }

    /**
     * Test of content method, of class SinkAdapter.
     */
    @Test
    void content() {
        instance.content();
        instance.content(null);
        instance.content_();
    }

    /**
     * Test of footer method, of class SinkAdapter.
     */
    @Test
    void footer() {
        instance.footer();
        instance.footer(null);
        instance.footer_();
    }

    /**
     * Test of paragraph method, of class SinkAdapter.
     */
    @Test
    void paragraph() {
        instance.paragraph();
        instance.paragraph(null);
        instance.paragraph_();
    }

    /**
     * Test of data method, of class SinkAdapter.
     */
    @Test
    void data() {
        String value = "";
        instance.data(value);
        instance.data(value, null);
        instance.data_();
    }

    /**
     * Test of time method, of class SinkAdapter.
     */
    @Test
    void time() {
        String datetime = "";
        instance.time(datetime);
        instance.time(datetime, null);
        instance.time_();
    }

    /**
     * Test of address method, of class SinkAdapter.
     */
    @Test
    void address() {
        instance.address();
        instance.address(null);
        instance.address_();
    }

    /**
     * Test of blockquote method, of class SinkAdapter.
     */
    @Test
    void blockquote() {
        instance.blockquote();
        instance.blockquote(null);
        instance.blockquote_();
    }

    /**
     * Test of division method, of class SinkAdapter.
     */
    @Test
    void division() {
        instance.division();
        instance.division(null);
        instance.division_();
    }

    /**
     * Test of verbatim method, of class SinkAdapter.
     */
    @Test
    void verbatim() {
        instance.verbatim(null);
        instance.verbatim_();
    }

    /**
     * Test of definedTerm method, of class SinkAdapter.
     */
    @Test
    void definedTerm() {
        instance.definedTerm();
        instance.definedTerm(null);
        instance.definedTerm_();
    }

    /**
     * Test of figureCaption method, of class SinkAdapter.
     */
    @Test
    void figureCaption() {
        instance.figureCaption();
        instance.figureCaption(null);
        instance.figureCaption_();
    }

    /**
     * Test of tableCell method, of class SinkAdapter.
     */
    @Test
    void tableCell() {
        instance.tableCell();
        instance.tableCell((SinkEventAttributes) null);
        instance.tableCell_();
    }

    /**
     * Test of tableCaption method, of class SinkAdapter.
     */
    @Test
    void tableCaption() {
        instance.tableCaption();
        instance.tableCaption(null);
        instance.tableCaption_();
    }

    /**
     * Test of figureGraphics method, of class SinkAdapter.
     */
    @Test
    void figureGraphics() {
        String name = "";
        instance.figureGraphics(name);
        instance.figureGraphics(name, null);
    }

    /**
     * Test of horizontalRule method, of class SinkAdapter.
     */
    @Test
    void horizontalRule() {
        instance.horizontalRule();
        instance.horizontalRule(null);
    }

    /**
     * Test of pageBreak method, of class SinkAdapter.
     */
    @Test
    void pageBreak() {
        instance.pageBreak();
    }

    /**
     * Test of anchor method, of class SinkAdapter.
     */
    @Test
    void anchor() {
        String name = "";
        instance.anchor(name);
        instance.anchor(name, null);
        instance.anchor_();
    }

    /**
     * Test of link method, of class SinkAdapter.
     */
    @Test
    void link() {
        String name = "";
        instance.link(name);
        instance.link(name, null);
        instance.link_();
    }

    /**
     * Test of inline method, of class SinkAdapter.
     */
    @Test
    void inline() {
        instance.inline();
        instance.inline(null);
        instance.inline_();
    }

    /**
     * Test of italic method, of class SinkAdapter.
     */
    @Test
    void italic() {
        instance.inline(SinkEventAttributeSet.Semantics.ITALIC);
        instance.inline_();
    }

    /**
     * Test of bold method, of class SinkAdapter.
     */
    @Test
    void bold() {
        instance.inline(SinkEventAttributeSet.Semantics.BOLD);
        instance.inline_();
    }

    /**
     * Test of monospaced method, of class SinkAdapter.
     */
    @Test
    void monospaced() {
        instance.inline(SinkEventAttributeSet.Semantics.MONOSPACED);
        instance.inline_();
    }

    /**
     * Test of lineBreak method, of class SinkAdapter.
     */
    @Test
    void lineBreaks() {
        instance.lineBreak();
        instance.lineBreak(null);
    }

    /**
     * Test of lineBreakOpportunity method, of class SinkAdapter.
     */
    @Test
    void lineBreakOpportunities() {
        instance.lineBreakOpportunity();
        instance.lineBreakOpportunity(null);
    }

    /**
     * Test of nonBreakingSpace method, of class SinkAdapter.
     */
    @Test
    void nonBreakingSpace() {
        instance.nonBreakingSpace();
    }

    /**
     * Test of text method, of class SinkAdapter.
     */
    @Test
    void text() {
        String text = "";
        instance.text(text);
        instance.text(text, null);
    }

    /**
     * Test of rawText method, of class SinkAdapter.
     */
    @Test
    void rawText() {
        String text = "";
        instance.rawText(text);
    }

    /**
     * Test of comment method, of class SinkAdapter.
     */
    @Test
    void comment() {
        instance.comment("");
    }

    /**
     * Test of flush method, of class SinkAdapter.
     */
    @Test
    void flush() {
        instance.flush();
    }

    /**
     * Test of close method, of class SinkAdapter.
     */
    @Test
    void close() {
        instance.close();
    }

    /**
     * Test of section method, of class SinkAdapter.
     */
    @Test
    void section() {
        int level = 0;
        instance.section(level, null);
        instance.section_(level);
    }

    /**
     * Test of unknown method, of class SinkAdapter.
     */
    @Test
    void unknown() {
        String name = "";
        Object[] requiredParams = null;
        instance.unknown(name, requiredParams, null);
    }
}
