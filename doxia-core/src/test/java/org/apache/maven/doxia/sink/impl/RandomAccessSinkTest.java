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

/*
 * @author Robert Scholte
 */
import java.io.ByteArrayOutputStream;
import java.io.Writer;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomAccessSinkTest
{
    private SinkFactory factory = new AbstractXmlSinkFactory()
    {
        protected Sink createSink( Writer writer, String encoding, String languageId )
        {
            return new TextSink( writer );
        }

        protected Sink createSink( Writer writer, String encoding )
        {
            return new TextSink( writer );
        }
    };

    private void buildSimple( Sink sink, String text )
    {
        sink.anchor( "foobar" );
        sink.text( text );
        sink.anchor_();
    }

    @Test
    public void testSimple()
        throws Exception
    {
        String encoding = "UTF-8";
        String text = "Hello World";
        ByteArrayOutputStream outFlatSink = new ByteArrayOutputStream();
        Sink flatSink = factory.createSink( outFlatSink, encoding );
        buildSimple( flatSink, text );
        flatSink.flush();
        flatSink.close();

        ByteArrayOutputStream outRandomAccessSink = new ByteArrayOutputStream();
        RandomAccessSink randomAccessSink = new RandomAccessSink( factory, outRandomAccessSink, encoding );
        buildSimple( randomAccessSink, text );
        randomAccessSink.flush();
        randomAccessSink.close();

        assertEquals( outFlatSink.toString( encoding ), outRandomAccessSink.toString( encoding ) );
    }

    @Test
    public void testComplex()
        throws Exception
    {
        String encoding = "UTF-8";
        String summaryText = "Summary text";
        String detailText = "Detail text";
        ByteArrayOutputStream outFlatSink = new ByteArrayOutputStream();
        Sink flatSink = factory.createSink( outFlatSink, encoding );
        buildSimple( flatSink, summaryText );
        flatSink.horizontalRule();
        buildSimple( flatSink, detailText );
        flatSink.flush();
        flatSink.close();

        ByteArrayOutputStream outRandomAccessSink = new ByteArrayOutputStream();
        RandomAccessSink randomAccessSink = new RandomAccessSink( factory, outRandomAccessSink, encoding );
        Sink summarySink = randomAccessSink.addSinkHook();
        randomAccessSink.horizontalRule();
        Sink detailSink = randomAccessSink.addSinkHook();

        // here's an example of the strength of randomAccessSink. Summary and detail are built in reverse order
        buildSimple( detailSink, detailText );
        buildSimple( summarySink, summaryText );

        randomAccessSink.flush();
        randomAccessSink.close();

        assertEquals( outFlatSink.toString( encoding ), outRandomAccessSink.toString( encoding ) );
    }
}