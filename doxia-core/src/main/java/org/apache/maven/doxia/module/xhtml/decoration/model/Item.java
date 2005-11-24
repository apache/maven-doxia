package org.apache.maven.doxia.module.xhtml.decoration.model;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Item
{
    String name;

    String href;

    boolean foldable;

    String group;

    List items = new ArrayList();

    public Item( String name, String href )
    {
        this.name = name;

        this.href = href;

        group = name;
    }

    // ----------------------------------------------------------------------
    // Name
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    // ----------------------------------------------------------------------
    // Items
    // ----------------------------------------------------------------------

    public void addItem( Item item )
    {
        items.add( item );
    }

    public List getItems()
    {
        return items;
    }

    // ----------------------------------------------------------------------
    // Href
    // ----------------------------------------------------------------------

    public String getHref()
    {
        return href;
    }

    // ----------------------------------------------------------------------
    // Folding
    // ----------------------------------------------------------------------

    public void setFoldable( boolean foldable )
    {
        this.foldable = foldable;
    }

    public boolean isFoldable()
    {
        return foldable;
    }

    // ----------------------------------------------------------------------
    // Visibility
    // ----------------------------------------------------------------------

    boolean itemsVisible()
    {
        return false;
    }

    // ----------------------------------------------------------------------
    // Group
    // ----------------------------------------------------------------------

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getGroup()
    {
        return group;
    }
}
