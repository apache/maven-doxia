package org.apache.maven.doxia.parser;

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

import org.apache.maven.doxia.AbstractModuleTest;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.TextSink;
import org.apache.maven.doxia.sink.impl.WellformednessCheckingSink;
import org.apache.maven.doxia.sink.Sink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the parsing of sample input files.
 * <br>
 * <b>Note</b>: you have to provide a sample "test." + outputExtension()
 * file in the test resources directory if you extend this class.
 * @since 1.0
 */
public abstract class AbstractParserTest
    extends AbstractModuleTest
{
    /**
     * Create a new instance of the parser to test.
     *
     * @return the parser to test.
     */
    protected abstract Parser createParser();

    /**
     * Returns the directory where all parser test output will go.
     *
     * @return The test output directory.
     */
    protected String getOutputDir()
    {
        return "parser/";
    }

    /**
     * Parse a test document '"test." + outputExtension()'
     * with parser from {@link #createParser()}, and output to a new
     * {@link WellformednessCheckingSink}. Asserts that output is well-formed.
     *
     * @throws IOException if the test document cannot be read.
     * @throws ParseException if the test document cannot be parsed.
     */
    @Test
    public final void testParser()
        throws IOException, ParseException
    {
        WellformednessCheckingSink sink = new WellformednessCheckingSink();

        try ( Reader reader = getTestReader( "test", outputExtension() ) )
        {
            createParser().parse( reader, sink );

            assertTrue( sink.isWellformed(),
                        "Parser output not well-formed, last offending element: " + sink.getOffender() );
        }
    }

     /**
     * Parse a test document '"test." + outputExtension()'
     * with parser from {@link #createParser()}, and output to a text file,
     * using the {@link org.apache.maven.doxia.sink.impl.TextSink TextSink}.
     *
     * @throws IOException if the test document cannot be read.
     * @throws ParseException if the test document cannot be parsed.
     */
     @Test
    public final void testDocument()
        throws IOException, ParseException
    {
        try ( Writer writer = getTestWriter( "test", "txt" );
              Reader reader = getTestReader( "test", outputExtension() ) )
        {
            Sink sink = new TextSink( writer );
            createParser().parse( reader, sink );
        }
    }

    protected static void assertSinkEquals( SinkEventElement element, String name, Object... args )
    {
        Assertions.assertEquals( name, element.getName(), "Name of element doesn't match" );
        Assertions.assertArrayEquals( args, element.getArgs(), "Arguments don't match" );
    }

    protected static void assertSinkAttributeEquals( SinkEventElement element, String name, String attr, String value )
    {
        Assertions.assertEquals( name, element.getName() );
        SinkEventAttributeSet atts = (SinkEventAttributeSet) element.getArgs()[0];
        Assertions.assertEquals( value, atts.getAttribute( attr ) );
    }

    protected static void assertSinkEquals( Iterator<SinkEventElement> it, String... names )
    {
        StringBuilder expected = new StringBuilder();
        StringBuilder actual = new StringBuilder();

        for ( String name : names )
        {
            expected.append( name ).append( '\n' );
        }

        while ( it.hasNext() )
        {
            actual.append( it.next().getName() ).append( '\n' );
        }

        Assertions.assertEquals( expected.toString(), actual.toString() );
    }

    protected static void assertSinkStartsWith( Iterator<SinkEventElement> it, String... names )
    {
        StringBuilder expected = new StringBuilder();
        StringBuilder actual = new StringBuilder();

        for ( String name : names )
        {
            expected.append( name ).append( '\n' );
            if ( it.hasNext() )
            {
                actual.append( it.next().getName() ).append( '\n' );
            }
        }
        Assertions.assertEquals( expected.toString(), actual.toString() );
    }
}
