package org.apache.maven.doxia.module.xhtml;

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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id$
 */
public class XhtmlParserTest
    extends AbstractParserTest
{
    private XhtmlParser parser;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (XhtmlParser) lookup( Parser.ROLE, "xhtml" );

        // AbstractXmlParser.CachedFileEntityResolver downloads DTD/XSD files in ${java.io.tmpdir}
        // Be sure to delete them
        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String excludes = "xhtml-lat1.ent, xhtml1-transitional.dtd, xhtml-special.ent, xhtml-symbol.ent";
        @SuppressWarnings( "unchecked" )
        List<String> tmpFiles = FileUtils.getFileNames( new File( tmpDir ), excludes, null, true );
        for ( String filename : tmpFiles )
        {
            File tmpFile = new File( filename );
            tmpFile.delete();
        }
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xhtml";
    }

    /** @throws Exception  */
    public void testDocumentBodyEventsList()
        throws Exception
    {
        String text = "<html><body></body></html>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        ( (XhtmlParser) createParser() ).parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "body", it.next().getName() );
        assertEquals( "body_", it.next().getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testHeadEventsList()
        throws Exception
    {
        String text = "<head><title>Title</title><meta name=\"author\" content=\"Author\" />"
                + "<meta name=\"date\" content=\"Date\" /><meta name=\"security\" content=\"low\"/></head>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        ( (XhtmlParser) createParser() ).parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "head", it.next().getName() );
        assertEquals( "title", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "title_", it.next().getName() );
        assertEquals( "author", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "author_", it.next().getName() );
        assertEquals( "date", it.next().getName() );
        assertEquals( "text", it.next().getName() );
        assertEquals( "date_", it.next().getName() );
        assertEquals( "unknown", it.next().getName() );
        assertEquals( "head_", it.next().getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testPreEventsList()
        throws Exception
    {
        String text = "<pre></pre>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        ( (XhtmlParser) createParser() ).parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "verbatim", it.next().getName() );
        assertEquals( "verbatim_", it.next().getName() );
        assertFalse( it.hasNext() );
    }

    /**
     * Test unknown tags.
     *
     * @throws java.lang.Exception if any.
     */
    public void testUnknown()
        throws Exception
    {
        String text = "<applet><param name=\"name\" value=\"value\"/><unknown/></applet>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        ( (XhtmlParser) createParser() ).parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals( "unknown", it.next().getName() );
        assertEquals( "unknown", it.next().getName() );
        assertEquals( "unknown", it.next().getName() );
        assertEquals( "unknown", it.next().getName() );
        assertFalse( it.hasNext() );
    }
}
