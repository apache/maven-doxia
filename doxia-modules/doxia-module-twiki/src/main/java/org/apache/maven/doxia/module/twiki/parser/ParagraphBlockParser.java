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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.util.ByLineSource;
import org.apache.maven.doxia.parser.ParseException;

/**
 * Parse paragraphs.
 *
 * @author Juan F. Codagnone
 * @version $Id$
 */
public class ParagraphBlockParser
    implements BlockParser
{
    /**
     * pattern used to dectect end of paragraph
     */
    private final Pattern paragraphSeparator = Pattern.compile( "^(\\s*)$" );

    /**
     * {@link SectionBlockParser} to use. injected
     */
    private SectionBlockParser sectionParser;

    /**
     * {@link ListBlockParser} to use. injected
     */
    private GenericListBlockParser listParser;

    /**
     * {@link FormatedTextParser} to use. injected
     */
    private FormatedTextParser textParser;

    /**
     * {@link HRuleBlockParser} to use. injected
     */
    private HRuleBlockParser hrulerParser;

    /**
     * {@link TableBlockParser} to use. injected
     */
    private TableBlockParser tableBlockParser;

    /**
     *  {@link TableBlockParser} to use. injected
     */
    private VerbatimBlockParser verbatimParser;

    /**
     * no operation block
     */
    private static final NopBlock NOP = new NopBlock();

    /** {@inheritDoc} */
    public final boolean accept( final String line )
    {
        return !sectionParser.accept( line ) && !hrulerParser.accept( line ) && !verbatimParser.accept( line );
    }

    /**
     * {@inheritDoc}
     */
    public final Block visit( final String line, final ByLineSource source )
        throws ParseException
    {
        StringBuilder sb = new StringBuilder();
        List<Block> childs = new ArrayList<>();

        boolean sawText = false;

        /*
        * 1. Skip begininig new lines
        * 2. Get the text, while \n\n is not found
        */
        boolean pre = false;
        String l = line;
        do
        {
            Matcher m = paragraphSeparator.matcher( l );

            if ( m.lookingAt() )
            {
                if ( sawText )
                {
                    break;
                }
            }
            else
            {
                sawText = true;

                /* be able to parse lists / enumerations */
                if ( listParser.accept( l ) )
                {
                    if ( sb.length() != 0 )
                    {
                        childs.addAll( Arrays.asList( textParser.parse( sb.toString().trim() ) ) );
                        sb = new StringBuilder();
                    }
                    childs.add( listParser.visit( l, source ) );
                }
                else if ( tableBlockParser.accept( l ) )
                {
                    childs.add( tableBlockParser.visit( l, source ) );
                }
                else
                {
                    sb.append( l );
                    // specific
                    if ( l.contains( "<pre>" ) )
                    {
                        pre = true;
                    }
                    if ( l.contains( "</pre>" ) )
                    {
                        pre = false;
                    }

                    if ( !pre )
                    {
                        sb.append( " " );
                    }
                    else
                    {
                        // TODO use EOL
                        sb.append( "\n" );
                    }
                }
            }
            l = source.getNextLine();
        }
        while ( l != null && accept( l ) );

        if ( line != null )
        {
            source.ungetLine();
        }

        if ( sb.length() != 0 )
        {
            childs.addAll( Arrays.asList( textParser.parse( sb.toString().trim() ) ) );
            sb = new StringBuilder();
        }

        if ( childs.size() == 0 )
        {
            return NOP;
        }

        return new ParagraphBlock( childs.toArray( new Block[] {} ) );
    }

    /**
     * Sets the sectionParser.
     *
     * @param aSectionParser <code>SectionBlockParser</code> with the sectionParser.
     */
    public final void setSectionParser( final SectionBlockParser aSectionParser )
    {
        if ( aSectionParser == null )
        {
            throw new IllegalArgumentException( "arg can't be null" );
        }
        this.sectionParser = aSectionParser;
    }

    /**
     * Sets the listParser.
     *
     * @param aListParser <code>ListBlockParser</code> with the listParser.
     */
    public final void setListParser( final GenericListBlockParser aListParser )
    {
        if ( aListParser == null )
        {
            throw new IllegalArgumentException( "arg can't be null" );
        }

        this.listParser = aListParser;
    }

    /**
     * Sets the formatTextParser.
     *
     * @param aTextParser <code>FormatedTextParser</code>
     *                   with the formatTextParser.
     */
    public final void setTextParser( final FormatedTextParser aTextParser )
    {
        if ( aTextParser == null )
        {
            throw new IllegalArgumentException( "arg can't be null" );
        }
        this.textParser = aTextParser;
    }

    /**
     * Sets the hrulerParser.
     *
     * @param aHrulerParser <code>HRuleBlockParser</code> with the hrulerParser.
     */
    public final void setHrulerParser( final HRuleBlockParser aHrulerParser )
    {
        if ( aHrulerParser == null )
        {
            throw new IllegalArgumentException( "arg can't be null" );
        }

        this.hrulerParser = aHrulerParser;
    }

    /**
     * <p>Setter for the field <code>tableBlockParser</code>.</p>
     *
     * @param aTableBlockParser Table parser to use
     */
    public final void setTableBlockParser( final TableBlockParser aTableBlockParser )
    {
        if ( aTableBlockParser == null )
        {
            throw new IllegalArgumentException( "arg can't be null" );
        }

        this.tableBlockParser = aTableBlockParser;
    }

    /**
     * Sets the verbatimParser.
     *
     * @param aVerbatimParser <code>VerbatimBlockParser</code> with the verbatimParser.
     * @since 1.1
     */
    public final void setVerbatimParser( final VerbatimBlockParser aVerbatimParser )
    {
        if ( aVerbatimParser == null )
        {
            throw new IllegalArgumentException( "arg can't be null" );
        }
        this.verbatimParser = aVerbatimParser;
    }
}
