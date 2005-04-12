package org.codehaus.doxia;

import org.codehaus.doxia.macro.manager.MacroManager;
import org.codehaus.doxia.plugin.manager.PluginManager;
import org.codehaus.doxia.parser.manager.ParserManager;
import org.codehaus.doxia.parser.manager.ParserNotFoundException;
import org.codehaus.doxia.parser.Parser;
import org.codehaus.doxia.parser.ParseException;
import org.codehaus.doxia.sink.manager.SinkManager;
import org.codehaus.doxia.sink.manager.SinkNotFoundException;
import org.codehaus.doxia.sink.Sink;

import java.io.Reader;

/**
 * @plexus.component
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DefaultDoxia.java,v 1.7 2004/11/02 05:00:40 jvanzyl Exp $
 */
public class DefaultDoxia
    implements Doxia
{
    //protected SinkManager sinkManager;

    protected ParserManager parserManager;

    protected MacroManager macroManager;

    protected PluginManager pluginManager;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void parse( Reader source, String parserId, String sinkId )
        throws ParserNotFoundException, SinkNotFoundException, ParseException
    {
        //Parser parser = parserManager.getParser( parserId );

        //Sink sink = sinkManager.getSink( sinkId );

        //parser.parse( source, sink );
    }

    // ----------------------------------------------------------------------
    // This remains because the sinks are not threadsafe which they probably
    // should be. In some places a constructor is used to initialize a sink
    // which can probably be done away with.
    // ----------------------------------------------------------------------

    public void parse( Reader source, String parserId, Sink sink )
        throws ParserNotFoundException, ParseException
    {
        Parser parser = parserManager.getParser( parserId );

        parser.parse( source, sink );
    }

    // ----------------------------------------------------------------------
    // Managers:
    //
    // Not sure if access to the managers will be required but until I flesh
    // out the functionality I'm just not certain.
    // ----------------------------------------------------------------------

    /*
    public SinkManager getSinkManager()
    {
        return sinkManager;
    }
    */

    public ParserManager getParserManager()
    {
        return parserManager;
    }

    public MacroManager getMacroManager()
    {
        return macroManager;
    }

    public PluginManager getPluginManager()
    {
        return pluginManager;
    }
}
