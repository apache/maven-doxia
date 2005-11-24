package org.apache.maven.doxia.macro;

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

import org.apache.maven.doxia.sink.Sink;

import java.util.Iterator;

/**
 * @plexus.component role="org.apache.maven.doxia.macro.Macro"
 * role-hint="echo"
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
