/*
 *  Copyright 2005 Zauber <info /at/ zauber dot com dot ar>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.maven.doxia.module.twiki;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.doxia.module.common.ByLineReaderSource;
import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.module.twiki.parser.*;
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

/**
 * Parse the <a href="http://twiki.org/cgi-bin/view/TWiki/TextFormattingRules">
 * twiki file format</a>
 *
 * @author Juan F. Codagnone
 * @plexus.component role="org.apache.doxia.parser.Parser" role-hint="twiki"
 * @since Oct 31, 2005
 */
public class TWikiParser extends AbstractParser
{
    /**
     * paragraph parser. stateless
     */
    private final ParagraphBlockParser paraParser = new ParagraphBlockParser();
    /**
     * section parser. stateless
     */
    private final SectionBlockParser sectionParser = new SectionBlockParser();
    /**
     * enumeration parser. stateless
     */
    private final GenericListBlockParser listParser =
        new GenericListBlockParser();
    /**
     * Text parser. stateless
     */
    private final FormatedTextParser formatTextParser =
        new FormatedTextParser();
    /**
     * text parser. stateless
     */
    private final TextParser textParser = new TextParser();
    /**
     * hruler parser. stateless
     */
    private final HRuleBlockParser hrulerParser = new HRuleBlockParser();
    /**
     * table parser
     */
    private final TableBlockParser tableParser = new TableBlockParser();

    /**
     * list of parsers to try to apply to the toplevel
     */
    private final BlockParser [] parsers;

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
        sectionParser.setParaParser( paraParser );
        sectionParser.setHrulerParser( hrulerParser );
        listParser.setTextParser( formatTextParser );
        formatTextParser.setTextParser( textParser );
        tableParser.setTextParser( formatTextParser );

        parsers = new BlockParser[]{
            sectionParser,
            hrulerParser,
            paraParser,
        };
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
                throw new ParseException( "don't  know how to handle line: "
                    + source.getLineNumber() + ": " + line );
            }
        }

        return ret;
    }

    /**
     * @see org.apache.maven.doxia.parser.Parser#parse(java.io.Reader,
     *      org.apache.maven.doxia.sink.Sink)
     */
    public final synchronized void parse( final Reader reader, final Sink sink )
        throws ParseException
    {

        List blocks;
        final ByLineSource source = new ByLineReaderSource( reader );

        try
        {
            blocks = parse( source );
        }
        catch ( final ParseException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ParseException( e, source.getName(),
                                      source.getLineNumber() );
        }

        sink.head();
        sink.head_();
        sink.body();
        for ( Iterator it = blocks.iterator(); it.hasNext(); )
        {
            final Block block = (Block) it.next();
            
            block.traverse( sink );
        }
        sink.body_();
    }
}
