package org.apache.maven.doxia.module.twiki.parser;

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

import java.util.Iterator;
import org.apache.maven.doxia.module.twiki.TWikiParser;
import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;

/**
 * @author ltheussl
 */
public class TwikiParserTest
    extends AbstractParserTest
{
    private TWikiParser parser;

    private static final String id = "twiki";

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        this.parser = lookup( Parser.ROLE, id );
    }

    @Override
    protected Parser createParser()
    {
        return parser;
    }

    @Override
    protected String outputExtension()
    {
        return id;
    }

    /** @throws Exception */
    public void testHtml()
        throws Exception
    {
        // DOXIA-441
        final String text = "_ita_, *b* and a bit of <font color=\"red\">red</font>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertStartsWith( it, "head", "head_", "body", "paragraph" );

        assertEquals( "italic", ( it.next() ).getName() );
        assertEquals( it.next(), "text", "ita" );
        assertEquals( "italic_", ( it.next() ).getName() );

        assertEquals( it.next(), "text", ", " );

        assertEquals( "bold", ( it.next() ).getName() );
        assertEquals( it.next(), "text", "b" );
        assertEquals( "bold_", ( it.next() ).getName() );

        assertEquals( it.next(), "text", " and a bit of " );

        assertEquals( it.next(), "rawText", "<font color=\"red\">" );
        assertEquals( it.next(), "text", "red" );
        assertEquals( it.next(), "rawText", "</font>" );

        assertEquals( it, "paragraph_", "body_", "flush", "close" );
    }
}