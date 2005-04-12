package org.codehaus.doxia.macro.manager;

import org.codehaus.doxia.macro.Macro;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DefaultMacroManager.java,v 1.5 2004/11/02 05:00:40 jvanzyl Exp $
 * @plexus.component
 */
public class DefaultMacroManager
    implements MacroManager
{
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
