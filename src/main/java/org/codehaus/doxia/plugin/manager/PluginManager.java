package org.codehaus.doxia.plugin.manager;

import org.codehaus.doxia.plugin.Plugin;

import java.util.Collection;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: PluginManager.java,v 1.2 2004/09/15 17:14:29 jvanzyl Exp $
 */
public interface PluginManager
{
    String ROLE = PluginManager.class.getName();

    Collection getPlugins();

    Plugin getPlugin( String id )
        throws PluginNotFoundException;
}
