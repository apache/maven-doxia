package org.apache.maven.doxia.module.confluence;

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

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.module.confluence.parser.DefinitionListBlockParser;
import org.apache.maven.doxia.module.confluence.parser.FigureBlockParser;
import org.apache.maven.doxia.module.confluence.parser.HorizontalRuleBlockParser;
import org.apache.maven.doxia.module.confluence.parser.ParagraphBlockParser;
import org.apache.maven.doxia.module.confluence.parser.SectionBlockParser;
import org.apache.maven.doxia.module.confluence.parser.VerbatimBlockParser;
import org.apache.maven.doxia.module.confluence.parser.list.ListBlockParser;
import org.apache.maven.doxia.module.confluence.parser.table.TableBlockParser;
import org.apache.maven.doxia.parser.AbstractTextParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.ByLineReaderSource;
import org.apache.maven.doxia.util.ByLineSource;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Parse the <a href="http://www.atlassian.com/software/confluence/">Confluence</a>.
 * See <a href="http://confluence.atlassian.com/display/CONF25/Confluence+Notation+Guide+Overview">
 * Confluence Notation Guide Overview</a>
 *
 * @version $Id$
 * @since 1.0
 */
@Component( role = Parser.class, hint = "confluence" )
public class ConfluenceParser
    extends AbstractTextParser
{
    private BlockParser[] parsers;

    /**
     * <p>Constructor for ConfluenceParser.</p>
     */
    public ConfluenceParser()
    {
        init();
    }

    private List<Block> parse( ByLineSource source )
        throws ParseException
    {
        init();

        List<Block> blocks = new ArrayList<>();

        String line;

        while ( ( line = source.getNextLine() ) != null )
        {
            //boolean accepted = false;

            for ( BlockParser parser : parsers )
            {
                if ( line.trim().length() == 0 )
                {
                    continue;
                }

                if ( parser.accept( line, source ) )
                {
                    //accepted = true;

                    blocks.add( parser.visit( line, source ) );

                    break;
                }
            }

/*
            if ( !accepted )
            {
                throw new ParseException( "don't know how to handle line: " + source.getLineNumber() + ": " + line );
            }
*/
        }

        return blocks;
    }

    @Override
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        parse( source, sink, "" );
    }

    @Override
    public synchronized void parse( Reader source, Sink sink, String reference )
        throws ParseException
    {
        ByLineSource src = new ByLineReaderSource( source, reference );

        try
        {
            List<Block> blocks = parse( src );

            sink.head();

            sink.head_();

            sink.body();

            for ( Block block : blocks )
            {
                block.traverse( sink );
            }

            sink.body_();
        }
        catch ( Exception e )
        {
            // TODO handle column number
            throw new ParseException( e, src.getName(), src.getLineNumber(), -1 );
        }
        finally
        {
            setSecondParsing( false );
            init();
        }
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        BlockParser headingParser = new SectionBlockParser();
        BlockParser figureParser = new FigureBlockParser();
        BlockParser verbatimParser = new VerbatimBlockParser();
        BlockParser definitionParser = new DefinitionListBlockParser();
        BlockParser horizontalRuleParser = new HorizontalRuleBlockParser();
        BlockParser listParser = new ListBlockParser();
        BlockParser tableParser = new TableBlockParser();

        BlockParser[] subparsers =
                new BlockParser[] { headingParser, figureParser, listParser, tableParser, verbatimParser };
        BlockParser paragraphParser = new ParagraphBlockParser( subparsers );

        this.parsers =
            new BlockParser[] { headingParser, figureParser, verbatimParser, definitionParser, horizontalRuleParser,
                listParser, tableParser, paragraphParser };
    }
}
