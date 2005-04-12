package org.codehaus.doxia.site.module.manager;

import org.codehaus.doxia.site.module.SiteModule;

import java.util.Collection;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SiteModuleManager.java,v 1.1 2004/09/15 01:04:04 jvanzyl Exp $
 */
public interface SiteModuleManager
{
    String ROLE = SiteModuleManager.class.getName();

    Collection getSiteModules();

    SiteModule getSiteModule( String id )
        throws SiteModuleNotFoundException;
}
