package org.apache.maven.doxia.macro;

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

import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;

import junit.framework.TestCase;

/**
 * Test swf macro.
 *
 * @author ltheussl
 */
public class SwfMacroTest
        extends TestCase
{

    /**
     * Test of execute method, of class SwfMacro.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    public void testExecute()
            throws MacroExecutionException
    {

        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put( "src", "src.swf" );
        macroParameters.put( "id", "Movie" );
        macroParameters.put( "width", "50" );
        macroParameters.put( "height", "60" );
        macroParameters.put( "quality", "best" );
        macroParameters.put( "menu", "true" );
        macroParameters.put( "loop", "3" );
        macroParameters.put( "play", "false" );
        macroParameters.put( "version", "6" );
        macroParameters.put( "allowScript", "always" );


        SinkEventTestingSink sink = new SinkEventTestingSink();
        MacroRequest request = new MacroRequest( macroParameters, new File( "." ) );
        SwfMacro macro = new SwfMacro();
        macro.required( "src", "value" );

        try
        {
            macro.required( "src", "" );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }

        try
        {
            macro.required( "src", null );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }

        macro.execute( sink, request );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        SinkEventElement event = it.next();

        assertEquals( "rawText", event.getName() );
        assertFalse( it.hasNext() );

        request = new MacroRequest( new HashMap<String, Object>(), new File( "." ) );
        sink.reset();

        macro.execute( sink, request );

        it = sink.getEventList().iterator();
        event = it.next();

        assertEquals( "rawText", event.getName() );
        assertFalse( it.hasNext() );
    }

    /**
     * Test that SwfMacro does not crash if other things then Strings are provided.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    public void testOthersThenStringParameters()
            throws MacroExecutionException
    {

        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put( "src", "src.swf" );
        macroParameters.put( "id", "Movie" );
        macroParameters.put( "width", "50" );
        macroParameters.put( "height", "60" );
        macroParameters.put( "quality", "best" );
        macroParameters.put( "menu", "true" );
        macroParameters.put( "loop", "3" );
        macroParameters.put( "play", "false" );
        macroParameters.put( "version", "6" );
        macroParameters.put( "allowScript", "always" );
        macroParameters.put( "notAString", 0 );


        SinkEventTestingSink sink = new SinkEventTestingSink();
        MacroRequest request = new MacroRequest( macroParameters, new File( "." ) );
        SwfMacro macro = new SwfMacro();
        macro.required( "src", "value" );

        macro.execute( sink, request );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        SinkEventElement event = it.next();

        assertEquals( "rawText", event.getName() );
        assertFalse( it.hasNext() );

        request = new MacroRequest( new HashMap<String, Object>(), new File( "." ) );
        sink.reset();

        macro.execute( sink, request );

        it = sink.getEventList().iterator();
        event = it.next();

        assertEquals( "rawText", event.getName() );
        assertFalse( it.hasNext() );
    }


}
