package org.apache.maven.doxia.sink.manager;

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

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:DefaultSinkManager.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 * @plexus.component role="org.apache.maven.doxia.sink.manager.SinkManager"
 */
public class DefaultSinkManager
    implements SinkManager
{
    /**
     * @plexus.requirement role="org.apache.maven.doxia.sink.Sink"
     */
    private Map sinks;

    public Collection getSinks()
    {
        return sinks.values();
    }

    public Sink getSink( String id )
        throws SinkNotFoundException
    {
        Sink sink = (Sink) sinks.get( id );

        if ( sink == null )
        {
            throw new SinkNotFoundException( "Cannot find sink with id = " + id );
        }

        return sink;
    }
}
