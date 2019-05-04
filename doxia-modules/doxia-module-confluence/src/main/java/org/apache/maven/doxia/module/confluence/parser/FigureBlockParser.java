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
 * <p>FigureBlockParser class.</p>
 *
 * @version $Id$
 * @since 1.1
 */
public class FigureBlockParser
    implements BlockParser
{
    /** {@inheritDoc} */
    public boolean accept( String line, ByLineSource source )
    {
        return line.startsWith( "!" ) && line.lastIndexOf( "!" ) > 1;
    }

    /** {@inheritDoc} */
    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        String image = line.substring( 1, line.lastIndexOf( "!" ) );
        if ( image.contains( "|" ) )
        {
            // DOXIA-303: handle figure attributes
            image = image.substring( 0, image.indexOf( "|" ) );
        }

        line = line.substring( line.lastIndexOf( "!" ) + 1 ).trim();

        if ( line.startsWith( "\\\\" ) )
        {
            // ignore linebreak at start of caption
            line = line.substring( 2 );
        }

        String caption = line + appendUntilEmptyLine( source );

        if ( caption.trim().length() > 0 )
        {
            return new FigureBlock( image, caption );
        }

        return new FigureBlock( image );
    }

    /**
     * Slurp lines from the source starting with the given line appending them together into a StringBuilder until an
     * empty line is reached, and while the source contains more lines.
     *
     * @param source the source to read new lines from
     * @return a StringBuilder appended with lines
     * @throws ParseException
     */
    private String appendUntilEmptyLine( ByLineSource source )
        throws ParseException
    {
        StringBuilder text = new StringBuilder();

        String line;

        while ( ( line = source.getNextLine() ) != null )
        {

            if ( line.trim().length() == 0 )
            {
                break;
            }

            if ( text.length() == 0 )
            {
                text.append( line.trim() );
            }
            else
            {
                text.append( " " ).append( line.trim() );
            }

        }

        return text.toString();
    }
}
