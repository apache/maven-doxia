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
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;

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

        parser = (AptParser) lookup( Parser.ROLE, "apt" );
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

        assertTrue( linebreak.indexOf( "Line\\" + EOL + "break." ) != -1 );
    }

    /** @throws Exception  */
    public void testSnippetMacro()
        throws Exception
    {
        String macro = parseFileToAptSink( "test/macro" );

        assertTrue( macro.indexOf( "<modelVersion\\>4.0.0\\</modelVersion\\>" ) != -1 );
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

        Reader reader = null;
        SinkEventTestingSink sink = new SinkEventTestingSink();

        try
        {
            reader = getTestReader( "test/snippet" );

            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( reader );
        }

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "list", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "list_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testTocMacro()
        throws Exception
    {
        String toc = parseFileToAptSink( "test/toc" );

        // No section, only subsection 1 and 2
        assertTrue( toc.indexOf( "* {{{SubSection_1.1}SubSection 1.1}}" ) != -1 );
        assertTrue( toc.indexOf( "* {{{SubSection_1.1.2.1.1}SubSection 1.1.2.1.1}}" ) == -1 );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        SinkEventElement element = it.next();
        assertEquals( "verbatim", element.getName() );
        Object[] args = element.getArgs();
        assertEquals( SinkEventAttributeSet.BOXED, args[0] );

        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );

        element = it.next();
        assertEquals( "verbatim", element.getName() );
        args = element.getArgs();
        assertNull( args[0] );

        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        assertEquals( "table", ( it.next() ).getName() );
        assertEquals( "tableRows", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        SinkEventElement element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1, 1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2, 2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3, 3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRows_", ( it.next() ).getName() );
        assertEquals( "table_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        assertEquals( "table", ( it.next() ).getName() );
        assertEquals( "tableRows", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        SinkEventElement element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,\u00A0", element.getArgs()[0] );
        assertEquals( "lineBreak", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,\u00A0", element.getArgs()[0] );
        assertEquals( "lineBreak", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,\u00A0", element.getArgs()[0] );
        assertEquals( "lineBreak", ( it.next() ).getName() );
        element = it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRows_", ( it.next() ).getName() );
        assertEquals( "table_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        assertEquals( "table", ( it.next() ).getName() );
        assertEquals( "tableRows", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        SinkEventElement event = it.next();
        assertEquals( "tableCell", event.getName() );
        SinkEventAttributeSet atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals( "center", atts.getAttribute( SinkEventAttributeSet.ALIGN ) );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertNotNull( event.getArgs()[0] );
        assertEquals( "Centered", event.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "tableCell", event.getName() );
        atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals( "center", atts.getAttribute( SinkEventAttributeSet.ALIGN ) );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertNotNull( event.getArgs()[0] );
        assertEquals( "Centered", event.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "tableCell", event.getName() );
        atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals( "center", atts.getAttribute( SinkEventAttributeSet.ALIGN ) );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertNotNull( event.getArgs()[0] );
        assertEquals( "Centered", event.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRow", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "tableCell", event.getName() );
        atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals( "center", atts.getAttribute( SinkEventAttributeSet.ALIGN ) );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertNotNull( event.getArgs()[0] );
        assertEquals( "Centered", event.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "tableCell", event.getName() );
        atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals( "left", atts.getAttribute( SinkEventAttributeSet.ALIGN ) );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertNotNull( event.getArgs()[0] );
        assertEquals( "Left-aligned", event.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "tableCell", event.getName() );
        atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals( "right", atts.getAttribute( SinkEventAttributeSet.ALIGN ) );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertNotNull( event.getArgs()[0] );
        assertEquals( "Right-aligned", event.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRows_", ( it.next() ).getName() );
        assertEquals( "table_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        assertEquals( "paragraph", ( it.next() ).getName() );
        SinkEventElement event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "~ = - + * [ ] < > { } \\ \u2713", event.getArgs()[0] );
        assertEquals( "paragraph_", ( it.next() ).getName() );

        assertEquals( "table", ( it.next() ).getName() );
        assertEquals( "tableRows", ( it.next() ).getName() );
        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );

        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "~ = - + * [ ] < > { } \\ \u2713", event.getArgs()[0] );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );

        assertEquals( "tableRows_", ( it.next() ).getName() );
        assertEquals( "table_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );

        SinkEventElement event = it.next();
        assertEquals( "anchor", event.getName() );
        assertEquals( "Anchor_with_spaces_and_brackets", event.getArgs()[0] );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "Anchor with spaces (and brackets)", event.getArgs()[0] );
        assertEquals( "anchor_", ( it.next() ).getName() );

        assertEquals( "text", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "link", event.getName() );
        assertEquals( "#Anchor_with_spaces_and_brackets", event.getArgs()[0] );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "Anchor with spaces (and brackets)", event.getArgs()[0] );
        assertEquals( "link_", ( it.next() ).getName() );

        assertEquals( "text", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "link", event.getName() );
        assertEquals( "http://fake.api#method(with, args)", event.getArgs()[0] );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "method(with, args)", event.getArgs()[0] );
        assertEquals( "link_", ( it.next() ).getName() );

        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
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

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );

        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "anchor", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );

        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "apt";
    }
}
