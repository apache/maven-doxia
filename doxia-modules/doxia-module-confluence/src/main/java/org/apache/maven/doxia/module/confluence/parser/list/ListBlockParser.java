package org.apache.maven.doxia.module.confluence.parser.list;

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

import org.apache.maven.doxia.util.ByLineSource;
import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.parser.ParseException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ListBlockParser
    implements BlockParser
{
    public static int BULLETED_LIST = 0;

    public static int NUMBERED_LIST = 1;

    public boolean accept( String line, ByLineSource source )
    {
        String nextLine = null;

        try
        {
            nextLine = source.getNextLine();

            source.ungetLine();
        }
        catch ( ParseException e )
        {
            // do nothing
        }

        if ( isList( line ) && isList( nextLine ) )
        {
            return true;
        }

        return false;
    }

    public  Block visit(  String line,  ByLineSource source )
        throws ParseException
    {
         TreeListBuilder treeListBuilder = new TreeListBuilder();

        String l = line;

        do
        {
            if ( !isList( l ) )
            {
                break;
            }

            if ( isBulletedList( l ) )
            {
                int level = getLevel( l, '*' );

                treeListBuilder.feedEntry( BULLETED_LIST, level, l.substring( level ) );
            }
            else
            {
                int level = getLevel( l, '#' );

                treeListBuilder.feedEntry( NUMBERED_LIST, level, l.substring( level ) );
            }
        }
        while ( ( l = source.getNextLine() ) != null );

        return treeListBuilder.getBlock();
    }

    private int  getLevel( String line, char c )
    {
        int level = 0;

        while ( line.charAt( level )  == c )
        {
            level++;
        }

        return level;
    }

    private boolean isBulletedList( String line )
    {
        return ( line.startsWith( "*" ) || line.startsWith( "-" ) );
    }

    private boolean isList( String line )
    {
        line = line.trim();

        return ( line.startsWith( "*" ) || line.startsWith( "-" ) || line.startsWith( "#" ) );
    }
}
