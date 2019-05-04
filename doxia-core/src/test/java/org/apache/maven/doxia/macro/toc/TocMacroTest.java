package org.apache.maven.doxia.macro.toc;

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
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.parser.XhtmlBaseParser;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.apache.maven.doxia.sink.impl.XhtmlBaseSink;

/**
 * Test toc macro.
 *
 * @author ltheussl
 * @version $Id$
 */
public class TocMacroTest
    extends TestCase
{
    /**
     * Test of execute method, of class TocMacro.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    public void testExecute()
        throws MacroExecutionException
    {
        String sourceContent = "<div><h2>h21</h2><h2>h22</h2><h3>h3</h3><h4>h4</h4><h2>h23</h2></div>";

        XhtmlBaseParser parser = new XhtmlBaseParser();
        parser.setSecondParsing( true );

        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put( "parser", parser );
        macroParameters.put( "sourceContent", sourceContent );
        macroParameters.put( "section", "sec1" );

        File basedir = new File( "" );

        SinkEventTestingSink sink = new SinkEventTestingSink();
        MacroRequest request = new MacroRequest( macroParameters, basedir );
        TocMacro macro = new TocMacro();
        macro.execute( sink, request );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals( "list", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "list", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "list", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "list_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "list_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "list_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

        // test parameters

        parser = new XhtmlBaseParser();
        parser.setSecondParsing( true );
        macroParameters.put( "parser", parser );
        macroParameters.put( "section", "2" );
        macroParameters.put( "fromDepth", "1" );
        macroParameters.put( "toDepth", "2" );
        macroParameters.put( "class", "myClass" );
        macroParameters.put( "id", "myId" );

        sink.reset();
        request = new MacroRequest( macroParameters, basedir );
        macro.execute( sink, request );

        it = sink.getEventList().iterator();
        SinkEventElement event = it.next();
        assertEquals( "list", event.getName() );
        SinkEventAttributeSet atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals( "myId", atts.getAttribute( "id" ) );
        assertEquals( "myClass", atts.getAttribute( "class" ) );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "h22", (String) event.getArgs()[0] );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "list", ( it.next() ).getName() );
        assertEquals( "listItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "h3", (String) event.getArgs()[0] );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "list_", ( it.next() ).getName() );
        assertEquals( "listItem_", ( it.next() ).getName() );
        assertEquals( "list_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /**
     * Test DOXIA-366.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    public void testTocStyle()
        throws MacroExecutionException
    {
        String sourceContent =
            "<div><h2>h<b>21</b></h2><h2>h<i>22</i></h2><h3>h<tt>3</tt></h3><h4>h4</h4><h2>h23</h2></div>";

        XhtmlBaseParser parser = new XhtmlBaseParser();
        parser.setSecondParsing( true );

        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put( "parser", parser );
        macroParameters.put( "sourceContent", sourceContent );
        macroParameters.put( "section", "sec1" );

        File basedir = new File( "" );

        StringWriter out = new StringWriter();
        XhtmlBaseSink sink = new XhtmlBaseSink( out );
        MacroRequest request = new MacroRequest( macroParameters, basedir );
        TocMacro macro = new TocMacro();
        macro.execute( sink, request );

        assertTrue( out.toString().contains( "<a href=\"#h21\">h21</a>" ) );
        assertTrue( out.toString().contains( "<a href=\"#h22\">h22</a>" ) );
        assertTrue( out.toString().contains( "<a href=\"#h3\">h3</a>" ) );
    }
}
