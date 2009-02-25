package org.apache.maven.doxia.parser;

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

import java.util.Iterator;

import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test for XhtmlBaseParser.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class XhtmlBaseParserTest
    extends PlexusTestCase
{
    private XhtmlBaseParser parser;
    private final SinkEventTestingSink sink = new SinkEventTestingSink();

    /** {@inheritDoc} */
    protected void setUp() throws Exception
    {
        super.setUp();

        parser = new XhtmlBaseParser();
        sink.reset();
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /** @throws Exception  */
    public void testHeadingEventsList()
        throws Exception
    {
        String text = "<p><h2></h2><h3></h3><h4></h4><h5></h5><h6></h6><h2></h2></p>";

        parser.parse( text, sink );

        Iterator it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section2", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle2", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle2_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section3", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle3", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle3_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section4", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle4", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle4_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section5", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle5", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle5_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section5_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section4_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section3_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section2_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testFigureEventsList()
        throws Exception
    {
        String text = "<img src=\"source\" title=\"caption\" />";

        parser.parse( text, sink );

        Iterator it = sink.getEventList().iterator();

        assertEquals( "figureGraphics", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testTableEventsList()
        throws Exception
    {
        // TODO: table caption, see DOXIA-177

        String text = "<table align=\"center\"><tr><th>Header</th></tr><tr><td>cell</td></tr></table>";

        parser.parse( text, sink );

        Iterator it = sink.getEventList().iterator();

        assertEquals( "table", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRows", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableHeaderCell", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableHeaderCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableCell_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRow_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "tableRows_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "table_", ( (SinkEventElement) it.next() ).getName() );

        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testSignificantWhiteSpace()
        throws Exception
    {
        // NOTE significant white space
        String text = "<p><b>word</b> <i>word</i></p>";

        parser.parse( text, sink );

        Iterator it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "bold", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "bold_", ( (SinkEventElement) it.next() ).getName() );

        SinkEventElement el = (SinkEventElement) it.next();
        assertEquals( "text", el.getName() );
        assertEquals( " ",  (String) el.getArgs()[0] );

        assertEquals( "italic", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "italic_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );


        // same test with EOL
        String eol = System.getProperty( "line.separator" );
        text = "<p><b>word</b>" + eol + "<i>word</i></p>";

        sink.reset();
        parser.parse( text, sink );
        it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "bold", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "bold_", ( (SinkEventElement) it.next() ).getName() );

        el = (SinkEventElement) it.next();
        assertEquals( "text", el.getName() );
        // according to section 2.11 of the XML spec, parsers must normalize line breaks to "\n"
        assertEquals( "\n",  (String) el.getArgs()[0] );

        assertEquals( "italic", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "italic_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );


        // DOXIA-189: there should be no EOL after closing tag
        text = "<p>There should be no space after the last <i>word</i>.</p>";

        sink.reset();
        parser.parse( text, sink );
        it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "italic", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "italic_", ( (SinkEventElement) it.next() ).getName() );

        el = (SinkEventElement) it.next();
        assertEquals( "text", el.getName() );
        assertEquals( ".",  (String) el.getArgs()[0] );

        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }
}
