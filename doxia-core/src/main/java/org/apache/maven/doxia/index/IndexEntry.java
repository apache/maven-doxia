package org.apache.maven.doxia.index;

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

import org.codehaus.plexus.util.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class IndexEntry
{
    private IndexEntry parent;

    private String id;

    private String title;

    private List childEntries = new ArrayList();

    private static final String EOL = System.getProperty( "line.separator" );

    public IndexEntry( String id )
    {
        this.id = id;
    }

    public IndexEntry( IndexEntry parent, String id )
    {
        if ( parent == null )
        {
            throw new NullPointerException( "parent cannot be null." );
        }

        if ( id == null )
        {
            throw new NullPointerException( "parent cannot be null." );
        }

        this.parent = parent;
        this.id = id;

        parent.childEntries.add( this );
    }

    public IndexEntry getParent()
    {
        return parent;
    }

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public List getChildEntries()
    {
        return Collections.unmodifiableList( childEntries );
    }

    public void setChildEntries( List childEntries )
    {
        if ( childEntries == null )
        {
            childEntries = new ArrayList();
        }

        this.childEntries = childEntries;
    }

    // -----------------------------------------------------------------------
    // Utils
    // -----------------------------------------------------------------------

    public IndexEntry getNextEntry()
    {
        if ( parent == null )
        {
            return null;
        }

        List entries = parent.getChildEntries();

        int index = entries.indexOf( this );

        if ( index + 1 >= entries.size() )
        {
            return null;
        }

        return (IndexEntry) entries.get( index + 1 );
    }

    public IndexEntry getPrevEntry()
    {
        if ( parent == null )
        {
            return null;
        }

        List entries = parent.getChildEntries();

        int index = entries.indexOf( this );

        if ( index == 0 )
        {
            return null;
        }

        return (IndexEntry) entries.get( index - 1 );
    }

    public IndexEntry getFirstEntry()
    {
        List entries = getChildEntries();

        if ( entries.size() == 0 )
        {
            return null;
        }
        else
        {
            return (IndexEntry) entries.get( 0 );
        }
    }

    public IndexEntry getLastEntry()
    {
        List entries = getChildEntries();

        if ( entries.size() == 0 )
        {
            return null;
        }
        else
        {
            return (IndexEntry) entries.get( entries.size() - 1 );
        }
    }

    public IndexEntry getRootEntry()
    {
        List entries = getChildEntries();

        if ( entries.size() == 0 )
        {
            return null;
        }
        else if ( entries.size() > 1 )
        {
            throw new RuntimeException( "This index has more than one root entry" );
        }
        else
        {
            return (IndexEntry) entries.get( 0 );
        }
    }

    // -----------------------------------------------------------------------
    // Object Overrides
    // -----------------------------------------------------------------------

    public String toString()
    {
        return toString( 0 );
    }

    public String toString( int depth )
    {
        StringBuffer message = new StringBuffer();

        message.append( "Id: " ).append( id );

        if ( StringUtils.isNotEmpty( title ) )
        {
            message.append( ", title: " ).append( title );
        }

        message.append( EOL );

        String indent = "";

        for ( int i = 0; i < depth; i++ )
        {
            indent += " ";
        }

        for ( Iterator it = getChildEntries().iterator(); it.hasNext(); )
        {
            IndexEntry entry = (IndexEntry) it.next();

            message.append( indent ).append( entry.toString( depth + 1 ) );
        }

        return message.toString();
    }
}
