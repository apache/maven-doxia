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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DecorationModel
{
    private String flavour;

    private Banner bannerLeft;

    private Banner bannerRight;

    private List menus = new ArrayList();

    private List links = new ArrayList();

    private boolean search;

    private Map itemGroups = new HashMap();

    public Menu addMenu( String name )
    {
        return addMenu( new Menu( name ) );
    }

    public Menu addMenu( Menu menu )
    {
        menus.add( menu );

        return menu;
    }

    public List getMenus()
    {
        return menus;
    }

    public void addLink( String name, String href )
    {
        addLink( new Link( name, href ) );
    }

    public void addLink( Link link )
    {
        links.add( link );
    }

    public List getLinks()
    {
        return links;
    }

    public Map getItemGroups()
    {
        return itemGroups;
    }

    public void addItemGroup( String href, String group )
    {
        itemGroups.put( href, group );
    }

    // ----------------------------------------------------------------------
    // Search
    // ----------------------------------------------------------------------

    public boolean isSearch()
    {
        return search;
    }

    public void setSearch( boolean search )
    {
        this.search = search;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public Banner getBannerLeft()
    {
        return bannerLeft;
    }

    public void setBannerLeft( Banner bannerLeft )
    {
        this.bannerLeft = bannerLeft;
    }

    public Banner getBannerRight()
    {
        return bannerRight;
    }

    public void setBannerRight( Banner bannerRight )
    {
        this.bannerRight = bannerRight;
    }

    public String getFlavour()
    {
        return flavour;
    }

    public void setFlavour( String flavour )
    {
        this.flavour = flavour;
    }
}
