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
import org.apache.maven.doxia.sink.SinkEventElement;
import org.apache.maven.doxia.sink.SinkEventTestingSink;

/**
 *
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

        this.parser = (TWikiParser) lookup( Parser.ROLE, id );
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

    /** @throws Exception  */
    public void testHtml()
        throws Exception
    {
        // DOXIA-441
        final String text = "_ita_, *b* and a bit of <font color=\"red\">red</font>";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse( text, sink );

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertEquals( "head", ( it.next() ).getName() );
        assertEquals( "head_", ( it.next() ).getName() );
        assertEquals( "body", ( it.next() ).getName() );
        assertEquals( "paragraph", ( it.next() ).getName() );

        assertEquals( "italic", ( it.next() ).getName() );
        SinkEventElement textElement = it.next();
        assertEquals( "text", textElement.getName() );
        assertEquals( "ita", textElement.getArgs()[0] );
        assertEquals( "italic_", ( it.next() ).getName() );

        textElement = it.next();
        assertEquals( "text", textElement.getName() );
        assertEquals( ", ", textElement.getArgs()[0] );

        assertEquals( "bold", ( it.next() ).getName() );
        textElement = it.next();
        assertEquals( "text", textElement.getName() );
        assertEquals( "b", textElement.getArgs()[0] );
        assertEquals( "bold_", ( it.next() ).getName() );

        // FIXME!
        //textElement = it.next();
        //assertEquals( "text", textElement.getName() );
        //assertEquals( "and a bit of", textElement.getArgs()[0] );

        textElement = it.next();
        assertEquals( "rawText", textElement.getName() );
        assertEquals( "<font color=\"red\">", textElement.getArgs()[0] );

        textElement = it.next();
        assertEquals( "text", textElement.getName() );
        assertEquals( "red", textElement.getArgs()[0] );

        textElement = it.next();
        assertEquals( "rawText", textElement.getName() );
        assertEquals( "</font>", textElement.getArgs()[0] );

        assertEquals( "paragraph_", ( it.next() ).getName() );
        assertEquals( "body_", ( it.next() ).getName() );
        assertEquals( "flush", ( it.next() ).getName() );
        assertEquals( "close", ( it.next() ).getName() );
        assertFalse( it.hasNext() );
    }
}