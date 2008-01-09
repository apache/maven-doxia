package org.apache.maven.doxia.module.xwiki;

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

import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.module.xwiki.parser.FigureBlockParser;
import org.apache.maven.doxia.module.xwiki.parser.ParagraphBlockParser;
import org.apache.maven.doxia.module.xwiki.parser.SectionBlockParser;
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
 * Parse <a href="http://xwiki.org">XWiki</a>.
 * See <a href="http://platform.xwiki.org/xwiki/bin/view/Main/XWikiSyntax">XWiki Syntax Guide Overview</a>.
 *
 * @version $Id: $
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="xwiki"
 * @since 1.0
 */
public class XWikiParser
    extends AbstractTextParser
{
    private BlockParser[] parsers;

    public XWikiParser()
    {
        BlockParser headingParser = new SectionBlockParser();
        BlockParser figureParser = new FigureBlockParser();
//        BlockParser verbatimParser = new VerbatimBlockParser();
//        BlockParser definitionParser = new DefinitionListBlockParser();
//        BlockParser horizontalRuleParser = new HorizontalRuleBlockParser();
//        BlockParser listParser = new ListBlockParser();
//        BlockParser tableParser = new TableBlockParser();

        BlockParser[] subparsers = new BlockParser[]{headingParser, figureParser/*, listParser, tableParser*/};
        BlockParser paragraphParser = new ParagraphBlockParser( subparsers );

        parsers = new BlockParser[]{headingParser, figureParser/*, verbatimParser, definitionParser, horizontalRuleParser,
                listParser, tableParser*/, paragraphParser};
    }

    public List parse( ByLineSource source )
        throws ParseException
    {
        List blocks = new ArrayList();

        String line;

        while ( ( line = source.getNextLine() ) != null )
        {
            for ( int i = 0; i < parsers.length; i++ )
            {
                BlockParser parser = parsers[i];

                if ( line.trim().length() == 0 )
                {
                    continue;
                }

                if ( parser.accept( line, source ) )
                {
                    blocks.add( parser.visit( line, source ) );

                    break;
                }
            }
        }

        return blocks;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void parse( Reader reader, Sink sink )
        throws ParseException
    {
        List blocks;

        ByLineSource source = new ByLineReaderSource( reader );

        try
        {
            blocks = parse( source );
        }
        catch ( ParseException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new ParseException( e, source.getName(), source.getLineNumber() );
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
