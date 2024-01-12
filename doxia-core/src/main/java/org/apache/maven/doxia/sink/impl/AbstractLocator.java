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

import java.nio.file.Path;

import org.apache.maven.doxia.sink.Locator;

/**
 * @since 2.0.0
 */
public abstract class AbstractLocator implements Locator {

    private Path file;

    protected AbstractLocator(Path file) {
        super();
        this.file = file;
    }

    @Override
    public Path getSourceFile() {
        return file;
    }

    @Override
    public String getLogPrefix() {
        return formatLocation(this);
    }

    /**
     * Creates a string with line/column information. Inspired by
     * {@code o.a.m.model.building.ModelProblemUtils.formatLocation(...)}.
     *
     * @param locator The locator must not be {@code null}.
     * @return The formatted location or an empty string if unknown, never {@code null}.
     */
    public static String formatLocation(Locator locator) {
        StringBuilder buffer = new StringBuilder();

        if (locator.getSourceFile() != null) {
            buffer.append(locator.getSourceFile());
        }
        if (locator.getLineNumber() > 0) {
            buffer.append(", line ").append(locator.getLineNumber());
        }
        if (locator.getColumnNumber() > 0) {
            buffer.append(", column ").append(locator.getLineNumber());
        }
        if (buffer.length() > 0) {
            buffer.append(": ");
        }
        return buffer.toString();
    }
}
