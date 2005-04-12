package org.codehaus.doxia.module.xhtml.decoration.model;

import org.codehaus.doxia.module.xhtml.decoration.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Menu.java,v 1.2 2004/09/09 19:54:20 jvanzyl Exp $
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
