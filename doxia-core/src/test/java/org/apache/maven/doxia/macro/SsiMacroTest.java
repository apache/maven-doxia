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

import org.apache.maven.doxia.parser.XhtmlBaseParser;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;

import junit.framework.TestCase;

public class SsiMacroTest
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
        macroParameters.put( "function", "include" );
        macroParameters.put( "file", "include-file.html" );

        MacroRequest request = new MacroRequest( "source", new XhtmlBaseParser(), macroParameters, new File( "." ) );
        SsiMacro macro = new SsiMacro();

        SinkEventTestingSink sink = new SinkEventTestingSink();
        macro.execute( sink, request );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        SinkEventElement event = it.next();

        assertEquals( "comment", event.getName() );
        String comment = (String) event.getArgs()[0];
        assertEquals( "#include file=\"include-file.html\" ", comment );
        assertFalse( it.hasNext() );
    }
}
