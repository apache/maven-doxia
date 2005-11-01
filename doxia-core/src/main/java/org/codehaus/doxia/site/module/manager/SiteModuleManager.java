package org.codehaus.doxia.site.module.manager;

import org.codehaus.doxia.site.module.SiteModule;

import java.util.Collection;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface SiteModuleManager
{
    String ROLE = SiteModuleManager.class.getName();

    Collection getSiteModules();

    SiteModule getSiteModule( String id )
        throws SiteModuleNotFoundException;
}
