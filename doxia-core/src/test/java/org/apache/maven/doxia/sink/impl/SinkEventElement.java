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

import java.util.Arrays;
import java.util.Objects;

/**
 * A single sink event, used for testing purposes in order to check
 * the order and effect of some parser events.
 *
 * @author ltheussl
 * @since 1.1
 */
public class SinkEventElement {
    /** The name of the sink event, ie the sink method name. */
    private final String methodName;

    /** The array of arguments to the sink method. */
    private final Object[] args;

    /** The line number of the source which emitted the event (-1 if unknown) */
    private final int lineNumber;

    /**
     * A SinkEventElement is characterized by the method name and associated array of arguments.
     *
     * @param name The name of the sink event, ie the sink method name.
     * @param arguments The array of arguments to the sink method.
     *      For a no-arg element this may be null or an empty array.
     */
    public SinkEventElement(String name, Object[] arguments, int lineNumber) {
        Objects.requireNonNull(name, "name cannot be null");

        this.methodName = name;
        this.args = arguments;
        this.lineNumber = lineNumber;
    }

    /**
     * Return the name of the this event.
     *
     * @return The name of the sink event.
     */
    public String getName() {
        return this.methodName;
    }

    /**
     * Return the array of arguments to the sink method.
     *
     * @return the array of arguments to the sink method.
     */
    public Object[] getArgs() {
        return this.args;
    }

    /**
     * {@inheritDoc}
     * @since 1.1.1
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append('[');
        builder.append("methodName: ").append(methodName).append(", ");
        builder.append("args: ").append(Arrays.deepToString(args));
        if (lineNumber != -1) {
            builder.append(", ");
            builder.append("lineNumber: ").append(lineNumber);
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(args);
        result = prime * result + Objects.hash(methodName);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SinkEventElement other = (SinkEventElement) obj;
        return Arrays.deepEquals(args, other.args) && Objects.equals(methodName, other.methodName);
    }
}
