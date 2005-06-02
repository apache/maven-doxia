package org.codehaus.doxia.parser;

import org.codehaus.doxia.macro.manager.MacroManager;
import org.codehaus.doxia.macro.manager.MacroNotFoundException;
import org.codehaus.doxia.macro.MacroRequest;
import org.codehaus.doxia.macro.Macro;
import org.codehaus.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AbstractParser.java,v 1.3 2004/11/02 05:00:40 jvanzyl Exp $
 * 
 * @plexus.component
 */
public abstract class AbstractParser
    implements Parser
{
    /**
     * @plexus.requirement
     */
    private MacroManager macroManager;

    public MacroManager getMacroManager()
    {
        return macroManager;
    }

    // Made public right now because of the structure of the APT parser and
    // all its inner classes.
    public void executeMacro( String macroId, MacroRequest request, Sink sink )
    {
        try
        {
            System.out.println( "macroId = " + macroId );

            System.out.println( "getMacroManager() = " + getMacroManager() );

            Macro macro = getMacroManager().getMacro( macroId );

            try
            {
                macro.execute( sink, request );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        catch ( MacroNotFoundException e )
        {
            System.out.println( "The requested macro with id = " + macroId + " cannot be found." );
        }
    }
}
