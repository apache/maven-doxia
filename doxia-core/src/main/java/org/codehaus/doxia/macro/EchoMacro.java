package org.codehaus.doxia.macro;

import org.codehaus.doxia.sink.Sink;

import java.util.Iterator;

/**
 * @plexus.component
 *   role="org.codehaus.doxia.macro.Macro"
 *   role-hint="echo"
 */
public class EchoMacro
    extends AbstractMacro
{
    public void execute( Sink sink, MacroRequest request )
        throws Exception
    {
        sink.verbatim( true );

        sink.text( "echo\n" );

        for ( Iterator i = request.getParameters().keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();

            sink.text( key + " ---> " + request.getParameter( key ) + "\n" );
        }

        sink.verbatim_();
    }
}
