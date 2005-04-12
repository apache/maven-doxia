package org.codehaus.doxia.module.xhtml.decoration.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Item.java,v 1.2 2004/09/09 19:54:20 jvanzyl Exp $
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
