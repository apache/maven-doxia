package org.apache.maven.doxia.module.markdown;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

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

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.codehaus.plexus.util.IOUtil;

/**
 * Tests for {@link MarkdownParser}.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 1.3
 */
public class MarkdownParserTest
    extends AbstractParserTest
{

    /**
     * The {@link MarkdownParser} used for the tests.
     */
    protected MarkdownParser parser;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        parser = lookup( Parser.ROLE, MarkdownParser.ROLE_HINT );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Parser createParser()
    {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String outputExtension()
    {
        return MarkdownParserModule.FILE_EXTENSION;
    }

    /**
     * Assert the paragraph sink event is fired when parsing "paragraph.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testParagraphSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "paragraph" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "paragraph", "text", "paragraph_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the bold sink event is fired when parsing "bold.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testBoldSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "bold" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "paragraph", "inline", "text", "inline_", "paragraph_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the italic sink event is fired when parsing "italic.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testItalicSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "italic" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "paragraph", "inline", "text", "inline_", "paragraph_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the code sink event is fired when parsing "code.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testCodeSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "code" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "paragraph", "text", "paragraph_", "text", "unknown", "verbatim", "text", "verbatim_", "unknown", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the image sink event is fired when parsing "image.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testImageSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "image" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "paragraph", "text", "figureGraphics", "text", "paragraph_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the link sink event is fired when parsing "link.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testLinkSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "link" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "paragraph", "text", "link", "text", "link_", "text", "paragraph_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the link sink event is fired when parsing "link.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testLinkRewriteSinkEvent()
        throws Exception
    {
        List<SinkEventElement> eventList = parseFileToEventTestingSink( "link_rewrite" ).getEventList();

        Iterator<SinkEventElement> it = eventList.iterator();
        assertEquals( it, "head", "head_", "body", "paragraph", "text", "link", "text", "link_", "text", "link", "text",
                      "link_", "text", "paragraph_", "body_" );

        assertFalse( it.hasNext() );

        assertEquals( "doc.html", eventList.get( 5 ).getArgs()[0] );
        assertEquals( "ftp://doc.md", eventList.get( 9 ).getArgs()[0] );
    }

    public void testLinkWithAnchorAndQuery() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "link_anchor_query" ).getEventList().iterator();
        
        assertEquals( it, "head", "head_", "body", "paragraph", "link", "text", "link_", "paragraph_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the list sink event is fired when parsing "list.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testListSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "list" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "list", "text", "listItem", "text", "listItem_", "listItem", "text",
                      "listItem_", "text", "list_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the numbered list sink event is fired when parsing "numbered-list.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testNumberedListSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "numbered-list" ).getEventList().iterator();

        assertEquals( it, "head", "head_", "body", "numberedList", "text", "numberedListItem", "text", "numberedListItem_",
                      "numberedListItem", "text", "numberedListItem_", "text", "numberedList_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the metadata is passed through when parsing "metadata.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testMetadataSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "metadata" ).getEventList().iterator();

        assertEquals( it, "head", "title", "text", "title_", "author", "text", "author_", "date", "text", "date_",
                      "head_", "body", "unknown", "text", "unknown", "paragraph", "text", "paragraph_", "section1",
                      "sectionTitle1", "text", "sectionTitle1_", "paragraph", "text", "paragraph_", "section1_",
                      "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the first header is passed as title event when parsing "first-heading.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testFirstHeadingSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "first-heading" ).getEventList().iterator();

        // NOTE: H1 is rendered as "unknown" and H2 is "section1" (see DOXIA-203)
        assertEquals( it, "head", "title", "text", "title_", "head_", "body", "section1", "sectionTitle1", "text",
                      "sectionTitle1_", "paragraph", "text", "paragraph_", "section1_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the first header is passed as title event when parsing "comment-before-heading.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testCommentBeforeHeadingSinkEvent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "comment-before-heading" ).getEventList().iterator();

        // NOTE: H1 is rendered as "unknown" and H2 is "section1" (see DOXIA-203)
        assertEquals( it, "head", "title", "text", "title_", "head_", "body", "comment", "text", "unknown", "text",
                      "unknown", "paragraph", "text", "link", "text", "link_", "text", "paragraph_", "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the first header is passed as title event when parsing "comment-before-heading.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testHtmlContent()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "html-content" ).getEventList().iterator();

        // NOTE: H1 and DIV are rendered as "unknown" and H2 is "section1" (see DOXIA-203)
        assertEquals( it, "head", "head_", "body", "unknown", "text", "paragraph", "inline", "text",
                      "inline_", "text", "inline", "text", "inline_", "text", "paragraph_", "text", "unknown", "text", "horizontalRule", "unknown",
                "text", "unknown", "paragraph", "text", "paragraph_", "text", "table", "tableRows", "text", "tableRow",
                "tableHeaderCell", "text", "tableHeaderCell_", "tableRow_", "text", "tableRow",
                                "tableCell", "text", "tableCell_", "tableRow_", "text", "tableRows_", "table_",
                "body_" );

        assertFalse( it.hasNext() );
    }

    /**
     * Parse the file and return a {@link SinkEventTestingSink}.
     *
     * @param file the file to parse with {@link #parser}.
     * @return a sink to test parsing events.
     * @throws ParseException if the document parsing failed.
     */
    protected SinkEventTestingSink parseFileToEventTestingSink( String file ) throws ParseException, IOException
    {
        SinkEventTestingSink sink = null;
        try ( Reader reader = getTestReader( file ) )
        {
            sink = new SinkEventTestingSink();
            parser.parse( reader, sink );
        }

        return sink;
    }

    /** @throws Exception  */
    public void testTocMacro()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "macro-toc" ).getEventList().iterator();

        assertEquals( it, "head", "title", "text", "title_", "head_",
                      "body",
                      "list", // TOC start
                      "listItem", "link", "text", "link_", // emtpy section 2 TOC entry
                      "list", // sections 3 list start
                      "listItem", "link", "text", "link_", "listItem_", // first section 3 TOC entry
                      "listItem", "link", "text", "link_", "listItem_", // second section 3 TOC entry
                      "list_", // sections 3 list end
                      "listItem_", // emtpy section 2 TOC entry end
                      "list_", // TOC end
                      "text",
                      "section1",
                      "section2", "sectionTitle2", "text", "sectionTitle2_", "section2_",
                      "section2", "sectionTitle2", "text", "sectionTitle2_", "section2_",
                      "section1_",
                      "body_" );
    }

    /**
     * TOC macro fails with EmptyStackException when title 2 followed by title 4 then title 2
     * 
     * @throws Exception
     */
    public void testTocMacroDoxia559()
        throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "macro-toc-DOXIA-559" ).getEventList().iterator();

        assertEquals( it, "head", "title", "text", "title_", "head_",
                      "body",
                      "list", // TOC start
                      "listItem", "link", "text", "link_", // first section 2 TOC entry
                      "list", // sections 3 list start
                      "listItem", "link", "text", "link_", "listItem_", // empty section 3 TOC entry
                      "list_", // sections 3 list end
                      "listItem_", // first section 2 TOC entry end
                      "listItem", "link", "text", "link_", "listItem_", // second section 2 TOC entry
                      "list_", // TOC end
                      "text",
                      "section1", "sectionTitle1", "text", "sectionTitle1_",
                      "section2",
                      "section3", "sectionTitle3", "text", "sectionTitle3_",
                      "section3_",
                      "section2_",
                      "section1_",
                      "section1", "sectionTitle1", "text", "sectionTitle1_",
                      "section1_",
                      "body_" );
    }
}
