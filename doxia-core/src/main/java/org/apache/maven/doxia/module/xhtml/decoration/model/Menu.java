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
 * @version $Id:Menu.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class Menu
{
    String name;

    List items = new ArrayList();

    public Menu( String name )
    {
        this.name = name;
    }

    public Menu addItem( String name, String href )
    {
        items.add( new Item( name, href ) );

        return this;
    }

    public Menu addItem( Item item )
    {
        items.add( item );

        return this;
    }

    public String getName()
    {
        return name;
    }

    public List getItems()
    {
        return items;
    }
}
