package org.codehaus.doxia.module.xdoc;

import org.codehaus.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.site.module.SiteModule"
 *   role-hint="xdoc"
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
