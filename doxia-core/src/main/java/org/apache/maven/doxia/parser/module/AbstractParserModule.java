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
    /** The source directory. */
    private final String sourceDirectory;

    /** The supported file extensions. */
    private final String[] extensions;

    /** The default file extension. */
    private final String parserId;

    /**
     * Constructor with null.
     */
    public AbstractParserModule()
    {
        this( null, null, (String[]) null );
    }

    /**
     * Constructor with same value for everything: source directory and file extension equal parserId.
     * 
     * @param parserId the parser id
     */
    public AbstractParserModule( String parserId )
    {
        this( parserId, parserId, parserId );
    }

    /**
     * Constructor with same value for parser id and source directory.
     * 
     * @param parserId the parser id
     * @param extension the file extension
     */
    public AbstractParserModule( String parserId, String extension )
    {
        this( parserId, parserId, new String[] { extension } );
    }

    /**
     * @param sourceDirectory not null
     * @param extension not null
     * @param parserId not null
     * @since 1.1.1
     * @deprecated can cause confusion with constructor with multiple extensions
     */
    protected AbstractParserModule( String sourceDirectory, String extension, String parserId )
    {
        super();
        this.sourceDirectory = sourceDirectory;
        this.extensions = new String[] { extension };
        this.parserId = parserId;
    }

    /**
     * @param sourceDirectory not null
     * @param parserId not null (usually equals sourceDirectory)
     * @param extensions not null
     * @since 1.7
     */
    protected AbstractParserModule( String sourceDirectory, String parserId, String... extensions )
    {
        super();
        this.sourceDirectory = sourceDirectory;
        this.extensions = extensions;
        this.parserId = parserId;
    }

    /** {@inheritDoc} */
    public String getSourceDirectory()
    {
        return sourceDirectory;
    }

    /** {@inheritDoc} */
    public String[] getExtensions()
    {
        return extensions;
    }

    /** {@inheritDoc} */
    public String getParserId()
    {
        return parserId;
    }
}
