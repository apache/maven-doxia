package org.apache.maven.doxia.module.confluence.parser.list;

import org.apache.maven.doxia.module.confluence.parser.TextBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/*
* Copyright 2004-2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

public class TreeListBuilder
{
    private final TreeComponent root;

    private TreeComponent current;

    public TreeListBuilder()
        throws IllegalArgumentException
    {
        root = new TreeComponent( null, "root", 0 );

        current = root;
    }

    public void feedEntry( int type, int level, String text )
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
            List components = current.getChildren();

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
                current = (TreeComponent) components.get( components.size() - 1 );
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
        current.addChildren( text, type );
    }

    public ListBlock getBlock()
    {
        return getList( root );
    }

    private ListBlock getList( TreeComponent treeComponent )
    {
        List list = getListItems( treeComponent );

        int type = ((TreeComponent)treeComponent.getChildren().get(0)).getType();

        if ( type == ListBlockParser.BULLETED_LIST )
        {
            return new BulletedListBlock( list );
        }
        else
        {
            return new NumberedListBlock( list );
        }
    }

    private List getListItems( TreeComponent tc )
    {
        List blocks = new ArrayList();

        for ( Iterator i = tc.getChildren().iterator(); i.hasNext(); )
        {
            TreeComponent child = (TreeComponent) i.next();

            System.out.println( "child.getText() = " + child.getText() );

            List text = new ArrayList();

            if ( child.getFather() != null )
            {
                text.add( new TextBlock( child.getText() ) );
            }

            if ( child.getChildren().size() != 0 )
            {
                blocks.add( new ListItemBlock( text, getList( child ) ) );
            }
            else
            {
                blocks.add( new ListItemBlock( text ) );
            }
        }

        return blocks;
    }
}
