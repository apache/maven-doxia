package org.codehaus.doxia.module.apt;

import org.codehaus.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AptSiteModule.java,v 1.4 2004/11/02 05:00:40 jvanzyl Exp $
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.site.module.SiteModule"
 *   role-hint="apt"
 */
public class AptSiteModule
    extends AbstractSiteModule
{
    public String getSourceDirectory()
    {
        return "apt";
    }

    public String getExtension()
    {
        return "apt";
    }

    public String getParserId()
    {
        return "apt";
    }
}
