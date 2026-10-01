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
package org.apache.maven.doxia.parser.manager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.Map;
import java.util.Objects;

import org.apache.maven.doxia.macro.manager.DefaultMacroManager;
import org.apache.maven.doxia.macro.manager.MacroManager;
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.impl.SinkWrapperFactory;
import org.apache.maven.doxia.util.ServiceLoaderSupport;

/**
 * Simple implementation of the <code>ParserManager</code> interface.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
@Singleton
@Named
public class DefaultParserManager implements ParserManager {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Inject
    private Map<String, Parser> parsers;

    /**
     * Creates a manager holding every {@link Parser} listed in
     * {@code META-INF/services/org.apache.maven.doxia.parser.Parser} of the given class loader, without a
     * dependency injection container. Each parser is registered under its {@code javax.inject.Named} value,
     * the same id the Sisu-based manager uses, and one instance is shared by all lookups, like a Sisu singleton.
     * A parser that cannot be loaded is skipped with a warning.
     * <p>
     * Sisu injects a {@link MacroManager} and the automatically registered {@link SinkWrapperFactory} instances
     * into every parser, so this method does the same: the macros come from
     * {@link DefaultMacroManager#fromServiceLoader(ClassLoader)} and the sink wrapper factories from
     * {@code META-INF/services/org.apache.maven.doxia.sink.impl.SinkWrapperFactory}, both read from the same
     * class loader.
     *
     * @param classLoader the class loader to look parsers up in, not {@code null}
     * @return a new manager
     * @throws IllegalStateException if a parser has no {@code @Named} value
     * @since 2.2.0
     */
    public static DefaultParserManager fromServiceLoader(ClassLoader classLoader) {
        Objects.requireNonNull(classLoader);
        return fromServiceLoader(classLoader, DefaultMacroManager.fromServiceLoader(classLoader));
    }

    /**
     * Like {@link #fromServiceLoader(ClassLoader)}, but wires the given macro manager into the parsers instead
     * of one built from the class loader.
     *
     * @param classLoader the class loader to look parsers up in, not {@code null}
     * @param macroManager the macro manager the parsers use, not {@code null}
     * @return a new manager
     * @throws IllegalStateException if a parser has no {@code @Named} value
     * @since 2.2.0
     */
    public static DefaultParserManager fromServiceLoader(ClassLoader classLoader, MacroManager macroManager) {
        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(macroManager);
        DefaultParserManager manager = new DefaultParserManager();
        manager.parsers = ServiceLoaderSupport.loadNamed(Parser.class, classLoader);
        Iterable<SinkWrapperFactory> wrapperFactories =
                ServiceLoaderSupport.load(SinkWrapperFactory.class, classLoader);
        for (Parser parser : manager.parsers.values()) {
            if (parser instanceof AbstractParser) {
                ((AbstractParser) parser).setMacroManager(macroManager);
            }
            for (SinkWrapperFactory factory : wrapperFactories) {
                parser.addSinkWrapperFactory(factory);
            }
        }
        return manager;
    }

    public Parser getParser(String id) throws ParserNotFoundException {
        Parser parser = parsers.get(id);

        if (parser == null) {
            throw new ParserNotFoundException("Cannot find parser with id '" + id + "'");
        }

        return parser;
    }
}
