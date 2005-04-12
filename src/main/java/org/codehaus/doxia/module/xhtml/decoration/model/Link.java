package org.codehaus.doxia.module.xhtml.decoration.model;


/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Link.java,v 1.1.1.1 2004/09/09 17:09:40 jvanzyl Exp $
 */
public class Link
{
    String name;

    String href;

    public Link( String name, String href )
    {
        this.name = name;

        this.href = href;
    }

    public String getName()
    {
        return name;
    }

    public String getHref()
    {
        return href;
    }
}
