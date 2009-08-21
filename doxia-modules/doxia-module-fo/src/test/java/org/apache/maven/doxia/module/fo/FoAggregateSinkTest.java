package org.apache.maven.doxia.module.fo;

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

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

/**
 * Test FoAggregateSink.
 *
 * @author ltheussl
 * @version $Id$
 */
public class FoAggregateSinkTest
    extends TestCase
{
    private FoAggregateSink sink;

    private Writer writer;

    /**
     * Set up the writer.
     *
     * @throws java.lang.Exception if any.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        writer = new StringWriter();
    }

    /**
     * Test of body method, of class FoAggregateSink.
     */
    public void testBody()
    {
        try
        {
            sink = new FoAggregateSink( writer );

            sink.setDocumentName( "folder/documentName.apt" );
            sink.setDocumentTitle( "documentTitle" );
            sink.body();
            sink.body_();
        }
        finally
        {
            sink.close();
        }

        assertTrue( writer.toString().indexOf( "<fo:block id=\"./folder/documentName\">" ) != -1 );
    }

    /**
     * Test of setDocumentName method, of class FoAggregateSink.
     */
    public void testSetDocumentName()
    {
        try
        {
            sink = new FoAggregateSink( writer );

            sink.setDocumentName( "folder\\documentName.boo" );
            sink.body();
        }
        finally
        {
            sink.close();
        }

        assertTrue( writer.toString().indexOf( "<fo:block id=\"./folder/documentName\">" ) != -1 );
    }

    /**
     * Test of figureGraphics method, of class FoAggregateSink.
     */
    public void testFigureGraphics()
    {
        try
        {
            sink = new FoAggregateSink( writer );
            sink.setDocumentName( "./folder\\docName.xml" );
            sink.figureGraphics( "./../images/fig.png", null );
        }
        finally
        {
            sink.close();
        }

        assertTrue( writer.toString().indexOf( "<fo:external-graphic src=\"./images/fig.png\"" ) != -1 );
    }

    /**
     * Test of anchor method, of class FoAggregateSink.
     */
    public void testAnchor()
    {
        try
        {
            sink = new FoAggregateSink( writer );
            sink.anchor( "invalid Anchor" );
            sink.setDocumentName( "./folder\\docName.xml" );
            sink.anchor( "validAnchor" );
        }
        finally
        {
            sink.close();
        }

        assertTrue( writer.toString().indexOf( "<fo:inline id=\"#invalid_Anchor\">" ) != -1 );
        assertTrue( writer.toString().indexOf( "<fo:inline id=\"./folder/docName#validAnchor\">" ) != -1 );
    }

    /**
     * Test of link method, of class FoAggregateSink.
     */
    public void testLink()
    {
        try
        {
            sink = new FoAggregateSink( writer );
            sink.link( "http://www.example.com/" );
            sink.setDocumentName( "./folder\\docName.xml" );
            sink.link( "#anchor" );
            sink.link( "./././index.html" );
            sink.link( "./../download.html" );
        }
        finally
        {
            sink.close();
        }

        String result = writer.toString();

        assertTrue( result.indexOf( "<fo:basic-link external-destination=\"http://www.example.com/\">" ) != -1 );
        assertTrue( result.indexOf( "<fo:basic-link internal-destination=\"./folder/docName#anchor\">" ) != -1 );
        assertTrue( result.indexOf( "<fo:basic-link internal-destination=\"./folder/index\">" ) != -1 );
        assertTrue( result.indexOf( "<fo:basic-link internal-destination=\"./download\">" ) != -1 );
    }
}
