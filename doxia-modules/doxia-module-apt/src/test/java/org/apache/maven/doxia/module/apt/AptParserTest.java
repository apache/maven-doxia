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

    /** {@inheritDoc} */
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

    /** @throws Exception  */
    public void testLineBreak()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;
        try
        {
            output = new StringWriter();
            reader = getTestReader( "test/linebreak", "apt" );

            Sink sink = new AptSink( output );
            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        assertTrue( output.toString().indexOf( "Line\\" + EOL + "break." ) != -1 );
    }

    /** @throws Exception  */
    public void testSnippetMacro()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;
        try
        {
            output = new StringWriter();
            reader = getTestReader( "test/macro", "apt" );

            Sink sink = new AptSink( output );
            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        assertTrue( output.toString().indexOf( "<modelVersion\\>4.0.0\\</modelVersion\\>" ) != -1 );
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
            reader = getTestReader( "test/snippet", "apt" );

            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( reader );
        }

        Iterator it = sink.getEventList().iterator();

        assertEquals( "head", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "verbatim", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "verbatim_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "verbatim", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "verbatim_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body_", ( (SinkEventElement) it.next() ).getName() );

        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testTocMacro()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;
        try
        {
            output = new StringWriter();
            reader = getTestReader( "test/toc", "apt" );

            Sink sink = new AptSink( output );
            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        // No section, only subsection 1 and 2
        assertTrue( output.toString().indexOf( "* {{{SubSection_1.1}SubSection 1.1}}" ) != -1 );
        assertTrue( output.toString().indexOf( "* {{{SubSection_1.1.2.1.1}SubSection 1.1.2.1.1}}" ) == -1 );
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

        Iterator it = sink.getEventList().iterator();

        assertEquals( "head", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body", ( (SinkEventElement) it.next() ).getName() );

        SinkEventElement element = (SinkEventElement) it.next();
        assertEquals( "verbatim", element.getName() );
        Object[] args = element.getArgs();
        assertEquals( SinkEventAttributeSet.BOXED, args[0] );

        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "verbatim_", ( (SinkEventElement) it.next() ).getName() );

        element = (SinkEventElement) it.next();
        assertEquals( "verbatim", element.getName() );
        args = element.getArgs();
        assertNull( args[0] );

        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "verbatim_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body_", ( (SinkEventElement) it.next() ).getName() );

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

        Iterator it = sink.getEventList().iterator();

        assertEquals( "head", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "table", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRows", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        SinkEventElement element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1, 1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2, 2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3, 3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRows_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "table_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body_", ( (SinkEventElement) it.next() ).getName() );

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

        Iterator it = sink.getEventList().iterator();

        assertEquals( "head", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "table", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRows", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        SinkEventElement element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,\u00A0", element.getArgs()[0] );
        assertEquals( "lineBreak", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 1,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,\u00A0", element.getArgs()[0] );
        assertEquals( "lineBreak", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 2,3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,1", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,2", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "cell 3,\u00A0", element.getArgs()[0] );
        assertEquals( "lineBreak", ( (SinkEventElement) it.next() ).getName() );
        element = (SinkEventElement) it.next();
        assertEquals( "text", element.getName() );
        assertNotNull( element.getArgs()[0] );
        assertEquals( "3", element.getArgs()[0] );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );

        assertEquals( "tableRows_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "table_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body_", ( (SinkEventElement) it.next() ).getName() );

        assertFalse( it.hasNext() );
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "apt";
    }
}
