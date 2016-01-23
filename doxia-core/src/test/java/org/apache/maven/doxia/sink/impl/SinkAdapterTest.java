package org.apache.maven.doxia.sink.impl;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkAdapter;

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

import junit.framework.TestCase;

/**
 *
 * @author ltheussl
 */
public class SinkAdapterTest
        extends TestCase
{
    private final SinkAdapter instance = new SinkAdapter();

    /**
     * Test of head method, of class SinkAdapter.
     */
    public void testHead()
    {
        instance.head();
        instance.head( null );
        instance.head_();
    }

    /**
     * Test of body method, of class SinkAdapter.
     */
    public void testBody()
    {
        instance.body();
        instance.body( null );
        instance.body_();
    }

    /**
     * Test of section1 method, of class SinkAdapter.
     */
    public void testSection1()
    {
        final int level = SinkAdapter.SECTION_LEVEL_1;
        instance.section1();
        instance.section1_();
        instance.section( level, null );
        instance.section_( level );
    }

    /**
     * Test of section2 method, of class SinkAdapter.
     */
    public void testSection2()
    {
        final int level = SinkAdapter.SECTION_LEVEL_2;
        instance.section2();
        instance.section2_();
        instance.section( level, null );
        instance.section_( level );
    }

    /**
     * Test of section3 method, of class SinkAdapter.
     */
    public void testSection3()
    {
        final int level = SinkAdapter.SECTION_LEVEL_3;
        instance.section3();
        instance.section3_();
        instance.section( level, null );
        instance.section_( level );
    }

    /**
     * Test of section4 method, of class SinkAdapter.
     */
    public void testSection4()
    {
        final int level = SinkAdapter.SECTION_LEVEL_4;
        instance.section4();
        instance.section4_();
        instance.section( level, null );
        instance.section_( level );
    }

    /**
     * Test of section5 method, of class SinkAdapter.
     */
    public void testSection5()
    {
        final int level = SinkAdapter.SECTION_LEVEL_5;
        instance.section5();
        instance.section5_();
        instance.section( level, null );
        instance.section_( level );
    }

    /**
     * Test of list method, of class SinkAdapter.
     */
    public void testList()
    {
        instance.list();
        instance.list( null );
        instance.list_();
    }

    /**
     * Test of listItem method, of class SinkAdapter.
     */
    public void testListItem()
    {
        instance.listItem();
        instance.listItem( null );
        instance.listItem_();
    }

    /**
     * Test of numberedList method, of class SinkAdapter.
     */
    public void testNumberedList()
    {
        final int numbering = SinkAdapter.NUMBERING_DECIMAL;
        instance.numberedList( numbering );
        instance.numberedList( numbering, null );
        instance.numberedList_();
    }

    /**
     * Test of numberedListItem method, of class SinkAdapter.
     */
    public void testNumberedListItem()
    {
        instance.numberedListItem();
        instance.numberedListItem( null );
        instance.numberedListItem_();
    }

    /**
     * Test of definitionList method, of class SinkAdapter.
     */
    public void testDefinitionList()
    {
        instance.definitionList();
        instance.definitionList( null );
        instance.definitionList_();
    }

    /**
     * Test of definitionListItem method, of class SinkAdapter.
     */
    public void testDefinitionListItem()
    {
        instance.definitionListItem();
        instance.definitionListItem( null );
        instance.definitionListItem_();
    }

    /**
     * Test of definition method, of class SinkAdapter.
     */
    public void testDefinition()
    {
        instance.definition();
        instance.definition( null );
        instance.definition_();
    }

    /**
     * Test of figure method, of class SinkAdapter.
     */
    public void testFigure()
    {
        instance.figure();
        instance.figure( null );
        instance.figure_();
    }

    /**
     * Test of table method, of class SinkAdapter.
     */
    public void testTable()
    {
        instance.table();
        instance.table( null );
        instance.table_();
    }

    /**
     * Test of tableRows method, of class SinkAdapter.
     */
    public void testTableRows()
    {
        final int[] justification = null;
        final boolean grid = false;
        instance.tableRows( justification, grid );
        instance.tableRows_();
    }

    /**
     * Test of tableRow method, of class SinkAdapter.
     */
    public void testTableRow()
    {
        instance.tableRow();
        instance.tableRow( null );
        instance.tableRow_();
    }

    /**
     * Test of title method, of class SinkAdapter.
     */
    public void testTitle()
    {
        instance.title();
        instance.title( null );
        instance.title_();
    }

    /**
     * Test of author method, of class SinkAdapter.
     */
    public void testAuthor()
    {
        instance.author();
        instance.author( null );
        instance.author_();
    }

    /**
     * Test of date method, of class SinkAdapter.
     */
    public void testDate()
    {
        instance.date();
        instance.date( null );
        instance.date_();
    }

    /**
     * Test of sectionTitle method, of class SinkAdapter.
     */
    public void testSectionTitle()
    {
        final int level = SinkAdapter.SECTION_LEVEL_1;
        instance.sectionTitle();
        instance.sectionTitle_();
        instance.sectionTitle( level, null );
        instance.sectionTitle_( level );
    }

    /**
     * Test of sectionTitle1 method, of class SinkAdapter.
     */
    public void testSectionTitle1()
    {
        final int level = SinkAdapter.SECTION_LEVEL_1;
        instance.sectionTitle1();
        instance.sectionTitle1_();
        instance.sectionTitle( level, null );
        instance.sectionTitle_( level );
    }

    /**
     * Test of sectionTitle2 method, of class SinkAdapter.
     */
    public void testSectionTitle2()
    {
        final int level = SinkAdapter.SECTION_LEVEL_2;
        instance.sectionTitle2();
        instance.sectionTitle2_();
        instance.sectionTitle( level, null );
        instance.sectionTitle_( level );
    }

    /**
     * Test of sectionTitle3 method, of class SinkAdapter.
     */
    public void testSectionTitle3()
    {
        final int level = SinkAdapter.SECTION_LEVEL_3;
        instance.sectionTitle3();
        instance.sectionTitle3_();
        instance.sectionTitle( level, null );
        instance.sectionTitle_( level );
    }

    /**
     * Test of sectionTitle4 method, of class SinkAdapter.
     */
    public void testSectionTitle4()
    {
        final int level = SinkAdapter.SECTION_LEVEL_4;
        instance.sectionTitle4();
        instance.sectionTitle4_();
        instance.sectionTitle( level, null );
        instance.sectionTitle_( level );
    }

    /**
     * Test of sectionTitle5 method, of class SinkAdapter.
     */
    public void testSectionTitle5()
    {
        final int level = SinkAdapter.SECTION_LEVEL_5;
        instance.sectionTitle5();
        instance.sectionTitle5_();
        instance.sectionTitle( level, null );
        instance.sectionTitle_( level );
    }

    /**
     * Test of paragraph method, of class SinkAdapter.
     */
    public void testParagraph()
    {
        instance.paragraph();
        instance.paragraph( null );
        instance.paragraph_();
    }

    /**
     * Test of verbatim method, of class SinkAdapter.
     */
    public void testVerbatim()
    {
        instance.verbatim( null );
        instance.verbatim( false );
        instance.verbatim_();
    }

    /**
     * Test of definedTerm method, of class SinkAdapter.
     */
    public void testDefinedTerm()
    {
        instance.definedTerm();
        instance.definedTerm( null );
        instance.definedTerm_();
    }

    /**
     * Test of figureCaption method, of class SinkAdapter.
     */
    public void testFigureCaption()
    {
        instance.figureCaption();
        instance.figureCaption( null );
        instance.figureCaption_();
    }

    /**
     * Test of tableCell method, of class SinkAdapter.
     */
    public void testTableCell()
    {
        instance.tableCell();
        instance.tableCell( (SinkEventAttributes) null );
        instance.tableCell( (String) null );
        instance.tableCell_();
    }

    /**
     * Test of tableHeaderCell method, of class SinkAdapter.
     */
    public void testTableHeaderCell()
    {
        instance.tableHeaderCell();
        instance.tableHeaderCell( (SinkEventAttributes) null );
        instance.tableHeaderCell( (String) null );
        instance.tableHeaderCell_();
    }

    /**
     * Test of tableCaption method, of class SinkAdapter.
     */
    public void testTableCaption()
    {
        instance.tableCaption();
        instance.tableCaption( null );
        instance.tableCaption_();
    }

    /**
     * Test of figureGraphics method, of class SinkAdapter.
     */
    public void testFigureGraphics()
    {
        String name = "";
        instance.figureGraphics( name );
        instance.figureGraphics( name, null );
    }

    /**
     * Test of horizontalRule method, of class SinkAdapter.
     */
    public void testHorizontalRule()
    {
        instance.horizontalRule();
        instance.horizontalRule( null );
    }

    /**
     * Test of pageBreak method, of class SinkAdapter.
     */
    public void testPageBreak()
    {
        instance.pageBreak();
    }

    /**
     * Test of anchor method, of class SinkAdapter.
     */
    public void testAnchor()
    {
        String name = "";
        instance.anchor( name );
        instance.anchor( name, null );
        instance.anchor_();
    }

    /**
     * Test of link method, of class SinkAdapter.
     */
    public void testLink()
    {
        String name = "";
        instance.link( name );
        instance.link( name, null );
        instance.link_();
    }

    /**
     * Test of italic method, of class SinkAdapter.
     */
    public void testItalic()
    {
        instance.italic();
        instance.italic_();
    }

    /**
     * Test of bold method, of class SinkAdapter.
     */
    public void testBold()
    {
        instance.bold();
        instance.bold_();
    }

    /**
     * Test of monospaced method, of class SinkAdapter.
     */
    public void testMonospaced()
    {
        instance.monospaced();
        instance.monospaced_();
    }

    /**
     * Test of lineBreak method, of class SinkAdapter.
     */
    public void testLineBreaks()
    {
        instance.lineBreak();
        instance.lineBreak( null );
    }

    /**
     * Test of nonBreakingSpace method, of class SinkAdapter.
     */
    public void testNonBreakingSpace()
    {
        instance.nonBreakingSpace();
    }

    /**
     * Test of text method, of class SinkAdapter.
     */
    public void testText()
    {
        String text = "";
        instance.text( text );
        instance.text( text, null );
    }

    /**
     * Test of rawText method, of class SinkAdapter.
     */
    public void testRawText()
    {
        String text = "";
        instance.rawText( text );
    }

    /**
     * Test of comment method, of class SinkAdapter.
     */
    public void testComment()
    {
        instance.comment( "" );
    }

    /**
     * Test of flush method, of class SinkAdapter.
     */
    public void testFlush()
    {
        instance.flush();
    }

    /**
     * Test of close method, of class SinkAdapter.
     */
    public void testClose()
    {
        instance.close();
    }

    /**
     * Test of section method, of class SinkAdapter.
     */
    public void testSection()
    {
        int level = 0;
        instance.section( level, null );
        instance.section_( level );
    }

    /**
     * Test of unknown method, of class SinkAdapter.
     */
    public void testUnknown()
    {
        String name = "";
        Object[] requiredParams = null;
        instance.unknown( name, requiredParams, null );
    }
}
