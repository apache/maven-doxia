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
 * Test echo macro.
 *
 * @author ltheussl
 */
public class EchoMacroTest
        extends TestCase
{

    /**
     * Test of execute method, of class EchoMacro.
     */
    public void testExecute()
    {
        final Map<String,Object> macroParameters = new HashMap<>();
        macroParameters.put( "paramName", "paramValue" );
        macroParameters.put( "parser", "parserValue" );
        macroParameters.put( "sourceContent", "sourceContentValue" );

        SinkEventTestingSink sink = new SinkEventTestingSink();
        MacroRequest request = new MacroRequest( macroParameters, new File( "." ) );

        new EchoMacro().execute( sink, request );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        SinkEventElement event = it.next();
        assertEquals( "verbatim", event.getName() );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "echo" + Macro.EOL,  (String) event.getArgs()[0] );
        event = it.next();
        assertEquals( "text", event.getName() );
        assertEquals( "paramName ---> paramValue" + Macro.EOL,  (String) event.getArgs()[0] );
        event = it.next();
        assertEquals( "verbatim_", event.getName() );
        assertFalse( it.hasNext() );
    }

    /**
     * Test log.
     */
    public void testLog()
    {
        EchoMacro macro = new EchoMacro();
        macro.enableLogging( null );
        assertNotNull ( macro.getLog() );
        assertNotNull ( macro.getLog() );
    }
}
