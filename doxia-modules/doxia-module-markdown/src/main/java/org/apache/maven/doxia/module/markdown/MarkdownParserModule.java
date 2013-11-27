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

import org.apache.maven.doxia.parser.module.AbstractParserModule;
import org.apache.maven.doxia.parser.module.ParserModule;
import org.codehaus.plexus.component.annotations.Component;

/**
 * {@link org.apache.maven.doxia.parser.module.ParserModule} for Markdown.
 *
 * @since 1.6
 */
@Component( role = ParserModule.class, hint = "markdown" )
public class MarkdownParserModule
    extends AbstractParserModule
{
    /**
     * The extension for Markdown files.
     */
    public static final String FILE_EXTENSION = "md";

    /**
     * Build a new instance of {@link MarkdownSiteModule}.
     */
    public MarkdownParserModule()
    {
        super( MarkdownParser.ROLE_HINT, FILE_EXTENSION );
    }
}
