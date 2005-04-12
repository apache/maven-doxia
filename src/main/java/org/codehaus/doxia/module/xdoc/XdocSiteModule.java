package org.codehaus.doxia.module.xdoc;

import org.codehaus.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: XdocSiteModule.java,v 1.4 2004/11/02 05:00:40 jvanzyl Exp $
 * @plexus.component
 */
public class XdocSiteModule
    extends AbstractSiteModule
{
    public String getSourceDirectory()
    {
        return "xdoc";
    }

    public String getExtension()
    {
        return "xml";
    }

    public String getParserId()
    {
        return "xdoc";
    }
}
