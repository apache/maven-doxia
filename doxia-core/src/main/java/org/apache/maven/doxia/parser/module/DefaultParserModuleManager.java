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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * Simple implementation of the ParserModuleManager interface.
 *
 * @since 1.6
 */
@Component( role = ParserModuleManager.class )
public class DefaultParserModuleManager
    implements ParserModuleManager
{
    @Requirement( role = ParserModule.class )
    private Map<String, ParserModule> parserModules;

    private Collection<ParserModule> parserModulesValues;

    /** {@inheritDoc} */
    public Collection<ParserModule> getParserModules()
    {
        if ( parserModulesValues == null )
        {
            Map<Class<?>, ParserModule> parserModulesTmp = new LinkedHashMap<>();
            for ( ParserModule module : parserModules.values() )
            {
                parserModulesTmp.put( module.getClass(), module );
            }
            parserModulesValues = parserModulesTmp.values();
        }

        return parserModulesValues;
    }

    /** {@inheritDoc} */
    public ParserModule getParserModule( String id )
        throws ParserModuleNotFoundException
    {
        ParserModule parserModule = parserModules.get( id );

        if ( parserModule == null )
        {
            throw new ParserModuleNotFoundException( "Cannot find parser module id = " + id );
        }

        return parserModule;
    }
}
