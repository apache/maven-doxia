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
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        // AbstractXmlParser.CachedFileEntityResolver downloads DTD/XSD files in ${java.io.tmpdir}
        // Be sure to delete them
        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String excludes = "xhtml-lat1.ent, xhtml1-transitional.dtd, xhtml-special.ent, xhtml-symbol.ent";
        List tmpFiles = FileUtils.getFileNames( new File( tmpDir ), excludes, null, true );
        for ( Iterator it = tmpFiles.iterator(); it.hasNext(); )
        {
            File tmpFile = new File( it.next().toString() );
            tmpFile.delete();
        }
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return new XhtmlParser();
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

        Iterator it = sink.getEventList().iterator();

        assertEquals( "body", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body_", ( (SinkEventElement) it.next() ).getName() );
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

        Iterator it = sink.getEventList().iterator();

        assertEquals( "head", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "title", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "title_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "author", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "author_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "date", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "date_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "unknown", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testPreEventsList()
        throws Exception
    {
        String text = "<pre></pre>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        ( (XhtmlParser) createParser() ).parse( text, sink );

        Iterator it = sink.getEventList().iterator();

        assertEquals( "verbatim", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "verbatim_", ( (SinkEventElement) it.next() ).getName() );
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

        Iterator it = sink.getEventList().iterator();
        assertEquals( "unknown", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "unknown", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "unknown", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "unknown", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }
}
