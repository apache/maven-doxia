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
import java.io.FileFilter;
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.apache.maven.doxia.sink.impl.XhtmlBaseSink;
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

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = lookup( Parser.ROLE, "fml" );

        // AbstractXmlParser.CachedFileEntityResolver downloads DTD/XSD files in ${java.io.tmpdir}
        // Be sure to delete them
        String tmpDir = System.getProperty( "java.io.tmpdir" );

        // Using FileFilter, because is it is much faster then FileUtils.listFiles 
        File[] tmpFiles = new File( tmpDir ).listFiles( new FileFilter()
        {
            Pattern xsdPatterns = Pattern.compile( "(xml|fml\\-.+)\\.xsd" );
            
            @Override
            public boolean accept( File pathname )
            {
                return xsdPatterns.matcher( pathname.getName() ).matches(); 
            }
        } );
        
        for ( File tmpFile : tmpFiles )
        {
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

        try ( Reader reader = getTestReader( "simpleFaq" ) )
        {
            createParser().parse( reader, sink );
        }

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "comment", ( it.next() ).getName() );
        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "title", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "title_", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "anchor", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "inline", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "inline_", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "numberedList", ( it.next() ).getName() );
        assertEquals( "numberedListItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "numberedListItem_", ( it.next() ).getName() );
        assertEquals( "numberedList_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "definitionList", ( it.next() ).getName() );
        assertEquals( "definedTerm", ( it.next() ).getName() );
        assertEquals( "anchor", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "definedTerm_", ( it.next() ).getName() );
        assertEquals( "definition", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "definition_", ( it.next() ).getName() );
        assertEquals( "definitionList_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );
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

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "title", ( it.next() ).getName() );

        // head title TODO: should be two events
        assertTextEvent( it.next(), "&&#913;" );

        assertEquals( "title_", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );
        assertEquals( "anchor", ( it.next() ).getName() );

        // faq title TODO: should be two events
        assertTextEvent( it.next(), "&&#913;" );

        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "inline", ( it.next() ).getName() );

        // part title in TOC
        assertTextEvent( it.next(), "<" );
        assertTextEvent( it.next(), "\u0391" );

        assertEquals( "inline_", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "numberedList", ( it.next() ).getName() );
        assertEquals( "numberedListItem", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );

        // question in TOC
        assertTextEvent( it.next(), ">" );
        assertTextEvent( it.next(), "\u0391" );

        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "numberedListItem_", ( it.next() ).getName() );
        assertEquals( "numberedList_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertEquals( "section1", ( it.next() ).getName() );
        assertEquals( "sectionTitle1", ( it.next() ).getName() );

        // part title
        assertTextEvent( it.next(), "<" );
        assertTextEvent( it.next(), "\u0391" );

        assertEquals( "sectionTitle1_", ( it.next() ).getName() );
        assertEquals( "definitionList", ( it.next() ).getName() );
        assertEquals( "definedTerm", ( it.next() ).getName() );
        assertEquals( "anchor", ( it.next() ).getName() );

        // question
        assertTextEvent( it.next(), ">" );
        assertTextEvent( it.next(), "\u0391" );

        assertEquals( "anchor_", ( it.next() ).getName() );
        assertEquals( "definedTerm_", ( it.next() ).getName() );
        assertEquals( "definition", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );

        // answer
        assertEquals( "inline", ( it.next() ).getName() );
        assertTextEvent( it.next(), "<" );
        assertTextEvent( it.next(), "img" );
        assertTextEvent( it.next(), ">" );
        assertEquals( "inline_", ( it.next() ).getName() );
        assertTextEvent( it.next(), "\"" );
        assertTextEvent( it.next(), "\u0391" );

        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );
        assertEquals( "link", ( it.next() ).getName() );
        assertEquals( "text", ( it.next() ).getName() );
        assertEquals( "link_", ( it.next() ).getName() );
        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "definition_", ( it.next() ).getName() );
        assertEquals( "definitionList_", ( it.next() ).getName() );
        assertEquals( "section1_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );

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

        assertTrue( content.contains( "<a name=\"macro-definition\">Macro Question</a>" ) );
    }

    private void assertTextEvent( SinkEventElement textEvt, String string )
    {
        assertEquals( "text", textEvt.getName() );
        assertEquals( string, textEvt.getArgs()[0] );
    }
}
