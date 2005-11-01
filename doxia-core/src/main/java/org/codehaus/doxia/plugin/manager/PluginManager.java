package org.codehaus.doxia.plugin.manager;

import org.codehaus.doxia.plugin.Plugin;

import java.util.Collection;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface PluginManager
{
    String ROLE = PluginManager.class.getName();

    Collection getPlugins();

    Plugin getPlugin( String id )
        throws PluginNotFoundException;
}
