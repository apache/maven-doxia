package org.apache.maven.doxia.macro.snippet;

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

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test snippet macro.
 *
 * @author ltheussl
 */
public class SnippetMacroTest
        extends PlexusTestCase
{
    /**
     * Test of execute method, of class SnippetMacro.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    public void testExecute()
            throws MacroExecutionException
    {
        File basedir = new File( getBasedir() );
        Map macroParameters = new HashMap();
        macroParameters.put( "file", "src/test/resources/macro/snippet/testSnippet.txt" );

        SinkEventTestingSink sink = new SinkEventTestingSink();

        MacroRequest request = new MacroRequest( macroParameters, basedir );
        SnippetMacro macro = new SnippetMacro();
        macro.execute( sink, request );

        Iterator it = sink.getEventList().iterator();
        assertEquals( "verbatim", ( (SinkEventElement) it.next() ).getName() );
        SinkEventElement event = (SinkEventElement) it.next();
        assertEquals( "text", event.getName() );
        String snippet = (String) event.getArgs()[0];
        assertEquals( "verbatim_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );

        assertTrue( snippet.indexOf( "preamble" ) != -1 );
        assertTrue( snippet.indexOf( "first snippet" ) != -1 );
        assertTrue( snippet.indexOf( "interlude" ) != -1 );
        assertTrue( snippet.indexOf( "second snippet" ) != -1 );
        assertTrue( snippet.indexOf( "conclusion" ) != -1 );

        // again

        macroParameters.put( "id", "firstId" );
        macroParameters.put( "verbatim", "" );
        sink.reset();
        request = new MacroRequest( macroParameters, basedir );
        macro.execute( sink, request );

        it = sink.getEventList().iterator();
        assertEquals( "verbatim", ( (SinkEventElement) it.next() ).getName() );
        event = (SinkEventElement) it.next();
        assertEquals( "text", event.getName() );
        snippet = (String) event.getArgs()[0];
        assertEquals( "verbatim_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );

        assertTrue( snippet.indexOf( "preamble" ) == -1 );
        assertTrue( snippet.indexOf( "first snippet" ) != -1 );
        assertTrue( snippet.indexOf( "interlude" ) == -1 );
        assertTrue( snippet.indexOf( "second snippet" ) == -1 );
        assertTrue( snippet.indexOf( "conclusion" ) == -1 );

        // again

        macroParameters.put( "id", "secondId" );
        macroParameters.put( "verbatim", "false" );
        sink.reset();
        request = new MacroRequest( macroParameters, basedir );
        macro.execute( sink, request );

        it = sink.getEventList().iterator();
        event = (SinkEventElement) it.next();
        assertEquals( "rawText", event.getName() );
        snippet = (String) event.getArgs()[0];
        assertFalse( it.hasNext() );

        assertTrue( snippet.indexOf( "preamble" ) == -1 );
        assertTrue( snippet.indexOf( "first snippet" ) == -1 );
        assertTrue( snippet.indexOf( "interlude" ) == -1 );
        assertTrue( snippet.indexOf( "second snippet" ) != -1 );
        assertTrue( snippet.indexOf( "conclusion" ) == -1 );
    }
}
