package org.codehaus.doxia;

import org.codehaus.doxia.sink.manager.SinkManager;
import org.codehaus.doxia.sink.manager.SinkNotFoundException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.parser.manager.ParserManager;
import org.codehaus.doxia.parser.manager.ParserNotFoundException;
import org.codehaus.doxia.parser.ParseException;
import org.codehaus.doxia.macro.manager.MacroManager;
import org.codehaus.doxia.plugin.manager.PluginManager;

import java.io.Reader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Doxia.java,v 1.4 2004/11/02 03:40:48 jvanzyl Exp $
 */
public interface Doxia
{
    String ROLE = Doxia.class.getName();

    void parse( Reader source, String parserId, String sinkId )
        throws ParserNotFoundException, SinkNotFoundException, ParseException;

    void parse( Reader source, String parserId, Sink sink )
        throws ParserNotFoundException, ParseException;

    //SinkManager getSinkManager();

    ParserManager getParserManager();

    MacroManager getMacroManager();

    PluginManager getPluginManager();
}
