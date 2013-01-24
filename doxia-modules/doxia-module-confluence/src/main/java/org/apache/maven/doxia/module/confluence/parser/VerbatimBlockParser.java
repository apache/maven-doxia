package org.apache.maven.doxia.module.confluence.parser;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.util.ByLineSource;
import org.apache.maven.doxia.parser.ParseException;

/**
 * <p>VerbatimBlockParser class.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class VerbatimBlockParser
    implements BlockParser
{
    static final String LS = System.getProperty( "line.separator" );

    /** {@inheritDoc} */
    public boolean accept( String line, ByLineSource source )
    {
        if ( line.startsWith( "{code" ) || line.startsWith( "{noformat}" ) )
        {
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        StringBuilder text = new StringBuilder();

        while ( ( line = source.getNextLine() ) != null )
        {
            if ( line.startsWith( "{code}" ) || line.startsWith( "{noformat}" ) )
            {
                break;
            }

            // TODO
            text.append( line ).append( LS );
        }

        return new VerbatimBlock( text.toString() );
    }
}
