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
import org.codehaus.plexus.util.IOUtil;
import org.junit.Assert;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * Test the parsing of sample input files.
 * <br>
 * <b>Note</b>: you have to provide a sample "test." + outputExtension()
 * file in the test resources directory if you extend this class.
 *
 * @version $Id$
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
    public final void testParser()
        throws IOException, ParseException
    {
        WellformednessCheckingSink sink = new WellformednessCheckingSink();

        try ( Reader reader = getTestReader( "test", outputExtension() ) )
        {
            createParser().parse( reader, sink );

            assertTrue( "Parser output not well-formed, last offending element: " + sink.getOffender(),
                    sink.isWellformed() );
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

    protected void assertEquals( SinkEventElement element, String name, Object... args )
    {
        assertEquals( "Name of element doesn't match", name, element.getName() );
        Assert.assertArrayEquals( "Arguments don't match",  args, element.getArgs() );
    }

    protected void assertAttributeEquals( SinkEventElement element, String name, String attr, String value )
    {
        assertEquals( name, element.getName() );
        SinkEventAttributeSet atts = (SinkEventAttributeSet) element.getArgs()[0];
        assertEquals( value, atts.getAttribute( attr ) );
    }

    protected void assertEquals( Iterator<SinkEventElement> it, String... names )
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

        assertEquals( expected.toString(), actual.toString() );
    }

    protected void assertStartsWith( Iterator<SinkEventElement> it, String... names )
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
        assertEquals( expected.toString(), actual.toString() );
    }
}
