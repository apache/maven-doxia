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
package org.apache.maven.doxia.parser;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserContextTest {

    @Test
    void emptyContextHasDefaults() {
        ParserContext context = ParserContext.builder().build();

        assertNull(context.getReference());
        assertFalse(context.getSourceFile().isPresent());
        assertFalse(context.getSourceDirectory().isPresent());
        assertFalse(context.getRootDirectory().isPresent());
        assertFalse(context.getOutputName().isPresent());
        assertFalse(context.getOutputDirectory().isPresent());
        assertEquals(Locale.ROOT, context.getLocale());
        assertEquals(StandardCharsets.UTF_8, context.getInputEncoding());
        assertEquals(StandardCharsets.UTF_8, context.getOutputEncoding());
        assertTrue(context.getProperties().isEmpty());
        assertTrue(context.getOptions().isEmpty());
    }

    @Test
    void contextKeepsWhatTheBuilderWasGiven() {
        Path root = Paths.get("project").toAbsolutePath();
        Path sourceDirectory = root.resolve("src/site/markdown");
        Path sourceFile = sourceDirectory.resolve("guide/index.md");
        Path outputDirectory = root.resolve("target/site");

        ParserContext context = ParserContext.builder()
                .reference("guide/index.md")
                .sourceFile(sourceFile)
                .sourceDirectory(sourceDirectory)
                .rootDirectory(root)
                .outputName("guide/index.html")
                .outputDirectory(outputDirectory)
                .locale(Locale.FRENCH)
                .inputEncoding(StandardCharsets.ISO_8859_1)
                .outputEncoding(StandardCharsets.US_ASCII)
                .build();

        assertEquals("guide/index.md", context.getReference());
        assertEquals(sourceFile, context.getSourceFile().get());
        assertEquals(sourceDirectory, context.getSourceDirectory().get());
        assertEquals(root, context.getRootDirectory().get());
        assertEquals("guide/index.html", context.getOutputName().get());
        assertEquals(outputDirectory, context.getOutputDirectory().get());
        assertEquals(Locale.FRENCH, context.getLocale());
        assertEquals(StandardCharsets.ISO_8859_1, context.getInputEncoding());
        assertEquals(StandardCharsets.US_ASCII, context.getOutputEncoding());
    }

    @Test
    void mapsAreCopiedAndUnmodifiable() {
        Map<String, String> properties = new HashMap<>();
        properties.put("project.version", "1.0");
        Map<String, String> options = new HashMap<>();
        options.put("attributes.toc", "left");

        ParserContext context =
                ParserContext.builder().properties(properties).options(options).build();
        properties.put("later", "ignored");
        options.clear();

        assertEquals(1, context.getProperties().size());
        assertEquals("1.0", context.getProperties().get("project.version"));
        assertEquals("left", context.getOptions().get("attributes.toc"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> context.getProperties().put("a", "b"));
        assertThrows(
                UnsupportedOperationException.class, () -> context.getOptions().put("a", "b"));
    }

    @Test
    void builderRejectsNullWhereTheContextGuaranteesAValue() {
        ParserContext.Builder builder = ParserContext.builder();

        assertThrows(NullPointerException.class, () -> builder.locale(null));
        assertThrows(NullPointerException.class, () -> builder.inputEncoding(null));
        assertThrows(NullPointerException.class, () -> builder.outputEncoding(null));
        assertThrows(NullPointerException.class, () -> builder.properties(null));
        assertThrows(NullPointerException.class, () -> builder.options(null));
    }

    @Test
    void toBuilderCopiesTheContext() {
        Map<String, String> options = new HashMap<>();
        options.put("attributes.toc", "left");
        ParserContext context = ParserContext.builder()
                .reference("guide/index.md")
                .outputName("guide/index.html")
                .locale(Locale.FRENCH)
                .options(options)
                .build();

        ParserContext copy = context.toBuilder().reference("guide/index.md.vm").build();

        assertEquals("guide/index.md.vm", copy.getReference());
        assertEquals("guide/index.html", copy.getOutputName().get());
        assertEquals(Locale.FRENCH, copy.getLocale());
        assertEquals(options, copy.getOptions());
        assertEquals("guide/index.md", context.getReference());
    }

    @Test
    void parserWithoutContextSupportReceivesTheReference() throws ParseException {
        AtomicReference<String> seen = new AtomicReference<>();
        Parser parser = new AbstractTextParser() {
            @Override
            public void parse(Reader source, Sink sink, String reference) {
                seen.set(reference);
            }
        };

        parser.parse(
                ParserContext.builder().reference("index.md").build(),
                new StringReader(""),
                new SinkEventTestingSink());

        assertEquals("index.md", seen.get());
    }

    @Test
    void parserWithContextSupportReceivesTheContext() throws ParseException {
        AtomicReference<ParserContext> seen = new AtomicReference<>();
        Parser parser = new AbstractTextParser() {
            @Override
            public void parse(Reader source, Sink sink, String reference) {
                throw new IllegalStateException("the context-aware method must be called");
            }

            @Override
            public void parse(ParserContext context, Reader source, Sink sink) {
                seen.set(context);
            }
        };
        ParserContext context = ParserContext.builder().reference("index.md").build();

        parser.parse(context, new StringReader(""), new SinkEventTestingSink());

        assertSame(context, seen.get());
    }
}
