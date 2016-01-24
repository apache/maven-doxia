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

import org.apache.maven.doxia.parser.module.ParserModule;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Test MarkdownParserModule.
 */
public class MarkdownParserModuleTest
    extends PlexusTestCase
{
    /**
     * The {@link MarkdownParser} used for the tests.
     */
    protected MarkdownParserModule parserModule;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        parserModule = (MarkdownParserModule) lookup( ParserModule.class, MarkdownParser.ROLE_HINT );
    }

    public void testExtensions()
    {
        assertEquals( 2, parserModule.getExtensions().length );
    }
}
