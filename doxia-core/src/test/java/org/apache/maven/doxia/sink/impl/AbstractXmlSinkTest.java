package org.apache.maven.doxia.sink.impl;

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

import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.SinkEventAttributes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ltheussl
 */
public class AbstractXmlSinkTest
{
    /**
     * Test of set/getNameSpace method, of class AbstractXmlSink.
     */
    @Test
    public void testNameSpace()
    {
        final Tag t = Tag.A;
        final String ns = "ns";
        final XmlTestSink instance = new XmlTestSink();

        instance.writeStartTag( t );
        instance.writeEndTag( t );
        assertEquals( "<a></a>", instance.getText() );

        instance.writeSimpleTag( t );
        assertEquals( "<a />", instance.getText() );

        instance.setNameSpace( ns );

        instance.writeStartTag( t );
        instance.writeEndTag( t );
        assertEquals( "<ns:a></ns:a>", instance.getText() );

        instance.writeSimpleTag( t );
        assertEquals( "<ns:a />", instance.getText() );

        assertEquals( ns, instance.getNameSpace() );

        try
        {
            instance.writeStartTag( null );
            fail( "null tag should fail!" );
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }

        try
        {
            instance.writeEndTag( null );
            fail( "null tag should fail!" );
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }
    }

    /**
     * Test of writeStartTag method, of class AbstractXmlSink.
     */
    @Test
    public void testWriteStartTag()
    {
        final Tag t = Tag.A;
        final SinkEventAttributes att = new SinkEventAttributeSet( SinkEventAttributeSet.BOLD );
        final XmlTestSink instance = new XmlTestSink();

        instance.writeStartTag( t );
        assertEquals( "<a>", instance.getText() );

        instance.writeStartTag( t, att );
        assertEquals( "<a style=\"bold\">", instance.getText() );

        instance.writeStartTag( t, att, false );
        assertEquals( "<a style=\"bold\">", instance.getText() );

        instance.writeStartTag( t, att, true );
        assertEquals( "<a style=\"bold\" />", instance.getText() );
    }

    /**
     * Test of writeEOL method, of class AbstractXmlSink.
     */
    @Test
    public void testWriteEOL()
    {
        final XmlTestSink instance = new XmlTestSink();

        instance.writeEOL();
        assertEquals( Markup.EOL, instance.getText() );
    }

    /**
     * Test of writeSimpleTag method, of class AbstractXmlSink.
     */
    @Test
    public void testWriteSimpleTag()
    {
        final Tag t = Tag.A;
        final SinkEventAttributes att = new SinkEventAttributeSet( SinkEventAttributeSet.BOLD );
        final XmlTestSink instance = new XmlTestSink();

        instance.writeSimpleTag( t );
        assertEquals( "<a />", instance.getText() );

        instance.writeSimpleTag( t, att );
        assertEquals( "<a style=\"bold\" />", instance.getText() );
    }

    /** Test sink. */
    private static class XmlTestSink extends AbstractXmlSink
    {
        private final StringBuilder buffer = new StringBuilder( 0 );

        public void reset()
        {
            buffer.setLength( 0 );
        }

        public String getText()
        {
            String text = buffer.toString();
            reset();

            return text;
        }

        protected void write( String text )
        {
            buffer.append( text );
        }
    }
}
