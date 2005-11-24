package org.apache.maven.doxia;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.doxia.macro.manager.MacroManager;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.manager.ParserManager;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.plugin.manager.PluginManager;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.manager.SinkNotFoundException;

import java.io.Reader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.doxia.Doxia"
 */
public class DefaultDoxia
    implements Doxia
{
    // protected SinkManager sinkManager;

    /**
     * @plexus.requirement
     */
    protected ParserManager parserManager;

    /**
     * @plexus.requirement
     */
    protected MacroManager macroManager;

    /**
     * @plexus.requirement
     */
    protected PluginManager pluginManager;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void parse( Reader source, String parserId, String sinkId )
        throws ParserNotFoundException, SinkNotFoundException, ParseException
    {
        // Parser parser = parserManager.getParser( parserId );

        // Sink sink = sinkManager.getSink( sinkId );

        // parser.parse( source, sink );
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
     * public SinkManager getSinkManager() { return sinkManager; }
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
