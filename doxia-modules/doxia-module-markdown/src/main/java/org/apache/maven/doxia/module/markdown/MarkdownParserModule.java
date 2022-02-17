package org.apache.maven.doxia.module.markdown;

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

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.doxia.parser.module.AbstractParserModule;

/**
 * {@link org.apache.maven.doxia.parser.module.ParserModule} for Markdown.
 *
 * @since 1.6
 */
@Singleton
@Named( "markdown" )
public class MarkdownParserModule
    extends AbstractParserModule
{
    /**
     * The extension for Markdown files.
     */
    public static final String FILE_EXTENSION = "md";

    /**
     * Alternate extension for Markdown files.
     */
    public static final String ALTERNATE_FILE_EXTENSION = "markdown";

    /**
     * Build a new instance of {@link MarkdownParserModule}.
     */
    public MarkdownParserModule()
    {
        super( "markdown", "markdown", FILE_EXTENSION, ALTERNATE_FILE_EXTENSION );
    }
}
