package org.apache.maven.doxia.module.xwiki;

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

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.TextSink;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Test class for XWikiParser.
 */
public class XWikiParserTest
    extends AbstractParserTest
{
    private XWikiParser parser;

    private StringWriter output;

    private Reader reader;

    private Writer writer;

    /**
     * {@inheritDoc}
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (XWikiParser) lookup( Parser.ROLE, "xwiki" );

        output = null;
        reader = null;
        writer = null;
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown()
        throws Exception
    {
        if ( output != null )
        {
            output.close();
        }
        if ( reader != null )
        {
            reader.close();
        }
        if ( writer != null )
        {
            writer.close();
        }

        super.tearDown();
    }

    /**
     * {@inheritDoc}
     */
    protected Parser createParser()
    {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    protected String outputExtension()
    {
        return "xwiki";
    }

    /**
     * @throws Exception
     */
    public void testSectionTitles()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "section" );

        for ( int i = 1; i <= 5; i++ )
        {
            assertContainsLines( "Could not locate section " + i + " title", result,
                                 "sectionTitle" + i + "\ntext: Section" + i );
        }

        assertContainsLines( "Section title has leading space", result, "sectionTitle1\ntext: TitleWithLeadingSpace" );
        assertContainsLines( "Section title has leading spaces before title", result,
                             "sectionTitle1\ntext: TitleWithSpacesBefore" );
    }

    /**
     * @throws Exception
     */
    public void testFigure()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "figure" );

        assertContainsLines( result, "begin:figure\nfigureGraphics, name: photo.jpg\nend:figure\n" );
        assertContainsLines( result, "Simple paragraph with {image:image.jpg}." );
    }

    /**
     * @throws Exception
     */
    public void testParagraphWithSimpleFormatting()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "simple-paragraph" );

        assertContainsLines( result, "begin:bold\ntext: bold\n" );
        assertContainsLines( result, "begin:italic\ntext: italic\n" );
        assertContainsLines( result, "begin:monospaced\ntext: monospaced\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: http://jira.codehaus.org" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: JIRA\n" );
        // four paragraphs in the input...
        assertEquals( 5, result.split( "end:paragraph" ).length );
    }

    private void assertContainsLines( String message, String result, String lines )
    {
        lines = StringUtils.replace( lines, "\n", EOL );
        if ( message != null )
        {
            assertTrue( message, result.indexOf( lines ) != -1 );
        }
        else
        {
            assertTrue( result.indexOf( lines ) != -1 );
        }
    }

    private void assertContainsLines( String result, String lines )
    {
        this.assertContainsLines( null, result, lines );
    }

    private String locateAndParseTestSourceFile( String stem )
        throws IOException, ParseException
    {
        output = new StringWriter();
        reader = getTestReader( stem, outputExtension() );
        writer = getTestWriter( stem, "txt" );

        Sink sink = new TextSink( output );
        createParser().parse( reader, sink );

        // write to file
        String expected = output.toString();
        writer.write( expected );
        writer.flush();
        return expected;
    }

}