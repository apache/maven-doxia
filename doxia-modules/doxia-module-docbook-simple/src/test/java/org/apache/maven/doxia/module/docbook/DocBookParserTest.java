package org.apache.maven.doxia.module.docbook;

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
import java.io.Writer;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

/**
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id$
 */
public class DocBookParserTest extends AbstractParserTest
{
    /** The parser to test. */
    private DocBookParser parser;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (DocBookParser) lookup( Parser.ROLE, "doc-book" );
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xml";
    }

    /**
     * Parses the test document test.xml and re-emits it into test.docbook.
     *
     * @throws IOException if something goes wrong
     * @throws ParseException if something goes wrong
     */
    public void testTestDocument()
        throws IOException, ParseException
    {
        Writer writer = null;

        Reader reader = null;

        try
        {
            writer = getTestWriter( "test", "docbook" );
            reader = getTestReader( "test" );

            Sink sink = new DocBookSink( writer );

            createParser().parse( reader, sink );
        }
        finally
        {
            if ( writer  != null )
            {
                writer.close();
            }

            if ( reader != null )
            {
                reader.close();
            }
        }
    }


}
