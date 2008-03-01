package org.apache.maven.doxia.module.confluence.parser.table;

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

import org.apache.maven.doxia.util.ByLineSource;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BoldBlock;
import org.apache.maven.doxia.module.confluence.parser.TextBlock;
import org.apache.maven.doxia.parser.ParseException;
import org.codehaus.plexus.util.StringUtils;


/**
 * Parse tables
 *
 * @author Juan F. Codagnone
 * @version $Id$
 */
public class TableBlockParser
    implements BlockParser
{
    /** {@inheritDoc} */
    public  boolean accept( String line, ByLineSource source )
    {
        return line.startsWith( "|" );
    }

    /** {@inheritDoc} */
    public  Block visit(  String line,  ByLineSource source )
        throws ParseException
    {
        if ( !accept( line, source ) )
        {
            throw new IllegalAccessError( "call accept before this ;)" );
        }

        List rows = new ArrayList();

        String l = line;

        do
        {
            l = l.substring( 0, l.lastIndexOf( "|" ) );

            List cells = new ArrayList();

            if ( l.startsWith( "||" ) )
            {
                String[] text = StringUtils.split( l, "||" );


                for ( int i = 0; i < text.length; i++ )
                {
                    List textBlocks = new ArrayList();

                    textBlocks.add( new TextBlock( text[i] ) );

                    List blocks = new ArrayList();

                    blocks.add( new BoldBlock( textBlocks ) );

                    cells.add( new TableCellHeaderBlock( blocks ) );
                }
            }
            else
            {
                String[] text = StringUtils.split( l, "|" );


                for ( int i = 0; i < text.length; i++ )
                {
                    List blocks = new ArrayList();

                    blocks.add( new TextBlock( text[i] ) );

                    cells.add( new TableCellBlock( blocks ) );
                }
            }

            rows.add( new TableRowBlock( cells ) );
        }

        while ( ( l = source.getNextLine() ) != null && accept( l, source ) );

        assert rows.size() >= 1;

        return new TableBlock( rows );
    }
}
