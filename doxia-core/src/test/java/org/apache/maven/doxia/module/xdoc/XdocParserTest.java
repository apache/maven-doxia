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

import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.maven.doxia.parser.AbstractParserTestCase;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id:XdocParserTest.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class XdocParserTest
    extends AbstractParserTestCase
{
    private XdocParser parser;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (XdocParser) lookup( Parser.ROLE, "xdoc" );
    }

    /**
     * @see org.apache.maven.doxia.parser.AbstractParserTestCase#getParser()
     */
    protected Parser getParser()
    {
        return parser;
    }

    /**
     * @see org.apache.maven.doxia.parser.AbstractParserTestCase#getDocument()
     */
    protected String getDocument()
    {
        return "src/test/site/xdoc/report.xml";
    }

    /**
     * @throws Exception
     */
    public void testSnippetMacro()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;

        try
        {
            output = new StringWriter();
            reader = new FileReader( getTestFile( getBasedir(), "src/test/site/xdoc/macro.xml" ) );

            Sink sink = new XdocSink( output );
            getParser().parse( reader, sink );

            assertTrue( output.toString().indexOf( "&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;" ) != -1 );
        }
        finally
        {
            output.close();
            reader.close();
        }
    }

    /**
     * @throws Exception
     */
    public void testTocMacro()
        throws Exception
    {
        StringWriter output = null;
        Reader reader = null;

        try
        {
            output = new StringWriter();
            reader = new FileReader( getTestFile( getBasedir(), "src/test/site/xdoc/toc.xml" ) );

            Sink sink = new XdocSink( output );
            getParser().parse( reader, sink );

            // No section, only subsection 1 and 2
            assertTrue( output.toString().indexOf( "<li><a href=\"#section_11\">Section 11</a></li>" ) != -1 );
            assertTrue( output.toString().indexOf( "<li><a href=\"#section_1211\">Section 1211</a></li>" ) == -1 );
        }
        finally
        {
            output.close();
            reader.close();
        }
    }
}
