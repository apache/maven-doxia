package org.apache.maven.doxia.module.xdoc;

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
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 * @since 1.0
 */
public class XdocParserTest
    extends AbstractParserTest
{
    private XdocParser parser;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (XdocParser) lookup( Parser.ROLE, "xdoc" );

        // AbstractXmlParser.CachedFileEntityResolver downloads DTD/XSD files in ${java.io.tmpdir}
        // Be sure to delete them
        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String excludes = "xdoc-*.xsd, xml.xsd";
        List<String> tmpFiles = FileUtils.getFileNames( new File( tmpDir ), excludes, null, true );
        for ( Iterator<String> it = tmpFiles.iterator(); it.hasNext(); )
        {
            File tmpFile = new File( it.next().toString() );
            tmpFile.delete();
        }
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xml";
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** @throws Exception  */
    public void testSnippetMacro()
        throws Exception
    {
        Writer output = null;
        Reader reader = null;

        try
        {
            output = getTestWriter( "macro" );
            reader = getTestReader( "macro" );

            Sink sink = new XdocSink( output );
            createParser().parse( reader, sink );
            sink.close();
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        File f = getTestFile( getBasedir(), outputBaseDir() + getOutputDir() + "macro.xml" );
        assertTrue( "The file " + f.getAbsolutePath() + " was not created", f.exists() );

        String content;
        try
        {
            reader = new FileReader( f );
            content = IOUtil.toString( reader );
        }
        finally
        {
            IOUtil.close( reader );
        }

        assertTrue( content.indexOf( "&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;" ) != -1 );
    }

    /** @throws Exception  */
    public void testTocMacro()
        throws Exception
    {
        Writer output = null;
        Reader reader = null;

        try
        {
            output = getTestWriter( "toc" );
            reader = getTestReader( "toc" );

            Sink sink = new XdocSink( output );
            createParser().parse( reader, sink );
            sink.close();
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        File f = getTestFile( getBasedir(), outputBaseDir() + getOutputDir() + "toc.xml" );
        assertTrue( "The file " + f.getAbsolutePath() + " was not created", f.exists() );

        String content;
        try
        {
            reader = new FileReader( f );
            content = IOUtil.toString( reader );
        }
        finally
        {
            IOUtil.close( reader );
        }

        // No section, only subsection 1 and 2
        assertTrue( content.indexOf( "<a href=\"#Section_11\">Section 11</a>" ) != -1 );
        assertTrue( content.indexOf( "<a href=\"#Section_1211\">Section 1211</a>" ) == -1 );
    }

    /** @throws Exception  */
    public void testHeadEventsList()
        throws Exception
    {
        String text = "<document>"
                + "<properties><title>title</title>"
                + "<!-- Test comment: DOXIA-312 -->"
                + "<author email=\"a@b.c\">John Doe</author></properties>"
                + "<head><meta name=\"security\" content=\"low\"/></head><body></body></document>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "title", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "title_", ( it.next() ).getName() );
        assertEquals( "comment", ( it.next() ).getName() );
        assertEquals( "author", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "author_", ( it.next() ).getName() );
        assertEquals( "unknown", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

        // DOXIA-359
        text = "<document>"
                + "<properties><title>properties title</title></properties>"
                + "<head><title>head title</title></head>"
                + "<body></body></document>";

        sink.reset();
        parser.parse( text, sink );

        it = sink.getEventList().iterator();

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "title", ( it.next() ).getName() );

        SinkEventElement title = it.next();
        assertEquals( "text", title.getName() );
        assertEquals( "properties title", title.getArgs()[0] );

        assertEquals( "title_", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testDocumentBodyEventsList()
        throws Exception
    {
        String text = "<document><body></body></document>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testSectionEventsList()
        throws Exception
    {
        String text = "<section name=\"sec 1\"><subsection name=\"sub 1\"></subsection></section>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "section2", ( it.next() ).getName() );
        assertEquals( "sectionTitle2", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle2_", ( it.next() ).getName() );
        assertEquals( "section2_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testNestedSectionsEventsList()
        throws Exception
    {
        // DOXIA-241
        String text = "<section name=\"section\"><h6>h6</h6><subsection name=\"subsection\"></subsection></section>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "section2", ( it.next() ).getName() );
        assertEquals( "section3", ( it.next() ).getName() );
        assertEquals( "section4", ( it.next() ).getName() );
        assertEquals( "section5", ( it.next() ).getName() );
        assertEquals( "sectionTitle5", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle5_", ( it.next() ).getName() );
        assertEquals( "section5_", ( it.next() ).getName() );
        assertEquals( "section4_", ( it.next() ).getName() );
        assertEquals( "section3_", ( it.next() ).getName() );
        assertEquals( "section2_", ( it.next() ).getName() );

        assertEquals( "section2", ( it.next() ).getName() );
        assertEquals( "sectionTitle2", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle2_", ( it.next() ).getName() );
        assertEquals( "section2_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testSourceEventsList()
        throws Exception
    {
        String text = "<source><a href=\"what.html\">what</a></source>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

        text = "<source><![CDATA[<a href=\"what.html\">what</a>]]></source>";
        sink.reset();
        parser.parse( text, sink );

        it = sink.getEventList().iterator();
        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

        text = "<source><![CDATA[<source>what</source>]]></source>";
        sink.reset();
        parser.parse( text, sink );

        it = sink.getEventList().iterator();
        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception  */
    public void testSourceContainingDTD()
        throws Exception
    {
        String text = "<source><![CDATA[" +
                          "<!DOCTYPE web-app PUBLIC " +
                          "\"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN\"" +
                          " \"http://java.sun.com/j2ee/dtds/web-app_2.2.dtd\">" +
                      "]]></source>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );

    }

    /** @throws Exception  */
    public void testPreEOL()
        throws Exception
    {
        // test EOLs within <source>: the sink MUST receive a text event for the EOL
        String text = "<source><a href=\"what.html\">what</a>" + EOL
                + "<a href=\"what.html\">what</a></source>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "verbatim", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "verbatim_", ( it.next() ).getName() );
    }

    /**
     * Test section with ids.
     *
     * @throws java.lang.Exception if any.
     */
    public void testSectionIdAnchor()
        throws Exception
    {
        String text = "<section name=\"test\" id=\"test-id\">This is a test."
                + "<subsection name=\"sub-test\" id=\"sub-id\">Sub-section</subsection></section>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        SinkEventElement anchorEvt = it.next();

        assertEquals( "anchor", anchorEvt.getName() );
        assertEquals( "test-id", anchorEvt.getArgs()[0] );
        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );

        anchorEvt = it.next();
        assertEquals( "anchor", anchorEvt.getName() );
        assertEquals( "sub-id", anchorEvt.getArgs()[0] );
        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "section2", ( it.next() ).getName() );
        assertEquals( "sectionTitle2", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle2_", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "section2_", ( it.next() ).getName() );

        assertEquals( "section1_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /**
     * Test script block.
     *
     * @throws java.lang.Exception if any.
     */
    public void testJavaScript()
        throws Exception
    {
        String text = "<script type=\"text/javascript\"><![CDATA[alert(\"Hello!\");]]></script>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals( "unknown", ( it.next() ).getName() );
        assertEquals( "unknown", ( it.next() ).getName() );
        assertEquals( "unknown", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /**
     * Test unknown tags.
     *
     * @throws java.lang.Exception if any.
     */
    public void testUnknown()
        throws Exception
    {
        String text = "<applet><param name=\"name\" value=\"value\"/><unknown/></applet>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals( "unknown", ( it.next() ).getName() );
        assertEquals( "unknown", ( it.next() ).getName() );
        assertEquals( "unknown", ( it.next() ).getName() );
        assertEquals( "unknown", ( it.next() ).getName() );
        assertEquals( "unknown", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /**
     * Test invalid macro tags.
     */
    public void testMacroExceptions()
    {
        SinkEventTestingSink sink = new SinkEventTestingSink();
        assertParseException( sink, "<macro/>" );
        assertParseException( sink, "<macro name=\"\"/>" );
        assertParseException( sink, "<macro name=\"name\"><param name=\"\" value=\"value\"/></macro>" );
        assertParseException( sink, "<macro name=\"name\"><param name=\"name\" value=\"\"/></macro>" );
        assertParseException( sink, "<macro name=\"name\"><param value=\"value\"/></macro>" );
        assertParseException( sink, "<macro name=\"name\"><param name=\"name\"/></macro>" );
        assertParseException( sink, "<macro name=\"unknown\"></macro>" );
    }

    private void assertParseException( Sink sink, String text )
    {
        try
        {
            parser.parse( text, sink );

            fail( "Should not be parseable: '" + text + "'" );
        }
        catch ( ParseException ex )
        {
            assertNotNull( ex );
        }
    }

    /** @throws Exception  */
    public void testEntities()
        throws Exception
    {
        final String text = "<!DOCTYPE test [<!ENTITY foo \"&#x159;\"><!ENTITY tritPos  \"&#x1d7ed;\">]>"
                + "<section name=\"&amp;&foo;&tritPos;\"><p>&amp;&foo;&tritPos;</p></section>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.setValidate( false );
        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );

        SinkEventElement textEvt = it.next();
        assertEquals( "text", textEvt.getName() );
        assertEquals( "&\u0159\uD835\uDFED", textEvt.getArgs()[0] );

        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );

        textEvt = it.next();
        assertEquals( "text", textEvt.getName() );
        assertEquals( "&", textEvt.getArgs()[0] );

        textEvt = it.next();
        assertEquals( "text", textEvt.getName() );
        assertEquals( "\u0159", textEvt.getArgs()[0] );

        textEvt = it.next();
        assertEquals( "text", textEvt.getName() );
        assertEquals( "\uD835\uDFED", textEvt.getArgs()[0] );

        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }
}
