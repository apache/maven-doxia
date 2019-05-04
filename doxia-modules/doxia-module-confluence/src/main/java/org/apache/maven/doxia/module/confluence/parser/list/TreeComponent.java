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

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
class TreeComponent
{
    private static final String EOL = System.getProperty( "line.separator" );

    private List<TreeComponent> children = new ArrayList<>();

    private String text;

    private TreeComponent father;

    private int type;

    TreeComponent( TreeComponent father, String text, int type )
    {
        this.text = text;
        this.father = father;
        this.type = type;
    }

    List<TreeComponent> getChildren()
    {
        return children;
    }

    TreeComponent addChildren( String t, int ttype )
    {
        if ( t == null )
        {
            throw new IllegalArgumentException( "argument is null" );
        }

        TreeComponent ret = new TreeComponent( this, t, ttype );

        children.add( ret );

        return ret;
    }

    TreeComponent getFather()
    {
        return father;
    }

    int getDepth()
    {
        int ret = 0;

        TreeComponent c = this;

        while ( ( c = c.getFather() ) != null )
        {
            ret++;
        }

        return ret;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return toString( "" );
    }

    String toString( String indent )
    {
        StringBuilder sb = new StringBuilder();

        if ( father != null )
        {
            sb.append( indent );
            sb.append( "- " );
            sb.append( text );
            sb.append( EOL );
        }

        for ( TreeComponent lc : children )
        {
            sb.append( lc.toString( indent + "   " ) );
        }

        return sb.toString();
    }

    String getText()
    {
        return text;
    }

    int getType()
    {
        return type;
    }
}
