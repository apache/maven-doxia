package org.apache.maven.doxia.parser.module;

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

/**
 * An abstract base class that implements the ParserModule interface.
 *
 * @since 1.6
 */
public abstract class AbstractParserModule
    implements ParserModule
{
    /**
     * The source directory.
     */
    private final String sourceDirectory;

    /**
     * The default file extension.
     */
    private final String[] extension;

    /**
     * The default file extension.
     */
    private final String parserId;

    /**
     * Constructor with null.
     */
    public AbstractParserModule()
    {
        this( null, null, null );
    }

    /**
     * Constructor with same value for everything: source directory and file extension equal parserId.
     */
    public AbstractParserModule( String parserId )
    {
        this( parserId, parserId, parserId );
    }

    /**
     * Constructor with same value for parser id and source directory.
     */
    public AbstractParserModule( String parserId, String... extension )
    {
        this( parserId, extension, parserId );
    }

    /**
     * @param sourceDirectory not null
     * @param extension       not null
     * @param parserId        not null
     * @since 1.1.1
     */
    protected AbstractParserModule( String sourceDirectory, String[] extension, String parserId )
    {
        super();
        this.sourceDirectory = sourceDirectory;
        this.extension = extension;
        this.parserId = parserId;
    }

    /**
     * {@inheritDoc}
     */
    public String getSourceDirectory()
    {
        return sourceDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getExtension()
    {
        return extension;
    }

    /**
     * {@inheritDoc}
     */
    public String getParserId()
    {
        return parserId;
    }
}
