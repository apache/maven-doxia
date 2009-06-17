package org.apache.maven.doxia.module.fml;

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
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;
import org.apache.maven.doxia.sink.XhtmlBaseSink;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class FmlParserTest
    extends AbstractParserTest
{
    private FmlParser parser;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (FmlParser) lookup( Parser.ROLE, "fml" );

        // AbstractXmlParser.CachedFileEntityResolver downloads DTD/XSD files in ${java.io.tmpdir}
        // Be sure to delete them
        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String excludes = "fml-*.xsd, xml.xsd";
        List tmpFiles = FileUtils.getFileNames( new File( tmpDir ), excludes, null, true );
        for ( Iterator it = tmpFiles.iterator(); it.hasNext(); )
        {
            File tmpFile = new File( it.next().toString() );
            tmpFile.delete();
        }
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "fml";
    }

    /** @throws Exception */
    public void testFaqEventsList()
        throws Exception
    {
        SinkEventTestingSink sink = new SinkEventTestingSink();

        Reader reader = null;
        try
        {
            reader = getTestReader( "simpleFaq" );

            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( reader );
        }

        Iterator it = sink.getEventList().iterator();

        assertEquals( "comment", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "title", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "title_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "anchor", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "anchor_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "bold", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "bold_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedList", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedListItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedListItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedList_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definitionList", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definedTerm", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "anchor", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "anchor_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definedTerm_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definition", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definition_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definitionList_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body_", ( (SinkEventElement) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }

    /** @throws Exception */
    public void testEntities()
        throws Exception
    {
        final String text = "<!DOCTYPE test [<!ENTITY Alpha \"&#913;\">]>"
                + "<faqs title=\"&amp;&Alpha;\"><part id=\"General\"><title>&lt;&Alpha;</title>"
                + "<faq id=\"id\"><question>&gt;&Alpha;</question>"
                + "<answer><p><code>&lt;img&gt;</code>&quot;&Alpha;</p></answer>"
                + "</faq></part></faqs>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.setValidate( false );
        parser.parse( text, sink );

        Iterator it = sink.getEventList().iterator();

        assertEquals( "head", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "title", ( (SinkEventElement) it.next() ).getName() );

        // head title TODO: should be two events
        assertTextEvent( (SinkEventElement) it.next(), "&&#913;" );

        assertEquals( "title_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "head_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "anchor", ( (SinkEventElement) it.next() ).getName() );

        // faq title TODO: should be two events
        assertTextEvent( (SinkEventElement) it.next(), "&&#913;" );

        assertEquals( "anchor_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "bold", ( (SinkEventElement) it.next() ).getName() );

        // part title in TOC
        assertTextEvent( (SinkEventElement) it.next(), "<" );
        assertTextEvent( (SinkEventElement) it.next(), "\u0391" );

        assertEquals( "bold_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedList", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedListItem", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );

        // question in TOC
        assertTextEvent( (SinkEventElement) it.next(), ">" );
        assertTextEvent( (SinkEventElement) it.next(), "\u0391" );

        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedListItem_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "numberedList_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "sectionTitle1", ( (SinkEventElement) it.next() ).getName() );

        // part title
        assertTextEvent( (SinkEventElement) it.next(), "<" );
        assertTextEvent( (SinkEventElement) it.next(), "\u0391" );

        assertEquals( "sectionTitle1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definitionList", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definedTerm", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "anchor", ( (SinkEventElement) it.next() ).getName() );

        // question
        assertTextEvent( (SinkEventElement) it.next(), ">" );
        assertTextEvent( (SinkEventElement) it.next(), "\u0391" );

        assertEquals( "anchor_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definedTerm_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definition", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );

        // answer
        assertEquals( "monospaced", ( (SinkEventElement) it.next() ).getName() );
        assertTextEvent( (SinkEventElement) it.next(), "<" );
        assertTextEvent( (SinkEventElement) it.next(), "img" );
        assertTextEvent( (SinkEventElement) it.next(), ">" );
        assertEquals( "monospaced_", ( (SinkEventElement) it.next() ).getName() );
        assertTextEvent( (SinkEventElement) it.next(), "\"" );
        assertTextEvent( (SinkEventElement) it.next(), "\u0391" );

        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "text", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "link_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "paragraph_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definition_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "definitionList_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "section1_", ( (SinkEventElement) it.next() ).getName() );
        assertEquals( "body_", ( (SinkEventElement) it.next() ).getName() );

        assertFalse( it.hasNext() );
    }

    /**
     * @throws Exception if any
     * @since 1.1.1
     */
    public void testFaqMacro()
        throws Exception
    {
        Writer output = null;
        Reader reader = null;
        try
        {
            output = getTestWriter( "macro" );
            reader = getTestReader( "macro" );

            Sink sink = new XhtmlBaseSink( output );
            createParser().parse( reader, sink );
            sink.close();
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        File f = getTestFile( getBasedir(), outputBaseDir() + getOutputDir() + "macro.fml" );
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

        assertTrue( content.indexOf( "<a name=\"macro-definition\">Macro Question</a>" ) != -1 );
    }

    private void assertTextEvent( SinkEventElement textEvt, String string )
    {
        assertEquals( "text", textEvt.getName() );
        assertEquals( string, textEvt.getArgs()[0] );
    }
}
