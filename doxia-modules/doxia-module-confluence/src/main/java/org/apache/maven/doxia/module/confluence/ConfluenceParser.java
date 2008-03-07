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
import java.util.Iterator;
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
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.ByLineReaderSource;
import org.apache.maven.doxia.util.ByLineSource;

/**
 * Parse the <a href="http://www.atlassian.com/software/confluence/">Confluence</a>.
 * See <a href="http://confluence.atlassian.com/display/CONF25/Confluence+Notation+Guide+Overview">
 * Confluence Notation Guide Overview</a>
 *
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="confluence"
 */
public class ConfluenceParser
    extends AbstractTextParser
{
    private BlockParser[] parsers;

    public ConfluenceParser()
    {
        BlockParser headingParser = new SectionBlockParser();
        BlockParser figureParser = new FigureBlockParser();
        BlockParser verbatimParser = new VerbatimBlockParser();
        BlockParser definitionParser = new DefinitionListBlockParser();
        BlockParser horizontalRuleParser = new HorizontalRuleBlockParser();
        BlockParser listParser = new ListBlockParser();
        BlockParser tableParser = new TableBlockParser();

        BlockParser[] subparsers = new BlockParser[] { headingParser, figureParser, listParser, tableParser };
        BlockParser paragraphParser = new ParagraphBlockParser( subparsers );

        parsers =
            new BlockParser[] { headingParser, figureParser, verbatimParser, definitionParser, horizontalRuleParser,
                listParser, tableParser, paragraphParser };
    }

    private List parse( ByLineSource source )
        throws ParseException
    {
        List blocks = new ArrayList();

        String line;

        while ( ( line = source.getNextLine() ) != null )
        {
            boolean accepted = false;

            for ( int i = 0; i < parsers.length; i++ )
            {
                BlockParser parser = parsers[i];

                if ( line.trim().length() == 0 )
                {
                    continue;
                }

                if ( parser.accept( line, source ) )
                {
                    accepted = true;

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

    /** {@inheritDoc} */
    public synchronized void parse( Reader source, Sink sink )
        throws ParseException
    {
        List blocks;

        ByLineSource src = new ByLineReaderSource( source );

        try
        {
            blocks = parse( src  );
        }
        catch ( ParseException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new ParseException( e, src.getName(), src.getLineNumber() );
        }

        sink.head();

        sink.head_();

        sink.body();

        for ( Iterator i = blocks.iterator(); i.hasNext(); )
        {
            Block block = (Block) i.next();

            block.traverse( sink );
        }

        sink.body_();
    }
}
