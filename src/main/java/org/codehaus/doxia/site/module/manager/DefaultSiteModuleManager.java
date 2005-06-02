package org.codehaus.doxia.site.module.manager;

import org.codehaus.doxia.site.module.SiteModule;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DefaultSiteModuleManager.java,v 1.3 2004/11/02 05:00:40 jvanzyl Exp $
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.site.module.manager.SiteModuleManager"
 */
public class DefaultSiteModuleManager
    implements SiteModuleManager
{
    /**
     * @plexus.requirement
     *   role="org.codehaus.doxia.site.module.SiteModule"
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
