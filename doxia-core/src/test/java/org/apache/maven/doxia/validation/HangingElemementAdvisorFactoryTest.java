package org.apache.maven.doxia.validation;

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

import junit.framework.TestCase;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.validation.advices.HangingElementAdvice;
import org.apache.maven.doxia.validation.advices.MethodBeforeAdvice;

/**
 * Unit test for
 * {@link org.apache.maven.doxia.validation.advices.HangingElemementAdvisorFactory}
 *
 * @author Juan F. Codagnone
 * @since Nov 6, 2005
 */
public class HangingElemementAdvisorFactoryTest
    extends TestCase
{

    /** unit test */
    public final void testOk()
    {
        final Sink sink = new AdvicedSink( new MethodBeforeAdvice[] { new HangingElementAdvice(), }, new SinkAdapter() );

        sink.text( "foo" );
        sink.paragraph();
        sink.list();
        sink.list_();
        sink.paragraph_();
    }

    /** unit test */
    public final void testWrongOrder()
    {
        final Sink sink = new AdvicedSink( new MethodBeforeAdvice[] { new HangingElementAdvice(), }, new SinkAdapter() );

        sink.paragraph();
        sink.list();
        try
        {
            sink.paragraph_();
            fail();
        }
        catch ( final IllegalStateException e )
        {
            // ok
        }
    }

    /** unit test */
    public final void testHanging()
    {
        final Sink sink = new AdvicedSink( new MethodBeforeAdvice[] { new HangingElementAdvice(), }, new SinkAdapter() );

        sink.paragraph();
        sink.paragraph_();
        try
        {
            sink.paragraph_();
            fail();
        }
        catch ( final IllegalStateException e )
        {
            // ok
        }
    }
}
