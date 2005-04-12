package org.codehaus.doxia.macro.manager;

import org.codehaus.doxia.macro.Macro;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: MacroManager.java,v 1.2 2004/09/15 19:32:42 jvanzyl Exp $
 */
public interface MacroManager
{
    String ROLE = MacroManager.class.getName();

    Macro getMacro( String id )
        throws MacroNotFoundException;

}
