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

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.util.ByLineSource;

/**
 * <p>DefinitionListBlockParser class.</p>
 *
 * @author Dave Syer
 * @version $Id$
 * @since 1.1
 */
public class DefinitionListBlockParser
    implements BlockParser
{
    static final String LS = System.getProperty( "line.separator" );

    /** {@inheritDoc} */
    public boolean accept( String line, ByLineSource source )
    {
        return ( line.startsWith( "{note" ) || line.startsWith( "{tip" )
            || line.startsWith( "{info" ) || line.startsWith( "{quote" ) );
    }

    /** {@inheritDoc} */
    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        StringBuilder text = new StringBuilder();
        StringBuilder title = new StringBuilder();

        int index = line.indexOf( "title=" );

        if ( index >= 0 )
        {
            line = line.substring( index + 6 );

            while ( !( line.contains( "}" ) ) && line != null )
            {
                append( title, line );
                line = source.getNextLine();
            }

            if ( line != null )
            {
                append( title, line.substring( 0, line.indexOf( "}" ) ) );
            }
        }

        while ( ( line = source.getNextLine() ) != null )
        {
            if ( line.startsWith( "{note" ) || line.startsWith( "{tip" )
                || line.startsWith( "{info" ) || line.startsWith( "{quote" ) )
            {
                break;
            }

            append( text, line );
        }

        return new DefinitionListBlock( title.toString(), text.toString() );
    }

    private void append( StringBuilder title, String line )
    {
        if ( title.length() > 0 )
        {
            title.append( " " );
        }

        title.append( line.trim() );
    }
}
