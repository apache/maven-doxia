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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * What the caller of a {@link Parser} knows about the document being parsed and its surroundings:
 * where the source comes from, where the result goes, and the properties and options that apply.
 * <p>
 * A context is immutable and belongs to one call of {@link Parser#parse(ParserContext, java.io.Reader,
 * org.apache.maven.doxia.sink.Sink)}. Parsers are usually shared between documents and threads,
 * so a parser must not keep a context beyond that call.
 * <p>
 * Everything except the encodings and the locale is optional, because not every caller renders files of a site.
 *
 * @since 2.3.0
 */
public final class ParserContext {

    private final String reference;

    private final Path sourceFile;

    private final Path sourceDirectory;

    private final Path rootDirectory;

    private final String outputName;

    private final Path outputDirectory;

    private final Locale locale;

    private final Charset inputEncoding;

    private final Charset outputEncoding;

    private final Map<String, String> properties;

    private final Map<String, String> options;

    private ParserContext(Builder builder) {
        this.reference = builder.reference;
        this.sourceFile = builder.sourceFile;
        this.sourceDirectory = builder.sourceDirectory;
        this.rootDirectory = builder.rootDirectory;
        this.outputName = builder.outputName;
        this.outputDirectory = builder.outputDirectory;
        this.locale = builder.locale;
        this.inputEncoding = builder.inputEncoding;
        this.outputEncoding = builder.outputEncoding;
        this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(builder.properties));
        this.options = Collections.unmodifiableMap(new LinkedHashMap<>(builder.options));
    }

    /**
     * @return a builder for a context without any information, UTF-8 encodings and the root locale
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return a builder that starts with the content of this context, for a parser that hands a modified
     * context to another parser
     */
    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.reference = reference;
        builder.sourceFile = sourceFile;
        builder.sourceDirectory = sourceDirectory;
        builder.rootDirectory = rootDirectory;
        builder.outputName = outputName;
        builder.outputDirectory = outputDirectory;
        builder.locale = locale;
        builder.inputEncoding = inputEncoding;
        builder.outputEncoding = outputEncoding;
        builder.properties = properties;
        builder.options = options;
        return builder;
    }

    /**
     * @return the string identifying the source, as passed to
     * {@link Parser#parse(java.io.Reader, org.apache.maven.doxia.sink.Sink, String)}, may be {@code null}
     */
    public String getReference() {
        return reference;
    }

    /**
     * @return the absolute path of the document being parsed, empty if the source is not a file
     */
    public Optional<Path> getSourceFile() {
        return Optional.ofNullable(sourceFile);
    }

    /**
     * @return the directory the document's path is relative to, for a Maven site the directory of the
     * document's format, such as {@code src/site/markdown}
     */
    public Optional<Path> getSourceDirectory() {
        return Optional.ofNullable(sourceDirectory);
    }

    /**
     * @return the root directory of what is being rendered, for a Maven site the project base directory
     */
    public Optional<Path> getRootDirectory() {
        return Optional.ofNullable(rootDirectory);
    }

    /**
     * @return the name of the rendered document relative to the output root, with {@code /} as separator,
     * for example {@code guide/index.html}
     */
    public Optional<String> getOutputName() {
        return Optional.ofNullable(outputName);
    }

    /**
     * @return the directory {@link #getOutputName()} is relative to, empty if the result is not written to a file
     */
    public Optional<Path> getOutputDirectory() {
        return Optional.ofNullable(outputDirectory);
    }

    /**
     * @return the locale the document is rendered for, {@link Locale#ROOT} if unknown
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return the encoding of the source, UTF-8 by default
     */
    public Charset getInputEncoding() {
        return inputEncoding;
    }

    /**
     * @return the encoding of the rendered document, UTF-8 by default
     */
    public Charset getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * @return the properties of what is being rendered, for a Maven site the project properties; unmodifiable
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @return the options the caller has for the parser that receives this context; unmodifiable
     */
    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * Builds a {@link ParserContext}.
     *
     * @since 2.3.0
     */
    public static final class Builder {

        private String reference;

        private Path sourceFile;

        private Path sourceDirectory;

        private Path rootDirectory;

        private String outputName;

        private Path outputDirectory;

        private Locale locale = Locale.ROOT;

        private Charset inputEncoding = StandardCharsets.UTF_8;

        private Charset outputEncoding = StandardCharsets.UTF_8;

        private Map<String, String> properties = Collections.emptyMap();

        private Map<String, String> options = Collections.emptyMap();

        private Builder() {}

        /**
         * @param reference the string identifying the source, may be {@code null}
         * @return this builder
         */
        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        /**
         * @param sourceFile the absolute path of the document, may be {@code null}
         * @return this builder
         */
        public Builder sourceFile(Path sourceFile) {
            this.sourceFile = sourceFile;
            return this;
        }

        /**
         * @param sourceDirectory the directory the document's path is relative to, may be {@code null}
         * @return this builder
         */
        public Builder sourceDirectory(Path sourceDirectory) {
            this.sourceDirectory = sourceDirectory;
            return this;
        }

        /**
         * @param rootDirectory the root directory of what is being rendered, may be {@code null}
         * @return this builder
         */
        public Builder rootDirectory(Path rootDirectory) {
            this.rootDirectory = rootDirectory;
            return this;
        }

        /**
         * @param outputName the name of the rendered document relative to the output root, may be {@code null}
         * @return this builder
         */
        public Builder outputName(String outputName) {
            this.outputName = outputName;
            return this;
        }

        /**
         * @param outputDirectory the directory the output name is relative to, may be {@code null}
         * @return this builder
         */
        public Builder outputDirectory(Path outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        /**
         * @param locale the locale the document is rendered for, not {@code null}
         * @return this builder
         */
        public Builder locale(Locale locale) {
            this.locale = Objects.requireNonNull(locale, "locale");
            return this;
        }

        /**
         * @param inputEncoding the encoding of the source, not {@code null}
         * @return this builder
         */
        public Builder inputEncoding(Charset inputEncoding) {
            this.inputEncoding = Objects.requireNonNull(inputEncoding, "inputEncoding");
            return this;
        }

        /**
         * @param outputEncoding the encoding of the rendered document, not {@code null}
         * @return this builder
         */
        public Builder outputEncoding(Charset outputEncoding) {
            this.outputEncoding = Objects.requireNonNull(outputEncoding, "outputEncoding");
            return this;
        }

        /**
         * @param properties the properties of what is being rendered, copied when the context is built, not {@code null}
         * @return this builder
         */
        public Builder properties(Map<String, String> properties) {
            this.properties = Objects.requireNonNull(properties, "properties");
            return this;
        }

        /**
         * @param options the options for the parser, copied when the context is built, not {@code null}
         * @return this builder
         */
        public Builder options(Map<String, String> options) {
            this.options = Objects.requireNonNull(options, "options");
            return this;
        }

        /**
         * @return a new context with the content of this builder
         */
        public ParserContext build() {
            return new ParserContext(this);
        }
    }
}
