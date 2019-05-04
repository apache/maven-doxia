package org.apache.maven.doxia.module.twiki.parser;

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.util.ByLineSource;

/**
 * Parse verbatim blocks
 *
 * @author Christian Nardi
 * @version $Id$
 * @since 1.1
 */
public class VerbatimBlockParser
    implements BlockParser
{
    /**
     * pattern to detect verbatim start tags
     */
    private static final Pattern VERBATIM_START_PATTERN = Pattern.compile( "\\s*<verbatim>" );

    private static final Pattern VERBATIM_END_PATTERN = Pattern.compile( "</verbatim>" );

    /** {@inheritDoc} */
    public final boolean accept( final String line )
    {
        return VERBATIM_START_PATTERN.matcher( line ).lookingAt();
    }

    /**
     * {@inheritDoc}
     */
    public final Block visit( final String line, final ByLineSource source )
        throws ParseException
    {
        if ( !accept( line ) )
        {
            throw new IllegalAccessError( "call accept before this ;)" );
        }

        final List<Block> lines = new ArrayList<>();
        Matcher matcher = VERBATIM_START_PATTERN.matcher( line );
        matcher.find();
        String l = line.substring( matcher.end() );

        while ( l != null )
        {
            matcher = VERBATIM_END_PATTERN.matcher( l );
            if ( matcher.find() )
            {
                lines.add( new TextBlock( l.substring( 0, matcher.start() ) + "\n" ) );
                break;
            }
            lines.add( new TextBlock( l + "\n" ) );
            l = source.getNextLine();
        }

        return new VerbatimBlock( lines.toArray( new Block[] {} ) );
    }
}
