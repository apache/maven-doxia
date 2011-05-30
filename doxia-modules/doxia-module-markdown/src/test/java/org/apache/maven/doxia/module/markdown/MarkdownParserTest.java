package org.apache.maven.doxia.module.markdown;

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

import java.io.Reader;

import java.util.Iterator;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;

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
    protected void setUp() throws Exception
    {
        super.setUp();
        parser = (MarkdownParser) lookup( Parser.ROLE, MarkdownParser.ROLE_HINT );
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
        return MarkdownSiteModule.FILE_EXTENSION;
    }

    /**
     * Assert the paragraph sink event is fired when parsing "paragraph.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testParagraphSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "paragraph" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "paragraph", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "paragraph_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the bold sink event is fired when parsing "bold.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testBoldSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "bold" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "paragraph", it.next().getName() );
        assertEquals( "bold", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "bold_", it.next().getName() );
        assertEquals( "paragraph_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the italic sink event is fired when parsing "italic.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testItalicSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "italic" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "paragraph", it.next().getName() );
        assertEquals( "italic", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "italic_", it.next().getName() );
        assertEquals( "paragraph_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the code sink event is fired when parsing "code.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testCodeSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "code" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "paragraph", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "paragraph_", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "verbatim", it.next().getName() );
        assertEquals( "monospaced", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "monospaced_", it.next().getName() );
        assertEquals( "verbatim_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the image sink event is fired when parsing "image.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testImageSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "image" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "paragraph", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "figureGraphics", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "paragraph_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the link sink event is fired when parsing "link.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testLinkSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "link" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "paragraph", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "link", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "link_", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "paragraph_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the list sink event is fired when parsing "list.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testListSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "list" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "list", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "listItem", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "listItem_", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "listItem", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "listItem_", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "list_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Assert the numbered list sink event is fired when parsing "numbered-list.md".
     *
     * @throws Exception if the event list is not correct when parsing the document.
     */
    public void testNumberedListSinkEvent() throws Exception
    {
        Iterator<SinkEventElement> it = parseFileToEventTestingSink( "numbered-list" ).getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "numberedList", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "numberedListItem", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "numberedListItem_", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "numberedListItem", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "numberedListItem_", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "numberedList_", it.next().getName() );
        assertEquals( "body_", it.next().getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * Parse the file and return a {@link SinkEventTestingSink}.
     *
     * @param file the file to parse with {@link #parser}.
     * @return a sink to test parsing events.
     * @throws ParseException if the document parsing failed.
     */
    protected SinkEventTestingSink parseFileToEventTestingSink( String file ) throws ParseException
    {
        Reader reader = null;
        SinkEventTestingSink sink = null;
        try
        {
            reader = getTestReader( file );
            sink = new SinkEventTestingSink();
            parser.parse( reader, sink );
        }
        finally
        {
            IOUtil.close( reader );
        }

        return sink;
    }
}
