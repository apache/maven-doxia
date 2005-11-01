package org.codehaus.doxia.module.xhtml.decoration.model;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class Image
{
    private String src;

    private String alt;

    private String title;

    private Link link;

    public Image()
    {
    }

    public String getSrc()
    {
        return src;
    }

    public void setSrc( String src )
    {
        this.src = src;
    }

    public Link getLink()
    {
        return link;
    }

    public void setLink( Link link )
    {
        this.link = link;
    }

    public String getAlt()
    {
        return alt;
    }

    public void setAlt( String alt )
    {
        this.alt = alt;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }
}
