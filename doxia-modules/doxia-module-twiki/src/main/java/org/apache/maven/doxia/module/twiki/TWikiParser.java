package org.apache.maven.doxia.module.twiki;

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

import org.apache.maven.doxia.module.twiki.parser.Block;
import org.apache.maven.doxia.module.twiki.parser.BlockParser;
import org.apache.maven.doxia.module.twiki.parser.FormatedTextParser;
import org.apache.maven.doxia.module.twiki.parser.GenericListBlockParser;
import org.apache.maven.doxia.module.twiki.parser.HRuleBlockParser;
import org.apache.maven.doxia.module.twiki.parser.ParagraphBlockParser;
import org.apache.maven.doxia.module.twiki.parser.SectionBlock;
import org.apache.maven.doxia.module.twiki.parser.SectionBlockParser;
import org.apache.maven.doxia.module.twiki.parser.TableBlockParser;
import org.apache.maven.doxia.module.twiki.parser.TextParser;
import org.apache.maven.doxia.module.twiki.parser.VerbatimBlockParser;
import org.apache.maven.doxia.module.twiki.parser.XHTMLWikiWordLinkResolver;
import org.apache.maven.doxia.parser.AbstractTextParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.ByLineReaderSource;
import org.apache.maven.doxia.util.ByLineSource;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parse the <a href="http://twiki.org/cgi-bin/view/TWiki/TextFormattingRules">
 * twiki file format</a>
 *
 * @author Juan F. Codagnone
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="twiki"
 */
public class TWikiParser
    extends AbstractTextParser
{
    private static final int EXTENSION_LENGTH = 6;

    /** paragraph parser. */
    private final ParagraphBlockParser paraParser = new ParagraphBlockParser();

    /** section parser. */
    private final SectionBlockParser sectionParser = new SectionBlockParser();

    /** enumeration parser. */
    private final GenericListBlockParser listParser = new GenericListBlockParser();

    /** Text parser. */
    private final FormatedTextParser formatTextParser = new FormatedTextParser();

    /**
     * text parser.
     * This only works for xhtml output, but there is no way
     * of transforming a wikiWord in another context.
     */
    private final TextParser textParser = new TextParser( new XHTMLWikiWordLinkResolver() );

    /** hruler parser. */
    private final HRuleBlockParser hrulerParser = new HRuleBlockParser();

    /** table parser. */
    private final TableBlockParser tableParser = new TableBlockParser();

    /** verbatim parser. */
    private final VerbatimBlockParser verbatimParser = new VerbatimBlockParser();

    /** list of parsers to try to apply to the toplevel */
    private final BlockParser[] parsers;

    /**
     * Creates the TWikiParser.
     */
    public TWikiParser()
    {
        paraParser.setSectionParser( sectionParser );
        paraParser.setListParser( listParser );
        paraParser.setTextParser( formatTextParser );
        paraParser.setHrulerParser( hrulerParser );
        paraParser.setTableBlockParser( tableParser );
        paraParser.setVerbatimParser( verbatimParser );
        sectionParser.setParaParser( paraParser );
        sectionParser.setHrulerParser( hrulerParser );
        sectionParser.setVerbatimBlockParser( verbatimParser );
        listParser.setTextParser( formatTextParser );
        formatTextParser.setTextParser( textParser );
        tableParser.setTextParser( formatTextParser );

        parsers = new BlockParser[] { sectionParser, hrulerParser, verbatimParser, paraParser };
    }

    /**
     * @param source source to parse
     * @return the blocks that represent source
     * @throws ParseException on error
     */
    public final List parse( final ByLineSource source )
        throws ParseException
    {
        final List ret = new ArrayList();

        String line;
        while ( ( line = source.getNextLine() ) != null )
        {
            boolean accepted = false;

            for ( int i = 0; i < parsers.length; i++ )
            {
                final BlockParser parser = parsers[i];

                if ( parser.accept( line ) )
                {
                    accepted = true;
                    ret.add( parser.visit( line, source ) );
                    break;
                }
            }
            if ( !accepted )
            {
                throw new ParseException( "Line number not handle : " + source.getLineNumber() + ": " + line );
            }
        }

        return ret;
    }

    /** {@inheritDoc} */
    public final synchronized void parse( final Reader source, final Sink sink )
        throws ParseException
    {

        List blocks;
        final ByLineSource src = new ByLineReaderSource( source );

        try
        {
            blocks = parse( src );
        }
        catch ( final Exception e )
        {
            // TODO handle column number
            throw new ParseException( e, src.getName(), src.getLineNumber(), -1 );
        }

        sink.head();

        final String title = getTitle( blocks, src );
        if ( title != null )
        {
            sink.title();
            sink.text( title );
            sink.title_();
        }

        sink.head_();
        sink.body();
        for ( Iterator it = blocks.iterator(); it.hasNext(); )
        {
            final Block block = (Block) it.next();

            block.traverse( sink );
        }
        sink.body_();
    }

    /**
     * Guess a title for the page. It uses the first section that it finds.
     * If it doesn't find any section tries to get it from
     * {@link ByLineReaderSource#getName()}
     *
     * @param blocks blocks to parse
     * @param source source to parse
     * @return a title for a page
     */
    public final String getTitle( final List blocks, final ByLineSource source )
    {
        String title = null;

        for ( Iterator it = blocks.iterator(); title == null && it.hasNext(); )
        {
            final Block block = (Block) it.next();

            if ( block instanceof SectionBlock )
            {
                final SectionBlock sectionBlock = (SectionBlock) block;
                title = sectionBlock.getTitle();
            }
        }

        if ( title == null )
        {
            String name = source.getName();
            if ( name != null )
            {
                name = name.trim();

                if ( name.length() != 0 )
                {
                    if ( name.endsWith( ".twiki" ) )
                    {
                        title = name.substring( 0, name.length() - EXTENSION_LENGTH );
                    }
                    else
                    {
                        title = name;
                    }
                }
            }
        }

        return title;
    }
}
