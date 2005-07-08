package org.codehaus.doxia.macro.manager;

import org.codehaus.doxia.macro.Macro;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface MacroManager
{
    String ROLE = MacroManager.class.getName();

    Macro getMacro( String id )
        throws MacroNotFoundException;

}
