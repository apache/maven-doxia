package org.codehaus.doxia.module.xhtml.decoration.model;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Hyperlink.java,v 1.1.1.1 2004/09/09 17:09:40 jvanzyl Exp $
 */
public class Hyperlink
{
    private String href;

    private String name;

    public String getHref()
    {
        return href;
    }

    public void setHref( String href )
    {
        this.href = href;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
}
