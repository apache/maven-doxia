package org.codehaus.doxia.module.xhtml.decoration.model;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Banner.java,v 1.1.1.1 2004/09/09 17:09:38 jvanzyl Exp $
 */
public class Banner
{
    private String left;

    private String right;

    public String getLeft()
    {
        return left;
    }

    public void setLeft( String left )
    {
        this.left = left;
    }

    public String getRight()
    {
        return right;
    }

    public void setRight( String right )
    {
        this.right = right;
    }
}
