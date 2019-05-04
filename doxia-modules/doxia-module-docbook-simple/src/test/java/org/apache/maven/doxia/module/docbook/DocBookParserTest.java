package org.apache.maven.doxia.module.docbook;

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
import java.io.Writer;

import java.util.Iterator;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id$
 */
public class DocBookParserTest extends AbstractParserTest
{
    /** The parser to test. */
    private DocBookParser parser;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = lookup( Parser.ROLE, "docbook" );
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xml";
    }

    /**
     * Parses the test document test.xml and re-emits it into test.docbook.
     *
     * @throws IOException if something goes wrong
     * @throws ParseException if something goes wrong
     */
    public void testTestDocument()
        throws IOException, ParseException
    {
        Writer writer = null;
        Reader reader = null;

        try
        {
            writer = getTestWriter( "test", "docbook" );
            reader = getTestReader( "test" );

            Sink sink = new DocBookSink( writer );

            createParser().parse( reader, sink );

            writer = getTestWriter( "sdocbook_full", "docbook" );
            reader = getTestReader( "sdocbook_full" );

            sink = new DocBookSink( writer );

            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( reader );
            IOUtil.close( writer );
        }
    }

    /** @throws Exception  */
    public void testSignificantWhiteSpace()
        throws Exception
    {
        // NOTE significant white space
        String text = "<para><command>word</command> <emphasis>word</emphasis></para>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "bold", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "bold_", ( it.next() ).getName() );

        SinkEventElement el = it.next();
        assertEquals( "text", el.getName() );
        assertEquals( " ",  (String) el.getArgs()[0] );

        assertEquals( "italic", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "italic_", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );


        // same test with EOL
        text = "<para><command>word</command>" + EOL + "<emphasis>word</emphasis></para>";

        sink.reset();
        parser.parse( text, sink );
        it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "bold", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "bold_", ( it.next() ).getName() );

        el = it.next();
        assertEquals( "text", el.getName() );
        assertEquals( EOL,  (String) el.getArgs()[0] );

        assertEquals( "italic", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "italic_", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testIds()
        throws Exception
    {
        final String text = "<article id=\"article\"><section id=\"section\">"
                + "<title id=\"title\">Title</title><para id=\"para\">Paragraph</para></section></article>";

        final SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );
        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        SinkEventElement event = it.next();
        assertEquals( "head", event.getName() );
        assertEquals( " id=article", event.getArgs()[0].toString() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );

        event = it.next();
        assertEquals( "section1", event.getName() );
        assertEquals( " id=section", event.getArgs()[0].toString() );

        event = it.next();
        assertEquals( "sectionTitle1", event.getName() );
        assertEquals( " id=title", event.getArgs()[0].toString() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );

        event = it.next();
        assertEquals( "paragraph", event.getName() );
        assertEquals( " id=para", event.getArgs()[0].toString() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testFigure()
        throws Exception
    {
        String text = "<mediaobject><imageobject>"
                + "<imagedata fileref=\"fileref\" format=\"PNG\" /></imageobject>"
                + "<caption><para>Figure caption</para></caption></mediaobject>";

        final SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );
        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "figure", ( it.next() ).getName() );
        assertEquals( "figureGraphics", ( it.next() ).getName() );
        assertEquals( "figureCaption", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "figureCaption_", ( it.next() ).getName() );
        assertEquals( "figure_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

        sink.reset();
        text = "<figure><title>Title</title><mediaobject><imageobject>"
                + "<imagedata fileref=\"fileref\" format=\"PNG\"/></imageobject>"
                + "<textobject><phrase>text</phrase></textobject></mediaobject></figure>";
        parser.parse( text, sink );
        it = sink.getEventList().iterator();

        assertEquals( "bold", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "bold_", ( it.next() ).getName() );
        assertEquals( "figure", ( it.next() ).getName() );
        assertEquals( "figureGraphics", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "figure_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testLinks()
        throws Exception
    {
        final String text = "<para><anchor id=\"Anchor\" />Anchor<!-- anchor_end -->."
                + "Link to <link linkend=\"Anchor\">Anchor</link>."
                + "Link to <ulink url=\"url.com\">url.com</ulink>.</para>";

        final SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );
        final Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "anchor", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );

        SinkEventElement event = it.next();
        assertEquals( "link", event.getName() );
        assertEquals( "#Anchor", event.getArgs()[0].toString() );

        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );

        event = it.next();
        assertEquals( "link", event.getName() );
        assertEquals( "url.com", event.getArgs()[0].toString() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testStyles()
        throws Exception
    {
        final String text = "<para><emphasis>Italic</emphasis><emphasis role=\"bold\">Bold</emphasis>"
                + "<literal>Monospaced</literal></para>";

        final SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );
        final Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "italic", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "italic_", ( it.next() ).getName() );
        assertEquals( "bold", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "bold_", ( it.next() ).getName() );
        assertEquals( "monospaced", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "monospaced_", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testLists()
        throws Exception
    {
        String text = "<itemizedlist><listitem><para>item</para></listitem></itemizedlist>";

        final SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );
        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "list", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "list_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

        text = "<orderedlist numeration=\"upperalpha\"><listitem><para>item</para></listitem></orderedlist>";
        sink.reset();
        parser.parse( text, sink );
        it = sink.getEventList().iterator();

        SinkEventElement event = it.next();
        assertEquals( "numberedList", event.getName() );
        assertEquals( Sink.NUMBERING_UPPER_ALPHA, ( (Integer) event.getArgs()[0] ).intValue() );
        assertEquals( "numberedListItem", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "numberedListItem_", ( it.next() ).getName() );
        assertEquals( "numberedList_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

        text = "<variablelist><varlistentry><term>term</term><listitem><para>definition</para>"
                + "</listitem></varlistentry></variablelist>";
        sink.reset();
        parser.parse( text, sink );
        it = sink.getEventList().iterator();

        assertEquals( "definitionList", ( it.next() ).getName() );
        assertEquals( "definitionListItem", ( it.next() ).getName() );
        assertEquals( "definedTerm", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "definedTerm_", ( it.next() ).getName() );
        assertEquals( "definition", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "definition_", ( it.next() ).getName() );
        assertEquals( "definitionListItem_", ( it.next() ).getName() );
        assertEquals( "definitionList_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testTables()
        throws Exception
    {
        String text = "<informaltable frame=\"none\"><tgroup cols=\"2\">"
                + "<colspec colwidth=\"0.5in\"/><colspec colwidth=\"0.5in\"/>"
                + "<thead><row><entry>head 1</entry><entry>head 2</entry></row></thead>"
                + "<tbody><row><entry>1</entry><entry>2</entry></row></tbody></tgroup></informaltable>";

        final SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );
        final Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "table", ( it.next() ).getName() );
        assertEquals( "tableRows", ( it.next() ).getName() );
        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableHeaderCell", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "tableHeaderCell_", ( it.next() ).getName() );
        assertEquals( "tableHeaderCell", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "tableHeaderCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );
        assertEquals( "tableRow", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableCell", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "tableCell_", ( it.next() ).getName() );
        assertEquals( "tableRow_", ( it.next() ).getName() );
        assertEquals( "tableRows_", ( it.next() ).getName() );
        assertEquals( "table_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testHead()
        throws Exception
    {
        String text = "<article><articleinfo><title>Title</title>"
                + "<corpauthor>CorpAuthor</corpauthor><date>Date</date></articleinfo>"
                + "<para>Paragraph</para></article>";

        final SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse( text, sink );

        final Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "title", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "title_", ( it.next() ).getName() );
        assertEquals( "author", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "author_", ( it.next() ).getName() );
        assertEquals( "date", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "date_", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }
}
