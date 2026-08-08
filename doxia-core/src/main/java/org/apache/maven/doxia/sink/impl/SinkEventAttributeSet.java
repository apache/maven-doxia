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

import javax.swing.text.AttributeSet;

/**
 * Retained so that code compiled against Doxia 2.1.0 or earlier keeps working. The class moved to
 * {@code doxia-sink-api}, next to the {@link org.apache.maven.doxia.sink.SinkEventAttributes} interface it
 * implements and the {@link org.apache.maven.doxia.sink.Sink} methods it is passed to, so that attributes can
 * be created without depending on Doxia internals.
 * <p>
 * Instances of this class still behave as before, but it is no longer the type of the attribute sets Doxia
 * hands out. The inherited constants, and every attribute set produced inside Doxia, are instances of
 * {@link org.apache.maven.doxia.sink.SinkEventAttributeSet} only. Code that tests them with {@code instanceof}
 * or casts them to this class has to move to the new class; the constants themselves are unchanged, so
 * reading them through either name yields the very same objects.
 *
 * @deprecated since 2.2.0, use {@link org.apache.maven.doxia.sink.SinkEventAttributeSet} instead.
 */
@Deprecated
public class SinkEventAttributeSet extends org.apache.maven.doxia.sink.SinkEventAttributeSet {

    /**
     * Constructs a new, empty SinkEventAttributeSet with default size 5.
     */
    public SinkEventAttributeSet() {
        super();
    }

    /**
     * Constructs a new, empty SinkEventAttributeSet with the specified initial size.
     *
     * @param size the initial number of attribs.
     */
    public SinkEventAttributeSet(int size) {
        super(size);
    }

    /**
     * Constructs a new SinkEventAttributeSet with the attribute name-value
     * mappings as given by the specified String array.
     *
     * @param attributes the specified String array. If the length of this array
     * is not an even number, an IllegalArgumentException is thrown.
     */
    public SinkEventAttributeSet(String... attributes) {
        super(attributes);
    }

    /**
     * Constructs a new SinkEventAttributeSet with the same attribute name-value
     * mappings as in the specified AttributeSet.
     *
     * @param attributes the specified AttributeSet.
     */
    public SinkEventAttributeSet(AttributeSet attributes) {
        super(attributes);
    }

    /**
     * {@inheritDoc}
     *
     * Overridden only to keep returning this type, so that code compiled against the old signature keeps
     * resolving the method.
     */
    @Override
    public SinkEventAttributeSet unmodifiable() {
        super.unmodifiable();

        return this;
    }

    /**
     * Retained so that code referencing the nested class by its old binary name keeps working. Unlike the
     * constants of the enclosing class, a nested class is not inherited under its old name, so it needs an
     * explicit subclass here.
     *
     * @deprecated Use {@link org.apache.maven.doxia.sink.SinkEventAttributeSet.Semantics} instead.
     */
    @Deprecated
    public static class Semantics extends org.apache.maven.doxia.sink.SinkEventAttributeSet.Semantics {}
}
