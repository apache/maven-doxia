package org.apache.maven.doxia.parser;

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

import java.io.File;

import org.apache.maven.doxia.macro.Macro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.macro.manager.MacroManager;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:AbstractParser.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 * @plexus.component
 */
public abstract class AbstractParser
    implements Parser
{
    protected boolean secondParsing = false;

    /**
     * @plexus.requirement
     */
    private MacroManager macroManager;

    // Made public right now because of the structure of the APT parser and
    // all its inner classes.
    public void executeMacro( String macroId, MacroRequest request, Sink sink )
        throws MacroExecutionException, MacroNotFoundException
    {
        Macro macro = macroManager.getMacro( macroId );

        macro.execute( sink, request );
    }

    protected File getBasedir()
    {
        // TODO: This is baaad, it should come in with the request.

        String basedir = System.getProperty( "basedir" );

        if ( basedir != null )
        {
            return new File( basedir );
        }

        return new File( new File( "" ).getAbsolutePath() );
    }

    /**
     * Set <code>secondParsing</code> to true, if we need a second parsing
     *
     * @param secondParsing
     */
    public void setSecondParsing( boolean secondParsing )
    {
        this.secondParsing = secondParsing;
    }
}
