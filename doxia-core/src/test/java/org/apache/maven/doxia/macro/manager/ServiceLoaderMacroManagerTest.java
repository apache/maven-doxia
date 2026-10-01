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
package org.apache.maven.doxia.macro.manager;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.doxia.ServiceLoaderTestSupport;
import org.apache.maven.doxia.macro.EchoMacro;
import org.apache.maven.doxia.macro.Macro;
import org.apache.maven.doxia.macro.snippet.SnippetMacro;
import org.apache.maven.doxia.macro.toc.TocMacro;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Builds a {@link MacroManager} without Sisu, from the {@code META-INF/services} entries. */
class ServiceLoaderMacroManagerTest {

    private static URLClassLoader loaderWithServices(Path dir, String... classNames) throws Exception {
        Path services = dir.resolve("META-INF/services/" + Macro.class.getName());
        Files.createDirectories(services.getParent());
        Files.write(services, String.join("\n", classNames).getBytes(StandardCharsets.UTF_8));
        return new URLClassLoader(
                new URL[] {dir.toUri().toURL()}, ServiceLoaderMacroManagerTest.class.getClassLoader());
    }

    @Test
    void findsAllMacrosByNamedValue() throws Exception {
        MacroManager manager = DefaultMacroManager.fromServiceLoader(getClass().getClassLoader());

        assertInstanceOf(EchoMacro.class, manager.getMacro("echo"));
        assertInstanceOf(SnippetMacro.class, manager.getMacro("snippet"));
        assertInstanceOf(TocMacro.class, manager.getMacro("toc"));
        assertThrows(MacroNotFoundException.class, () -> manager.getMacro("weirdId"));
    }

    @Test
    void lookupReturnsTheSameInstanceLikeASisuSingleton() throws Exception {
        MacroManager manager = DefaultMacroManager.fromServiceLoader(getClass().getClassLoader());

        assertSame(manager.getMacro("toc"), manager.getMacro("toc"));
    }

    @Test
    void brokenProviderIsSkipped(@TempDir Path dir) throws Exception {
        try (URLClassLoader loader = loaderWithServices(dir, "org.example.MissingMacro")) {
            MacroManager manager = DefaultMacroManager.fromServiceLoader(loader);

            assertInstanceOf(EchoMacro.class, manager.getMacro("echo"));
            assertThrows(MacroNotFoundException.class, () -> manager.getMacro("missing"));
        }
    }

    @Test
    void providerWithoutNamedFailsClearly(@TempDir Path dir) throws Exception {
        try (URLClassLoader loader = loaderWithServices(dir, UnnamedMacro.class.getName())) {
            IllegalStateException e =
                    assertThrows(IllegalStateException.class, () -> DefaultMacroManager.fromServiceLoader(loader));
            assertEquals(true, e.getMessage().contains(UnnamedMacro.class.getName()));
        }
    }

    @Test
    void servicesFileAndSisuIndexListTheSameMacros() throws Exception {
        assertEquals(
                ServiceLoaderTestSupport.sisuIndex(Macro.class), ServiceLoaderTestSupport.servicesFile(Macro.class));
    }

    /** A macro that cannot be registered because it has no {@code @Named}. */
    public static class UnnamedMacro extends EchoMacro {}
}
