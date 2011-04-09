package org.apache.maven.doxia.plugin;

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

import java.util.List;

/**
 * A model for a Book.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @since 1.0
 */
public class Book
{
    /** Path to the book descriptor file. */
    private String descriptor;

    /** The list of formats to produce. */
    private List<Format> formats;

    /** The base directory of source files. */
    private String directory;

    /** Files to include. */
    private List<String> includes;

    /** Files to exclude. */
    private List<String> excludes;

    /**
     * Returns the path to the book descriptor file.
     *
     * @return the book descriptor file.
     */
    public String getDescriptor()
    {
        return descriptor;
    }

    /**
     * Returns the list of {@link Format}s to produce.
     *
     * @return the list of formats.
     */
    public List<Format> getFormats()
    {
        return formats;
    }

    /**
     * Returns the base directory of source files.
     *
     * @return the base directory.
     */
    public String getDirectory()
    {
        return directory;
    }

    /**
     * Returns the list of files to include.
     *
     * @return the list of files to include.
     */
    public List<String> getIncludes()
    {
        return includes;
    }

    /**
     * Returns the list of files to exclude.
     *
     * @return the list of files to exclude.
     */
    public List<String> getExcludes()
    {
        return excludes;
    }
}
