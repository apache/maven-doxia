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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.apache.maven.doxia.sink.impl.TextSink;
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

        parser = lookup( Parser.ROLE, "confluence" );

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
    public void testMarkupTestPage()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "test" );
        assertContainsLines( result, "end:body" );
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
        assertContainsLines( result, "trailing slash\\\n" );
    }

    /** @throws Exception */
    public void testSectionTitles()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "section" );

        for ( int i = 1; i <= 5; i++ )
        {
            assertContainsLines( "Could not locate section " + i + " title", result,
                    "sectionTitle" + i + "\ntext: Section" + i );
        }

        assertContainsLines( "Section title has leading space", result, "sectionTitle1\ntext: TitleWithLeadingSpace" );
    }

    /** @throws Exception */
    public void testNestedBulletList()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "nested-list" );

        assertContainsLines( "Nested list not found", result,
                "begin:listItem\ntext: A top level list item\nbegin:list" );
        // two lists in the input...
        assertEquals( 3, result.split( "end:list\n" ).length );
        // ...and 4 list items
        assertEquals( 5, result.split( "end:listItem\n" ).length );
    }

    /** @throws Exception */
    public void testNestedHeterogenousList()
        throws Exception
    {
    	String result = locateAndParseTestSourceFile( "nested-list-heterogenous" );

        // test heterogenous list
        assertContainsLines( "Nested list not found", result, "begin:listItem\ntext: A top level list item\nbegin:numberedList" );

        // exactly one list and one numberedList
        assertEquals( 2, result.split( "begin:list\n" ).length );
        assertEquals( 2, result.split( "begin:numberedList" ).length );

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
        assertContainsLines( result, "begin:monospaced\ntext: some escaped monospaced \\\\unc\\path\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: http://jira.codehaus.org\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: JIRA\n" );
        assertContainsLines( result, "begin:listItem\ntext: Item with no formatting\nend:listItem\n" );
        assertContainsLines( result, "begin:listItem\ntext: One bullet\nend:listItem\n" );
        assertContainsLines( result, "begin:listItem\ntext: A list item with more than one line\nend:listItem\n" );
        // 3 lists in the input...
        assertEquals( 4, result.split( "end:list\n" ).length );
        // ...and 7 list items
        assertEquals( 9, result.split( "end:listItem\n" ).length );
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
        // 5 paragraphs in the input...
        assertEquals( 5, result.split( "end:paragraph\n" ).length );
        // 3 verbatim in the input...
        assertEquals( 3, result.split( "end:verbatim\n" ).length );
    }

    /** @throws Exception */
    public void testFigure()
        throws Exception
    {
        Reader result = getTestReader( "figure" );

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( result, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "paragraph" );
        assertEquals( it.next(), "text", "Simple paragraph." );
        assertStartsWith( it, "paragraph_", "figure" );
        assertEquals( it.next(), "figureGraphics", "images/photo.jpg" );
        assertStartsWith( it, "figure_", "paragraph" );
        assertEquals( it.next(), "text", "Simple paragraph with attempted inline !image.jpg! (should fail)." );
        assertStartsWith( it, "paragraph_", "figure" );
        assertEquals( it.next(), "figureGraphics", "images/photo.jpg" );
        assertEquals( it.next().getName(), "figureCaption" ); 
        assertEquals( it.next(), "text", "With caption on same line" );
        assertStartsWith( it, "figureCaption_", "figure_", "figure" );
        assertEquals( it.next(), "figureGraphics", "images/linebreak.jpg" );
        assertEquals( it.next().getName(), "figureCaption" );
        assertEquals( it.next(), "text", "With caption underneath and linebreak" );
        assertStartsWith( it, "figureCaption_", "figure_", "figure" );
        assertEquals( it.next(), "figureGraphics", "images/nolinebreak.jpg" );
        assertEquals( it.next().getName(), "figureCaption" );
        assertEquals( it.next(), "text", "With caption underneath and no linebreak" );
        assertStartsWith( it, "figureCaption_", "figure_", "figure" );
        assertEquals( it.next(), "figureGraphics", "images/bold.jpg" );
        assertEquals( it.next().getName(), "figureCaption" );
        assertEquals( it.next(), "text", "With *bold* caption underneath" );
        assertStartsWith( it, "figureCaption_", "figure_", "figure" );
        assertEquals( it.next(), "figureGraphics", "image.gif" );
        assertEquals( it, "figure_", "body_" );
    }

    /** @throws Exception */
    public void testLink()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "link" );

        assertContainsLines( result, "begin:link, name: middle.html\ntext: middle\nend:link" );
        assertContainsLines( result, "begin:link, name: end.html\ntext: end\nend:link" );
        assertContainsLines( result, "begin:link, name: link.html\ntext: alias\nend:link" );
        assertContainsLines( result, "begin:link, name: link.html#anchor\ntext: link#anchor\nend:link" );
        assertContainsLines( result, "begin:link, name: #simple\ntext: simple\nend:link" );
        assertContainsLines( result, "begin:link, name: resource.pdf\ntext: resource.pdf\nend:link" );
        assertContainsLines( result, "begin:link, name: resource.pdf\ntext: alias pdf\nend:link" );
        assertContainsLines( result, "begin:link, name: http://link.to/page_with_underscore-and-dash\ntext: underscore_-dash\nend:link" );
        // 5 paragraphs in the input...
        assertEquals( 6, result.split( "end:paragraph\n" ).length );
        // 8 links in the input...
        assertEquals( 9, result.split( "end:link\n" ).length );
    }

    public void testTable()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "table" );
        
        // DOXIA-537
        // |1|2|3|
        assertContainsLines( result, "begin:tableRow\nbegin:tableCell\ntext: 1\nend:tableCell\n\n\nbegin:tableCell\ntext: 2\nend:tableCell\n\n\nbegin:tableCell\ntext: 3\nend:tableCell\n" );
        // |1||3|
        assertContainsLines( result, "begin:tableRow\nbegin:tableCell\ntext: 1\nend:tableCell\n\n\nbegin:tableCell\ntext: 3\nend:tableCell\n" );
        // |1| |3|
        assertContainsLines( result, "begin:tableRow\nbegin:tableCell\ntext: 1\nend:tableCell\n\n\nbegin:tableCell\ntext:  \nend:tableCell\n\n\nbegin:tableCell\ntext: 3\nend:tableCell\n" );
        
    }

    /** @throws Exception */
    public void testTableWithLinks()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "table-link" );

        assertContainsLines( result, "begin:tableCell\nbegin:link, name: http://example.com/release.0.1.3/ex-win32-win32.x86.zip\ntext: Download\nend:link\n\n\nend:tableCell\n" );
        assertContainsLines( result, "begin:tableCell\nbegin:link, name: http://example.com/release.0.1.2/ex-win32-win32.x86.zip\ntext: http://example.com/release.0.1.2/ex-win32-win32.x86.zip\nend:link\n\n\nend:tableCell\n" );

        // 3 links in the input
        assertEquals( 4, result.split( "end:link\n" ).length );
    }

    /** @throws Exception */
    public void testTableWithImages()
        throws Exception
    {
        // DOXIA-493
        StringReader reader =
            new StringReader( "Table containing image in cell:\n" + "\n" + "||Header 1||\n"
                + "|!images/test/Image.png!|" );

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( reader, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "paragraph" );
        assertEquals( it.next(), "text", "Table containing image in cell:" );
        assertStartsWith( it, "paragraph_", "table", "tableRows", "tableRow", "tableHeaderCell", "bold" );
        assertEquals( it.next(), "text", "Header 1" );
        assertStartsWith( it, "bold_", "tableHeaderCell_", "tableRow_", "tableRow", "tableCell", "figure" );
        assertEquals( it.next(), "figureGraphics", "images/test/Image.png" );
        assertEquals( it, "figure_", "tableCell_", "tableRow_", "tableRows_", "table_", "body_" );
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
        assertContainsLines( result, "begin:monospaced\ntext: monospaced-with-dashes\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced_with_underscores\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced*with*stars\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced+with+plus\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced~with~tilde\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced^with^circumflex^accent\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced[with]brackets\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced{with}curly{brackets\nend:monospaced");
        assertContainsLines( result, "begin:monospaced\ntext: monospaced\\\\\\with\\\\backslashes\nend:monospaced");

        // 3 paragraphs in the input...
        assertEquals( 4, result.split( "end:paragraph\n" ).length );
        // 6 bolds in the input...
        assertEquals( 7, result.split( "end:bold\n" ).length );
        // 4 italics in the input...
        assertEquals( 5, result.split( "end:italic\n" ).length );
        // 11 monospaced in the input...
        assertEquals( 12, result.split( "end:monospaced\n" ).length );
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
     * @throws ParseException if something goes wrong.
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

    public void testDoxia382SinkCannotBeReused()
            throws ParseException
    {
        String document1 = "Test A"
            + "\n\n* list1"
            + "\n\n* list2"
            + "\n\n* list2{pre}123{/pre} "
            + "\n123";

        String document2 = "Test B"
            + "\n\n* list1"
            + "\n\n* list2"
            + "\n\n* list2{pre}123{/pre}"
            + "\n123";

        output = new StringWriter();
        Sink sink = new TextSink( new FilterWriter( output )
        {
            public void close() throws IOException
            {
                super.close();
                this.out = null;
            }

            public void write( String str, int off, int len )
                    throws IOException
            {
                if ( out == null )
                {
                    throw new IOException( "Writing to an already closed Writer" );
                }
            }
        });

        createParser().parse( new StringReader( document1 ), sink );
        createParser().parse( new StringReader( document2 ), sink );
    }
    

    /**
     * DOXIA-370
     *
     * @throws ParseException
     */
    public void testSeparatorInParagraph()
        throws ParseException
    {
        String document = "Up\n---\nDown\n";

        output = new StringWriter();
        Sink sink = new TextSink( output );

        /* parsing with separator in middle of paragraph */
        createParser().parse( new StringReader( document ), sink );
        assertTrue( "generated document should have a size > 0", output.toString().length() > 0 );

    }
    
    public void testListFollowedByMacro() throws Exception
    {
        // @todo FIX
        // DOXIA-371
        String document = "- This is a little test. \r\n" +
                "\r\n" + // with extra linebreak it succeeds, without it should too 
        		"{code}\r\n" + 
        		"    @Autowired\r\n" + 
        		"    private DataSource dataSource;\r\n" + 
        		"{code}\r\n"; 
        output = new StringWriter();
        SinkEventTestingSink sink = new SinkEventTestingSink();
        createParser().parse( new StringReader( document ), sink );
        
        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals("head", it.next().getName() );
        assertEquals("head_", it.next().getName() );
        assertEquals("body", it.next().getName() );
        assertEquals("list", it.next().getName() );
        assertEquals("listItem", it.next().getName() );
        assertEquals( it.next(), "text", "This is a little test." );
        assertEquals("listItem_", it.next().getName() );
        assertEquals("list_", it.next().getName() );
        assertEquals("verbatim", it.next().getName() );
        assertEquals( it.next(), "text", "    @Autowired\n    private DataSource dataSource;\n" );
        assertEquals("verbatim_", it.next().getName() );
        assertEquals("body_", it.next().getName() );
    }

	public void testLinethrough() throws Exception {
		String document = "-Linethrough-";
		output = new StringWriter();
		SinkEventTestingSink sink = new SinkEventTestingSink();
		createParser().parse(new StringReader(document), sink);

		Iterator<SinkEventElement> it = sink.getEventList().iterator();
		assertStartsWith(it, "head", "head_", "body", "paragraph");
		assertEquals(it.next(), "text", "Linethrough",
				new SinkEventAttributeSet("decoration", "line-through"));
		assertEquals(it, "paragraph_", "body_");
	}

	public void testUnderline() throws Exception {
		String document = "+Underline+";
		output = new StringWriter();
		SinkEventTestingSink sink = new SinkEventTestingSink();
		createParser().parse(new StringReader(document), sink);

		Iterator<SinkEventElement> it = sink.getEventList().iterator();
		assertStartsWith(it, "head", "head_", "body", "paragraph");
		assertEquals(it.next(), "text", "Underline", new SinkEventAttributeSet(
				"decoration", "underline"));
		assertEquals(it, "paragraph_", "body_");
	}

	public void testSub() throws Exception {
		String document = "~Sub~";
		output = new StringWriter();
		SinkEventTestingSink sink = new SinkEventTestingSink();
		createParser().parse(new StringReader(document), sink);

		Iterator<SinkEventElement> it = sink.getEventList().iterator();
		assertStartsWith(it, "head", "head_", "body", "paragraph");
		assertEquals(it.next(), "text", "Sub", new SinkEventAttributeSet(
				"valign", "sub"));
		assertEquals(it, "paragraph_", "body_");
	}

	public void testSup() throws Exception {
		String document = "^Sup^";
		output = new StringWriter();
		SinkEventTestingSink sink = new SinkEventTestingSink();
		createParser().parse(new StringReader(document), sink);

		Iterator<SinkEventElement> it = sink.getEventList().iterator();
		assertStartsWith(it, "head", "head_", "body", "paragraph");
		assertEquals(it.next(), "text", "Sup", new SinkEventAttributeSet(
				"valign", "sup"));
		assertEquals(it, "paragraph_", "body_");
	}

    private void assertContainsLines( String message, String result, String lines )
    {
        lines = StringUtils.replace( lines, "\n", EOL );
        if ( message != null )
        {
            assertTrue( message, result.contains( lines ) );
        }
        else
        {
            assertTrue( result.contains( lines ) );
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
