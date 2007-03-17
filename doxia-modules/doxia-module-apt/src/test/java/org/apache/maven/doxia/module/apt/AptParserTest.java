package org.apache.maven.doxia.module.apt;

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

import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.maven.doxia.parser.AbstractParserTestCase;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class AptParserTest
    extends AbstractParserTestCase
{
    private static final String EOL = System.getProperty( "line.separator" );

    private AptParser parser;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (AptParser) lookup( Parser.ROLE, "apt" );
    }

    /**
     * @see org.apache.maven.doxia.parser.AbstractParserTestCase#getParser()
     */
    protected Parser getParser()
    {
        return parser;
    }

    /**
     * @see org.apache.maven.doxia.parser.AbstractParserTestCase#getDocument()
     */
    protected String getDocument()
    {
        return "src/test/site/apt/linebreak.apt";
    }

    /**
     * @throws Exception
     */
    public void testLineBreak()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;

        try
        {
            output = new StringWriter();
            reader = new FileReader( getTestFile( getBasedir(), getDocument() ) );

            Sink sink = new AptSink( output );
            getParser().parse( reader, sink );

            assertTrue( output.toString().indexOf( "Line\\" + EOL + "break." ) != -1 );
        }
        finally
        {
            output.close();
            reader.close();
        }
    }

    /**
     * @throws Exception
     */
    public void testSnippetMacro()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;

        try
        {
            output = new StringWriter();
            reader = new FileReader( getTestFile( getBasedir(), "src/test/site/apt/macro.apt" ) );

            Sink sink = new AptSink( output );
            getParser().parse( reader, sink );

            assertTrue( output.toString().indexOf( "<modelVersion\\>4.0.0\\</modelVersion\\>" ) != -1 );
        }
        finally
        {
            output.close();
            reader.close();
        }
    }

    /**
     * @throws Exception
     */
    public void testTocMacro()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;

        try
        {
            output = new StringWriter();
            reader = new FileReader( getTestFile( getBasedir(), "src/test/site/apt/toc.apt" ) );

            Sink sink = new AptSink( output );
            getParser().parse( reader, sink );

            // No section, only subsection 1 and 2
            assertTrue( output.toString().indexOf( "* {{{#subsection_1}SubSection 1}}" ) != -1 );
            assertTrue( output.toString().indexOf( "* {{{#subsection_1211}SubSection 1211}}" ) == -1 );
        }
        finally
        {
            output.close();
            reader.close();
        }
    }
}
