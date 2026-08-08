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

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Pins down how the deprecated {@link SinkEventAttributeSet} behaves for code written against Doxia 2.1.0 or
 * earlier. japicmp covers the signatures; what it cannot see is the runtime type of what these methods and
 * fields hand back, which is what the move changes and what these tests fix in place.
 */
@SuppressWarnings("deprecation")
class SinkEventAttributeSetCompatibilityTest {

    @Test
    void constantsAreStillReachableThroughTheDeprecatedClass() {
        assertSame(org.apache.maven.doxia.sink.SinkEventAttributeSet.SOURCE, SinkEventAttributeSet.SOURCE);
        assertSame(
                org.apache.maven.doxia.sink.SinkEventAttributeSet.Semantics.CODE, SinkEventAttributeSet.Semantics.CODE);
    }

    /**
     * The deliberate half of the trade-off: the constants keep their identity across both names, which is
     * only possible if they are instances of the new class alone. Redeclaring them on this class would keep
     * the old runtime type at the price of that identity.
     */
    @Test
    void constantsAreNoLongerInstancesOfTheDeprecatedClass() {
        assertInstanceOf(org.apache.maven.doxia.sink.SinkEventAttributeSet.class, SinkEventAttributeSet.SOURCE);
        assertFalse(SinkEventAttributeSet.SOURCE instanceof SinkEventAttributeSet);
    }

    @Test
    void unmodifiableStillReturnsThisType() {
        SinkEventAttributeSet attributes = new SinkEventAttributeSet(SinkEventAttributes.DECORATION, "source");

        assertSame(attributes, attributes.unmodifiable());
    }

    @Test
    void cloneStillReturnsThisType() {
        SinkEventAttributeSet attributes = new SinkEventAttributeSet(SinkEventAttributes.DECORATION, "source");

        Object clone = attributes.clone();

        assertInstanceOf(SinkEventAttributeSet.class, clone);
        assertEquals(attributes, clone);
    }
}
