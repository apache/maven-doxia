package org.apache.maven.doxia.module.confluence;

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

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.TextSink;

/**
 * Test class for ConfluenceParser.
 */
public class ConfluenceParserTest
    extends AbstractParserTest
{
    private ConfluenceParser parser;

    private StringWriter output;

    private Reader reader;

    private Writer writer;


    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (ConfluenceParser) lookup( Parser.ROLE, "confluence" );

        output = null;
        reader = null;
        writer = null;
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "confluence";
    }

    /** @throws Exception */
    public void testLineBreak()
        throws Exception
    {
        String lineBreak = getLineBreakString();

        try
        {
            output = new StringWriter();
            reader = getTestReader( "linebreak", outputExtension() );
            writer = getTestWriter( "linebreak", "txt" );

            Sink sink = new TextSink( output );
            createParser().parse( reader, sink );

            // write to file
            String expected = output.toString();
            writer.write( expected );
            writer.flush();

            assertTrue( expected.indexOf( "Line" + EOL + lineBreak ) != -1 );
            assertTrue( expected.indexOf( "with 2" + EOL + lineBreak ) != -1 );
            assertTrue( expected.indexOf( "inline" + EOL + lineBreak ) != -1 );
        }
        finally
        {
            output.close();
            reader.close();
            writer.close();
        }
    }

    public void testSectionTitles()
        throws Exception
    {
        try
        {
            output = new StringWriter();
            reader = getTestReader( "section", outputExtension() );
            writer = getTestWriter( "section", "txt" );

            Sink sink = new TextSink( output );
            createParser().parse( reader, sink );

            // write to file
            String expected = output.toString();
            writer.write( expected );
            writer.flush();

            for ( int i = 1; i <= 5; i++ )
            {
                assertTrue( "Could not locate section " + i + " title",
                            expected.indexOf( "sectionTitle" + i + EOL + "text: " + "Section" + i ) != -1 );
            }

            assertTrue( "Section title has leading space",
                         expected.indexOf( "sectionTitle1" + EOL + "text: " + "TitleWithLeadingSpace" ) != -1 );
        }
        finally
        {
            output.close();
            reader.close();
            writer.close();
        }
    }

    private String getLineBreakString()
    {
        StringWriter sw = new StringWriter();
        Sink sink = new TextSink( sw );
        sink.lineBreak();

        return sw.toString();
    }

}
