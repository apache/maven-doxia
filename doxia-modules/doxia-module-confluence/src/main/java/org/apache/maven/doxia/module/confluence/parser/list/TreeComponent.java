package org.apache.maven.doxia.module.confluence.parser.list;

/*
* Copyright 2004-2006 The Apache Software Foundation.
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class TreeComponent
{
    private static final String EOL = System.getProperty( "line.separator" );

    private List children = new ArrayList();

    private String text;

    private TreeComponent father;

    private int type;

    public TreeComponent( TreeComponent father, String text, int type )
    {
        this.text = text;
        this.father = father;
        this.type = type;
    }

    public List getChildren()
    {
        return children;
    }

    public TreeComponent addChildren( String t, int ttype )
    {
        if ( t == null )
        {
            throw new IllegalArgumentException( "argument is null" );
        }

        TreeComponent ret = new TreeComponent( this, t, ttype );

        children.add( ret );

        return ret;
    }

    public TreeComponent getFather()
    {
        return father;
    }

    public int getDepth()
    {
        int ret = 0;

        TreeComponent c = this;

        while ( ( c = c.getFather() ) != null )
        {
            ret++;
        }

        return ret;
    }

    public String toString()
    {
        return toString( "" );
    }

    public String toString( String indent )
    {
        StringBuffer sb = new StringBuffer();

        if ( father != null )
        {
            sb.append( indent );
            sb.append( "- " );
            sb.append( text );
            sb.append( EOL );
        }

        for ( Iterator i = children.iterator(); i.hasNext(); )
        {
            TreeComponent lc = (TreeComponent) i.next();

            sb.append( lc.toString( indent + "   " ) );
        }

        return sb.toString();
    }

    public String getText()
    {
        return text;
    }

    public int getType()
    {
        return type;
    }
}
