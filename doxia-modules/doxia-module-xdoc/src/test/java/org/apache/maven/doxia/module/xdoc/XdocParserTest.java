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
import java.io.FileFilter;
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
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

        parser = lookup( Parser.ROLE, "xdoc" );

        // AbstractXmlParser.CachedFileEntityResolver downloads DTD/XSD files in ${java.io.tmpdir}
        // Be sure to delete them
        String tmpDir = System.getProperty( "java.io.tmpdir" );

        final Pattern xsdFilePattern = Pattern.compile( "(xdoc\\-.*|xml)\\.xsd" );

        File[] xsdFiles = new File( tmpDir ).listFiles( new FileFilter()
        {

            public boolean accept( File pathname )
            {
                return pathname.isFile() && xsdFilePattern.matcher( pathname.getName() ).matches();
            }
        } );

        for ( File xsdFile : xsdFiles )
        {
            xsdFile.delete();
        }

        /* FileUtils 3.0.10 is about 5-8 times slower than File.listFiles() + regexp */
//        String includes = "xdoc-*.xsd, xml.xsd";
//        List<File> tmpFiles = FileUtils.getFiles( new File( tmpDir ), includes, null, true );
//        for ( File tmpFile  : tmpFiles )
//        {
//            tmpFile.delete();
//        }
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

        assertTrue( content.contains( "&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;" ) );
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
        assertTrue( content.contains( "<a href=\"#Section_11\">Section 11</a>" ) );
        assertTrue( !content.contains( "<a href=\"#Section_1211\">Section 1211</a>" ) );
    }

    private Iterator<SinkEventElement> parseText( String text )
        throws ParseException
    {
        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        return sink.getEventList().iterator();
    }

    /** @throws Exception  */
    public void testHeadEventsList()
        throws Exception
    {
        String text = "<document>"
                + "<properties><title>title</title>"
                + "<!-- Test comment: DOXIA-312 -->"
                + "<author email=\"a@b.c\">John Doe</author></properties>"
                + "<head>"
                + "<meta name=\"security\" content=\"low\"/>"
                + "<base href=\"http://maven.apache.org/\"/>"
                + "</head>"
                + "<body></body></document>";

        Iterator<SinkEventElement> it = parseText( text );

        assertStartsWith( it, "head", "title", "text", "title_", "comment", "author", "text", "author_" );

        SinkEventElement unknown = it.next();
        assertEquals( "unknown", unknown.getName() );
        assertEquals( "meta", unknown.getArgs()[0] );

        unknown = it.next();
        assertEquals( "unknown", unknown.getName() );
        assertEquals( "base", unknown.getArgs()[0] );

        assertEquals( it, "head_", "body", "body_" );

        // DOXIA-359
        text = "<document>"
                + "<properties><title>properties title</title></properties>"
                + "<head><title>head title</title></head>"
                + "<body></body></document>";

        it = parseText( text );

        assertStartsWith( it, "head", "title" );

        SinkEventElement title = it.next();
        assertEquals( "text", title.getName() );
        assertEquals( "properties title", title.getArgs()[0] );

        assertEquals( it, "title_", "head_", "body",  "body_" );
    }

    /** @throws Exception  */
    public void testDocumentBodyEventsList()
        throws Exception
    {
        String text = "<document><body></body></document>";

        Iterator<SinkEventElement> it = parseText( text );

        assertEquals( it, "body", "body_" );
    }

    /** @throws Exception  */
    public void testSectionEventsList()
        throws Exception
    {
        String text = "<section name=\"sec 1\"><subsection name=\"sub 1\"></subsection></section>";

        Iterator<SinkEventElement> it = parseText( text );

        assertEquals( it, "section1", "sectionTitle1", "text", "sectionTitle1_", "section2", "sectionTitle2", "text",
                      "sectionTitle2_", "section2_", "section1_" );
    }

    /** @throws Exception  */
    public void testSectionAttributes()
        throws Exception
    {
        // DOXIA-448
        String text = "<section name=\"section name\" class=\"foo\" id=\"bar\"></section>";

        Iterator<SinkEventElement> it = parseText( text );

        assertStartsWith( it, "anchor", "anchor_" );

        SinkEventElement next = it.next();
        assertEquals( "section1", next.getName() );
        SinkEventAttributeSet set = (SinkEventAttributeSet) next.getArgs()[0];
        assertEquals( 3, set.getAttributeCount() );
        assertTrue( set.containsAttribute( "name", "section name" ) );
        assertTrue( set.containsAttribute( "class", "foo" ) );
        assertTrue( set.containsAttribute( "id", "bar" ) );

        next = it.next();
        assertEquals( "sectionTitle1", next.getName() );
        assertNull( next.getArgs()[0] );

        assertEquals( it, "text", "sectionTitle1_", "section1_" );
    }

    /** @throws Exception  */
    public void testNestedSectionsEventsList()
        throws Exception
    {
        // DOXIA-241
        String text = "<section name=\"section\"><h6>h6</h6><subsection name=\"subsection\"></subsection></section>";

        Iterator<SinkEventElement> it = parseText( text );

        assertEquals( it, "section1", "sectionTitle1", "text", "sectionTitle1_", "section2", "section3", "section4",
                      "section5", "sectionTitle5", "text", "sectionTitle5_", "section5_", "section4_", "section3_",
                      "section2_", "section2", "sectionTitle2", "text", "sectionTitle2_", "section2_", "section1_" );
    }

    /** @throws Exception  */
    public void testSourceEventsList()
        throws Exception
    {
        String text = "<source><a href=\"what.html\">what</a></source>";

        Iterator<SinkEventElement> it = parseText( text );

        assertEquals( it, "verbatim", "link", "text", "link_", "verbatim_" );

        text = "<source><![CDATA[<a href=\"what.html\">what</a>]]></source>";
        it = parseText( text );

        assertEquals( it, "verbatim", "text", "verbatim_" );

        text = "<source><![CDATA[<source>what</source>]]></source>";
        it = parseText( text );

        assertEquals( it, "verbatim", "text", "verbatim_" );
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

        Iterator<SinkEventElement> it = parseText( text );

        assertEquals( it, "verbatim", "text", "verbatim_" );
    }

    /** @throws Exception  */
    public void testPreEOL()
        throws Exception
    {
        // test EOLs within <source>: the sink MUST receive a text event for the EOL
        String text = "<source><a href=\"what.html\">what</a>" + EOL
                + "<a href=\"what.html\">what</a></source>";

        Iterator<SinkEventElement> it = parseText( text );

        assertEquals( it, "verbatim", "link", "text", "link_", "text", "link", "text", "link_", "verbatim_" );
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

        Iterator<SinkEventElement> it = parseText( text );

        assertEquals( it.next(), "anchor", "test-id" );

        assertStartsWith( it, "anchor_", "section1", "sectionTitle1", "text", "sectionTitle1_", "text" );

        assertEquals( it.next(), "anchor", "sub-id" );

        assertEquals( it, "anchor_", "section2", "sectionTitle2", "text", "sectionTitle2_", "text", "section2_",
                      "section1_" );
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

        Iterator<SinkEventElement> it = parseText( text );
        assertEquals( it, "unknown", "unknown", "unknown" );
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

        Iterator<SinkEventElement> it = parseText( text );
        assertEquals( it, "unknown", "unknown", "unknown", "unknown", "unknown" );
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

        assertStartsWith( it, "section1", "sectionTitle1" );

        assertEquals( it.next(), "text", "&\u0159\uD835\uDFED" );

        assertStartsWith( it, "sectionTitle1_", "paragraph" );

        assertEquals( it.next(), "text", "&" );

        assertEquals( it.next(), "text", "\u0159" );

        assertEquals( it.next(), "text", "\uD835\uDFED" );

        assertEquals( it, "paragraph_", "section1_" );
    }
    
    public void testStyleWithCData() throws Exception
    {
        // DOXIA-449
        final String text = "<style type=\"text/css\">\n" + 
        		"<![CDATA[\n" + 
        		"h2 {\n" + 
        		"font-size: 50px;\n" + 
        		"}\n" + 
        		"]]>\n" + 
        		"</style>"; 
        
        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.setValidate( false );
        parser.parse( text, sink );
        
        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        SinkEventElement styleElm = it.next(); 
        assertEquals( "unknown", styleElm.getName() );
        assertEquals( "style", styleElm.getArgs()[0] );
        SinkEventElement cdataElm = it.next(); 
        assertEquals( "unknown", cdataElm.getName() );
        assertEquals( "CDATA", cdataElm.getArgs()[0] );
        SinkEventElement styleElm_ = it.next(); 
        assertEquals( "unknown", styleElm_.getName() );
        assertEquals( "style", styleElm_.getArgs()[0] );
        assertFalse( it.hasNext() );
    }
}
