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
package org.apache.maven.doxia.module.twiki.parser;

import java.io.StringReader;

import org.apache.maven.doxia.module.twiki.TWikiParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.util.ByLineReaderSource;
import org.apache.maven.doxia.util.ByLineSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link TWikiParser#getTitle(java.util.List)}
 *
 *
 * @author Juan F. Codagnone
 * @since Nov 10, 2007
 */
public class TitleTest
    extends AbstractBlockTestCase
{
    @Test
    public void testSectionTitle() throws Exception
    {
        final ByLineSource source = new ByLineReaderSource( new StringReader( "---++ Test\n hello world" ) );

        final TWikiParser parser = new TWikiParser();

        assertEquals( "Test", parser.getTitle( parser.parse( source ), source ) );
    }

    @Test
    public void testNoSectionTitle() throws Exception
    {
        final ByLineSource source = new NamedByLineSource( new ByLineReaderSource( new StringReader( "hello world" ) ),
                "testpage" );

        final TWikiParser parser = new TWikiParser();

        assertEquals( "testpage", parser.getTitle( parser.parse( source ), source ) );
    }

    @Test
    public void testNoSectionTwikiExtensionTitle() throws Exception
    {
        final ByLineSource source = new NamedByLineSource( new ByLineReaderSource( new StringReader( "hello world" ) ),
                "testpage.twiki" );

        final TWikiParser parser = new TWikiParser();

        assertEquals( "testpage", parser.getTitle( parser.parse( source ), source ) );
    }

    static class NamedByLineSource implements ByLineSource
    {
        /**
         * reader
         */
        private final ByLineReaderSource reader;

        /**
         * reader's name
         */
        private final String name;

        public NamedByLineSource( final ByLineReaderSource reader, final String name )
        {
            if ( reader == null || name == null )
            {
                throw new IllegalArgumentException( "null arguments are not allowed" );
            }

            this.reader = reader;
            this.name = name;
        }

        public final void close()
        {
            reader.close();
        }

        public final int getLineNumber()
        {
            return reader.getLineNumber();
        }

        public final String getName()
        {
            return name;
        }

        public final String getNextLine() throws ParseException
        {
            return reader.getNextLine();
        }

        public final void unget( final String s ) throws IllegalStateException
        {
            reader.unget( s );
        }

        public final void ungetLine() throws IllegalStateException
        {
            reader.ungetLine();
        }
    }
}