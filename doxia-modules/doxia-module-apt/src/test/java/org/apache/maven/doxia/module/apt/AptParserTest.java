package org.apache.maven.doxia.module.apt;

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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.ParseException;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class AptParserTest
    extends AbstractParserTest
{

    private AptParser parser;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = lookup( Parser.ROLE, "apt" );
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    protected String parseFileToAptSink( String file )
        throws ParseException
    {
        StringWriter output = null;
        Reader reader = null;
        try
        {
            output = new StringWriter();
            reader = getTestReader( file );

            Sink sink = new AptSink( output );
            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        return output.toString();
    }

    /** @throws Exception  */
    public void testLineBreak()
        throws Exception
    {
        String linebreak = parseFileToAptSink( "test/linebreak" );

        assertTrue( linebreak.contains( "Line\\" + EOL + "break." ) );
    }

    /** @throws Exception  */
    public void testSnippetMacro()
        throws Exception
    {
        String macro = parseFileToAptSink( "test/macro" );

        assertTrue( macro.contains( "<modelVersion\\>4.0.0\\</modelVersion\\>" ) );
    }

    /** @throws Exception  */
    public void testCommentsBeforeTitle()
        throws Exception
    {
        String comments = parseFileToAptSink( "test/comments" );

        assertEquals( 0, comments.indexOf( "~~ comments before title" + EOL + "~~ like a license header, for example"
            + EOL + " -----" + EOL + " Test DOXIA-379" ) );
    }

    /** @throws Exception  */
    public void testSnippet()
        throws Exception
    {
        // DOXIA-259

        SinkEventTestingSink sink = new SinkEventTestingSink();
        try ( Reader reader = getTestReader( "test/snippet" ) )
        {
            createParser().parse( reader, sink );
        }

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "list", "listItem", "text", "verbatim", "text", "verbatim_",
                      "paragraph", "text", "paragraph_", "listItem_", "listItem", "text", "verbatim", "text",
                      "verbatim_", "paragraph", "text", "paragraph_", "listItem_", "list_", "body_" );
    }


    /** @throws Exception  */
    public void testSnippetTrailingSpace()
        throws Exception
    {
        // DOXIA-425
        String text = "%{snippet|id=myid|file=pom.xml}  " + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "verbatim", "text", "verbatim_", "body_" );
    }

    /** @throws Exception  */
    public void testTocMacro()
        throws Exception
    {
        String toc = parseFileToAptSink( "test/toc" );

        // No section, only subsection 1 and 2
        assertTrue( toc.contains( "* {{{SubSection_1.1}SubSection 1.1}}" ) );
        assertTrue( !toc.contains( "* {{{SubSection_1.1.2.1.1}SubSection 1.1.2.1.1}}" ) );
    }

    /**
     * Parses the test document test.apt and re-emits
     * it into parser/test.apt.
     *
     * @throws java.io.IOException if the test file cannot be read.
     * @throws org.apache.maven.doxia.parser.ParseException if the test file cannot be parsed.
     */
    public void testTestDocument()
        throws IOException, ParseException
    {
        Writer writer = null;
        Reader reader = null;
        try
        {
            writer = getTestWriter( "test" );
            reader = getTestReader( "test" );

            Sink sink = new AptSink( writer );

            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( writer );
            IOUtil.close( reader );
        }
    }

    /** @throws Exception  */
    public void testBoxedVerbatim()
        throws Exception
    {
        String text = "+--" + EOL + "boxed verbatim" + EOL + "+--" + EOL
                + "---" + EOL + "un-boxed verbatim" + EOL + "---" + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body" );
        assertEquals( it.next(), "verbatim", SinkEventAttributeSet.BOXED );
        assertStartsWith( it, "text", "verbatim_" );

        assertEquals( it.next(), "verbatim", new Object[] { null } );
        assertEquals( it, "text", "verbatim_", "body_" );
    }

    /** @throws Exception  */
    public void testMultiLinesInTableCells()
        throws Exception
    {
        String text = "*----------*--------------+----------------:" + EOL +
                " cell 1, | cell 1,2       | cell 1,3" + EOL +
                " 1       |                | " + EOL +
                "*----------*--------------+----------------:" + EOL +
                " cell 2,1 | cell 2,       | cell 2,3" + EOL +
                "          | 2             |" + EOL +
                "*----------*--------------+----------------:" + EOL +
                " cell 3,1 | cell 3,2      | cell 3," + EOL +
                "          |               | 3" + EOL +
                "*----------*--------------+----------------:" + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "table", "tableRows", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "cell 1, 1" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 1,2" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 1,3" );

        assertStartsWith( it, "tableCell_", "tableRow_", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "cell 2,1" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 2, 2" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 2,3" );
        
        assertStartsWith( it, "tableCell_", "tableRow_", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "cell 3,1" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 3,2" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 3, 3" );

        assertEquals( it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_" );
    }

    /** @throws Exception  */
    public void testLineBreakInTableCells()
        throws Exception
    {
        String text = "*----------*--------------+----------------:" + EOL +
                " cell 1,\\ | cell 1,2       | cell 1,3" + EOL +
                " 1       |                | " + EOL +
                "*----------*--------------+----------------:" + EOL +
                " cell 2,1 | cell 2,\\     | cell 2,3" + EOL +
                "          | 2             |" + EOL +
                "*----------*--------------+----------------:" + EOL +
                " cell 3,1 | cell 3,2      | cell 3,\\" + EOL +
                "          |               | 3" + EOL +
                "*----------*--------------+----------------:" + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "table", "tableRows", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "cell 1,\u00A0" );

        assertEquals( it.next().getName(), "lineBreak" );
        assertEquals( it.next(), "text", "1" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 1,2" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 1,3" );

        assertStartsWith( it, "tableCell_", "tableRow_", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "cell 2,1" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 2,\u00A0" );

        assertEquals( it.next().getName(), "lineBreak" );
        assertEquals( it.next(), "text", "2" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 2,3" );

        assertStartsWith( it, "tableCell_", "tableRow_", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "cell 3,1" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 3,2" );

        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "cell 3,\u00A0" );

        assertEquals( it.next().getName(), "lineBreak" );
        assertEquals( it.next(), "text", "3" );

        assertEquals( it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_" );
    }

    /** @throws Exception  */
    public void testDOXIA38()
        throws Exception
    {
        String text =
                "*----------*--------------*---------------*" + EOL +
                "| Centered |   Centered   |   Centered    |" + EOL +
                "*----------*--------------+---------------:" + EOL +
                "| Centered | Left-aligned | Right-aligned |" + EOL +
                "*----------*--------------+---------------:";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "table", "tableRows", "tableRow" );
        assertAttributeEquals( it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center" );
        assertEquals( it.next(), "text", "Centered" );
        assertEquals( it.next().getName(), "tableCell_" );
        
        assertAttributeEquals( it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center" );
        assertEquals( it.next(), "text", "Centered" );
        assertEquals( it.next().getName(), "tableCell_" );
        
        assertAttributeEquals( it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center" );
        assertEquals( it.next(), "text", "Centered" );
        assertStartsWith( it, "tableCell_", "tableRow_", "tableRow" );
        
        assertAttributeEquals( it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center" );
        assertEquals( it.next(), "text", "Centered" );
        assertEquals( it.next().getName(), "tableCell_" );
        
        assertAttributeEquals( it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "left" );
        assertEquals( it.next(), "text", "Left-aligned" );
        assertEquals( it.next().getName(), "tableCell_" );
        
        assertAttributeEquals( it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "right" );
        assertEquals( it.next(), "text", "Right-aligned" );
        assertEquals( it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_" );
    }

    /** @throws Exception  */
    public void testSpecialCharactersInTables()
        throws Exception
    {
        // DOXIA-323, DOXIA-433
        String text =
                "  \\~ \\= \\- \\+ \\* \\[ \\] \\< \\> \\{ \\} \\\\ \\u2713" + EOL
                + EOL
                + "*--------------------------------------------------+---------------+" + EOL
                + "| \\~ \\= \\- \\+ \\* \\[ \\] \\< \\> \\{ \\} \\\\ \\u2713 | special chars |" + EOL
                + "*--------------------------------------------------+---------------+";

        SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "paragraph" );
        assertEquals( it.next(), "text", "~ = - + * [ ] < > { } \\ \u2713" );

        assertStartsWith( it, "paragraph_", "table", "tableRows", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "~ = - + * [ ] < > { } \\ \u2713" );

        assertEquals( it, "tableCell_", "tableCell", "text", "tableCell_", "tableRow_", "tableRows_", "table_", "body_" );
    }

    /** @throws Exception  */
    public void testSpacesAndBracketsInAnchors()
        throws Exception
    {
        final String text = "  {Anchor with spaces (and brackets)}" + EOL
            + "  Link to {{Anchor with spaces (and brackets)}}" + EOL
            + "  {{{http://fake.api#method(with, args)}method(with, args)}}" + EOL;

        final SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "paragraph" );
        assertEquals( it.next(), "anchor", "Anchor_with_spaces_and_brackets" );

        assertEquals( it.next(), "text", "Anchor with spaces (and brackets)" );

        assertStartsWith( it, "anchor_", "text" );
        assertEquals( it.next(), "link", "#Anchor_with_spaces_and_brackets" );

        assertEquals( it.next(), "text", "Anchor with spaces (and brackets)" );

        assertStartsWith( it, "link_", "text" );
        assertEquals( it.next(), "link", "http://fake.api#method(with, args)" );

        assertEquals( it.next(), "text", "method(with, args)" );

        assertEquals( it, "link_", "paragraph_", "body_" );
    }

    /** @throws Exception  */
    public void testSectionTitleAnchors()
        throws Exception
    {
        // DOXIA-420
        String text = "Enhancements to the APT format" + EOL + EOL
            + "{Title with anchor}" + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "section1", "sectionTitle1", "text", "sectionTitle1_", "section1_",
                      "section1", "sectionTitle1", "anchor", "text", "anchor_", "sectionTitle1_", "section1_", "body_" );
    }
    
    /**
     * @throws Exception
     */
    public void testTableHeaders() throws Exception
    {
        // DOXIA-404
        String text = "*-----------+-----------+" + EOL + 
        		"|| Header 1 || Header 2 |" + EOL +
        		"*-----------+-----------+" + EOL +
        		"  Cell 1    | Cell 2    |" + EOL +
        		"*-----------+-----------+" + EOL +
        		"  Cell 3    | Cell 4    |" + EOL +
        		"*-----------+-----------+" + EOL;
        
        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "table", "tableRows" );
        assertStartsWith( it, "tableRow", "tableHeaderCell", "text", "tableHeaderCell_", "tableHeaderCell", "text",
                          "tableHeaderCell_", "tableRow_" );
        assertStartsWith( it, "tableRow", "tableCell", "text", "tableCell_", "tableCell", "text", "tableCell_",
                          "tableRow_" );
        assertStartsWith( it, "tableRow", "tableCell", "text", "tableCell_", "tableCell", "text", "tableCell_",
                          "tableRow_" );
        assertEquals( it, "tableRows_", "table_", "body_" );
    }
    
    public void testEscapedPipeInTableCell() throws Exception
    {
        // DOXIA-479
        String text="*---+---+" + EOL + 
        		"| cell \\| pipe | next cell " + EOL + 
        		"*---+---+" + EOL;
        
        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertStartsWith( it, "head", "head_", "body", "table", "tableRows", "tableRow", "tableCell" );
        assertEquals( it.next(), "text", "cell | pipe" );
        assertStartsWith( it, "tableCell_", "tableCell" );
        assertEquals( it.next(), "text", "next cell" );
        assertEquals( it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_" );
    }

    public void testLiteralAnchor()
        throws Exception
    {
        // DOXIA-397
        String text =
            "{{{../apidocs/groovyx/net/http/ParserRegistry.html##parseText(org.apache.http.HttpResponse)}ParserRegistry}}";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertStartsWith( it, "head", "head_", "body", "section1", "sectionTitle1" );
        assertEquals( it.next(), "link",
                      "../apidocs/groovyx/net/http/ParserRegistry.html#parseText(org.apache.http.HttpResponse)" );
        assertEquals( it.next(), "text", "ParserRegistry" );
        assertEquals( it, "link_", "sectionTitle1_", "section1_", "body_" );
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "apt";
    }
}
