package org.apache.maven.doxia.parser.manager;

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

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.doxia.parser.Parser;

import java.util.Map;

/**
 * Simple implementation of the <code>ParserManager</code> interface.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
@Named
public class DefaultParserManager
    implements ParserManager
{
    @SuppressWarnings( "MismatchedQueryAndUpdateOfCollection" )
    @Inject
    private Map<String, Parser> parsers;

    /** {@inheritDoc} */
    public Parser getParser( String id )
        throws ParserNotFoundException
    {
        Parser parser = parsers.get( id );

        if ( parser == null )
        {
            throw new ParserNotFoundException( "Cannot find parser with id = " + id );
        }

        return parser;
    }
}
