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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.TextSink;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * Test class for ConfluenceParser.
 */
public class ConfluenceParserTest
    extends AbstractParserTest
{
    private ConfluenceParser parser;

    private StringWriter output;

    private Reader reader;

    private Writer writer;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (ConfluenceParser) lookup( Parser.ROLE, "confluence" );

        output = null;
        reader = null;
        writer = null;
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        IOUtil.close( output );
        IOUtil.close( reader );
        IOUtil.close( writer );

        super.tearDown();
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "confluence";
    }

    /** @throws Exception */
    public void testParagraphWithSimpleFormatting()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "simple-paragraph" );

        assertContainsLines( result, "begin:bold\ntext: bold\n" );
        assertContainsLines( result, "begin:italic\ntext: italic\n" );
        assertContainsLines( result, "begin:monospaced\ntext: monospaced\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: http://jira.codehaus.org" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: JIRA\n" );
        // four paragraphs in the input...
        assertEquals( 5, result.split( "end:paragraph" ).length );
    }

    /** @throws Exception */
    public void testLineBreak()
        throws Exception
    {
        String lineBreak = getLineBreakString();

        String result = locateAndParseTestSourceFile( "linebreak" );

        assertContainsLines( result, "Line\n" + lineBreak );
        assertContainsLines( result, "with 2\n" + lineBreak );
        assertContainsLines( result, "inline\n" + lineBreak );
    }

    /** @throws Exception */
    public void testEscapes()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "escapes" );

        assertContainsLines( result, "asterisk *" );
        assertContainsLines( result, "underline _" );
        assertContainsLines( result, "asterisk *not bold*" );
        assertContainsLines( result, "underline _not italic_" );
        assertContainsLines( result, "normal character" );
    }

    /** @throws Exception */
    public void testSectionTitles()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "section" );

        for ( int i = 1; i <= 5; i++ )
        {
            assertContainsLines( "Could not locate section " + i + " title", result, "sectionTitle" + i +
                "\ntext: Section" + i );
        }

        assertContainsLines( "Section title has leading space", result, "sectionTitle1\ntext: TitleWithLeadingSpace" );
    }

    /** @throws Exception */
    public void testNestedBulletList()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "nested-list" );

        assertContainsLines( "Nested list not found", result, "begin:listItem\ntext: A top level list item\nbegin:list" );
        // two lists in the input...
        assertEquals( 3, result.split( "end:list\n" ).length );
        // ...and 4 list items
        assertEquals( 5, result.split( "end:listItem\n" ).length );
    }

    /** @throws Exception */
    public void testListWithSimpleFormatting()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "simple-list" );

        assertContainsLines( result, "begin:bold\ntext: bold\n" );
        assertContainsLines( result, "begin:italic\ntext: italic\n" );
        assertContainsLines( result, "begin:monospaced\ntext: monospaced\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: http://jira.codehaus.org\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: JIRA\n" );
        assertContainsLines( result, "begin:listItem\ntext: Item with no formatting\nend:listItem\n" );
        assertContainsLines( result, "begin:listItem\ntext: One bullet\nend:listItem\n" );
        assertContainsLines( result, "begin:listItem\ntext: A list item with more than one line\nend:listItem\n" );
        // 3 lists in the input...
        assertEquals( 4, result.split( "end:list\n" ).length );
        // ...and 7 list items
        assertEquals( 8, result.split( "end:listItem\n" ).length );
    }

    /** @throws Exception */
    public void testAnchor()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "anchor" );

        assertContainsLines( result, "begin:paragraph\nbegin:anchor, name: start\nend:anchor" );
        assertContainsLines( result, "begin:anchor, name: middle\nend:anchor" );
        assertContainsLines( result, "begin:paragraph\ntext: Simple paragraph\nbegin:anchor, name: end\nend:anchor" );
        // 3 anchors in the input...
        assertEquals( 4, result.split( "end:anchor\n" ).length );
    }

    /** @throws Exception */
    public void testUnknownMacro()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "unknown-macro" );

        assertContainsLines( result, "begin:paragraph\ntext: {unknown:start}" );
    }

    /** @throws Exception */
    public void testCodeMacro()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "code" );

        assertContainsLines( result, "begin:verbatim, boxed: true\ntext: public class Cat {" );
        // 3 paragraphs in the input...
        assertEquals( 4, result.split( "end:paragraph\n" ).length );
        // 1 verbatim in the input...
        assertEquals( 2, result.split( "end:verbatim\n" ).length );
    }

    /** @throws Exception */
    public void testFigure()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "figure" );

        assertContainsLines( result, "begin:figure\nfigureGraphics, name: images/photo.jpg\nend:figure\n" );
        assertContainsLines( result, "attempted inline !image.jpg! (should fail)" );
        // this isn't ideal... Doxia captions are not the same as what people would use to add text to a confluence
        assertContainsLines( result, "figureGraphics, name: images/photo.jpg\n"
            + "begin:figureCaption\ntext: With caption on same line\n" + "end:figureCaption" );
        assertContainsLines( result, "figureGraphics, name: images/nolinebreak.jpg\n"
            + "begin:figureCaption\ntext: With caption underneath and no linebreak\nend:figureCaption" );
        // ignore linebreak after figure insert...
        assertContainsLines( result, "figureGraphics, name: images/linebreak.jpg\n"
            + "begin:figureCaption\ntext: With caption underneath and linebreak\nend:figureCaption" );
        // ignore formtting in caption...
        assertContainsLines( result, "figureGraphics, name: images/bold.jpg\n"
            + "begin:figureCaption\ntext: With *bold* caption underneath\nend:figureCaption" );
        // 2 paragraphs in the input... (the figures do not go in a paragraph by analogy with AptParser)
        assertEquals( 3, result.split( "end:paragraph\n" ).length );
    }

    /** @throws Exception */
    public void testLink()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "link" );

        assertContainsLines( result, "begin:link, name: middle\ntext: middle\nend:link" );
        assertContainsLines( result, "begin:link, name: end\ntext: end\nend:link" );
        assertContainsLines( result, "begin:link, name: link\ntext: alias\nend:link" );
        assertContainsLines( result, "begin:link, name: link#anchor\ntext: link#anchor\nend:link" );
        assertContainsLines( result, "begin:link, name: #simple\ntext: simple\nend:link" );
        // 3 paragraphs in the input...
        assertEquals( 4, result.split( "end:paragraph\n" ).length );
        // 5 links in the input...
        assertEquals( 6, result.split( "end:link\n" ).length );
    }

    /** @throws Exception */
    public void testParagraphWithList()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "paragraph-list" );

        assertContainsLines( result, "begin:paragraph\ntext: A paragraph\nend:paragraph\n" );
        assertContainsLines( result, "begin:listItem\ntext: A nested list item\nend:listItem\n" );
        assertContainsLines( result, "begin:listItem\ntext: Another nested list item with two lines\nend:listItem\n" );
        // 2 paragraphs in the input...
        assertEquals( 3, result.split( "end:paragraph\n" ).length );
        // 1 list in the input...
        assertEquals( 2, result.split( "end:list\n" ).length );
    }

    /** @throws Exception */
    public void testParagraphWithFigure()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "paragraph-figure" );

        assertContainsLines( result, "begin:paragraph\ntext: A paragraph\nend:paragraph\n" );
        assertContainsLines( result, "begin:figure\nfigureGraphics, name: images/logo.png\nbegin:figureCaption\ntext: with a figure\nend:figureCaption" );
        // 2 paragraphs in the input...
        assertEquals( 3, result.split( "end:paragraph\n" ).length );
        // 1 figure in the input...
        assertEquals( 2, result.split( "end:figure\n" ).length );
    }

    /** @throws Exception */
    public void testParagraphWithHeader()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "paragraph-header" );

        assertContainsLines( result, "begin:paragraph\ntext: A paragraph\nend:paragraph\n" );
        assertContainsLines( result, "begin:section2\nbegin:sectionTitle2\ntext: A header\nend:sectionTitle2" );
        // 3 paragraphs in the input...
        assertEquals( 4, result.split( "end:paragraph\n" ).length );
        // 1 header in the input...
        assertEquals( 2, result.split( "end:sectionTitle2\n" ).length );
    }

    /** @throws Exception */
    public void testNestedFormats()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "nested-format" );

        assertContainsLines( result, "begin:bold\nbegin:italic\ntext: bold italic\nend:italic" );
        assertContainsLines( result, "begin:italic\nbegin:bold\ntext: italic bold\nend:bold" );
        assertContainsLines( result, "begin:bold\nbegin:monospaced\ntext: bold monospaced\nend:monospaced" );
        assertContainsLines( result, "text: A paragraph with \nbegin:bold\ntext: bold \nbegin:italic\ntext: italic\nend:italic" );
        assertContainsLines( result, "begin:italic\ntext: italic \nbegin:bold\ntext: bold\nend:bold" );
        assertContainsLines( result, "begin:bold\ntext: bold \nbegin:monospaced\ntext: monospaced\nend:monospaced" );
        // 2 paragraphs in the input...
        assertEquals( 3, result.split( "end:paragraph\n" ).length );
        // 6 bolds in the input...
        assertEquals( 7, result.split( "end:bold\n" ).length );
        // 4 italics in the input...
        assertEquals( 5, result.split( "end:italic\n" ).length );
        // 2 monospaced in the input...
        assertEquals( 3, result.split( "end:monospaced\n" ).length );
    }

    /** @throws Exception */
    public void testNoteInfoTipQuote()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "note-tip-info" );

        assertContainsLines( result, "begin:definedTerm\ntext: Be Careful\nend:definedTerm\n" );
        assertContainsLines( result, "begin:definition\ntext: The body of the note here..\nend:definition" );
        assertContainsLines( result, "begin:definedTerm\ntext: Guess What?\nend:definedTerm\n" );
        assertContainsLines( result, "begin:definition\ntext: The body of the tip here..\nend:definition" );
        assertContainsLines( result, "begin:definedTerm\ntext: Some Info\nend:definedTerm\n" );
        assertContainsLines( result, "begin:definition\ntext: The body of the info here..\nend:definition" );
        assertContainsLines( result, "begin:definedTerm\ntext: Simon Says\nend:definedTerm\n" );
        assertContainsLines( result, "begin:definition\ntext: The body of the \nbegin:bold\ntext: quote\nend:bold" );

        // 5 paragraphs in the input...
        assertEquals( 6, result.split( "end:paragraph\n" ).length );
        // 4 dinitionList in the input...
        assertEquals( 5, result.split( "end:definitionList\n" ).length );
    }

    /**
     * DOXIA-247
     *
     * @throws ParseException
     */
    public void testEndBracket()
        throws ParseException
    {
        String document = "Test"
            + "\n\n* list1"
            + "\n\n* list2"
            + "\n\n* list2"
            + "\n{pre}123{/pre}";

        output = new StringWriter();
        Sink sink = new TextSink( output );

        /* parsing with additional space at end works */
        createParser().parse( new StringReader( document + " " ), sink );
        assertTrue( "generated document should have a size > 0", output.toString().length() > 0 );

        /* parsing with document ending in } should not fail */
        try
        {
            createParser().parse( new StringReader( document ), sink );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "parsing with document ending in } should not fail" );
        }

        assertTrue( "generated document should have a size > 0", output.toString().length() > 0 );
    }

    /**
     * DOXIA-247
     *
     * @throws ParseException
     */
    public void testEndBracketInList()
        throws ParseException
    {
        String document1 = "Test"
            + "\n\n* list1"
            + "\n\n* list2"
            + "\n\n* list2{pre}123{/pre} "
            + "\n123";

        String document2 = "Test"
            + "\n\n* list1"
            + "\n\n* list2"
            + "\n\n* list2{pre}123{/pre}"
            + "\n123";

        output = new StringWriter();
        Sink sink = new TextSink( output );

        /* parsing with additional space at end of list item works */
        createParser().parse( new StringReader( document1 ), sink );
        assertTrue( "generated document should have a size > 0", output.toString().length() > 0 );

        /* parsing with end of list item ending in } should not fail */
        try
        {
            createParser().parse( new StringReader( document2 ), sink );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "parsing with end of list item ending in } should not fail" );
        }

        assertTrue( "generated document should have a size > 0", output.toString().length() > 0 );
    }

    private void assertContainsLines( String message, String result, String lines )
    {
        lines = StringUtils.replace( lines, "\n", EOL );
        if ( message != null )
        {
            assertTrue( message, result.indexOf( lines ) != -1 );
        }
        else
        {
            assertTrue( result.indexOf( lines ) != -1 );
        }
    }

    private void assertContainsLines( String result, String lines )
    {
        this.assertContainsLines( null, result, lines );
    }

    private String getLineBreakString()
    {
        StringWriter sw = new StringWriter();
        Sink sink = new TextSink( sw );
        sink.lineBreak();

        return sw.toString();
    }

    private String locateAndParseTestSourceFile( String stem )
        throws IOException, ParseException
    {
        output = new StringWriter();
        reader = getTestReader( stem, outputExtension() );
        writer = getTestWriter( stem, "txt" );

        Sink sink = new TextSink( output );
        createParser().parse( reader, sink );

        // write to file
        String expected = output.toString();
        writer.write( expected );
        writer.flush();
        return expected;
    }

}
