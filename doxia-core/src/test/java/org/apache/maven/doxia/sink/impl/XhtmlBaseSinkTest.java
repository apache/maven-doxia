package org.apache.maven.doxia.sink.impl;

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

import java.io.StringWriter;
import java.io.Writer;

import javax.swing.text.html.HTML.Attribute;

import junit.framework.TestCase;

import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.XhtmlBaseSink;

/**
 * Test for XhtmlBaseSink.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class XhtmlBaseSinkTest
    extends TestCase
{
    protected static final String LS = Markup.EOL;
    private final SinkEventAttributes attributes = SinkEventAttributeSet.BOLD;
    private XhtmlBaseSink sink;
    private Writer writer;

    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        writer =  new StringWriter();
    }

    public void testSpaceAfterClosingTag()
        throws Exception
    {
        // DOXIA-189
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.paragraph();
            sink.text( "There should be no space before the " );
            sink.inline( SinkEventAttributeSet.Semantics.ITALIC );
            sink.text( "period" );
            sink.inline_();
            sink.text( "." );
            sink.paragraph_();
        }
        finally
        {
            if ( sink != null )
            {
                sink.close();
            }
        }

        String actual = writer.toString();
        String expected = "<p>There should be no space before the <i>period</i>.</p>";

        assertEquals( expected, actual );
    }

    /**
     * @throws Exception if any
     */
    public void testNestedTables()
        throws Exception
    {
        // DOXIA-177
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.table();
            sink.tableRows( new int[] { Sink.JUSTIFY_CENTER }, false );
            sink.tableRow();
            sink.tableCell();
            sink.text( "cell11" );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "cell12" );
            sink.tableCell_();
            sink.tableRow_();

            sink.tableRow();
            sink.tableCell();
            sink.table( SinkEventAttributeSet.LEFT );
            sink.tableRows( new int[] { Sink.JUSTIFY_LEFT }, false );
            sink.tableRow();
            sink.tableCell();
            sink.text( "nestedTable1Cell11" );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "nestedTable1Cell12" );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRow();
            sink.tableCell();

            sink.table( SinkEventAttributeSet.RIGHT );
            sink.tableRows( new int[] { Sink.JUSTIFY_RIGHT }, false );
            sink.tableRow();
            sink.tableCell();
            sink.text( "nestedTable2Cell11" );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "nestedTable2Cell12" );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRow();
            sink.tableCell();
            sink.text( "nestedTable2Cell21" );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "nestedTable2Cell22" );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text( "caption3" );
            sink.tableCaption_();
            sink.table_();

            sink.tableCell_();
            sink.tableCell();
            sink.text( "nestedTable1Cell22" );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text( "caption2" );
            sink.tableCaption_();
            sink.table_();

            sink.tableCell_();
            sink.tableCell();
            sink.text( "cell22" );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text( "caption1" );
            sink.tableCaption_();
            sink.table_();
        }
        finally
        {
            sink.close();
        }

        String actual = writer.toString();
        assertTrue( actual.indexOf( "<table align=\"center\" border=\"0\" class=\"bodyTable\">"
            + "<caption>caption1</caption>" ) != 1 );
        assertTrue( actual.indexOf( "<table border=\"0\" class=\"bodyTable\" align=\"left\">"
            + "<caption>caption2</caption>" ) != 1 );
        assertTrue( actual.indexOf( "<table align=\"center\" border=\"0\" class=\"bodyTable\">"
            + "<caption>caption3</caption>" ) != 1 );

        assertTrue( actual.indexOf( "<td align=\"center\">cell11</td>" ) != 1 );
        assertTrue( actual.indexOf( "<td align=\"left\">nestedTable1Cell11</td>" ) != 1 );
        assertTrue( actual.indexOf( "<td align=\"right\">nestedTable2Cell11</td>" ) != 1 );
        assertTrue( actual.indexOf( "<td align=\"left\">nestedTable1Cell22</td>" ) != 1 );
        assertTrue( actual.indexOf( "<td align=\"center\">cell22</td>" ) != 1 );
    }

    /**
     * Test of section method, of class XhtmlBaseSink.
     */
    public void testSection()
    {
        final int level = XhtmlBaseSink.SECTION_LEVEL_1;

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section( level, attributes );
            sink.sectionTitle( level, attributes );
            sink.sectionTitle_( level );
            sink.section_( level );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\" style=\"bold\">" + LS + "<h2 style=\"bold\"></h2></div>", writer.toString() );
    }

    /**
     * Test of section method, of class XhtmlBaseSink.
     */
    public void testSectionAttributes()
    {
        final int level = XhtmlBaseSink.SECTION_LEVEL_1;
        final SinkEventAttributeSet set = new SinkEventAttributeSet( "name", "section name", "class", "foo", "id",
                "bar" );

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section( level, set );
            sink.sectionTitle( level, null );
            sink.sectionTitle_( level );
            sink.section_( level );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"foo\" id=\"bar\">" + LS + "<h2></h2></div>", writer.toString() );
    }

    /**
     * Test of section1 method, of class XhtmlBaseSink.
     */
    public void testSection1()
    {

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section1();
            sink.sectionTitle1();
            sink.sectionTitle1_();
            sink.section1_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\">" + LS + "<h2></h2></div>", writer.toString() );
    }

    /**
     * Test of section2 method, of class XhtmlBaseSink.
     */
    public void testSection2()
    {

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section2();
            sink.sectionTitle2();
            sink.sectionTitle2_();
            sink.section2_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\">" + LS + "<h3></h3></div>", writer.toString() );
    }

    /**
     * Test of section3 method, of class XhtmlBaseSink.
     */
    public void testSection3()
    {

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section3();
            sink.sectionTitle3();
            sink.sectionTitle3_();
            sink.section3_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\">" + LS + "<h4></h4></div>", writer.toString() );
    }

    /**
     * Test of section4 method, of class XhtmlBaseSink.
     */
    public void testSection4()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section4();
            sink.sectionTitle4();
            sink.sectionTitle4_();
            sink.section4_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\">" + LS + "<h5></h5></div>", writer.toString() );
    }

    /**
     * Test of section5 method, of class XhtmlBaseSink.
     */
    public void testSection5()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section5();
            sink.sectionTitle5();
            sink.sectionTitle5_();
            sink.section5_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\">" + LS + "<h6></h6></div>", writer.toString() );
    }

    /**
     * Test of list method, of class XhtmlBaseSink.
     * @throws java.lang.Exception if any.
     */
    public void testList()
            throws Exception
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.list();
            sink.listItem();
            sink.listItem_();
            sink.list_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ul>" + LS + "<li></li></ul>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.list( attributes );
            sink.listItem( attributes );
            sink.listItem_();
            sink.list_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ul style=\"bold\">" + LS + "<li style=\"bold\"></li></ul>", writer.toString() );
    }

    /**
     * Test of numberedList method, of class XhtmlBaseSink.
     */
    public void testNumberedList()
    {
        final int numbering = XhtmlBaseSink.NUMBERING_DECIMAL;

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.numberedList( numbering );
            sink.numberedListItem();
            sink.numberedListItem_();
            sink.numberedList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ol style=\"list-style-type: decimal\">" + LS + "<li></li></ol>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.numberedList( numbering, attributes );
            sink.numberedListItem( attributes );
            sink.numberedListItem_();
            sink.numberedList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ol style=\"list-style-type: decimal\">" + LS + "<li style=\"bold\"></li></ol>", writer.toString() );
    }

    /**
     * Test of definitionList method, of class XhtmlBaseSink.
     */
    public void testDefinitionList()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.definitionList();
            sink.definedTerm();
            sink.definedTerm_();
            sink.definition();
            sink.definition_();
            sink.definitionList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<dl>" + LS + "<dt></dt>" + LS + "<dd></dd></dl>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.definitionList( attributes );
            sink.definedTerm( attributes );
            sink.definedTerm_();
            sink.definition( attributes );
            sink.definition_();
            sink.definitionList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<dl style=\"bold\">" + LS + "<dt style=\"bold\"></dt>" + LS + "<dd style=\"bold\"></dd></dl>", writer.toString() );
    }

    /**
     * Test of figure method, of class XhtmlBaseSink.
     */
    public void testFigure()
    {
        final String src = "src.jpg";

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.figure( attributes );
            sink.figureGraphics( src, attributes );
            sink.figureCaption( attributes );
            sink.figureCaption_();
            sink.figure_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div style=\"bold\" class=\"figure\">"
                + "" + LS + "<p align=\"center\"><img src=\"src.jpg\" style=\"bold\" alt=\"\" /></p>"
                + "" + LS + "<p align=\"center\" style=\"bold\"><i></i></p></div>", writer.toString() );
    }

    /**
     * Test of figureGraphics method, of class XhtmlBaseSink.
     */
    public void testFigureGraphics()
    {
        String src = "source.png";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.figureGraphics( src, attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<img src=\"source.png\" style=\"bold\" alt=\"\" />", writer.toString() );
    }

    /**
     * Test of paragraph method, of class XhtmlBaseSink.
     */
    public void testParagraph()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.paragraph();
            sink.paragraph_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<p></p>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.paragraph( attributes );
            sink.paragraph_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<p style=\"bold\"></p>", writer.toString() );
    }

    /**
     * Test of verbatim method, of class XhtmlBaseSink.
     */
    public void testVerbatim()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.verbatim( true );
            sink.verbatim_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"source\">" + LS + "<pre></pre></div>", writer.toString() );

        checkVerbatimAttributes( attributes, "<div>" + LS + "<pre style=\"bold\"></pre></div>" );

        final SinkEventAttributes att =
            new SinkEventAttributeSet( SinkEventAttributes.ID, "id" );
        checkVerbatimAttributes( att, "<div>" + LS + "<pre id=\"id\"></pre></div>" );

        att.addAttribute( Attribute.CLASS, "class" );
        checkVerbatimAttributes( att, "<div>" + LS + "<pre id=\"id\" class=\"class\"></pre></div>" );

        att.addAttribute( SinkEventAttributes.DECORATION, "boxed" );
        checkVerbatimAttributes( att, "<div class=\"source\">" + LS + "<pre id=\"id\" class=\"class\"></pre></div>" );

        att.removeAttribute( Attribute.CLASS.toString() );
        checkVerbatimAttributes( att, "<div class=\"source\">" + LS + "<pre id=\"id\"></pre></div>" );
    }

    private void checkVerbatimAttributes( final SinkEventAttributes att, final String expected )
    {

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.verbatim( att );
            sink.verbatim_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( expected, writer.toString() );
    }

    /**
     * Test of horizontalRule method, of class XhtmlBaseSink.
     */
    public void testHorizontalRule()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.horizontalRule();
            sink.horizontalRule( attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<hr /><hr style=\"bold\" />", writer.toString() );
    }

    /**
     * Test of table method, of class XhtmlBaseSink.
     */
    public void testTable()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.table( attributes );
            sink.table_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "</table>", writer.toString() );
    }

    /**
     * Test of tableRows method, of class XhtmlBaseSink.
     */
    public void testTableRows()
    {
        final int[] justification = null;
        final boolean grid = false;

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableRows( justification, grid );
            sink.tableRows_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<table border=\"0\" class=\"bodyTable\">", writer.toString() );
    }

    /**
     * Test of tableRow method, of class XhtmlBaseSink.
     */
    public void testTableRow()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableRow( attributes );
            sink.tableRow_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<tr class=\"a\" style=\"bold\"></tr>", writer.toString() );
    }

    /**
     * Test of tableCell method, of class XhtmlBaseSink.
     */
    public void testTableCell()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableCell( attributes );
            sink.tableCell_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<td style=\"bold\"></td>", writer.toString() );
    }

    /**
     * Test of tableHeaderCell method, of class XhtmlBaseSink.
     */
    public void testTableHeaderCell()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableHeaderCell( attributes );
            sink.tableHeaderCell_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<th style=\"bold\"></th>", writer.toString() );
    }

    /**
     * Test of tableCaption method, of class XhtmlBaseSink.
     */
    public void testTableCaption()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.table();
            sink.tableRows( null, false );
            sink.tableCaption( attributes );
            sink.text( "caption" );
            sink.tableCaption_();
            sink.tableRows_();
            sink.table_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<table border=\"0\" class=\"bodyTable\">" +
                "<caption style=\"bold\">caption</caption></table>", writer.toString() );
    }

    /**
     * Test of anchor method, of class XhtmlBaseSink.
     */
    public void testAnchor()
    {
        String name = "anchor";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.anchor( name, attributes );
            sink.anchor_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<a name=\"anchor\" style=\"bold\"></a>", writer.toString() );
    }

    /**
     * Test of link method, of class XhtmlBaseSink.
     */
    public void testLink()
    {
        final String name = "link.html";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.link( name, attributes );
            sink.link_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<a href=\"link.html\" style=\"bold\"></a>", writer.toString() );
    }

    /**
     * Test of italic/bold/monospaced method, of class XhtmlBaseSink.
     */
    public void testItalic()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.inline( SinkEventAttributeSet.Semantics.ITALIC );
            sink.inline_();
            sink.inline( SinkEventAttributeSet.Semantics.BOLD );
            sink.inline_();
            sink.inline( SinkEventAttributeSet.Semantics.MONOSPACED );
            sink.inline_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<i></i><b></b><tt></tt>", writer.toString() );
    }

    /**
     * Test of lineBreak/pageBreak/nonBreakingSpace method, of class XhtmlBaseSink.
     */
    public void testLineBreak()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.lineBreak( attributes );
            sink.pageBreak();
            sink.nonBreakingSpace();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<br style=\"bold\" /><!-- PB -->&#160;", writer.toString() );
    }

    /**
     * Test of text method, of class XhtmlBaseSink.
     */
    public void testText()
    {
        String text = "a text & \u00c6";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.text( text );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "a text &amp; &#xc6;", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.text( text, attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "a text &amp; &#xc6;", writer.toString() );
    }

    /**
     * Test of rawText method, of class XhtmlBaseSink.
     */
    public void testRawText()
    {
        String text = "raw text";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.rawText( text );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "raw text", writer.toString() );
    }

    /**
     * Test of comment method, of class XhtmlBaseSink.
     */
    public void testComment()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.comment( "a comment" );
            sink.comment( " a comment" );
            sink.comment( "a comment " );
            sink.comment( " a comment " );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<!--a comment--><!-- a comment--><!--a comment --><!-- a comment -->", writer.toString() );
    }

    /**
     * Test of unknown method, of class XhtmlBaseSink.
     */
    public void testUnknown()
    {
        final String name = "unknown";
        final Object[] requiredParams = null;

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.unknown( name, requiredParams, attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "", writer.toString() );
    }

    /**
     * Test entities in attribute values.
     */
    public void testAttributeEntities()
    {
        final Object[] startTag = new Object[] { XhtmlBaseSink.TAG_TYPE_START };
        final Object[] endTag = new Object[] { XhtmlBaseSink.TAG_TYPE_END };
        final String script = XhtmlBaseSink.SCRIPT.toString();
        final SinkEventAttributes src = new SinkEventAttributeSet( SinkEventAttributes.SRC.toString(),
                "http://ex.com/ex.js?v=l&l=e" );

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.unknown( script, startTag, src );
            sink.unknown( script, endTag, null );

            sink.figureGraphics( "http://ex.com/ex.jpg?v=l&l=e", src );
        }
        finally
        {
            sink.close();
        }

        String result = writer.toString();

        assertTrue( result.contains( "ex.js?v=l&amp;l=e" ) );
        assertTrue( result.contains( "ex.jpg?v=l&amp;l=e" ) );
    }

    /**
     * Test of entity.
     */
    public void testEntity()
    {
        // DOXIA-314
        String text = "a text '&#x1d7ed;'";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.text( text );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "a text '&#x1d7ed;'", writer.toString() );
    }

    /**
     * Test unicode chracters in tables. DOXIA-433.
     */
    public void testSpecialCharacters()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.table( null );
            sink.tableRows( null, true );
            sink.tableRow( null );
            sink.tableCell();
            sink.text( "\u2713", null );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.table_();
        }
        finally
        {
            sink.close();
        }

        final String result = writer.toString();

        assertTrue( result.contains( "&#x2713;" ) );
    }
}
