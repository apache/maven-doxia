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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.ChildBlocksBuilder;

/**
 * <p>TreeListBuilder class.</p>
 *
 * @version $Id$
 */
public class TreeListBuilder
{
    private TreeComponent root;

    private TreeComponent current;

    TreeListBuilder()
    {
        root = new TreeComponent( null, "root", 0 );

        current = root;
    }

    void feedEntry( int type, int level, String text )
    {
        int currentDepth = current.getDepth();

        int incomingLevel = level - 1;

        if ( incomingLevel == currentDepth )
        {
            // nothing to move
        }
        else if ( incomingLevel > currentDepth )
        {
            // el actual ahora es el �ltimo que insert�
            List<TreeComponent> components = current.getChildren();

            if ( components.size() == 0 )
            {
                /* for example:
                 *        * item1
                 *     * item2
                 */
                for ( int i = 0, n = incomingLevel - currentDepth; i < n; i++ )
                {
                    current = current.addChildren( "", type );
                }
            }
            else
            {
                current = components.get( components.size() - 1 );
            }
        }
        else
        {
            for ( int i = 0, n = currentDepth - incomingLevel; i < n; i++ )
            {
                current = current.getFather();

                if ( current == null )
                {
                    throw new IllegalStateException();
                }
            }
        }
        current.addChildren( text.trim(), type );
    }

    ListBlock getBlock()
    {
        return getList( root );
    }

    private ListBlock getList( TreeComponent treeComponent )
    {
        List<Block> list = getListItems( treeComponent );

        int type = treeComponent.getChildren().get( 0 ).getType();

        if ( type == ListBlockParser.BULLETED_LIST )
        {
            return new BulletedListBlock( list );
        }

        return new NumberedListBlock( list );
    }

    private List<Block> getListItems( TreeComponent tc )
    {
        List<Block> blocks = new ArrayList<>();

        for ( TreeComponent child : tc.getChildren() )
        {
            List<Block> childBlocks = new ArrayList<>();

            if ( child.getFather() != null )
            {
                childBlocks.addAll( new ChildBlocksBuilder( child.getText() ).getBlocks() );
            }

            if ( child.getChildren().size() != 0 )
            {
                blocks.add( new ListItemBlock( childBlocks, getList( child ) ) );
            }
            else
            {
                blocks.add( new ListItemBlock( childBlocks ) );
            }
        }

        return blocks;
    }
}
