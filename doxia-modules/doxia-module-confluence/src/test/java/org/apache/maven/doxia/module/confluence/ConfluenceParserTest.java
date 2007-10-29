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

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.TextSink;
import org.codehaus.plexus.util.StringUtils;

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
    protected void tearDown()
        throws Exception
    {
        if ( output != null )
            output.close();
        if ( reader != null )
            reader.close();
        if ( writer != null )
            writer.close();

        super.tearDown();
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

    /** @throws Exception */
    public void testLineBreak()
        throws Exception
    {
        String lineBreak = getLineBreakString();

        String result = locateAndParseTestSourceFile( "linebreak" );

        assertContainsLines( result, "Line\n" + lineBreak );
        assertContainsLines( result, "with 2\n" + lineBreak );
        assertContainsLines( result, "inline\n" + lineBreak );
    }

    /** @throws Exception */
    public void testEscapes()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "escapes" );

        assertContainsLines( result, "asterisk *" );
        assertContainsLines( result, "underline _" );
        assertContainsLines( result, "asterisk *not bold*" );
        assertContainsLines( result, "underline _not italic_" );
        assertContainsLines( result, "normal character" );
    }

    /** @throws Exception */
    public void testSectionTitles()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "section" );

        for ( int i = 1; i <= 5; i++ )
        {
            assertContainsLines( "Could not locate section " + i + " title", result, "sectionTitle" + i +
                "\ntext: Section" + i );
        }

        assertContainsLines( "Section title has leading space", result, "sectionTitle1\ntext: TitleWithLeadingSpace" );
    }

    /** @throws Exception */
    public void testNestedBulletList()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "nested-list" );

        assertContainsLines( "Nested list not found", result,
                             "begin:listItem\ntext:  A top level list item\nbegin:list" );
        // two lists in the input...
        assertEquals( 3, result.split( "end:list\n" ).length );
        // ...and 4 list items
        assertEquals( 5, result.split( "end:listItem\n" ).length );
    }

    /** @throws Exception */
    public void testListWithSimpleFormatting()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "simple-list" );

        assertContainsLines( result, "begin:bold\ntext: bold\n" );
        assertContainsLines( result, "begin:italic\ntext: italic\n" );
        assertContainsLines( result, "begin:monospaced\ntext: monospaced\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: http://jira.codehaus.org\n" );
        assertContainsLines( result, "begin:link, name: http://jira.codehaus.org\ntext: JIRA\n" );
        assertContainsLines( result, "begin:listItem\ntext:  Item with no formatting\nend:listItem\n" );
        // one list in the input...
        assertEquals( 2, result.split( "end:list\n" ).length );
        // ...and 5 list items
        assertEquals( 6, result.split( "end:listItem\n" ).length );
    }

    /** @throws Exception */
    public void testAnchor()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "anchor" );

        assertContainsLines( result, "begin:paragraph\nbegin:anchor, name: start\nend:anchor" );
        assertContainsLines( result, "begin:anchor, name: middle\nend:anchor" );
        assertContainsLines( result, "begin:paragraph\ntext: Simple paragraph\nbegin:anchor, name: end\nend:anchor" );
        // 3 anchors in the input...
        assertEquals( 4, result.split( "end:anchor\n" ).length );
    }

    /** @throws Exception */
    public void testUnknownMacro()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "unknown-macro" );

        assertContainsLines( result, "begin:paragraph\ntext: {unknown:start}" );
    }

    /** @throws Exception */
    public void testCodeMacro()
        throws Exception
    {
        String result = locateAndParseTestSourceFile( "code" );

        assertContainsLines( result, "begin:verbatim, boxed: true\ntext: public class Cat {" );
        // 3 paragraphs in the input...
        assertEquals( 4, result.split( "end:paragraph\n" ).length );
        // 1 verbatim in the input...
        assertEquals( 2, result.split( "end:verbatim\n" ).length );
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

    private String getLineBreakString()
    {
        StringWriter sw = new StringWriter();
        Sink sink = new TextSink( sw );
        sink.lineBreak();

        return sw.toString();
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
