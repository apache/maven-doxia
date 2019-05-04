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
 * <p>ParagraphBlockParser class.</p>
 *
 * @version $Id$
 */
public class ParagraphBlockParser
    implements BlockParser
{
    private BlockParser[] parsers;

    /**
     * <p>Constructor for ParagraphBlockParser.</p>
     *
     * @param parsers the parsers.
     */
    public ParagraphBlockParser( BlockParser[] parsers )
    {
        super();
        this.parsers = parsers;
    }

    /** {@inheritDoc} */
    public boolean accept( String line, ByLineSource source )
    {
        return true;
    }

    /**
     * Visit the Block.
     *
     * @param line the line to visit.
     * @param source the source.
     * @param generateParagraphTags whether to generate a paragraph.
     * @return the visited Block.
     *
     * @throws org.apache.maven.doxia.parser.ParseException if any
     */
    public Block visit( String line, ByLineSource source, boolean generateParagraphTags )
            throws ParseException
    {
        if ( generateParagraphTags )
        {
            return this.visit( line, source );
        }
        else
        {
            ChildBlocksBuilder builder = new ChildBlocksBuilder( line );
            return new ParagraphBlock( builder.getBlocks(), generateParagraphTags );
        }
    }

    /** {@inheritDoc} */
    public Block visit( String line, ByLineSource source )
        throws ParseException
    {

        ChildBlocksBuilder builder = new ChildBlocksBuilder( appendUntilEmptyLine( line, source ) );
        return new ParagraphBlock( builder.getBlocks() );
    }

    /**
     * Slurp lines from the source starting with the given line appending them together into a StringBuilder until an
     * empty line is reached, and while the source contains more lines. The result can be passed to the
     * {@link #getBlocks(String)} method.
     *
     * @param line the first line
     * @param source the source to read new lines from
     * @return a StringBuilder appended with lines
     * @throws ParseException
     */
    private String appendUntilEmptyLine( String line, ByLineSource source )
        throws ParseException
    {
        StringBuilder text = new StringBuilder();

        do
        {

            if ( line.trim().length() == 0 )
            {
                break;
            }

            boolean accepted = false;
            for ( BlockParser parser : parsers )
            {
                if ( parser.accept( line, source ) )
                {
                    accepted = true;
                    break;
                }
            }
            if ( accepted )
            {
                // Slightly fragile - if any of the parsers need to do this in order to decide whether to accept a line,
                // then it will barf because of the contract of ByLineSource
                source.ungetLine();
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
        while ( ( line = source.getNextLine() ) != null );

        return text.toString();
    }

}
