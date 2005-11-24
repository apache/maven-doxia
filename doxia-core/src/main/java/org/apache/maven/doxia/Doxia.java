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
import org.apache.maven.doxia.parser.manager.ParserManager;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.plugin.manager.PluginManager;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.manager.SinkNotFoundException;

import java.io.Reader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:Doxia.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public interface Doxia
{
    String ROLE = Doxia.class.getName();

    void parse( Reader source, String parserId, String sinkId )
        throws ParserNotFoundException, SinkNotFoundException, ParseException;

    void parse( Reader source, String parserId, Sink sink )
        throws ParserNotFoundException, ParseException;

    // SinkManager getSinkManager();

    ParserManager getParserManager();

    MacroManager getMacroManager();

    PluginManager getPluginManager();
}
