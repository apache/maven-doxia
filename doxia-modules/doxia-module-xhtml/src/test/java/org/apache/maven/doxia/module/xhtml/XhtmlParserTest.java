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

import java.io.StringWriter;
import java.util.Iterator;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;


/**
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id$
 */
public class XhtmlParserTest
    extends AbstractParserTest
{
    protected Parser createParser()
    {
        return new XhtmlParser();
    }

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
        String text = "<head><title>Title</title><meta name=\"author\" content=\"Author\" /><meta name=\"date\" content=\"Date\" /></head>";

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
     * @throws Exception if any
     */
    public void testDoxia250()
        throws Exception
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "<!DOCTYPE test [" ).append( EOL );
        sb.append( "<!ENTITY   " ).append( EOL ).append( "   foo   " ).append( EOL ).append( "   \"   " )
          .append( EOL ).append( "   &#x159;   " ).append( EOL ).append( "   \">" ).append( EOL );
        sb.append( "<!ENTITY   " ).append( EOL ).append( "   foo1   " ).append( EOL ).append( "   \"   " )
          .append( EOL ).append( "   &nbsp;   " ).append( EOL ).append( "   \">" ).append( EOL );
        sb.append( "<!ENTITY   " ).append( EOL ).append( "   foo2   " ).append( EOL ).append( "  \"   " )
          .append( EOL ).append( "   &#x161;   " ).append( EOL ).append( "   \">" ).append( EOL );
        sb.append( "]>" ).append( EOL );
        sb.append( "<html><body>&foo;&foo1;&foo2;</body></html>" );

        String text = sb.toString();
        StringWriter w = new StringWriter();
        Sink sink = new XhtmlSink( w );
        XhtmlParser parser = (XhtmlParser) createParser();
        parser.setValidate( false );
        parser.parse( text.toString(), sink );
        sink.close();
        String result = w.toString();

        assertTrue( result.indexOf( "&#x159;" ) != -1 );
        assertTrue( result.indexOf( "&nbsp;" ) != -1 );
        assertTrue( result.indexOf( "&#x161;" ) != -1 );
    }
}
