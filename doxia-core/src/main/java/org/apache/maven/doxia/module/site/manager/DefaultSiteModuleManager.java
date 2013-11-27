package org.apache.maven.doxia.module.site.manager;

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

import org.apache.maven.doxia.module.site.SiteModule;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * Simple implementation of the SiteModuleManager interface.
 *
 * @author Jason van Zyl
 * @version $Id$
 * @since 1.0
 * @deprecated replaced by DefaultParserModuleManager
 */
@Component( role = SiteModuleManager.class )
public class DefaultSiteModuleManager
    implements SiteModuleManager
{
    @Requirement( role = SiteModule.class )
    private Map<String, SiteModule> siteModules;

    private Collection<SiteModule> siteModulesValues;

    /** {@inheritDoc} */
    public Collection<SiteModule> getSiteModules()
    {
        if ( siteModulesValues == null )
        {
            Map<Class<?>, SiteModule> siteModulesTmp = new LinkedHashMap<Class<?>, SiteModule>();
            for ( SiteModule module : siteModules.values() )
            {
                siteModulesTmp.put( module.getClass(), module );
            }
            siteModulesValues = siteModulesTmp.values();
        }

        return siteModulesValues;
    }

    /** {@inheritDoc} */
    public SiteModule getSiteModule( String id )
        throws SiteModuleNotFoundException
    {
        SiteModule siteModule = siteModules.get( id );

        if ( siteModule == null )
        {
            throw new SiteModuleNotFoundException( "Cannot find site module id = " + id );
        }

        return siteModule;
    }
}
