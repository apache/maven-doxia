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

    /** {@inheritDoc} */
    public boolean accept( String line, ByLineSource source )
    {
        if ( isList( line ) )
        {
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        TreeListBuilder treeListBuilder = new TreeListBuilder();

        StringBuffer text = new StringBuffer();

        do
        {
            if ( line.trim().length() == 0 )
            {
                break;
            }

            if (text.length()>0 && isList( line ) )
            {
                // We reached a new line with list prefix
                addItem( treeListBuilder, text );
            }

            if ( text.length() == 0 )
            {
                text.append( line.trim() );
            }
            else
            {
                text.append( " " + line.trim() );
            }

        }
        while ( ( line = source.getNextLine() ) != null );

        if ( text.length() > 0 )
        {
            addItem( treeListBuilder, text );
        }

        return treeListBuilder.getBlock();
    }

    private void addItem( TreeListBuilder treeListBuilder, StringBuffer text )
    {
        String item = text.toString();
        if ( isBulletedList( item ) )
        {
            int level = getLevel( item, '*' );

            treeListBuilder.feedEntry( BULLETED_LIST, level, item.substring( level ) );
        }
        else
        {
            int level = getLevel( item, '#' );

            treeListBuilder.feedEntry( NUMBERED_LIST, level, item.substring( level ) );
        }
        text.setLength( 0 );
    }

    private int getLevel( String line, char c )
    {
        int level = 0;

        while ( line.charAt( level ) == c )
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
