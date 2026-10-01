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
package org.apache.maven.doxia.module.markdown;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.doxia.ServiceLoaderTestSupport;
import org.apache.maven.doxia.module.xhtml5.Xhtml5SinkFactory;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.manager.DefaultParserManager;
import org.apache.maven.doxia.parser.manager.ParserManager;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.parser.module.DefaultParserModuleManager;
import org.apache.maven.doxia.parser.module.ParserModule;
import org.apache.maven.doxia.parser.module.ParserModuleManager;
import org.apache.maven.doxia.parser.module.ParserModuleNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Builds the parser managers without Sisu, from the {@code META-INF/services} entries of this module. */
class ServiceLoaderManagersTest {

    private final ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void parserManagerFindsTheParserById() throws Exception {
        ParserManager manager = DefaultParserManager.fromServiceLoader(classLoader);

        assertInstanceOf(MarkdownParser.class, manager.getParser("markdown"));
        assertSame(manager.getParser("markdown"), manager.getParser("markdown"));
        assertThrows(ParserNotFoundException.class, () -> manager.getParser("weirdId"));
    }

    @Test
    void parserModuleManagerFindsTheModuleById() throws Exception {
        ParserModuleManager manager = DefaultParserModuleManager.fromServiceLoader(classLoader);

        ParserModule module = manager.getParserModule("markdown");
        assertInstanceOf(MarkdownParserModule.class, module);
        assertEquals("markdown", module.getParserId());
        assertSame(module, manager.getParserModule("markdown"));
        assertEquals(true, manager.getParserModules().contains(module));
        assertThrows(ParserModuleNotFoundException.class, () -> manager.getParserModule("weirdId"));
    }

    @Test
    void parserCreatedWithoutSisuUsesItsDefaultHtmlParser() throws Exception {
        Parser parser = DefaultParserManager.fromServiceLoader(classLoader).getParser("markdown");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        parser.parse(new StringReader("# Title\n\nSome *text*.\n"), new Xhtml5SinkFactory().createSink(out));
        assertEquals(true, out.toString("UTF-8").contains("<h1>Title</h1>"));
    }

    @Test
    void brokenProvidersAreSkipped(@TempDir Path dir) throws Exception {
        write(dir, Parser.class, "org.example.MissingParser");
        write(dir, ParserModule.class, "org.example.MissingParserModule");

        try (URLClassLoader loader = new URLClassLoader(new URL[] {dir.toUri().toURL()}, classLoader)) {
            assertInstanceOf(
                    MarkdownParser.class,
                    DefaultParserManager.fromServiceLoader(loader).getParser("markdown"));
            assertInstanceOf(
                    MarkdownParserModule.class,
                    DefaultParserModuleManager.fromServiceLoader(loader).getParserModule("markdown"));
        }
    }

    @Test
    void servicesFilesAndSisuIndexListTheSameClasses() throws Exception {
        // MarkdownHtmlParser is a bare @Named helper injected into MarkdownParser, not a parser registered by id
        assertEquals(
                ServiceLoaderTestSupport.sisuIndex(
                        Parser.class, "org.apache.maven.doxia.module.markdown.MarkdownParser$MarkdownHtmlParser"),
                ServiceLoaderTestSupport.servicesFile(Parser.class));
        assertEquals(
                ServiceLoaderTestSupport.sisuIndex(ParserModule.class),
                ServiceLoaderTestSupport.servicesFile(ParserModule.class));
    }

    private static void write(Path dir, Class<?> role, String className) throws Exception {
        Path services = dir.resolve("META-INF/services/" + role.getName());
        Files.createDirectories(services.getParent());
        Files.write(services, className.getBytes(StandardCharsets.UTF_8));
    }
}
