package org.codehaus.doxia.plugin.manager;

import org.codehaus.doxia.plugin.Plugin;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.plugin.manager.PluginManager"
 */
public class DefaultPluginManager
    implements PluginManager
{
    private Map plugins;

        public Collection getPlugins()
        {
            return plugins.values();
        }

        public Plugin getPlugin( String id )
            throws PluginNotFoundException
        {
            Plugin plugin = (Plugin) plugins.get( id );

            if ( plugin == null )
            {
                throw new PluginNotFoundException( "Cannot find plugin with id = " + id );
            }

            return plugin;
        }
            
}
