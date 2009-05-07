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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.parser.XhtmlBaseParser;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;

/**
 * Test toc macro.
 *
 * @author ltheussl
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

        Map macroParameters = new HashMap();
        macroParameters.put( "parser", parser );
        macroParameters.put( "sourceContent", sourceContent );
        macroParameters.put( "section", "sec1" );

        File basedir = new File( "" );

        SinkEventTestingSink sink = new SinkEventTestingSink();
        MacroRequest request = new MacroRequest( macroParameters, basedir );
        TocMacro macro = new TocMacro();
        macro.execute( sink, request );

        Iterator it = sink.getEventList().iterator();
        assertEquals( "list", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );

        // test parameters

        parser = new XhtmlBaseParser();
        parser.setSecondParsing( true );
        macroParameters.put( "parser", parser );
        macroParameters.put( "section", "2" );
        macroParameters.put( "fromDepth", "0" );
        macroParameters.put( "toDepth", "1" );

        sink.reset();
        request = new MacroRequest( macroParameters, basedir );
        macro.execute( sink, request );

        it = sink.getEventList().iterator();
        assertEquals( "list", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        SinkEventElement event = (SinkEventElement) it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "h22",  (String) event.getArgs()[0] );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        event = (SinkEventElement) it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "h3",  (String) event.getArgs()[0] );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "listItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "list_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }
}
