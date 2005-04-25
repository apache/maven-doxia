package org.codehaus.doxia.module.xhtml.decoration.model;

import org.codehaus.doxia.module.xhtml.decoration.model.Hyperlink;
import org.codehaus.doxia.module.xhtml.decoration.model.Link;
import org.codehaus.doxia.module.xhtml.decoration.model.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Site.java,v 1.2 2004/09/09 19:54:20 jvanzyl Exp $
 */
public class DecorationModel
{
    private String flavour;

    private Hyperlink bannerLeft;

    private Hyperlink bannerRight;

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

    public Hyperlink getBannerLeft()
    {
        return bannerLeft;
    }

    public void setBannerLeft( Hyperlink bannerLeft )
    {
        this.bannerLeft = bannerLeft;
    }

    public Hyperlink getBannerRight()
    {
        return bannerRight;
    }

    public void setBannerRight( Hyperlink bannerRight )
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
