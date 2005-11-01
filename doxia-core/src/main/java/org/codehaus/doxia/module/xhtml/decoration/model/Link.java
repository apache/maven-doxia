package org.codehaus.doxia.module.xhtml.decoration.model;


/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Link
{
    private String text;

    private String href;

    public Link()
    {
    }

    public Link( String name, String href )
    {
        this.text = name;

        this.href = href;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public void setHref( String href )
    {
        this.href = href;
    }

    public String getHref()
    {
        return href;
    }
}
