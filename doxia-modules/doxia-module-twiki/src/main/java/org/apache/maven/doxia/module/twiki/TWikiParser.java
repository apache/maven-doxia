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
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.ByLineReaderSource;
import org.apache.maven.doxia.util.ByLineSource;
import org.codehaus.plexus.component.annotations.Component;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parse the <a href="http://twiki.org/cgi-bin/view/TWiki/TextFormattingRules">
 * twiki file format</a>
 *
 * @author Juan F. Codagnone
 * @version $Id$
 * @since 1.0
 */
@Component( role = Parser.class, hint = "twiki" )
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
    private BlockParser[] parsers;

    /**
     * Creates the TWikiParser.
     */
    public TWikiParser()
    {
        init();
    }

    /**
     * <p>parse.</p>
     *
     * @param source source to parse.
     * @return the blocks that represent source.
     * @throws org.apache.maven.doxia.parser.ParseException on error.
     */
    public final List<Block> parse( final ByLineSource source )
        throws ParseException
    {
        final List<Block> ret = new ArrayList<>();

        String line;
        while ( ( line = source.getNextLine() ) != null )
        {
            boolean accepted = false;

            for ( BlockParser parser : parsers )
            {
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

    @Override
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        parse( source, sink, "" );
    }
    
    @Override
    public final synchronized void parse( final Reader source, final Sink sink, String reference )
        throws ParseException
    {
        init();

        List<Block> blocks;
        final ByLineSource src = new ByLineReaderSource( source, reference );

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
        for ( Block block : blocks )
        {
            block.traverse( sink );
        }
        sink.body_();
        sink.flush();
        sink.close();

        setSecondParsing( false );
        init();
    }

    /**
     * Guess a title for the page. It uses the first section that it finds.
     * If it doesn't find any section tries to get it from
     * {@link ByLineReaderSource#getName()}
     *
     * @param blocks blocks to parse
     * @param source source to parse
     * @return a title for a page
     * @since 1.1
     */
    public final String getTitle( final List<Block> blocks, final ByLineSource source )
    {
        String title = null;

        for ( Block block : blocks )
        {
            if ( block instanceof SectionBlock )
            {
                final SectionBlock sectionBlock = (SectionBlock) block;
                title = sectionBlock.getTitle();
                break;
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

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

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

        this.parsers = new BlockParser[] { sectionParser, hrulerParser, verbatimParser, paraParser };
    }
}
