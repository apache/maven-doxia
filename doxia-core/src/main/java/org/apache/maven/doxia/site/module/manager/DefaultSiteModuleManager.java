package org.apache.maven.doxia.site.module.manager;

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

import org.apache.maven.doxia.site.module.SiteModule;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:DefaultSiteModuleManager.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 * @plexus.component role="org.apache.maven.doxia.site.module.manager.SiteModuleManager"
 */
public class DefaultSiteModuleManager
    implements SiteModuleManager
{
    /**
     * @plexus.requirement role="org.apache.maven.doxia.site.module.SiteModule"
     */
    private Map siteModules;

    public Collection getSiteModules()
    {
        return siteModules.values();
    }

    public SiteModule getSiteModule( String id )
        throws SiteModuleNotFoundException
    {
        SiteModule siteModule = (SiteModule) siteModules.get( id );

        if ( siteModule == null )
        {
            throw new SiteModuleNotFoundException( "Cannot find site module id = " + id );
        }

        return siteModule;
    }
}
