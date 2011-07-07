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

import org.apache.maven.doxia.module.site.AbstractSiteModule;

/**
 * {@link org.apache.maven.doxia.module.site.SiteModule} for Markdown.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @plexus.component role="org.apache.maven.doxia.module.site.SiteModule" role-hint="markdown"
 * @since 1.3
 */
public class MarkdownSiteModule
    extends AbstractSiteModule
{

    /**
     * The source directory for Markdown files.
     */
    public static final String SOURCE_DIRECTORY = "markdown";

    /**
     * The extension for Markdown files.
     */
    public static final String FILE_EXTENSION = "md";

    /**
     * Build a new instance of {@link MarkdownSiteModule}.
     */
    public MarkdownSiteModule()
    {
        super( SOURCE_DIRECTORY, FILE_EXTENSION, MarkdownParser.ROLE_HINT );
    }
}
