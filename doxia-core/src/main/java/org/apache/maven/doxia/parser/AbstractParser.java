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

import org.apache.maven.doxia.macro.Macro;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.macro.manager.MacroManager;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
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
//            System.out.println( "macroId = " + macroId );
//
//            System.out.println( "getMacroManager() = " + getMacroManager() );

            Macro macro = getMacroManager().getMacro( macroId );

            try
            {
                macro.execute( sink, request );
            }
            catch ( Exception e )
            {
                // TODO: this is not right
                e.printStackTrace();
            }
        }
        catch ( MacroNotFoundException e )
        {
            // TODO: this should probably be thrown out somewhere
            System.out.println( "The requested macro with id = " + macroId + " cannot be found." );
        }
    }
}
