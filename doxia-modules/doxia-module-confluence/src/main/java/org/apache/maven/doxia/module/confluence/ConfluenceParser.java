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

import org.apache.maven.doxia.module.common.ByLineReaderSource;
import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.module.confluence.parser.SectionBlockParser;
import org.apache.maven.doxia.module.confluence.parser.ParagraphBlockParser;
import org.apache.maven.doxia.module.confluence.parser.VerbatimBlockParser;
import org.apache.maven.doxia.module.confluence.parser.HorizontalRuleBlockParser;
import org.apache.maven.doxia.module.confluence.parser.table.TableBlockParser;
import org.apache.maven.doxia.module.confluence.parser.list.ListBlockParser;
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @plexus.component role="org.apache.maven.doxia.parser.Parser"
 * role-hint="confluence"
 */
public class ConfluenceParser
    extends AbstractParser
{
    private BlockParser[] parsers;

    public ConfluenceParser()
    {
        BlockParser headingParser = new SectionBlockParser();
        BlockParser verbatimParser = new VerbatimBlockParser();
        BlockParser horizontalRuleParser = new HorizontalRuleBlockParser();
        BlockParser paragraphParser = new ParagraphBlockParser();
        BlockParser listParser = new ListBlockParser();
        BlockParser tableParser = new TableBlockParser();

        parsers = new BlockParser[]
            {
                headingParser,
                verbatimParser,
                horizontalRuleParser,
                listParser,
                tableParser,
                paragraphParser
            };
    }

    //TODO: (empty line) Produces a new paragraph
    //TODO: \\ Creates a line break. Not often needed, most of the time Confluence will guess new lines for you appropriately.
    //TODO: better support for anchors

    public List parse( ByLineSource source )
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
                    /*
                    System.out.println( "------------------------------------------------------------" );
                    System.out.println( "line = " + line );
                    System.out.println( "line accepted by: " + parser );
                    System.out.println( "------------------------------------------------------------" );
                    */

                    accepted = true;

                    blocks.add( parser.visit( line, source ) );

                    break;
                }
            }

            /*
            if ( !accepted )
            {
                throw new ParseException( "don't  know how to handle line: " + source.getLineNumber() + ": " + line );
            }
            */
        }

        return blocks;
    }

    public  synchronized void parse( Reader reader, Sink sink )
        throws ParseException
    {
        List blocks;

        ByLineSource source = new ByLineReaderSource( reader );

        try
        {
            blocks = parse( source );
        }
        catch (  ParseException e )
        {
            throw e;
        }
        catch (  Exception e )
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
