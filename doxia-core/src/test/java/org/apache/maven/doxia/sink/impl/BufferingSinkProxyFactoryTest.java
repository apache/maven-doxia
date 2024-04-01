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
package org.apache.maven.doxia.sink.impl;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.sink.Sink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BufferingSinkProxyFactoryTest {

    @Test
    void testCastAsBufferingSink() {
        SinkEventTestingSink testingSink = new SinkEventTestingSink();
        assertThrows(IllegalArgumentException.class, () -> BufferingSinkProxyFactory.castAsBufferingSink(testingSink));
        BufferingSinkProxyFactory factory = new BufferingSinkProxyFactory();
        Sink bufferingSink = factory.createWrapper(testingSink);
        BufferingSinkProxyFactory.castAsBufferingSink(bufferingSink);
    }

    @Test
    void testBufferingSink() {
        SinkEventTestingSink testingSink = new SinkEventTestingSink();
        BufferingSinkProxyFactory factory = new BufferingSinkProxyFactory();
        Sink bufferingSink = factory.createWrapper(testingSink);
        assertNotNull(bufferingSink.getDocumentLocator());
        assertEquals(
                testingSink,
                BufferingSinkProxyFactory.castAsBufferingSink(bufferingSink).getBufferedSink());

        bufferingSink.text("buffered text");
        assertEquals(0, testingSink.getEventList().size());

        bufferingSink.flush();
        AbstractParserTest.assertSinkStartsWith(testingSink.getEventList().iterator(), "text");
    }
}
