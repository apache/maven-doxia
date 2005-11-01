package org.codehaus.doxia.macro.manager;

import org.codehaus.doxia.macro.Macro;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.macro.manager.MacroManager"
 */
public class DefaultMacroManager
    implements MacroManager
{
    /**
     * @plexus.requirement
     *   role="org.codehaus.doxia.macro.Macro"
     */
    private Map macros;

    public Macro getMacro( String id )
        throws MacroNotFoundException
    {
        Macro macro = (Macro) macros.get( id );

        if ( macro == null )
        {
            throw new MacroNotFoundException( "Cannot find macro with id = " + id );
        }

        return macro;
    }
}
