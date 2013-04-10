package org.apache.maven.doxia.module.twiki.parser;

import static org.junit.Assert.assertArrayEquals;

import java.io.StringReader;
import java.util.List;

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.util.ByLineReaderSource;
import org.apache.maven.doxia.util.ByLineSource;

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

/**
 * Tests the {@link org.apache.maven.doxia.module.twiki.parser.VerbatimBlock}
 *
 * @author Christian Nardi
 * @since Nov 8, 2007
 */
public class VerbatimTest
    extends AbstractBlockTestCase
{

    /**
     * unit test the regex
     */
    public final void testRegex()
    {
        assertTrue( getVerbatimParser().accept( "<verbatim>" ) );
        assertTrue( getVerbatimParser().accept( "   <verbatim>" ) );
        assertTrue( getVerbatimParser().accept( "<verbatim> a word" ) );
        assertTrue( getVerbatimParser().accept( "<verbatim> another Word" ) );
    }

    /**
     * @throws ParseException if the parser does not accept the line
     *
     */
    public void testVerbatim()
        throws ParseException
    {
        final StringReader sw =
            new StringReader( "" + "  <verbatim> hello, \n" + " this is a verbatim text \n"
                + " which i would like to test \n" + " Thanks </verbatim>" );

        final ByLineSource source = new ByLineReaderSource( sw );

        Block block, expected;
        expected =
            new VerbatimBlock( new Block[] { new TextBlock( " hello, \n" ),
                new TextBlock( " this is a verbatim text \n" ), new TextBlock( " which i would like to test \n" ),
                new TextBlock( " Thanks \n" ) } );

        block = getVerbatimParser().visit( source.getNextLine(), source );
        assertEquals( block, expected );
    }

    /**
     * @throws Exception .
     */
    public void testTwiki()
        throws Exception
    {
        final StringReader sw =
            new StringReader( "hello this is a paragraph \n" + "  <verbatim> hello, \n"
                + " this is a verbatim text \n" + " which i would like to test \n" + " Thanks </verbatim>" );
        final ByLineSource source = new ByLineReaderSource( sw );

        Block[] expected;
        expected =
            new Block[] {
                new ParagraphBlock( new Block[] { new TextBlock( "hello this is a paragraph" ) } ),
                new VerbatimBlock( new Block[] { new TextBlock( " hello, \n" ),
                    new TextBlock( " this is a verbatim text \n" ),
                    new TextBlock( " which i would like to test \n" ), new TextBlock( " Thanks \n" ) } ) };

        List<Block> block = twikiParser.parse( source );
        assertArrayEquals( expected, block.toArray() );

    }

    /** test
     * @throws org.apache.maven.doxia.parser.ParseException
     */
    public void testVerbatimAfterSection()
        throws ParseException
    {
        final StringReader sw =
            new StringReader( "---++ fooo\n" + "  <verbatim> hello, \n" + " Thanks </verbatim>" );
        final ByLineSource source = new ByLineReaderSource( sw );

        Block[] expected;
        expected =
            new Block[] { new SectionBlock( "foo", 2, new Block[] { new VerbatimBlock( new Block[] {
                new TextBlock( " hello, \n" ), new TextBlock( " Thanks \n" ) } ) } ) };

        List<Block> block = twikiParser.parse( source );
        assertArrayEquals( expected, block.toArray() );
    }
}
